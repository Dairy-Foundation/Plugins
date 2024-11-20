repositories {
	mavenCentral()
	google()
	maven("https://repo.dairy.foundation/releases")
}

plugins {
	`kotlin-dsl`
	id("java-gradle-plugin")
	//noinspection GradleDependency
	id("org.jetbrains.kotlin.jvm") version "1.9.23"
	id("maven-publish")
}

val sdkVersion = "10.1.1"

group = "dev.frozenmilk"
version = "$sdkVersion-0.0.0"

kotlin {
	jvmToolchain(17)
	compilerOptions {
		freeCompilerArgs.add("-Xjvm-default=all")
	}
}

dependencies {
	//noinspection AndroidGradlePluginVersion
	implementation("com.android.tools.build:gradle:8.7.0")
	//noinspection GradleDependency
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.23")
	//noinspection GradleDynamicVersion
	implementation("dev.frozenmilk:FTCLibraries:$sdkVersion-0.0.+")
}

publishing {
	repositories {
		maven {
			name = "Dairy"
			url = uri("https://repo.dairy.foundation/releases")
			credentials(PasswordCredentials::class)
			authentication {
				create<BasicAuthentication>("basic")
			}
		}
		maven {
			name = "DairySNAPSHOT"
			url = uri("https://repo.dairy.foundation/snapshots")
			credentials(PasswordCredentials::class)
			authentication {
				create<BasicAuthentication>("basic")
			}
		}
	}
}

gradlePlugin {
	plugins {
		create("TeamCode") {
			id = "dev.frozenmilk.teamcode"
			implementationClass = "dev.frozenmilk.TeamCode"
		}
	}
}
