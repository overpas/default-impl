package by.overpass.defaultimpl.sample

import by.overpass.defaultimpl.DefaultImpl

@DefaultImpl(MyInterface3Impl::class)
interface MyInterface3

private class MyInterface3Impl : MyInterface3
