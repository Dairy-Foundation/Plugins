package dev.frozenmilk

import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.declarative.dsl.model.annotations.Adding

abstract class BuildMetaDataExtension {
    abstract val packagePathProperty: Property<String>
    /**
     * dot separated package path. e.g. "dev.frozenmilk"
     */
    var packagePath: String
        get() = run {
            check(packagePathProperty.isPresent && packagePathProperty.get() != null) { "meta.packagePath not set" }
            packagePathProperty.get()
        }
        set(value) {
            packagePathProperty.value(value)
        }

    abstract val nameProperty: Property<String>

    /**
     * project name
     */
    var name: String
        get() = run {
            check(nameProperty.isPresent && nameProperty.get() != null) { "meta.name not set" }
            nameProperty.get()
        }
        set(value) {
            nameProperty.value(value)
        }

    abstract val fieldsProperty: MapProperty<String, Pair<String, () -> String>>
    init {
        fieldsProperty.empty()
    }

    /**
     * registers a build generated metadata field
     *
     * outputs `@JvmStatic val `$name`: $type = $value;`
     */
    @Adding
    fun registerField(name: String, type: String, value: () -> String) {
        require(fieldsProperty.isPresent && !fieldsProperty.get().contains(name)) { "already registered build metadata field for name $name" }
        fieldsProperty.put(name, type to value)
    }
    /**
     * registers a build generated metadata field
     *
     * outputs `@JvmStatic val `$name`: $type = $value;`
     */
    @Adding
    fun registerField(name: String, type: String, value: String) = registerField(name, type) { value }
}