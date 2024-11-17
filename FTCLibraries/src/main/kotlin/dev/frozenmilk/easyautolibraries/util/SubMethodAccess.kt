package dev.frozenmilk.easyautolibraries.util

import org.gradle.internal.metaobject.DynamicInvokeResult

interface SubMethodAccess {
	/**
	 * Returns true when this object is known to have a method with the given name that accepts the given arguments.
	 *
	 * <p>Note that not every method is known. Some methods may require an attempt invoke it in order for them to be discovered.</p>
	 */
	fun hasMethod(vararg arguments: Any?): Boolean

	/**
	 * Invokes the method with the given name and arguments.
	 */
	fun tryInvokeMethod(vararg arguments: Any?): DynamicInvokeResult
}