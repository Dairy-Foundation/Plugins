package dev.frozenmilk.publishing

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.authentication.http.BasicAuthentication

@Suppress("unused")
class DairyPublishingPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        plugins.apply(MavenPublishPlugin::class.java)

        val extension = extensions.create(
            "dairyPublishing",
            DairyPublishingExtensionImpl::class.java,
            this
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
            extensions.getByType(PublishingExtension::class.java).run {
                repositories.run {
                    maven {it.run {
                        name = extension.repositoryName.get()
                        url = uri(
                            if (extension.snapshot) extension.snapshotsRepository.get()
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