package com.example.dagger

import dagger.Component
import dagger.Module
import dagger.Provides
import javax.inject.Inject

class A {
    @Inject
    lateinit var b: B
    fun doInit() {
        DaggerC.create().injectA222(this)
    }

    fun dump() {
        println("A.dump. p=${b.p}")
        println("A.dump. b=$b, ${b.bMem}")
    }
}

class B @Inject constructor(
    val p: Param
) {
    val bMem = "bMem"
}

class Param {//@Inject constructor() {
    val p = 1
}

@Component(modules = [M::class])
interface C {
    fun injectA222(a: A)

}
@Module
class M{
    @Provides
    fun provideParam() = Param()
}