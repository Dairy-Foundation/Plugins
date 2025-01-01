pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		google()
	}

	includeBuild("../DairyPublishing")
}

includeBuild("../FTCLibraries") {
	dependencySubstitution {
		substitute(module("dev.frozenmilk:FTCLibraries")).using(project(":"))
	}
}
