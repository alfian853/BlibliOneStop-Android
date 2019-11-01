package com.gdn.android.onestop.base.util

import android.content.Intent

class Navigator {


    enum class Destination(val packagePath : String){
        LOGIN("$PACKAGE_NAME.login.LoginActivity"),
        MAIN_ACTIVITY("$PACKAGE_NAME.app.MainActivity")
    }

    companion object {

        const val PACKAGE_NAME = "com.gdn.android.onestop"

        fun getIntent(destination: Destination): Intent {
            return Intent(Intent.ACTION_VIEW).setClassName(PACKAGE_NAME, destination.packagePath)
        }

    }
}