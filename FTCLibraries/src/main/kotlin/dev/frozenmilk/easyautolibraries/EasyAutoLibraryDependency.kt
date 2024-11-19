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
	override val parent: AbstractEasyAutoLibrary<*>,
	override val name: String,
	/**
	 * converts input version string to full dependency notation string
	 */
	private val notation: (version: String) -> String,
	/**
	 * repositories that this depends on
	 */
	private val repository: () -> EasyAutoLibraryRepository
) : EasyAutoLibraryMarker<EasyAutoLibraryDependency>(), VersionProvider, SubMethodAccess, SubPropertyAccess {
	override fun onAccess() {
		repository().access()
		parent.project.afterEvaluate {
			configurationNames.forEach {
				dependencies.add(it, notationProvider)
			}
		}
	}

	private var _configurationNames: Set<String>? = null
	/**
	 * the gradle dependencies configuration name, automatically resolves to find the most local one
	 */
	var configurationNames: Set<String>
		get() =
			// trys the configuration name of the parent
			try {
				_configurationNames ?: parent.configurationNames
			}
			// if it crashes, we know there is a parent
			catch (_: IllegalStateException) {
				error("unable to find configuration for $name, try setting the `configurationName` property directly for $name or a parent in $nameTree")
			}

		set(value) { _configurationNames = value }

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

	private val notationProvider: Provider<String> = parent.project.provider { notation(version) }
	private val dynamicNotationProvider = DynamicInvokeResult.found(notationProvider)
	override fun tryGetProperty() = dynamicNotationProvider.also { access() }

	@JvmOverloads
	operator fun invoke(version: String = this.version, configurationNames: Collection<String>) {
		this._configurationNames = configurationNames.toSet()
		this.version = version
		access()
	}
	operator fun invoke(version: String, configurationName: String) {
		this.configurationNames += configurationName
		this.version = version
		access()
	}
	operator fun invoke(version: String) {
		this.version = version
		access()
	}
	operator fun invoke() = access()

	private val funArgs = arrayOf<Array<NullableType<*>>>(
		// <name>()
		arrayOf(),
		// <name>(version)
		arrayOf(String::class.java.typeOf()),
		// <name>(configurationNames)
		arrayOf(Collection::class.java.typeOf()),
		// <name>(version, configurationName)
		arrayOf(String::class.java.typeOf(), String::class.java.typeOf()),
		// <name>(version, configurationNames)
		arrayOf(String::class.java.typeOf(), Collection::class.java.typeOf()),
	)

	override fun hasMethod(vararg arguments: Any?): Boolean {
		if (name != "") return false
		return arguments.map { typeOf(it) }.run {
			funArgs.any { it.isAssignableFrom(this) }
		}
	}

	override fun tryInvokeMethod(vararg arguments: Any?): DynamicInvokeResult {
		return DynamicInvokeResult.found(arguments.map { typeOf(it) }.run {
			@Suppress("UNCHECKED_CAST")
			if (funArgs[0].isAssignableFrom(this)) invoke()
			else if (funArgs[1].isAssignableFrom(this)) invoke(arguments[0] as String)
			else if (funArgs[2].isAssignableFrom(this)) invoke(this@EasyAutoLibraryDependency.version, arguments[0] as Collection<String>)
			else if (funArgs[3].isAssignableFrom(this)) invoke(arguments[0] as String, arguments[1] as String)
			else if (funArgs[4].isAssignableFrom(this)) invoke(arguments[0] as String, arguments[1] as Collection<String>)
			else return DynamicInvokeResult.notFound()
		})
	}
}