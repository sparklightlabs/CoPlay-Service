package info.dvkr.screenstream.receiver


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.elvishew.xlog.XLog
import info.dvkr.screenstream.data.other.getLog
import info.dvkr.screenstream.data.settings.SettingsReadOnly
import info.dvkr.screenstream.service.helper.IntentAction
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import android.net.NetworkInfo
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.ComponentName















class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val settingsReadOnly: SettingsReadOnly by inject()

    override fun onReceive(context: Context, intent: Intent) {
        XLog.d(getLog("onReceive", "Invoked"))
        Log.d("COPLAY B-CAST RECEIVE", intent!!.action)
        if (settingsReadOnly.startOnBoot.not()) Runtime.getRuntime().exit(0)

        if (
                intent.action == "android.intent.action.BOOT_COMPLETED" ||
                intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            context.startActivity(context.packageManager.getLaunchIntentForPackage("com.SparklightLabs.CoPlay"))
            //IntentAction.StartOnBoot.sendToAppService(context)
        }
        if (intent.action == "android.net.wifi.STATE_CHANGE") {
            val wifiMgr = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (wifiMgr.isWifiEnabled) { // Wi-Fi adapter is ON
                val wifiInfo = wifiMgr.connectionInfo
                if (wifiInfo.networkId != -1 && wifiInfo.ipAddress != 0) { // Currently connected to a network with an address
                    val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                    val currentlyRunningApp = manager.runningAppProcesses[0].processName
                    if (currentlyRunningApp == null || !currentlyRunningApp.equals("com.SparklightLabs.CoPlay"))
                        context.startActivity(context.packageManager.getLaunchIntentForPackage("com.SparklightLabs.CoPlay"))
                }
            }
        }
    }
}