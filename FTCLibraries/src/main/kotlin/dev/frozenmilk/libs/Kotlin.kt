package dev.frozenmilk.libs

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import dev.frozenmilk.easyautolibraries.util.SubMethodAccess
import org.gradle.internal.metaobject.DynamicInvokeResult
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import javax.inject.Inject

open class Kotlin @Inject constructor(override val parent: AbstractEasyAutoLibrary<*>) : AbstractEasyAutoLibrary<Kotlin>("kotlin"), SubMethodAccess {
	val version = project.objects.property(JavaLanguageVersion::class.java)
	init {
		version.set(JavaLanguageVersion.of(8))
	}
	override fun onAccess() {
		with(project) {
			plugins.apply("org.jetbrains.kotlin.android")

			with(extensions.getByType(KotlinAndroidProjectExtension::class.java)) {
				jvmToolchain {
					languageVersion.set(this@Kotlin.version)
				}
				compilerOptions {
					freeCompilerArgs.add("-Xjvm-default=all")
				}
			}
		}
	}

	override fun hasMethod(vararg arguments: Any?): Boolean {
		return arguments.size == 1 && (
				Integer::class.java.isInstance(arguments[0]!!)
						|| JavaLanguageVersion::class.java.isInstance(arguments[0]!!))
	}

	override fun tryInvokeMethod(vararg arguments: Any?): DynamicInvokeResult {
		return if (arguments.size == 1 && Int::class.java.isInstance(arguments[0]!!)) {
			if (Int::class.java.isInstance(arguments[0]!!)) {
				version.set(JavaLanguageVersion.of(arguments[0] as Int))
				DynamicInvokeResult.found()
			}
			else if (JavaLanguageVersion::class.java.isInstance(arguments[0]!!)) {
				version.set(arguments[0] as JavaLanguageVersion)
				DynamicInvokeResult.found()
			}
			else DynamicInvokeResult.notFound()
		}
		else DynamicInvokeResult.notFound()
	}
}