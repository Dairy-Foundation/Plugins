# FTC Plugins

This repository contains a series of gradle plugins and related repositories to
make setting up and publishing Dairy libraries easier and gradle plugins
designed to make using FTC libraries (not limited to Dairy) easier.

All plugins can be found on the Dairy maven repo:

[Releases Dashboard](https://repo.dairy.foundation/#/releases/)

[Snapshots Dashboard](https://repo.dairy.foundation/#/snapshots/)

I recommend adding the link to your plugin management repositories, for example:

settings.gradle.kts
```kt
pluginManagement {
	repositories {
		gradlePluginPortal()
		mavenCentral()
		google()
		maven("https://repo.dairy.foundation/releases/")
	}
}
```

If you're developing a plugin that uses one of these or uses one of these as a
regular dependency:

build.gradle.kts
```kt
repositories {
    maven("https://repo.dairy.foundation/releases/")
}
```

[Examples can be found in the Templates repository](https://github.com/Dairy-Foundation/Templates)

## FtcRobotController
This is not a plugin, rather it is a portable version of the FtcRobotController
module from the [official FtcRobotController repository](https://github.com/FIRST-Tech-Challenge/FtcRobotController)

It is recommended to get this via applying the `TeamCode` plugin below.

otherwise:
```kt
dependencies {
    implementation("com.qualcomm.ftcrobotcontroller:FtcRobotController:11.1.0")
}
```

[You can find the latest version here](https://repo.dairy.foundation/#/releases/com/qualcomm/ftcrobotcontroller/FtcRobotController)

## EasyAutoLibraries
A gradle plugin library (not a plugin) that makes it easy to set up library and
dependency dsls. Used for FTCLibraries, which is also a good demonstration of
how to use it.

```kt
dependencies {
    implementation("dev.frozenmilk:EasyAutoLibraries:1.1.2")
}
```

[You can find the latest version here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/EasyAutoLibraries)

## FTCLibraries
A plugin that makes it easy to set up FTC related libraries, uses
`EasyAutoLibraries`.

```kt
plugins {
    id("dev.frozenmilk.ftc-libraries") version "11.1.0-1.1.1"
}
```

[You can find the latest version here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/FTCLibraries)

Demo Usage:

Note that this is not a recommended gradle file to use. Take a look at the
templates for that.

```kt
// the ftc block contains the ftc libraries dsl
ftc {
    // calling the kotlin function will add kotlin to your project
    kotlin()

    // the sdk block contains dependencies related to the sdk
    sdk {
        // just like when adding a dependency normally,
        // we use the configuration

        // this adds RobotCore to implementation
        implementation(RobotCore)
        // we can also specify a version
        implementation(FtcCommon("11.1.0"))

        // the sdk block specifically has a shared version
        version = "11.1.0"
        // once you change it,
        // all un-specified versions for sdk dependencies will have this version
        // note that changing it won't affect previous actions

        // the sdk block also has a TeamCode function
        TeamCode()
        // or:
        TeamCode("11.1.0")
        // these functions are recommended for use in team code modules,
        // as they provide all the dependencies for you, rather than manually
        // specifying it
    }

    // the acmerobotics blocks contains roadrunner and dashboard
    acmerobotics {
        // the acmerobotics block also contains a road runner block:
        roadrunner {
            implementation(core)
            implementation(ftc)
            implementation(actions)
        }
        // and dashboard
        implementation(dashboard)
    }

    // the dairy block contains dairy dependencies
    dairy {
        // this adds the sloth library to the runtime,
        // you'll still need to set up the plugin
        implementation(Sloth)

        // slothboard is also available
        // slothboard is mutually exclusive with dashboard,
        // and gradle will crash with an error telling you
        // why if you have them both
        implementation(slothboard)

        // the sloth versions of panels are also available
        // these versions are mutually exclusive with the non-sloth versions,
        // and gradle will crash with an error telling you
        // why if you have them both
        // see below for all the ftcontrol libraries
        // the sloth version has all the same options
        ftControl {
            implementation(fullpanels)
        }

        // you can also get latest Mercurial 2.0 beta
        implementation(MercurialFTC)
    }

    // the next block contains next ftc dependencies
    next {
        // core libraries
        implementation(ftc)
        implementation(bindings)
        implementation(control)
        implementation(control2)

        // extensions
        implementation(pedro)
        implementation(roadrunner)
        implementation(fateweaver)
    }

    // the pedro block contains pedro pathing dependencies
    pedro {
        implementation(core)
        implementation(ftc)
        implementation(telemetry)
    }

    // the ftcontrol block contains panels dependencies
    ftControl {
        // base library
        implementation(panels)

        // plugins
        implementation(battery)
        implementation(camerastream)
        implementation(capture)
        implementation(configurables)
        implementation(field)
        implementation(gamepad)
        implementation(graph)
        implementation(lights)
        implementation(limelightproxy)
        implementation(opmodecontrol)
        implementation(pinger)
        implementation(telemetry)
        implementation(themes)
        implementation(utils)

        // or fullpanels preset
        implementation(fullpanels)
    }

    // the solvers block contains solverslib dependencies
    solvers {
        implementation(core)
        implementation(pedroPathing)
    }

    // the psiLynx block contains psikit dependencies
    psiLynx {
        // psiLynx also contains a common version,
        // as shown in examples above
        implementation(core)
        implementation(ftc)
    }

    // the fateWeaver block contains fateWeaver dependencies
    fateWeaver {
        // fateWeaver also contains a common version,
        // as shown in examples above
        implementation(core)
        implementation(ftc)
    }
    
    // the marrow block containing the marrow library
    marrow {
        // marrow doesn't have any dependencies other than the core
        implementation(core)
    }
}
```

If you're interested in adding more libraries, or maintain one of these
libraries and want to make a PR, please do.

Further usage can be seen at the Templates repo, which shows how to use the
TeamCode, JVMLibrary and AndroidLibrary plugins.

## TeamCode
A plugin that sets up FTC teamcode projects using FTCLibraries, allowing teams
to easily add other libraries to their project

This automatically sets up the android application config.
See the templates repository for examples.

```kt
plugins {
    id("dev.frozenmilk.teamcode") version "11.1.0-1.1.1"
}
```

[You can find the latest verion here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/FTCProjects)

## Android-Library
A plugin that sets up android FTC library projects using FTCLibraries, allowing
teams to easily add other libraries to their project.

This automatically sets up the android library config.
See the templates repository for examples.

```kt
plugins {
    id("dev.frozenmilk.android-library") version "11.1.0-1.1.1"
}
```

[You can find the latest verion here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/FTCProjects)

## JVM-Library
A plugin that sets up non-android FTC library projects.

This automatically sets up the java library config.
See the templates repository for examples.

```kt
plugins {
    id("dev.frozenmilk.jvm-library") version "11.1.0-1.1.1"
}
```

[You can find the latest verion here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/JVMProjects)

## Publish
Used to set up publishing with a library to the Dairy maven repository.

Integrates with git to prevent you from publishing an unclean working tree and
to auto generate snapshot and release versions from either git tags or commit
hashes.

```kt
plugins {
    id("dev.frozenmilk.publish") version "0.0.5"
}
```

See full examples in the Templates repository.

[You can find the latest verion here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/DairyPublishing)


## Doc
Used to set up dokka for a library.

Makes it easy to add javadoc and html jars to publications.

```kt
plugins {
    id("dev.frozenmilk.doc") version "0.0.5"
    // publication plugin required, try the one above!
}

//
// ...
//

publishing {
	publications {
		register<MavenPublication>("release") {
            // add these lines to the publication
			artifact(dairyDoc.dokkaHtmlJar)
			artifact(dairyDoc.dokkaJavadocJar)
		}
	}
}
```

You need to add the following DokkaV2 migration lines to your gradle.properties:
```
org.jetbrains.dokka.experimental.gradle.pluginMode=V2Enabled
org.jetbrains.dokka.experimental.gradle.pluginMode.noWarn=true
```

See full examples in the Templates repository.

[You can find the latest verion here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/DairyPublishing)

## BuildMetaData

used to generate a metadata object at compile time.

```kt
plugins {
    id("dev.frozenmilk.build-meta-data") version "0.0.2"
    // for example, to embed git meta data:
    id("dev.frozenmilk.publish") version "0.0.5"
}

meta {
    // will generate `com.example.MyLibraryBuildMetaData`
    packagePath = "com.example"
    name = "MyLibrary"

    // each call to register field will add a static field
	registerField("name", "String", "\"com.example.MyLibrary\"")
	registerField("clean", "Boolean") { "${dairyPublishing.clean}" }
	registerField("gitRef", "String") { "\"${dairyPublishing.gitRef}\"" }
	registerField("snapshot", "Boolean") { "${dairyPublishing.snapshot}" }
	registerField("version", "String") { "\"${dairyPublishing.version}\"" }
}

```

the above configuration will generate a file like this:

```kt
package com.example
object MyLibraryBuildMetaData {
	@JvmStatic val `name`: String = "com.example.MyLibrary";
	@JvmStatic val `clean`: Boolean = false;
	@JvmStatic val `gitRef`: String = "ef7510f";
	@JvmStatic val `snapshot`: Boolean = true;
	@JvmStatic val `version`: String = "SNAPSHOT-ef7510f";
}
```

[You can find the latest verion here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/BuildMetaData)
