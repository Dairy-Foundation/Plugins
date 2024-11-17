package dev.frozenmilk.easyautolibraries.util

import org.gradle.internal.metaobject.DynamicInvokeResult

interface SubPropertyAccess {
	fun tryGetProperty(): DynamicInvokeResult
}