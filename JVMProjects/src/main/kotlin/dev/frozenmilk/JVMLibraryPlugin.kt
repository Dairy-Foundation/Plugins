package dev.frozenmilk

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper

@Suppress("unused")
class JVMLibraryPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		with(project) {
			// note: "org.jetbrains.kotlin.jvm"
			plugins.apply(KotlinPluginWrapper::class.java)
			plugins.apply("java-library")

			repositories.run {
				google()
				mavenCentral()
			}

			extensions.getByType(KotlinJvmProjectExtension::class.java).run {
				jvmToolchain(8)
				compilerOptions {
					freeCompilerArgs.add("-Xjvm-default=all")
				}
			}

			extensions.getByType(JavaPluginExtension::class.java).run {
				withSourcesJar()
			}

			dependencies.add("testImplementation", "junit:junit:4.13.2")
		}
	}
}