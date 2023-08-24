package by.overpass.defaultimpl.sample

import kotlin.test.Test
import kotlin.test.assertTrue

class MyInterfaceDefaultImplTest {

    @Test
    fun testMyInterfaceDefaultImplementationConstructorGenerated() {
        val actual = MyInterface("", 0.0)

        assertTrue { actual is MyInterfaceImpl }
    }

    @Test
    fun testMyInterfaceDefaultImplementationSecondaryConstructorGenerated() {
        val actual = MyInterface(0)

        assertTrue { actual is MyInterfaceImpl }
    }

    @Test
    fun testMyInterface2DefaultImplementationConstructorGenerated() {
        val actual = MyInterface2()

        assertTrue { actual is MyInterface2Impl }
    }
}
