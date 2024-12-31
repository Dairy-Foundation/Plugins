package dev.frozenmilk.doc

import org.gradle.api.reflect.HasPublicType
import org.gradle.api.reflect.TypeOf
import org.gradle.api.tasks.TaskProvider
import org.gradle.jvm.tasks.Jar

abstract class DairyDocExtensionImpl constructor(
    override val dokkaHtmlJar: TaskProvider<Jar>,
    override val dokkaJavadocJar: TaskProvider<Jar>,
) : DairyDocExtension, HasPublicType {
    override fun getPublicType(): TypeOf<DairyDocExtension> = TypeOf.typeOf(DairyDocExtension::class.java)
}