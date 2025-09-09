package com.github.erikhuizinga.demo.welcome

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import java.lang.ref.WeakReference

class WelcomeApplication : Application() {

    private var welcomedActivityReference: WeakReference<Activity>? = null

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(activityWelcomingCallbacks)
        Log.d(TAG, "WelcomeApplication created and lifecycle callbacks registered")
    }

    private val activityWelcomingCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
            // Skip welcoming the welcome activity itself
            if (activity is WelcomeActivity) return

            val originalIntent = activity.intent

            // Skip if already welcomed
            if (originalIntent.getBooleanExtra(FROM_WELCOME, false)) return

            // Avoid double-welcoming the same instance
            if (welcomedActivityReference?.get() === activity) {
                Log.d(TAG, "Already welcoming (layer) ${activity.localClassName}, skipping")
                return
            }

            welcomedActivityReference = WeakReference(activity)
            Log.d(TAG, "Welcoming (layer) ${activity.localClassName}")
            val welcomeIntent = Intent(activity, WelcomeActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(welcomeIntent)
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Log.d(TAG, "Created: ${activity.localClassName}")
        }

        override fun onActivityStarted(activity: Activity) {
            Log.d(TAG, "Started: ${activity.localClassName}")
        }

        override fun onActivityResumed(activity: Activity) {
            Log.d(TAG, "Resumed: ${activity.localClassName}")
        }

        override fun onActivityPaused(activity: Activity) {
            Log.d(TAG, "Paused: ${activity.localClassName}")
        }

        override fun onActivityStopped(activity: Activity) {
            Log.d(TAG, "Stopped: ${activity.localClassName}")
        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
            Log.d(TAG, "SaveInstanceState: ${activity.localClassName}")
        }

        override fun onActivityDestroyed(activity: Activity) {
            if (welcomedActivityReference?.get() === activity) welcomedActivityReference = null
            Log.d(TAG, "Destroyed: ${activity.localClassName}")
        }
    }

    fun proceedPastWelcome() {
        val activity = welcomedActivityReference?.get() ?: return
        activity.startActivity(activity.intent.putExtra(FROM_WELCOME, true))
        welcomedActivityReference = null
    }

    fun finshWelcomed() {
        welcomedActivityReference?.get()?.apply {
            if (!isFinishing) finish()
        }
        welcomedActivityReference = null
    }

    companion object {
        private const val TAG = "WelcomeApplication"
        private const val FROM_WELCOME = "from_welcome"
    }
}
