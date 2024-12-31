import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
	mavenCentral()
	google()
	maven("https://repo.dairy.foundation/releases")
}

plugins {
	id("org.jetbrains.kotlin.jvm") version "2.0.21"
	id("java-gradle-plugin")
	id("dev.frozenmilk.publish")
}

group = "dev.frozenmilk"

kotlin {
	jvmToolchain(17)
	compilerOptions {
		freeCompilerArgs.add("-Xjvm-default=all")
	}
	coreLibrariesVersion = "1.9.24"
}

tasks.withType<KotlinCompile>().configureEach {
	compilerOptions.apiVersion.set(KotlinVersion.KOTLIN_1_9)
}

dependencies {
	//noinspection AndroidGradlePluginVersion
	implementation("com.android.tools.build:gradle:8.7.0")
	//noinspection GradleDependency
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
	implementation("dev.frozenmilk:FTCLibraries")
}

dairyPublishing {
	// git directory is in the parent
	gitDir = file("..")
}

gradlePlugin {
	plugins {
		create("Library") {
			id = "dev.frozenmilk.android-library"
			implementationClass = "dev.frozenmilk.AndroidLibraryPlugin"
		}
	}
	plugins {
		create("TeamCode") {
			id = "dev.frozenmilk.teamcode"
			implementationClass = "dev.frozenmilk.TeamCodePlugin"
		}
	}
}
