package com.rkgroup.adsmanager

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback

/**
 * This Class handles the app open ad
 *
 */

class AppOpenAdManager @JvmOverloads constructor(
    private val application: Application,
    private val adUnitID: String = application.getString(R.string.app_open_test_ad_id)
) : ActivityLifecycleCallbacks, LifecycleObserver {
    private var currentActivity: Activity? = null
    private var appOpenAd: AppOpenAd? = null


    /**
     * LifecycleObserver methods
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        showAdIfAvailable()
        Log.d(LOG_TAG, "onStart")
    }

    /**
     * Request an ad
     */
    fun fetchAd() {
        if (isAdAvailable) {
            return
        }
        val loadCallback: AppOpenAdLoadCallback = object : AppOpenAdLoadCallback() {
            override fun onAdLoaded(appOpenAd: AppOpenAd) {
                this@AppOpenAdManager.appOpenAd = appOpenAd
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                super.onAdFailedToLoad(loadAdError)
            }
        }
        val request = adRequest
        AppOpenAd.load(
            application,
            adUnitID,
            request,
            AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
            loadCallback
        )
    }

    /**
     * Creates and returns ad request.
     */
    private val adRequest: AdRequest
        get() = AdRequest.Builder().build()

    /**
     * Utility method that checks if ad exists and can be shown.
     */
    private val isAdAvailable: Boolean
        get() = appOpenAd != null

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        currentActivity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        //   currentActivity = activity
    }

    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {
        currentActivity = null
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }

    fun showAdIfAvailable(appOpenAdListener: AppOpenAdListener? = null) {
        // Only show ad if there is not already an app open ad currently showing
        // and an ad is available.
        if (currentActivity != null && !isShowingAppOpenAd && isAdAvailable) {
            Log.d(LOG_TAG, "Will show ad.")
            val fullScreenContentCallback: FullScreenContentCallback =
                object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        // Set the reference to null so isAdAvailable() returns false.
                        appOpenAd = null
                        isShowingAppOpenAd = false
                        if (appOpenAdListener != null) {
                            appOpenAdListener.onAdCompleted()
                        }
                        fetchAd()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {

                    }

                    override fun onAdShowedFullScreenContent() {
                        isShowingAppOpenAd = true
                    }
                }
            appOpenAd!!.fullScreenContentCallback = fullScreenContentCallback
            if (InterstitialAdManager.isAdAlreadyShowing.not()) {
                appOpenAd!!.show(currentActivity!!)
            }
        } else {
            if (appOpenAdListener != null) {
                appOpenAdListener.onAdCompleted()
            }
            fetchAd()
        }
    }

    fun onDestroy() {
        appOpenAd = null
    }

    companion object {
        /**
         * This variable indicates that app open add is currently showing or not
         * true: App open is showing, false otherwise.
         */
        @JvmStatic
        var isShowingAppOpenAd: Boolean = false
        private const val LOG_TAG = "AppOpenAdManager"
    }

    init {
        application.registerActivityLifecycleCallbacks(this)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }
}