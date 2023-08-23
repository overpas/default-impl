package by.overpass.defaultimpl.sample

import kotlin.test.assertTrue
import org.junit.Test

class MyInterfaceDefaultImplTest {

    @Test
    fun `MyInterface default implementation constructor generated`() {
        val actual = MyInterface("", 0.0)

        assertTrue { actual is MyInterfaceImpl }
    }

    @Test
    fun `MyInterface default implementation secondary constructor generated`() {
        val actual = MyInterface(0)

        assertTrue { actual is MyInterfaceImpl }
    }

    @Test
    fun `MyInterface2 default implementation constructor generated`() {
        val actual = MyInterface2()

        assertTrue { actual is MyInterface2Impl }
    }
}
