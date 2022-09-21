package me.smp.core.network

import java.lang.reflect.Method

class NetworkHandlerMeta(private val obj: Any,private val method: Method) {

    operator fun invoke(value: Any){
        method.invoke(obj, value)
    }
}