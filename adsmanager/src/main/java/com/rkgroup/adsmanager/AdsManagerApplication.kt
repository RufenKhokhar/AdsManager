package com.rkgroup.adsmanager

import android.app.Application

abstract class AdsManagerApplication : Application() {

    var appOpenAdManager: AppOpenAdManager? = null

    /**
     * @param appOpenAdListener: AppOpenAdListener calls each time when this function is called
     *
     * This method useful if you want to show app open ad after Splash Screen
     *
     * Otherwise app open ad show each time when the onStart() function is called from Activity
     *
     *
     */
    fun showAppOpenAddIfLoaded(appOpenAdListener: AppOpenAdListener) {
        appOpenAdManager?.showAdIfAvailable(appOpenAdListener)
    }
}