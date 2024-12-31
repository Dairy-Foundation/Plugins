package dev.frozenmilk.libs

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import org.gradle.api.provider.Property
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

@Suppress("unused", "MemberVisibilityCanBePrivate")
open class Kotlin (override val parent: AbstractEasyAutoLibrary<*>) : AbstractEasyAutoLibrary<Kotlin>("kotlin") {
	val javaLanguageVersionProperty: Property<JavaLanguageVersion> = project.objects.property(JavaLanguageVersion::class.java)

	var javaLanguageVersion: JavaLanguageVersion
		get() = javaLanguageVersionProperty.get()
		set(value) {
			javaLanguageVersionProperty.set(value)
		}

	fun javaLanguageVersion(version: Int) {
		javaLanguageVersion = JavaLanguageVersion.of(version)
	}

	init {
		javaLanguageVersion = JavaLanguageVersion.of(8)
	}

	override fun onAccess() {
		with(project) {
			plugins.apply("org.jetbrains.kotlin.android")

			with(extensions.getByType(KotlinAndroidProjectExtension::class.java)) {
				jvmToolchain {
					it.languageVersion.set(this@Kotlin.javaLanguageVersionProperty)
				}
				compilerOptions {
					freeCompilerArgs.add("-Xjvm-default=all")
				}
			}
		}
	}
}