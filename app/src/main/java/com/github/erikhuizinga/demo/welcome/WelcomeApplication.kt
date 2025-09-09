package com.github.erikhuizinga.demo.welcome

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log

class WelcomeApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(activityWelcomingCallbacks)
        Log.d(TAG, "WelcomeApplication created and lifecycle callbacks registered")
    }

    private val activityWelcomingCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (activity is WelcomeActivity) return

            val previousIntent = activity.intent
            Log.d(TAG, "Activity PreCreated: ${activity.localClassName}, Intent: $previousIntent")
            if (previousIntent.getBooleanExtra(WelcomeActivity.FROM_WELCOME, false)) return

            val nextIntent = Intent(previousIntent)
            val welcomeIntent = Intent(activity, WelcomeActivity::class.java)
                .putExtra(WelcomeActivity.NEXT_INTENT, nextIntent)
            activity.startActivity(welcomeIntent)
            activity.finish()
        }

        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            Log.d(TAG, "Activity Created: ${activity.localClassName}")
        }

        override fun onActivityStarted(activity: Activity) {
            Log.d(TAG, "Activity Started: ${activity.localClassName}")
        }

        override fun onActivityResumed(activity: Activity) {
            Log.d(TAG, "Activity Resumed: ${activity.localClassName}")
        }

        override fun onActivityPaused(activity: Activity) {
            Log.d(TAG, "Activity Paused: ${activity.localClassName}")
        }

        override fun onActivityStopped(activity: Activity) {
            Log.d(TAG, "Activity Stopped: ${activity.localClassName}")
        }

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {
            Log.d(TAG, "Activity SaveInstanceState: ${activity.localClassName}")
        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.d(TAG, "Activity Destroyed: ${activity.localClassName}")
        }
    }

    companion object Companion {
        private const val TAG = "WelcomeApplication"
    }
}