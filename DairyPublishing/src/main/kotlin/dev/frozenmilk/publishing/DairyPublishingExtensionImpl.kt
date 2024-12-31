package dev.frozenmilk.publishing

import org.gradle.api.Action
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.reflect.HasPublicType
import org.gradle.api.reflect.TypeOf
import java.net.URI
import javax.inject.Inject

@Suppress("MemberVisibilityCanBePrivate")
abstract class DairyPublishingExtensionImpl @Inject constructor(objectFactory: ObjectFactory) : DairyPublishingExtension, HasPublicType {
    override fun getPublicType(): TypeOf<DairyPublishingExtension> = TypeOf.typeOf(DairyPublishingExtension::class.java)/**
     * the directory that contains the `.git` directory, by default this is the root project directory
     */
    override val gitDir: DirectoryProperty = objectFactory.directoryProperty()
    /**
     * the name of the `git` executable, by default this is "git"
     */
    override val gitExecutable: Property<String> = objectFactory.property(String::class.java).apply { set("git") }
    /**
     * the repository name, by default this is "Dairy"
     */
    override val repositoryName: Property<String> = objectFactory.property(String::class.java).apply { set("Dairy") }
    /**
     * the uri of the releases repository, by default this is the dairy releases repository
     */
    override val releasesRepository: Property<URI> = objectFactory.property(URI::class.java)
    /**
     * the uri of the snapshots repository, by default this is the dairy snapshots repository
     */
    override val snapshotsRepository: Property<URI> = objectFactory.property(URI::class.java)

    private val configurationActions = mutableListOf<Action<DairyPublishingExtensionImpl>>()

    /**
     * adds an action to run before this is consumed, actions are run in order of registration
     */
    fun configureBeforeConsume(action: Action<DairyPublishingExtensionImpl>) {
        configurationActions.add(action)
    }

    internal fun finalize() {
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
    }
}