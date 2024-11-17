package dev.frozenmilk

import org.gradle.api.Plugin
import org.gradle.api.Project

class FTCLibrariesPlugin : Plugin<Project> {
	override fun apply(project: Project) {
		project.extensions.add("ftc", project.objects.newInstance(FTC::class.java, project))
	}
}