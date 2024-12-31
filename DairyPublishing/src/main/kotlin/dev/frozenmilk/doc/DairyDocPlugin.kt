package dev.frozenmilk.doc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.DokkaPlugin
import org.jetbrains.dokka.gradle.formats.DokkaJavadocPlugin
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jetbrains.kotlin.gradle.utils.named

@Suppress("unused")
class DairyDocPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run{
        plugins.apply(DokkaPlugin::class.java)
        plugins.apply(DokkaJavadocPlugin::class.java)

        val dokkaHtmlJar = tasks.register("dokkaHtmlJar", Jar::class.java) { task ->
            task.run {
                description = "A HTML Documentation JAR containing Dokka HTML"
                from(tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationHtml").flatMap { it.outputDirectory })
                archiveClassifier.set("html-doc")
            }
        }
        val dokkaJavadocJar = tasks.register("dokkaJavadocJar", Jar::class.java) { task ->
            task.run {
                description = "A Javadoc JAR containing Dokka Javadoc"
                from(tasks.named<DokkaGeneratePublicationTask>("dokkaGeneratePublicationJavadoc").flatMap { it.outputDirectory })
                archiveClassifier.set("javadoc")
            }
        }

        extensions.create(
            "dairyDoc",
            DairyDocExtensionImpl::class.java,
            dokkaHtmlJar,
            dokkaJavadocJar
        )

        Unit
    }
}