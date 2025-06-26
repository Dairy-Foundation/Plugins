package dev.frozenmilk.libs

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import dev.frozenmilk.easyautolibraries.util.VersionProvider

@Suppress("unused", "MemberVisibilityCanBePrivate", "PropertyName")
open class SDK (
	override val parent: AbstractEasyAutoLibrary<*>,
) : AbstractEasyAutoLibrary<SDK>("sdk"), VersionProvider {
	override var version = "10.2.0"

	val mavenCentral by getOrRegisterRepository("mavenCentral", { mavenCentral() })
	val google by getOrRegisterRepository("google", { google() })

	val FtcCommon by registerDependency(
		"FtcCommon",
		{ version -> "org.firstinspires.ftc:FtcCommon:$version" },
		{ mavenCentral }
	)
	val RobotCore by registerDependency(
		"RobotCore",
		{ version -> "org.firstinspires.ftc:RobotCore:$version" },
		{ mavenCentral }
	)
	val RobotServer by registerDependency(
		"RobotServer",
		{ version -> "org.firstinspires.ftc:RobotServer:$version" },
		{ mavenCentral }
	)
	val Hardware by registerDependency(
		"Hardware",
		{ version -> "org.firstinspires.ftc:Hardware:$version" },
		{ mavenCentral }
	)
	val Vision by registerDependency(
		"Vision",
		{ version -> "org.firstinspires.ftc:Vision:$version" },
		{ mavenCentral }
	)
	val Inspection by registerDependency(
		"Inspection",
		{ version -> "org.firstinspires.ftc:Inspection:$version" },
		{ mavenCentral }
	)
	val Blocks by registerDependency(
		"Blocks",
		{ version -> "org.firstinspires.ftc:Blocks:$version" },
		{ mavenCentral }
	)
	val OnBotJava by registerDependency(
		"OnBotJava",
		{ version -> "org.firstinspires.ftc:OnBotJava:$version" },
		{ mavenCentral }
	)

	val appcompat by registerDependency(
		"appcompat",
		{ version -> "androidx.appcompat:appcompat:$version" },
		{ google }
	) {
		it.version = "1.2.0"
	}

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