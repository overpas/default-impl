package by.overpass.defaultimpl.sample

import by.overpass.defaultimpl.DefaultImpl

@DefaultImpl(MyInterface5Impl::class)
interface MyInterface5

private object MyInterface5Impl : MyInterface5
