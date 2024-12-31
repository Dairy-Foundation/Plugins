pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		google()
	}

	includeBuild("../DairyPublishing")
}

includeBuild("../EasyAutoLibraries") {
	dependencySubstitution {
		substitute(module("dev.frozenmilk:EasyAutoLibraries")).using(project(":"))
	}
}