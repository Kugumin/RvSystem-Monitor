package com.rve.systemmonitor.shizuku

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import rikka.shizuku.Shizuku

@Singleton
class ShizukuManager @Inject constructor(@param:ApplicationContext private val context: Context) {
    private val TAG = "ShizukuManager"

    private val _isShizukuAvailable = MutableStateFlow(false)
    val isShizukuAvailable: StateFlow<Boolean> = _isShizukuAvailable.asStateFlow()

    private val commandMutex = Mutex()
    @Volatile private var commandRunner: ICommandRunner? = null
    @Volatile private var isConnecting = false
    private var userServiceConnection: ServiceConnection? = null

    private val _hasPermission = MutableStateFlow(false)
    val hasPermission: StateFlow<Boolean> = _hasPermission.asStateFlow()

    private val binderReceivedListener = Shizuku.OnBinderReceivedListener {
        _isShizukuAvailable.value = true
        checkPermission()
        if (_hasPermission.value) connectUserService()
    }

    private val binderDeadListener = Shizuku.OnBinderDeadListener {
        _isShizukuAvailable.value = false
        _hasPermission.value = false
    }

    private val requestPermissionResultListener = Shizuku.OnRequestPermissionResultListener { requestCode, grantResult ->
        if (requestCode == REQUEST_CODE_PERMISSION) {
            _hasPermission.value = grantResult == PackageManager.PERMISSION_GRANTED
            if (_hasPermission.value) connectUserService()
        }
    }

    fun init() {
        try {
            Shizuku.addBinderReceivedListenerSticky(binderReceivedListener)
            Shizuku.addBinderDeadListener(binderDeadListener)
            Shizuku.addRequestPermissionResultListener(requestPermissionResultListener)

            _isShizukuAvailable.value = Shizuku.pingBinder()
            if (_isShizukuAvailable.value) {
                checkPermission()
                if (_hasPermission.value) connectUserService()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Shizuku init error: ${e.message}")
            _isShizukuAvailable.value = false
        }
    }

    fun refreshState() {
        try {
            val alive = Shizuku.pingBinder()
            _isShizukuAvailable.value = alive
            if (alive) {
                checkPermission()
                if (_hasPermission.value && commandRunner == null && !isConnecting) {
                    connectUserService()
                }
            }
        } catch (e: Exception) {
            _isShizukuAvailable.value = false
        }
    }

    fun destroy() {
        disconnectUserService()
        Shizuku.removeBinderReceivedListener(binderReceivedListener)
        Shizuku.removeBinderDeadListener(binderDeadListener)
        Shizuku.removeRequestPermissionResultListener(requestPermissionResultListener)
    }

    fun checkPermission() {
        if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) {
            _hasPermission.value = false
            return
        }
        _hasPermission.value = Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission() {
        if (Shizuku.isPreV11() || Shizuku.getVersion() < 11) return
        if (Shizuku.checkSelfPermission() != PackageManager.PERMISSION_GRANTED) {
            Shizuku.requestPermission(REQUEST_CODE_PERMISSION)
        }
    }

    suspend fun executeCommand(command: String): String {
        if (!_isShizukuAvailable.value || !_hasPermission.value) {
            return ""
        }
        return commandMutex.withLock {
            val runner = commandRunner
            if (runner == null) {
                connectUserService()
                return@withLock ""
            }
            try {
                withContext(Dispatchers.IO) {
                    runner.executeCommand(command)
                }
            } catch (e: Exception) {
                Log.e(TAG, "executeCommand Error: ${e.message}")
                ""
            }
        }
    }

    private fun connectUserService() {
        if (commandRunner != null || isConnecting) return
        isConnecting = true
        val serviceName = CommandRunnerService::class.java.name
        val args = Shizuku.UserServiceArgs(
            ComponentName(context.packageName, serviceName),
        ).daemon(false).processNameSuffix("runner")

        val connection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                commandRunner = ICommandRunner.Stub.asInterface(service)
                isConnecting = false
            }
            override fun onServiceDisconnected(name: ComponentName?) {
                commandRunner = null
                isConnecting = false
                if (_isShizukuAvailable.value && _hasPermission.value) {
                    connectUserService()
                }
            }
        }
        userServiceConnection = connection
        try {
            Shizuku.bindUserService(args, connection)
        } catch (e: Exception) {
            Log.e(TAG, "Shizuku bind error: ${e.message}")
            isConnecting = false
        }
    }

    private fun disconnectUserService() {
        val conn = userServiceConnection ?: return
        val args = Shizuku.UserServiceArgs(
            ComponentName(context.packageName, CommandRunnerService::class.java.name),
        ).daemon(false).processNameSuffix("runner")
        try {
            Shizuku.unbindUserService(args, conn, false)
        } catch (e: Exception) {
            Log.e(TAG, "Shizuku unbind error: ${e.message}")
        }
        userServiceConnection = null
        commandRunner = null
        isConnecting = false
    }

    companion object {
        const val REQUEST_CODE_PERMISSION = 1001
    }
}
