package dev.frozenmilk

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import dev.frozenmilk.libs.Kotlin
import dev.frozenmilk.libs.SDK
import org.gradle.api.Project

@Suppress("unused")
class FTC (
	project: Project
) : AbstractEasyAutoLibrary<FTC>("ftc", project) {
	override val parent = null
	// warn: does not get called
	override fun onAccess() {}
	val mavenCentral by getOrRegisterRepository("mavenCentral", { mavenCentral() })
	val google by getOrRegisterRepository("google", { google() })
	val sdk by registerSubLibrary(SDK(this))
	val kotlin by registerSubLibrary(Kotlin(this))
}