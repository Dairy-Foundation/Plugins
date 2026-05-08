package dev.frozenmilk

import dev.frozenmilk.easyautolibraries.EasyAutoScopeRoot
import dev.frozenmilk.easyautolibraries.brightBlue
import dev.frozenmilk.libs.ACMERobotics
import dev.frozenmilk.libs.Dairy
import dev.frozenmilk.libs.FateWeaver
import dev.frozenmilk.libs.FtControl
import dev.frozenmilk.libs.Marrow
import dev.frozenmilk.libs.Next
import dev.frozenmilk.libs.Pedro
import dev.frozenmilk.libs.PsiLynx
import dev.frozenmilk.libs.SDK
import dev.frozenmilk.libs.Solvers
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.jetbrains.kotlin.gradle.dsl.HasConfigurableKotlinCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompilerOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("unused")
class FTC(project: Project) : EasyAutoScopeRoot<FTC>(project, LogLevel.LIFECYCLE) {
    @Suppress("UNCHECKED_CAST")
    class KotlinConfig(
        private val extension: KotlinBaseExtension,
    ) : KotlinBaseExtension by extension,
        HasConfigurableKotlinCompilerOptions<KotlinJvmCompilerOptions> by extension as HasConfigurableKotlinCompilerOptions<KotlinJvmCompilerOptions> {
        override fun toString() = extension.toString()
    }

    fun kotlin(f: KotlinConfig.() -> Unit = {}) {
        val isAndroid = project.plugins.hasPlugin("com.android.base")
        if (isAndroid) {
            project.logger.log(logLevel, brightBlue("kotlin-android"))
            project.plugins.apply("org.jetbrains.kotlin.android")

            with(project.extensions.getByType(KotlinAndroidProjectExtension::class.java)) {
                jvmToolchain { it.languageVersion.set(JavaLanguageVersion.of(8)) }
                compilerOptions {
                    if (project.plugins.hasPlugin("com.android.library")) {
                        freeCompilerArgs.add("-Xreturn-value-checker=full")
                    } else {
                        freeCompilerArgs.add("-Xreturn-value-checker=check")
                    }
                    jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)
                }
                KotlinConfig(this).f()
            }
        } else {
            project.logger.log(logLevel, brightBlue("kotlin-jvm"))
            project.plugins.apply("org.jetbrains.kotlin.jvm")

            project.tasks.withType(KotlinCompile::class.java) { task ->
                task.compilerOptions.jvmTarget.set(JvmTarget.JVM_1_8)
            }

            with(project.extensions.getByType(KotlinJvmProjectExtension::class.java)) {
                jvmToolchain { it.languageVersion.set(JavaLanguageVersion.of(8)) }
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                    if (project.plugins.hasPlugin("java-library")) {
                        freeCompilerArgs.add("-Xreturn-value-checker=full")
                    } else {
                        freeCompilerArgs.add("-Xreturn-value-checker=check")
                    }
                    jvmDefault.set(JvmDefaultMode.NO_COMPATIBILITY)
                }
                KotlinConfig(this).f()
            }
        }
    }

    val sdk = SDK(this)

    val acmerobotics = ACMERobotics(this)

    val ftControl = FtControl(this)

    // note: needs to come after acmerobotics' dashboard
    // note: needs to come after ftControl
    val dairy = Dairy(this)

    val next = Next(this)

    val pedro = Pedro(this)

    val solvers = Solvers(this)

    val psiLynx = PsiLynx(this)

    val fateWeaver = FateWeaver(this)

    val marrow = Marrow(this)
}