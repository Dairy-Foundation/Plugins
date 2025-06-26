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
    implementation("com.qualcomm.ftcrobotcontroller:FtcRobotController:10.3.0")
}
```

[You can find the latest version here](https://repo.dairy.foundation/#/releases/com/qualcomm/ftcrobotcontroller/FtcRobotController)

## EasyAutoLibraries
A gradle plugin library (not a plugin) that makes it easy to set up library and
dependency dsls. Used for FTCLibraries, which is also a good demonstration of
how to use it.

```kt
dependencies {
    implementation("dev.frozenmilk:EasyAutoLibraries:0.0.0")
}
```

[You can find the latest version here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/EasyAutoLibraries)

## FTCLibraries
A plugin that makes it easy to set up FTC related libraries, uses
`EasyAutoLibraries`.

```kt
plugins {
    id("dev.frozenmilk.ftc-libraries") version "10.3.0-0.1.3"
}
```

[You can find the latest version here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/FTCLibraries)

Usage:

```kt
// adds this `ftc` block
ftc {
    sdk {
        // the sdk block allows you to add dependencies from the FTC SDK
        RobotServer // adds it at the sdk version
        FtcCommon("10.1.0") // specifies a different version
        Blocks("10.1.1", "compileOnly") // varargs specifies different configurations
        OnBotJava {
            version = "10.1.0"
            configurationNames = setOf("compileOnly")
        }
        RobotCore {
            configurationNames += "api"
        }
        // you can specify the default version for the whole sdk:
        version = "10.3.0" // default for this plugin version
        // and configurationNames
        configurationNames = setOf("implementation")
    }
    // note: the sdk is special, if you access the SDK at all,
    //  it will automatically add all of the SDK dependencies, but will make
    //  `Blocks` runtimeOnly. This configuration is good for TeamCode
    //  repositories

    kotlin // adds kotlin to the project
}
```

At the moment, this plugin only provides the sdk and kotlin support, but you can
PR this repo to add other libraries, we're happy for anything.

Further usage can be seen at the Templates repo, which shows how to use the
TeamCode, JVMLibrary and AndroidLibrary plugins.

## TeamCode
A plugin that sets up FTC teamcode projects using FTCLibraries, allowing teams
to easily add other libraries to their project

This automatically sets up the android application config, adds the whole sdk,
and the FtcRobotController module from above. See the templates repository for
examples.

```kt
plugins {
    id("dev.frozenmilk.teamcode") version "10.3.0-0.1.3"
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
    id("dev.frozenmilk.android-library") version "10.3.0-0.1.3"
}
```

[You can find the latest verion here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/FTCProjects)

## JVM-Library
A plugin that sets up non-android FTC library projects.

This automatically sets up the java library config.
See the templates repository for examples.

```kt
plugins {
    id("dev.frozenmilk.jvm-library") version "10.3.0-0.1.3"
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
    id("dev.frozenmilk.publish") version "0.0.4"
}
```

See full examples in the Templates repository.

[You can find the latest verion here](https://repo.dairy.foundation/#/releases/dev/frozenmilk/DairyPublishing)


## Doc
Used to set up dokka for a library.

Makes it easy to add javadoc and html jars to publications.

```kt
plugins {
    id("dev.frozenmilk.doc") version "0.0.4"
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
