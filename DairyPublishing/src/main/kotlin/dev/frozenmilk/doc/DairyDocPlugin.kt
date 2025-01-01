package dev.frozenmilk.doc

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.tasks.Jar
import org.jetbrains.dokka.gradle.tasks.DokkaGeneratePublicationTask
import org.jetbrains.kotlin.gradle.utils.named

@Suppress("unused")
class DairyDocPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run{
        "org.jetbrains.dokka".let {
            if (!plugins.hasPlugin(it)) plugins.apply(it)
        }
        "org.jetbrains.dokka-javadoc".let {
            if (!plugins.hasPlugin(it)) plugins.apply(it)
        }
        pluginManager.withPlugin("com.android.library") {
            dependencies.add("dokkaPlugin", "org.jetbrains.dokka:android-documentation-plugin:2.0.0")
        }

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