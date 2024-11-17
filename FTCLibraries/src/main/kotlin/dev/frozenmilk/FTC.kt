package dev.frozenmilk

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import dev.frozenmilk.libs.Kotlin
import dev.frozenmilk.libs.SDK
import org.gradle.api.Project
import org.gradle.internal.instantiation.InstantiatorFactory
import javax.inject.Inject

abstract class FTC @Inject constructor(
	override val project: Project,
	instantiatorFactory: InstantiatorFactory
) : AbstractEasyAutoLibrary("ftc", null, instantiatorFactory) {
	// does not get called
	override fun onAccess() {}
	init {
		registerRepository("mavenCentral", { mavenCentral() })
		registerRepository("google", { google() })
		registerSubLibrary(SDK::class.java)
		registerSubLibrary(Kotlin::class.java)
	}
}