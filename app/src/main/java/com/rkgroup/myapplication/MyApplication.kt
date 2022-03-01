package com.rkgroup.myapplication

import android.app.Application
import com.rkgroup.adsmanager.AppOpenAdListener
import com.rkgroup.adsmanager.AppOpenAdManager

class MyApplication : Application() {

    private lateinit var appOpenAdManager: AppOpenAdManager
    override fun onCreate() {
        super.onCreate()
        /**
         * @param application: Application Context
         * @param adUnitID: ADUnitID for ad load
         */
        appOpenAdManager = AppOpenAdManager(this)
    }

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
        appOpenAdManager.showAdIfAvailable(appOpenAdListener)
    }
}