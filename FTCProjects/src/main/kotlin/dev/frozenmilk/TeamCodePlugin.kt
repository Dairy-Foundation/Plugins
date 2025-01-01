package dev.frozenmilk

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JavaToolchainService
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.regex.Pattern
import java.util.zip.ZipFile
import javax.inject.Inject

@Suppress("unused")
class TeamCodePlugin @Inject constructor(private val javaToolchainService: JavaToolchainService) : Plugin<Project> {
	override fun apply(project: Project) {
		with(project) {
			plugins.apply("com.android.application")
			plugins.apply("dev.frozenmilk.ftc-libraries")
		}

		val ftcLibraries = project.extensions.getByType(FTC::class.java)
		ftcLibraries.configurationNames = mutableSetOf("implementation")
		val sdk = ftcLibraries.sdk

		sdk {
			it.google
			it.mavenCentral
		}

		val ftcRobotControllerConfiguration: Configuration
		with(project) {
			extensions.getByType(JavaPluginExtension::class.java).run {
				toolchain {
					it.languageVersion.set(JavaLanguageVersion.of(8))
				}
			}

			repositories.maven {
				it.url = uri("https://repo.dairy.foundation/releases")
			}

			ftcRobotControllerConfiguration = configurations.create("FtcRobotController") {
				it.isCanBeResolved = true
				it.isVisible = false
			}

			afterEvaluate {
				dependencies.add(ftcRobotControllerConfiguration.name, "com.qualcomm.ftcrobotcontroller:FtcRobotController:${sdk.version}")
				dependencies.add("implementation", "com.qualcomm.ftcrobotcontroller:FtcRobotController:${sdk.version}")
			}

			dependencies.add("testImplementation", "junit:junit:4.13.2")
			tasks.withType(Test::class.java).configureEach { testTask ->
				testTask.javaLauncher.set(javaToolchainService.launcherFor {
					it.languageVersion.set(JavaLanguageVersion.of(17))
				})
			}
		}

		val androidComponentsExtension = project.extensions.getByType(AndroidComponentsExtension::class.java)

		if (androidComponentsExtension !is ApplicationAndroidComponentsExtension)
			error("TeamCode can only be applied to an Android Application")

		androidComponentsExtension.finalizeDsl { it ->
			it.apply {
				namespace = namespace ?: "org.firstinspires.ftc.teamcode"

				compileSdk = 30

				signingConfigs {
					create("release") {
						val apkStoreFile = System.getenv("APK_SIGNING_STORE_FILE")
						if (apkStoreFile != null) {
							it.keyAlias = System.getenv("APK_SIGNING_KEY_ALIAS")
							it.keyPassword = System.getenv("APK_SIGNING_KEY_PASSWORD")
							it.storeFile = project.file(System.getenv("APK_SIGNING_STORE_FILE"))
							it.storePassword = System.getenv("APK_SIGNING_STORE_PASSWORD")
						} else {
							it.keyAlias = "androiddebugkey"
							it.keyPassword = "android"
							it.storeFile = project.file("libs/ftc.debug.keystore")
							it.storePassword = "android"
						}
					}

					getByName("debug") {
						it.keyAlias = "androiddebugkey"
						it.keyPassword = "android"
						it.storeFile = project.file("libs/ftc.debug.keystore")
						it.storePassword = "android"
					}
				}

				defaultConfig {
					signingConfig = signingConfigs.getByName("debug")
					applicationId = "com.qualcomm.ftcrobotcontroller"
					minSdk = 24
					targetSdk = 28


					// we need to ensure that the sdk config is setup here
					//sdk.applyDefaultsIfNeeded()

					/**
					 * We keep the versionCode and versionName of robot controller applications in sync with
					 * the master information published in the AndroidManifest.xml file of the FtcRobotController
					 * module. This helps avoid confusion that might arise from having multiple versions of
					 * a robot controller app simultaneously installed on a robot controller device.
					 *
					 * We accomplish this with the help of a funky little Groovy script that maintains that
					 * correspondence automatically.
					 *
					 * @see <a href="http://developer.android.com/tools/building/configuring-gradle.html">Configure Your Build</a>
					 * @see <a href="http://developer.android.com/tools/publishing/versioning.html">Versioning Your App</a>
					 */
					val ftcRobotControllerJar = ZipFile(ftcRobotControllerConfiguration.resolvedConfiguration.files.find {
						it.path.contains("com.qualcomm.ftcrobotcontroller${File.separator}FtcRobotController${File.separator}${sdk.version}")
					}!!)
					val readStream = ftcRobotControllerJar.getInputStream(ftcRobotControllerJar.getEntry("AndroidManifest.xml"))
					val bis = BufferedInputStream(readStream)
					val buf = ByteArrayOutputStream()
					var read = bis.read()
					while (read != -1) {
						buf.write(read)
						read = bis.read()
					}
					val manifestText = buf.toString(Charsets.UTF_8)
					//
					val vCodePattern = Pattern.compile("versionCode=\"(\\d+(\\.\\d+)*)\"")
					var matcher = vCodePattern.matcher(manifestText)
					matcher.find()
					val vCode = Integer.parseInt(matcher.group(1))
					//
					val vNamePattern = Pattern.compile("versionName=\"(.*)\"")
					matcher = vNamePattern.matcher(manifestText)
					matcher.find()
					val vName = matcher.group(1)
					//
					versionCode = vCode
					versionName = vName
				}

				buildTypes {
					release {
						signingConfig = signingConfigs.getByName("release")

						ndk {
							abiFilters.add("armeabi-v7a")
							abiFilters.add("arm64-v8a")
						}
					}
					debug {
						isDebuggable = true
						isJniDebuggable = true

						ndk {
							abiFilters.add("armeabi-v7a")
							abiFilters.add("arm64-v8a")
						}
					}
				}

				compileOptions {
					sourceCompatibility = JavaVersion.VERSION_1_8
					targetCompatibility = JavaVersion.VERSION_1_8
				}

				packaging {
					jniLibs.pickFirsts.add("**/*.so")
					jniLibs.useLegacyPackaging = true
				}

				ndkVersion = "21.3.6528147"
			}
		}
	}
}