package by.overpass.defaultimpl.sample

import by.overpass.defaultimpl.DefaultImpl

@DefaultImpl(MyInterface2Impl::class)
interface MyInterface2

internal object MyInterface2Impl : MyInterface2
