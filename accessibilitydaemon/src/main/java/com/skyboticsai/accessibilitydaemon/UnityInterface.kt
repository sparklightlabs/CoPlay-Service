package com.skyboticsai.accessibilitydaemon

import android.content.Context
import android.content.Intent
import info.dvkr.screenstream.ui.activity.AppActivity


// I need a service message handler
// I need intent Actions.
// The thing that would make the most sense would be to just rewrite the main app, but
// That seems skeevy.
class DaemonInterface {

    companion object {
        fun getStartIntent(context: Context): Intent =
                Intent(context.applicationContext, AppActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    fun start(imageSize: Int, imageQuality: Int) {

    }

    fun stop() {

    }

    fun getAppState() {

    }

}