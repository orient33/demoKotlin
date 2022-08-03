package com.example

import android.util.Log
import com.example.ReflectUtils
import java.lang.Exception
import java.lang.reflect.Field
import java.lang.reflect.Method

object ReflectUtils {
    private const val TAG = "ReflectUtils"

    @JvmStatic
    fun getClass(name: String?): Class<*>? {
        var clazz: Class<*>? = null
        try {
            clazz = Class.forName(name)
        } catch (e: Exception) {
            Log.e(TAG, "getClass Exception: $e")
        }
        return clazz
    }

    @JvmStatic
    fun newInstance(name: String?): Any? {
        var `object`: Any? = null
        try {
            val clazz = getClass(name)
            `object` = clazz!!.newInstance()
        } catch (e: Exception) {
            Log.e(TAG, "newInstance Exception: $e")
        }
        return `object`
    }

    @JvmStatic
    fun getField(clazz: Class<*>, name: String?): Field? {
        var field: Field? = null
        try {
            field = clazz.getField(name)
        } catch (e: Exception) {
            Log.e(TAG, "getField Exception: $e")
        }
        return field
    }

    @JvmStatic
    fun getDeclaredField(clazz: Class<*>, name: String?): Field? {
        var field: Field? = null
        try {
            field = clazz.getDeclaredField(name)
        } catch (e: Exception) {
            Log.e(TAG, "getDeclaredField Exception: $e")
        }
        return field
    }

    @JvmStatic
    fun getStaticMethod(clazz: Class<*>, name: String?, vararg parameterTypes: Class<*>?): Method? {
        var method: Method? = null
        try {
            method = clazz.getDeclaredMethod(name, *parameterTypes)
        } catch (e: Exception) {
            Log.e(TAG, "getMethod Exception: $e")
        }
        return method
    }

    @JvmStatic
    fun getMethod(clazz: Class<*>, name: String?, vararg parameterTypes: Class<*>?): Method? {
        var method: Method? = null
        try {
            method = clazz.getMethod(name, *parameterTypes)
        } catch (e: Exception) {
            Log.e(TAG, "getMethod Exception: $e")
        }
        return method
    }

    @JvmStatic
    operator fun <T> invoke(method: Method, instance: Any?, vararg args: Any?): T? {
        var result: T? = null
        try {
            method.isAccessible = true
            result = method.invoke(instance, *args) as T
        } catch (e: Exception) {
            Log.e(TAG, "invoke Exception: ", e)
        }
        return result
    }

    @JvmStatic
    fun <T> getFieldValue(clazz: Class<*>, name: String?, instance: Any?): T? {
        var value: T? = null
        try {
            val field = getField(clazz, name)
            field!!.isAccessible = true
            value = field[instance] as T
        } catch (e: Exception) {
            Log.e(TAG, "getFieldValue Exception: $e")
        }
        return value
    }

    @JvmStatic
    fun <T> setFieldValue(clazz: Class<*>, name: String?, instance: Any?, newValue: T) {
        try {
            val field = getDeclaredField(clazz, name)
            field!!.isAccessible = true
            field[instance] = newValue
        } catch (e: Exception) {
            Log.e(TAG, "setFieldValue Exception: $e")
        }
    }
}