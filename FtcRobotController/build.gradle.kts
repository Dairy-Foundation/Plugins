import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

repositories {
    mavenCentral()
    google()
}

plugins {
    //noinspection AndroidGradlePluginVersion
    id("com.android.library") version "8.7.0"
    id("maven-publish")
    id("dev.frozenmilk.ftc-libraries")
}

ftc.sdk {
    configurationNames = mutableSetOf("implementation")
    FtcCommon
    RobotCore
    RobotServer
    Hardware
    Vision
    Inspection
    Blocks
    OnBotJava
    appcompat
}

android {
    defaultConfig {
        minSdk = 24
        @Suppress("DEPRECATION")
        //noinspection ExpiredTargetSdkVersion
        targetSdk = 28
        buildConfigField("String", "APP_BUILD_TIME", "\"${SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ROOT).format(Date())}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    //noinspection GradleDependency
    compileSdk = 30

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    namespace = "com.qualcomm.ftcrobotcontroller"
    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
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
    publications {
        register<MavenPublication>("release") {
            groupId = "com.qualcomm.ftcrobotcontroller"
            artifactId = "FtcRobotController"
            version = "10.2.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}