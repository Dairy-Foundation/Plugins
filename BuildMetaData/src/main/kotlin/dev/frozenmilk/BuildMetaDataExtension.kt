package dev.frozenmilk

import org.gradle.declarative.dsl.model.annotations.Adding

open class BuildMetaDataExtension {
    private var _packagePath: String? = null
    /**
     * dot separated package path. e.g. "dev.frozenmilk"
     */
    var packagePath: String
        get() = run {
            check(_packagePath != null) { "meta.packagePath not set" }
            _packagePath!!
        }
        set(value) {
            _packagePath = value
        }

    private var _name: String? = null

    /**
     * project name
     */
    var name: String
        get() = run {
            check(_name != null) { "meta.name not set" }
            _name!!
        }
        set(value) {
            _name = value
        }

    private val fields = LinkedHashMap<String, Pair<String, () -> String>>()

    /**
     * registers a build generated metadata field
     *
     * outputs `@JvmStatic val `$name`: $type = $value;`
     */
    @Adding
    fun registerField(name: String, type: String, value: () -> String) {
        require(!fields.contains(name)) { "already registered build metadata field for name $name" }
        fields[name] = type to value
    }
    /**
     * registers a build generated metadata field
     *
     * outputs `@JvmStatic val `$name`: $type = $value;`
     */
    @Adding
    fun registerField(name: String, type: String, value: String) = registerField(name, type) { value }

    internal fun write(out: Appendable) {
        out.appendLine("package $packagePath")
        out.appendLine("object ${name}BuildMetaData {")
        fields.entries.forEach { (name, tv) ->
            val (type, value) = tv
            out.appendLine("\t@JvmStatic val `$name`: $type = ${value()};")
        }
        out.appendLine("}")
    }
}