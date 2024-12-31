package dev.frozenmilk.doc

import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

interface DairyDocExtension {
    val dokkaJavadocJar: TaskProvider<Jar>
    val dokkaHtmlJar: TaskProvider<Jar>
}