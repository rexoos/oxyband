package com.rexus.bandselector.data.root

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Singleton service for handling Root interactions via libsu.
 */
object RootService {
    
    // Configure libsu
    init {
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_MOUNT_MASTER)
                .setTimeout(10)
        )
    }

    suspend fun isRootAvailable(): Boolean = withContext(Dispatchers.IO) {
        Shell.getShell().isRoot
    }

    suspend fun run(command: String): Shell.Result = withContext(Dispatchers.IO) {
        Shell.cmd(command).exec()
    }
    
    suspend fun writeToFile(path: String, content: String): Boolean = withContext(Dispatchers.IO) {
        val result = Shell.cmd("echo '$content' > $path").exec()
        result.isSuccess
    }

    suspend fun readFile(path: String): String = withContext(Dispatchers.IO) {
        val result = Shell.cmd("cat $path").exec()
        if (result.isSuccess) result.out.joinToString("\n") else ""
    }
}
