package dev.frozenmilk.easyautolibraries

import dev.frozenmilk.easyautolibraries.util.NullableType
import dev.frozenmilk.easyautolibraries.util.NullableType.Companion.isAssignableFrom
import dev.frozenmilk.easyautolibraries.util.NullableType.Companion.typeOf
import dev.frozenmilk.easyautolibraries.util.SubMethodAccess
import dev.frozenmilk.easyautolibraries.util.SubPropertyAccess
import dev.frozenmilk.easyautolibraries.util.VersionProvider
import org.gradle.api.artifacts.Configuration
import org.gradle.api.provider.Provider
import org.gradle.internal.metaobject.DynamicInvokeResult

class EasyAutoLibraryDependency(
	override val parent: AbstractEasyAutoLibrary,
	override val name: String,
	/**
	 * converts input version string to full dependency notation string
	 */
	private val notation: (version: String) -> String,
	/**
	 * repositories that this depends on
	 */
	private val repository: EasyAutoLibraryRepository
) : EasyAutoLibraryMarker(), VersionProvider, SubMethodAccess, SubPropertyAccess {
	private var _configurationName: String? = null
	override fun onAccess() = repository.access()

	/**
	 * the gradle dependencies configuration name, automatically resolves to find the most local one
	 */
	var configurationName: String
		get() =
			// trys the configuration name of the parent
			try {
				_configurationName ?: parent.configurationName
			}
			// if it crashes, we know there is a parent
			catch (_: IllegalStateException) {
				error("unable to find configuration for $name, try setting the `configurationName` property directly for $name or a parent in $nameTree")
			}

		set(value) { _configurationName = value }

	private var _version: String? = null

	/**
	 * version of this library, if [parent] is a [VersionProvider] then it will fall back to it
	 */
	override var version: String
		get() = try {
			_version ?: parent.let { if (it is VersionProvider) it.version else null }
		}
		catch (_: IllegalStateException) {
			error("version was not set for $name")
		} ?: error("version was not set for $name, try setting the `version` property directly for $name or a parent in $nameTree")

		set(value) { _version = value }

	private val notationProperty: Provider<String> = parent.project.provider { notation(version) }
	private val dynamicNotationProperty = DynamicInvokeResult.found(notationProperty)
	override fun tryGetProperty() = dynamicNotationProperty.also { access() }

	fun apply(configurationName: String = this.configurationName) = parent.project.dependencies.add(configurationName, notationProperty).also { access() }
	fun apply(configuration: Configuration, version: String) = apply(configuration.name, version)
	fun apply(configurationName: String = this.configurationName, version: String) = parent.project.dependencies.add(configurationName, notation(version)).also { access() }

	private val funArgs = arrayOf<Array<NullableType<*>>>(
		// <name>()
		arrayOf(),
		// <name>(version)
		arrayOf(String::class.java.typeOf()),
		// <name>(configurationName, version)
		arrayOf(String::class.java.typeOf(), String::class.java.typeOf()),
		// <name>(configuration, version)
		arrayOf(Configuration::class.java.typeOf(), String::class.java.typeOf()),
	)

	override fun hasMethod(vararg arguments: Any?): Boolean {
		if (name != "") return false
		return arguments.map { typeOf(it) }.run {
			funArgs.any { it.isAssignableFrom(this) }
		}
	}

	override fun tryInvokeMethod(vararg arguments: Any?): DynamicInvokeResult {
		return DynamicInvokeResult.found(arguments.map { typeOf(it) }.run {
			if (funArgs[0].isAssignableFrom(this)) apply()
			else if (funArgs[1].isAssignableFrom(this)) apply(version = arguments[0] as String)
			else if (funArgs[2].isAssignableFrom(this)) apply(arguments[0] as String, arguments[1] as String)
			else if (funArgs[3].isAssignableFrom(this)) apply(arguments[0] as Configuration, arguments[1] as String)
			else return DynamicInvokeResult.notFound()
		})
	}
}