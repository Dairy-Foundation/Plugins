package dev.frozenmilk.libs

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import dev.frozenmilk.easyautolibraries.util.VersionProvider
import org.gradle.internal.instantiation.InstantiatorFactory
import javax.inject.Inject

abstract class SDK @Inject constructor(
	parent: AbstractEasyAutoLibrary,
	instantiatorFactory: InstantiatorFactory
) : AbstractEasyAutoLibrary("sdk", parent, instantiatorFactory), VersionProvider {
	override val version = "10.1.1"
	init {
		registerDependency("FtcCommon", { version -> "org.firstinspires.ftc:FtcCommon:$version" }, "mavenCentral")
		registerDependency("RobotCore", { version -> "org.firstinspires.ftc:RobotCore:$version" }, "mavenCentral")
		registerDependency("RobotServer", { version -> "org.firstinspires.ftc:RobotServer:$version" }, "mavenCentral")
		registerDependency("Hardware", { version -> "org.firstinspires.ftc:Hardware:$version" }, "mavenCentral")
		registerDependency("Vision", { version -> "org.firstinspires.ftc:Vision:$version" }, "mavenCentral")
		registerDependency("Inspection", { version -> "org.firstinspires.ftc:Inspection:$version" }, "mavenCentral")
		registerDependency("Blocks", { version -> "org.firstinspires.ftc:Blocks:$version" }, "mavenCentral")
		registerDependency("OnBotJava", { version -> "org.firstinspires.ftc:OnBotJava:$version" }, "mavenCentral")

		registerDependency("appcompat", { version -> "org.firstinspires.ftc:OnBotJava:$version" }, "google")
	}

	/**
	 * applies all dependencies as though for TeamCode if not configured
	 */
	fun applyDefaultsIfNeeded() {
		if (this@SDK.dependencies.all { !it.accessed }) {
			// apply all
			this@SDK.dependencies.forEach {
				// we don't want teams to accidentally import that blocks opmode companion
				if (it.name == "Blocks") it.apply(configurationName = "runtimeOnly")
				else it.apply()
			}
		}
	}
	override fun onAccess() {
		project.afterEvaluate {
			applyDefaultsIfNeeded()
		}
	}
}