import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

repositories {
	mavenCentral()
	google()
}

plugins {
	id("org.jetbrains.kotlin.jvm") version "2.0.21"
	id("java-gradle-plugin")
}

group = "dev.frozenmilk"
version = "0.0.0"

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
	//noinspection GradleDependency
	implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
	implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
}

gradlePlugin {
	plugins {
		create("BuildMetaData") {
			id = "dev.frozenmilk.build-meta-data"
			implementationClass = "dev.frozenmilk.BuildMetaData"
		}
	}
}
