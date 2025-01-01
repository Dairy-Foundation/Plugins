package dev.frozenmilk

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformJvmPlugin

@Suppress("unused")
class JVMLibraryPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		with(project) {
			plugins.apply(KotlinPlatformJvmPlugin::class.java)
			plugins.apply("java-library")
		}

		project.extensions.getByType(JavaPluginExtension::class.java).run {
			toolchain {
				it.languageVersion.set(JavaLanguageVersion.of(8))
			}
		}


	}
}