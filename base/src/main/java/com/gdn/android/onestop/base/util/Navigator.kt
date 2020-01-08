package com.gdn.android.onestop.base.util

import android.content.Intent
import androidx.fragment.app.Fragment

class Navigator {


    enum class Destination(val packagePath : String){
        LOGIN_ACTIVITY("$PACKAGE_NAME.login.LoginActivity"),
        MAIN_ACTIVITY("$PACKAGE_NAME.app.MainActivity"),
        PROFILE_DIALOG_FRAGMENT("$PACKAGE_NAME.profile.fragment.ProfileDialogFragment")
    }

    enum class Argument(val key: String) {
        PROFILE_USERNAME("username")
    }

    companion object {

        const val PACKAGE_NAME = "com.gdn.android.onestop"

        fun getIntent(destination: Destination): Intent {
            return Intent(Intent.ACTION_VIEW).setClassName(PACKAGE_NAME, destination.packagePath)
        }

        fun getFragment(destination: Destination): Fragment {
            return Class.forName(Destination.PROFILE_DIALOG_FRAGMENT.packagePath).newInstance() as Fragment
        }
    }
}