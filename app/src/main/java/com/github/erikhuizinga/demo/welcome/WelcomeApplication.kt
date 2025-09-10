package com.github.erikhuizinga.demo.welcome

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import java.lang.ref.WeakReference

class WelcomeApplication : Application() {
    private class WelcomingState(
        private val activityReference: WeakReference<Activity>,
        val intent: Intent,
    ) {
        val activity get() = activityReference.get()
    }

    @Volatile
    private var welcomingState: WelcomingState? = null

    private val activityWelcomingCallbacks = object : ActivityLifecycleCallbacks {
        override fun onActivityPreCreated(activity: Activity, savedInstanceState: Bundle?) {
            if (shouldWelcome(activity, savedInstanceState)) {
                welcome(activity)
            }
        }

        private fun shouldWelcome(activity: Activity, savedInstanceState: Bundle?) =
            savedInstanceState == null /* not a recreation */ &&
                    activity !is WelcomeActivity &&
                    !activity.intent.getBooleanExtra(WAS_WELCOMED, false) &&
                    welcomingState?.activity !== activity

        private fun welcome(activity: Activity) {
            Log.d(TAG, "Welcoming ${activity.localClassName}")
            welcomingState = WelcomingState(
                activityReference = WeakReference(activity),
                intent = activity.intent.sanitizeForWelcome()
            )
            val welcomeIntent = Intent(activity, WelcomeActivity::class.java)
            activity.startActivity(welcomeIntent, null)
            activity.finish()
        }

        private fun Intent.sanitizeForWelcome() = Intent(this).apply {
            // Clear launcher action and category, if present
            if (action == Intent.ACTION_MAIN) {
                action = null
            }
            removeCategory(Intent.CATEGORY_LAUNCHER)

            // Remove flags that can cause task reshuffling when replayed
            val strip = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED or
                    Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
            flags = flags and strip.inv()

            putExtra(WAS_WELCOMED, true)
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
            if (welcomingState?.activity === activity &&
                activity.intent.getBooleanExtra(WAS_WELCOMED, false)
            ) {
                welcomingState = null
            }
            Log.d(TAG, "Destroyed: ${activity.localClassName}")
        }
    }

    override fun onCreate() {
        super.onCreate()
        registerActivityLifecycleCallbacks(activityWelcomingCallbacks)
        Log.d(TAG, "WelcomeApplication created and lifecycle callbacks registered")
    }

    fun startWelcomedActivityFrom(activity: Activity) {
        welcomingState?.intent?.also {
            activity.startActivity(it)
            welcomingState = null
        }
    }

    fun finishWelcomedActivity() {
        welcomingState?.activity?.apply {
            if (!isFinishing) finish()
        }
        welcomingState = null
    }

    companion object {
        private const val TAG = "WelcomeApplication"
        private const val WAS_WELCOMED = "was_welcomed"
    }
}
