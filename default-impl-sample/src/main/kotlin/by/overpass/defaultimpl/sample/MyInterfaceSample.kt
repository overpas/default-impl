package by.overpass.defaultimpl.sample

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
