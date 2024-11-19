package dev.frozenmilk

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import dev.frozenmilk.easyautolibraries.EasyAutoLibraryRepository
import dev.frozenmilk.libs.Kotlin
import dev.frozenmilk.libs.SDK
import org.gradle.api.Project
import javax.inject.Inject

open class FTC @Inject constructor(
	override val project: Project,
) : AbstractEasyAutoLibrary<FTC>("ftc") {
	override val parent = null
	// does not get called
	override fun onAccess() {}
	val mavenCentral: EasyAutoLibraryRepository by getOrRegisterRepository("mavenCentral", { mavenCentral() }).name
	val google: EasyAutoLibraryRepository by getOrRegisterRepository("google", { google() }).name
	val sdk: SDK by registerSubLibrary(SDK(this)).name
	val kotlin: Kotlin by registerSubLibrary(Kotlin(this)).name
}