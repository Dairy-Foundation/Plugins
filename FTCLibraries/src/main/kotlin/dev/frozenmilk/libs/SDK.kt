package dev.frozenmilk.libs

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import dev.frozenmilk.easyautolibraries.EasyAutoLibraryDependency
import dev.frozenmilk.easyautolibraries.EasyAutoLibraryRepository
import dev.frozenmilk.easyautolibraries.util.VersionProvider
import javax.inject.Inject

open class SDK @Inject constructor(
	override val parent: AbstractEasyAutoLibrary<*>,
) : AbstractEasyAutoLibrary<SDK>("sdk"), VersionProvider {
	override val version = "10.1.1"

	val mavenCentral: EasyAutoLibraryRepository by getOrRegisterRepository("mavenCentral", { mavenCentral() }).name
	val google: EasyAutoLibraryRepository by getOrRegisterRepository("google", { google() }).name

	val FtcCommon: EasyAutoLibraryDependency by registerDependency("FtcCommon", { version -> "org.firstinspires.ftc:FtcCommon:$version" }, { mavenCentral }).name
	val RobotCore: EasyAutoLibraryDependency by registerDependency("RobotCore", { version -> "org.firstinspires.ftc:RobotCore:$version" }, { mavenCentral }).name
	val RobotServer: EasyAutoLibraryDependency by registerDependency("RobotServer", { version -> "org.firstinspires.ftc:RobotServer:$version" }, { mavenCentral }).name
	val Hardware: EasyAutoLibraryDependency by registerDependency("Hardware", { version -> "org.firstinspires.ftc:Hardware:$version" }, { mavenCentral }).name
	val Vision: EasyAutoLibraryDependency by registerDependency("Vision", { version -> "org.firstinspires.ftc:Vision:$version" }, { mavenCentral }).name
	val Inspection: EasyAutoLibraryDependency by registerDependency("Inspection", { version -> "org.firstinspires.ftc:Inspection:$version" }, { mavenCentral }).name
	val Blocks: EasyAutoLibraryDependency by registerDependency("Blocks", { version -> "org.firstinspires.ftc:Blocks:$version" }, { mavenCentral }).name
	val OnBotJava: EasyAutoLibraryDependency by registerDependency("OnBotJava", { version -> "org.firstinspires.ftc:OnBotJava:$version" }, { mavenCentral }).name

	val appcompat: EasyAutoLibraryDependency by registerDependency(
		"appcompat",
		{ version -> "androidx.appcompat:appcompat:$version" },
		{ google }
	) {
		version = "1.2.0"
	}.name

	override fun onAccess() {
		project.afterEvaluate {
			if (this@SDK.dependencies.none { it.accessed }) {
				// apply all
				this@SDK.dependencies.forEach {
					// we don't want teams to accidentally import that blocks opmode companion
					if (it.name == "Blocks" && it.configurationNames.contains("implementation")) {
						it.configurationNames -= "implementation"
						it.configurationNames += "runtimeOnly"
					}
					it()
				}
			}
		}
	}
}