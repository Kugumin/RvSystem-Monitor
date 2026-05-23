package com.rve.systemmonitor.shizuku

import android.content.Context
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.system.exitProcess

class CommandRunnerService(private val context: Context) : ICommandRunner.Stub() {

    override fun executeCommand(command: String): String {
        return try {
            val process = Runtime.getRuntime().exec(arrayOf("sh", "-c", command))
            val output = StringBuilder()

            val reader = BufferedReader(InputStreamReader(process.inputStream))
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                output.append(line).append("\n")
            }

            val errorReader = BufferedReader(InputStreamReader(process.errorStream))
            while (errorReader.readLine().also { line = it } != null) {
                output.append("ERROR: ").append(line).append("\n")
            }

            process.waitFor()
            output.toString().trim()
        } catch (e: Exception) {
            e.printStackTrace()
            "Error executing command: ${e.message}"
        }
    }

    override fun destroy() {
        exitProcess(0)
    }
}
