package dev.frozenmilk

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class AndroidLibraryPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		with(project) {
			plugins.apply("com.android.library")
			plugins.apply("dev.frozenmilk.ftc-libraries")
		}

		val ftc = project.extensions.getByType(FTC::class.java)
		ftc.sdk {
			it.configurationNames = mutableSetOf("compileOnly")
			it.appcompat
		}

		val androidComponentsExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)

		if (androidComponentsExtension !is LibraryAndroidComponentsExtension)
			error("Library can only be applied to an Android Library")

		androidComponentsExtension.finalizeDsl { it ->
			it.apply {
				compileSdk = 30

				defaultConfig {
					minSdk = 24
					@Suppress("DEPRECATION")
					targetSdk = 28

					testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
				}

				compileOptions {
					sourceCompatibility = JavaVersion.VERSION_1_8
					targetCompatibility = JavaVersion.VERSION_1_8
				}

				ndkVersion = "21.3.6528147"
			}
		}
	}
}