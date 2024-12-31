package dev.frozenmilk.publishing

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.publish.plugins.PublishingPlugin
import org.gradle.authentication.http.BasicAuthentication
import java.io.ByteArrayOutputStream

@Suppress("unused")
class DairyPublishingPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        plugins.apply(MavenPublishPlugin::class.java)

        val extension = extensions.create(
            "dairyPublishing",
            DairyPublishingExtensionImpl::class.java,
        )

        extension.gitDir.set(rootDir)

        extension.releasesRepository.set(uri("https://repo.dairy.foundation/releases"))
        extension.snapshotsRepository.set(uri("https://repo.dairy.foundation/snapshots"))

        tasks.create("displayVersion") {it.run {
            group = "Help"
            doLast {
                println(version)
            }
        }}

        afterEvaluate {
            extension.finalize()

            val clean = run {
                val sout = ByteArrayOutputStream()
                exec {it.run {
                    workingDir = extension.gitDir.get().asFile
                    standardOutput = sout
                    commandLine(extension.gitExecutable.get(), "status", "--porcelain")
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
                val sout = ByteArrayOutputStream()
                exec {it.run {
                    workingDir = extension.gitDir.get().asFile
                    standardOutput = sout
                    commandLine(extension.gitExecutable.get(), "tag", "--points-at", "HEAD")
                }}.assertNormalExitValue()
                sout.toString().trim()
            }

            val snapshot = if (tags.isBlank()) {
                val hash = run {
                    val sout = ByteArrayOutputStream()
                    exec {it.run {
                        workingDir = extension.gitDir.get().asFile
                        standardOutput = sout
                        commandLine(extension.gitExecutable.get(), "rev-parse", "--short", "HEAD")
                    }}.assertNormalExitValue()
                    sout.toString().trim()
                }.ifBlank { throw UnknownError("unable to determine hashcode for HEAD, this shouldn't be reachable") }
                version = "SNAPSHOT@$hash"
                true
            }
            else {
                // first tag
                val tag = tags.split('\n').also {
                    if (it.size != 1) logger.warn("Found multiple tags for HEAD:\n$tags\nSelected the first one: ${it.first().trim()}")
                }.first().trim()
                version = tag
                false
            }

            extensions.getByType(PublishingExtension::class.java).run {
                repositories.run {
                    maven {it.run {
                        name = extension.repositoryName.get()
                        url = uri(
                            if (snapshot) extension.snapshotsRepository.get()
                            else extension.releasesRepository.get()
                        )
                        credentials(PasswordCredentials::class.java)
                        authentication.create("basic", BasicAuthentication::class.java)
                    }}
                }
            }
        }
    }
}