package dev.frozenmilk.publishing

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.api.reflect.HasPublicType
import org.gradle.api.reflect.TypeOf
import java.io.ByteArrayOutputStream
import java.net.URI

@Suppress("MemberVisibilityCanBePrivate")
abstract class DairyPublishingExtensionImpl (private val project: Project) : DairyPublishingExtension, HasPublicType {
    override fun getPublicType(): TypeOf<DairyPublishingExtension> = TypeOf.typeOf(DairyPublishingExtension::class.java)/**
     * the directory that contains the `.git` directory, by default this is the root project directory
     */
    override val gitDir: DirectoryProperty = project.objects.directoryProperty()
    /**
     * the name of the `git` executable, by default this is "git"
     */
    override val gitExecutable: Property<String> = project.objects.property(String::class.java).apply { set("git") }
    /**
     * the repository name, by default this is "Dairy"
     */
    override val repositoryName: Property<String> = project.objects.property(String::class.java).apply { set("Dairy") }
    /**
     * the uri of the releases repository, by default this is the dairy releases repository
     */
    override val releasesRepository: Property<URI> = project.objects.property(URI::class.java)
    /**
     * the uri of the snapshots repository, by default this is the dairy snapshots repository
     */
    override val snapshotsRepository: Property<URI> = project.objects.property(URI::class.java)

    private val configurationActions = mutableListOf<Action<DairyPublishingExtensionImpl>>()

    private var _gitRef = ""
    override val gitRef: String
        get() {
            finalize()
            return _gitRef
        }

    private var _version = ""
    override val version: String
        get() {
            finalize()
            return _version
        }

    private var _clean = false
    override val clean: Boolean
        get() {
            finalize()
            return _clean
        }

    private var _snapshot = false
    override val snapshot: Boolean
        get() {
            finalize()
            return _snapshot
        }

    /**
     * adds an action to run before this is consumed, actions are run in order of registration
     */
    fun configureBeforeConsume(action: Action<DairyPublishingExtensionImpl>) {
        configurationActions.add(action)
    }

    private var finalised = false
    internal fun finalize() {
        if (finalised) return
        finalised = true
        // NOTE: allows for configuration actions register themselves
        var i = 0
        while (i < configurationActions.size) {
            configurationActions[i].execute(this)
            i++
        }
        gitDir.finalizeValue()
        gitExecutable.finalizeValue()
        repositoryName.finalizeValue()
        releasesRepository.finalizeValue()
        snapshotsRepository.finalizeValue()

        project.run {
            val clean = run {
                val sout = ByteArrayOutputStream()
                exec {it.run {
                    workingDir = gitDir.get().asFile
                    standardOutput = sout
                    commandLine(gitExecutable.get(), "status", "--porcelain")
                }}.assertNormalExitValue()
                sout.toString()
            }.isBlank()

            if (!clean) {
                tasks.all {
                    if (it.group == PublishingPlugin.PUBLISH_TASK_GROUP) {
                        it.doFirst {
                            throw UncleanWorkingTree()
                        }
                    }
                }
            }

            val tags = run {
                val out = ByteArrayOutputStream()
                exec {it.run {
                    workingDir = gitDir.get().asFile
                    standardOutput = out
                    commandLine(gitExecutable.get(), "tag", "--points-at", "HEAD")
                }}.assertNormalExitValue()
                out.toString().trim()
            }

            _snapshot = if (tags.isBlank()) {
                val hash = run {
                    val sout = ByteArrayOutputStream()
                    exec {it.run {
                        workingDir = gitDir.get().asFile
                        standardOutput = sout
                        commandLine(gitExecutable.get(), "rev-parse", "--short", "HEAD")
                    }}.assertNormalExitValue()
                    sout.toString().trim()
                }.ifBlank { throw UnknownError("unable to determine hashcode for HEAD, this shouldn't be reachable") }
                _gitRef = hash
                _version = "SNAPSHOT-$hash"
                true
            }
            else {
                // first tag
                val tag = tags.split('\n').also {
                    if (it.size != 1) logger.warn("Found multiple tags for HEAD:\n$tags\nSelected the first one: ${it.first().trim()}")
                }.first().trim()
                _gitRef = tag
                _version = tag
                false
            }

            version = _version
        }
    }
}