package dev.frozenmilk.publishing

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import java.net.URI

interface DairyPublishingExtension {
    /**
     * the directory that contains the `.git` directory, by default this is the root project directory
     */
    val gitDir: DirectoryProperty
    /**
     * the name of the `git` executable, by default this is "git"
     */
    val gitExecutable: Property<String>
    /**
     * the repository name, by default this is "Dairy"
     */
    val repositoryName: Property<String>
    /**
     * the uri of the releases repository, by default this is the dairy releases repository
     */
    val releasesRepository: Property<URI>
    /**
     * the uri of the snapshots repository, by default this is the dairy snapshots repository
     */
    val snapshotsRepository: Property<URI>

    /**
     * git reference -- either a commit hash, or a tag
     *
     * accessing this will cache the result, so configure the plugin first
     */
    val gitRef: String

    /**
     * the version as determined by git
     *
     * accessing this will cache the result, so configure the plugin first
     */
    val version: String

    /**
     * if the working tree is clean
     *
     * accessing this will cache the result, so configure the plugin first
     */
    val clean: Boolean

    /**
     * if the version is a snapshot
     *
     * accessing this will cache the result, so configure the plugin first
     */
    val snapshot: Boolean
}