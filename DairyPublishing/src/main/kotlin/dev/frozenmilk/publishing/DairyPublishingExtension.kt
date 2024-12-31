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
}