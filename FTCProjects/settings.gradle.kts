pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		google()
		maven("https://repo.dairy.foundation/releases")
	}

	includeBuild("../FTCLibraries")
	includeBuild("../DairyPublishing")
}