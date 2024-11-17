package dev.frozenmilk.easyautolibraries.util

import dev.frozenmilk.easyautolibraries.AbstractEasyAutoLibrary
import dev.frozenmilk.easyautolibraries.util.NullableType.Companion.isAssignableFrom
import dev.frozenmilk.easyautolibraries.util.NullableType.Companion.typeOf
import org.gradle.api.provider.Provider
import org.gradle.internal.metaobject.DynamicInvokeResult

class ConfigurationConverter(private val parent: AbstractEasyAutoLibrary, private val name: String) :
	SubMethodAccess {
	private fun apply(notationProvider: Provider<String>) = parent.project.dependencies.add(name, notationProvider)
	// <name>(notationProvider)
	val args = arrayOf<NullableType<*>>(Provider::class.java.typeOf())
	override fun hasMethod(vararg arguments: Any?): Boolean {
		if (name != "") return false
		val types = arguments.map { typeOf(it) }
		return args.isAssignableFrom(types)
	}

	@Suppress("UNCHECKED_CAST")
	override fun tryInvokeMethod(vararg arguments: Any?): DynamicInvokeResult {
		return DynamicInvokeResult.found(arguments.map { typeOf(it) }.run {
			if (args.isAssignableFrom(this)) apply(arguments[0] as Provider<String>)
			else return DynamicInvokeResult.notFound()
		})
	}
}