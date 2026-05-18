package com.rve.systemmonitor.utils

import android.app.ActivityManager
import android.content.Context
import android.opengl.EGL14
import android.opengl.EGLConfig
import android.opengl.GLES20
import android.util.Log

object GpuUtils {
    private const val TAG = "GpuUtils"

    init {
        NativeLoader.load()
    }

    @JvmStatic
    private external fun getVulkanVersionNative(): String

    @JvmStatic
    private external fun getGpuTemperatureNative(): Double

    fun getGpuTemperature(): Double = runCatching {
        getGpuTemperatureNative()
    }.getOrElse {
        Log.e(TAG, "getGpuTemperature error: ${it.message}", it)
        0.0
    }

    private var cachedGpuDetails: Pair<String, String>? = null
    private var cachedGlesVersion: String? = null
    private var cachedDetailedGlesVersion: String? = null
    private var cachedVulkanVersion: String? = null

    fun getGpuDetails(): Pair<String, String> {
        cachedGpuDetails?.let { return it }
        return runCatching {
            val display = EGL14.eglGetDisplay(EGL14.EGL_DEFAULT_DISPLAY)
            val version = IntArray(2)
            EGL14.eglInitialize(display, version, 0, version, 1)

            val configAttribs = intArrayOf(
                EGL14.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL14.EGL_NONE,
            )
            val configs = arrayOfNulls<EGLConfig>(1)
            val numConfigs = IntArray(1)
            EGL14.eglChooseConfig(display, configAttribs, 0, configs, 0, 1, numConfigs, 0)

            val contextAttribs = intArrayOf(
                EGL14.EGL_CONTEXT_CLIENT_VERSION, 2,
                EGL14.EGL_NONE,
            )
            val context = EGL14.eglCreateContext(display, configs[0], EGL14.EGL_NO_CONTEXT, contextAttribs, 0)

            val surfaceAttribs = intArrayOf(
                EGL14.EGL_WIDTH, 1,
                EGL14.EGL_HEIGHT, 1,
                EGL14.EGL_NONE,
            )
            val surface = EGL14.eglCreatePbufferSurface(display, configs[0], surfaceAttribs, 0)

            EGL14.eglMakeCurrent(display, surface, surface, context)
            val renderer = GLES20.glGetString(GLES20.GL_RENDERER) ?: "Unknown"
            val vendor = GLES20.glGetString(GLES20.GL_VENDOR) ?: "Unknown"
            val fullVersion = GLES20.glGetString(GLES20.GL_VERSION)
            if (fullVersion != null) {
                cachedDetailedGlesVersion = fullVersion.removePrefix("OpenGL ES ").trim()
            }

            EGL14.eglMakeCurrent(display, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_SURFACE, EGL14.EGL_NO_CONTEXT)
            EGL14.eglDestroySurface(display, surface)
            EGL14.eglDestroyContext(display, context)
            EGL14.eglTerminate(display)

            val result = Pair(renderer, vendor)
            cachedGpuDetails = result
            result
        }.getOrElse {
            Log.e(TAG, "getGpuDetails error: ${it.message}", it)
            Pair("Unknown", "Unknown")
        }
    }

    fun getDetailedGlesVersion(): String {
        cachedDetailedGlesVersion?.let { return it }
        getGpuDetails()
        return cachedDetailedGlesVersion ?: "Unknown"
    }

    fun getGlesVersion(context: Context): String {
        cachedGlesVersion?.let { return it }
        return runCatching {
            val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            val configurationInfo = activityManager.deviceConfigurationInfo
            val result = configurationInfo.glEsVersion
            cachedGlesVersion = result
            result
        }.getOrElse {
            Log.e(TAG, "getGlesVersion error: ${it.message}", it)
            "Unknown"
        }
    }

    fun getVulkanVersion(context: Context): String {
        cachedVulkanVersion?.let { return it }
        return runCatching {
            val result = getVulkanVersionNative()
            cachedVulkanVersion = result
            result
        }.getOrElse {
            Log.e(TAG, "getVulkanVersion error: ${it.message}", it)
            "Unknown"
        }
    }
}
