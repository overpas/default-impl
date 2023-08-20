package by.overpass.defaultimpl

import kotlin.reflect.KClass

/**
 * Generates a constructor function returning the default implementation specified by [impl]
 */
@MustBeDocumented
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class DefaultImpl(
    /**
     * Specifies the default implementation class
     */
    val impl: KClass<*>,
)
