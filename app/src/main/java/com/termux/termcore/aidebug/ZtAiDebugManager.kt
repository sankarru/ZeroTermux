package com.termux.termcore.aidebug

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.termux.app.TermuxService
import com.termux.termcore.ftp.utils.UserSetManage

object ZtAiDebugManager {

    const val PORT = 19998

    @Volatile
    var termuxService: TermuxService? = null
        private set

    @Volatile
    var cameraHelper: com.termux.termcore.workstation.ZtWorkstationCameraHelper? = null
        private set

    fun setTermuxService(service: TermuxService?) {
        termuxService = service
    }

    fun setCameraHelper(helper: com.termux.termcore.workstation.ZtWorkstationCameraHelper?) {
        cameraHelper = helper
    }

    fun start(context: Context) {
        val intent = Intent(context, ZtAiDebugService::class.java)
        ContextCompat.startForegroundService(context.applicationContext, intent)
    }

    fun stop(context: Context) {
        context.applicationContext.stopService(Intent(context, ZtAiDebugService::class.java))
    }

    @JvmStatic
    fun ensureRunningIfEnabled(context: Context) {
        if (UserSetManage.get().getZTUserBean().isZtAiDebugEnabled) {
            start(context)
        }
    }

    @JvmStatic
    fun ensureRunningForActiveSession(context: Context) {
        val bean = UserSetManage.get().getZTUserBean()
        if (bean.isZtAiDebugEnabled && !isRunning()) {
            start(context)
        }
    }

    fun isRunning(): Boolean = ZtAiDebugService.isRunning
}
