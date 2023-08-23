package by.overpass.defaultimpl

import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspIncremental
import com.tschuchort.compiletesting.symbolProcessorProviders
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import org.junit.Test

class SymbolProcessorTest {

    @Test
    fun `interface with internal class implementation and secondary constructor = OK`() {
        val result = SourceFile.kotlin(
            "MyInterface.kt",
            """
                package by.overpass.defaultimpl.test               

                import by.overpass.defaultimpl.DefaultImpl

                @DefaultImpl(MyInterfaceImpl::class)
                interface MyInterface {

                    val string: String

                    val double: Double
                }

                internal class MyInterfaceImpl(
                    override val string: String,
                    override val double: Double,
                ) : MyInterface {

                    constructor(int: Int) : this("", 0.0)
                }
            """.trimIndent(),
        ).compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `interface with internal object implementation = OK`() {
        val result = SourceFile.kotlin(
            "MyInterface2.kt",
            """
                package by.overpass.defaultimpl.test

                import by.overpass.defaultimpl.DefaultImpl

                @DefaultImpl(MyInterface2Impl::class)
                interface MyInterface2

                internal object MyInterface2Impl : MyInterface2
            """.trimIndent(),
        ).compile()

        assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
    }

    @Test
    fun `interface with private class implementation = COMPILATION_ERROR`() {
        val result = SourceFile.kotlin(
            "MyInterface3.kt",
            """
                package by.overpass.defaultimpl.test

                import by.overpass.defaultimpl.DefaultImpl

                @DefaultImpl(MyInterface3Impl::class)
                interface MyInterface3

                private class MyInterface3Impl : MyInterface3
            """.trimIndent(),
        ).compile()

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue {
            result.messages.contains(
                "Default implementation class can't be private: by.overpass.defaultimpl.test.MyInterface3Impl",
            )
        }
    }

    @Test
    fun `interface with non-descendant implementation = COMPILATION_ERROR`() {
        val result = SourceFile.kotlin(
            "MyInterface4.kt",
            """
                package by.overpass.defaultimpl.test

                import by.overpass.defaultimpl.DefaultImpl

                @DefaultImpl(MyInterface4Impl::class)
                interface MyInterface4

                internal object MyInterface4Impl
            """.trimIndent(),
        ).compile()

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue {
            result.messages.contains(
                "by.overpass.defaultimpl.test.MyInterface4Impl is not a subtype of " +
                        "by.overpass.defaultimpl.test.MyInterface4",
            )
        }
    }

    @Test
    fun `interface with private object implementation = COMPILATION_ERROR`() {
        val result = SourceFile.kotlin(
            "MyInterface5.kt",
            """
                package by.overpass.defaultimpl.test

                import by.overpass.defaultimpl.DefaultImpl

                @DefaultImpl(MyInterface5Impl::class)
                interface MyInterface5

                private object MyInterface5Impl : MyInterface5
            """.trimIndent(),
        ).compile()

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue {
            result.messages.contains(
                "Default implementation class can't be private: by.overpass.defaultimpl.test.MyInterface5Impl",
            )
        }
    }

    @Test
    fun `interface with enum class implementation = COMPILATION_ERROR`() {
        val result = SourceFile.kotlin(
            "MyInterface6.kt",
            """
                package by.overpass.defaultimpl.test

                import by.overpass.defaultimpl.DefaultImpl

                @DefaultImpl(MyInterface6Impl::class)
                interface MyInterface6

                enum class MyInterface6Impl : MyInterface6 {
                    A,
                    B,
                }
            """.trimIndent(),
        ).compile()

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue {
            result.messages.contains(
                "Only plain classes and object are allowed as default implementations: " +
                        "by.overpass.defaultimpl.test.MyInterface6Impl",
            )
        }
    }

    @Test
    fun `interface with interface implementation = COMPILATION_ERROR`() {
        val result = SourceFile.kotlin(
            "MyInterface7.kt",
            """
                package by.overpass.defaultimpl.test

                import by.overpass.defaultimpl.DefaultImpl

                @DefaultImpl(MyInterface7Impl::class)
                interface MyInterface7

                interface MyInterface7Impl : MyInterface7
            """.trimIndent(),
        ).compile()

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue {
            result.messages.contains(
                "Only plain classes and object are allowed as default implementations: " +
                        "by.overpass.defaultimpl.test.MyInterface7Impl",
            )
        }
    }

    @Test
    fun `interface with non-existent implementation = COMPILATION_ERROR`() {
        val result = SourceFile.kotlin(
            "MyInterface8.kt",
            """
                package by.overpass.defaultimpl.test

                import by.overpass.defaultimpl.DefaultImpl

                @DefaultImpl(MyInterface8Impl::class)
                interface MyInterface8
            """.trimIndent(),
        ).compile()

        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        assertTrue {
            result.messages.contains(
                "Couldn't find the default implementation class for by.overpass.defaultimpl.test.MyInterface8",
            )
        }
    }

    private fun List<SourceFile>.compile(): KotlinCompilation.Result =
        KotlinCompilation().apply {
            sources = this@compile
            symbolProcessorProviders = listOf(SymbolProcessor.Provider())
            kspIncremental = false
            inheritClassPath = true
            verbose = false
        }.compile()

    private fun SourceFile.compile(): KotlinCompilation.Result =
        listOf(this).compile()
}
