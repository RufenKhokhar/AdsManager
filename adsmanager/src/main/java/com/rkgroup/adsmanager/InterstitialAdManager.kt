package com.rkgroup.adsmanager

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest.ERROR_CODE_NETWORK_ERROR
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


/**
 * @author Rufen Khokhar
 *
 * If you want share only one Interstitial Ad on the entire app,
 * you can use this class the handle the Interstitial AD
 *
 *
 * This is helpful when you are showing the AdLoader Dialog before
 * showing the Interstitial Ad.
 *
 *
 */
object InterstitialAdManager {
    private const val TAG = "SplashAdManager"

    @JvmStatic
    var interstitialAd: InterstitialAd? = null

    private var adLoadWaitingTime: Int = 5

    @JvmStatic
    private var adAlreadyRequesting: Boolean = false

    private lateinit var progressDialog: AlertDialog

    /**
     *
     *  This Method handles the ad loading and ad view callbacks.
     *  Auto reload for feature use, after showing or failed to load
     *
     * @param mContext Activity or fragment context
     * @param adID adUnit ID
     * @param retryOnFailed Time interval in SEC to retry the ad loading if the ad loads failed
     */
    @JvmStatic
    fun loadInterstitialAd(mContext: Context, adID: String, retryOnFailed: Long) {
        if (interstitialAd == null && !adAlreadyRequesting) {
            adAlreadyRequesting = true
            AdsManager.loadInterstitialAd(mContext, adID,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        adAlreadyRequesting = false
                        interstitialAd = ad
                        addFullScreenContentCallback(mContext, adID)
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adAlreadyRequesting = false
                        interstitialAd = null
                        if (adError.code !in listOf(ERROR_CODE_NETWORK_ERROR)) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                loadInterstitialAd(mContext, adID, retryOnFailed)
                            }, retryOnFailed)

                        }
                    }
                })
        }
    }

    /**
     *
     *  This Method handles the ad loading and ad view callbacks.
     *  Auto reload for feature use, after showing or failed to load
     *
     *  This method uses default ad load time 30 SEC
     *
     * @param mContext Activity or fragment context
     * @param adID adUnit ID
     */

    @JvmStatic
    fun loadInterstitialAd(mContext: Context, adID: String) {
        loadInterstitialAd(mContext, adID, 30_000L)
    }

    /**
     *
     *  This Method handles the ad loading and ad view callbacks.
     *  Auto reload for feature use, after showing or failed to load
     *
     *  This method uses default ad load time 30 SEC.
     *  And test AD Id
     *
     * @param mContext Activity or fragment context
     */

    @JvmStatic
    fun loadInterstitialAd(mContext: Context) {
        loadInterstitialAd(mContext, mContext.getString(R.string.interstitial_test_ad_id))
    }

    private fun addFullScreenContentCallback(mContext: Context, adID: String) {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                interstitialAd = null

            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null
            }

            override fun onAdDismissedFullScreenContent() {
                loadInterstitialAd(mContext, adID)
            }
        }
    }

    fun showInterstitialAdIfLoaded(activity: Activity, adLoadWaitingTime: Int = 5) {
        if (!activity.isDestroyed && isInternetConnected(activity)) {
            this.adLoadWaitingTime = adLoadWaitingTime
            showProgress(activity)
            Handler(Looper.getMainLooper()).postDelayed({ showInterstitialAd(activity) }, 1000)
        }
    }

    private fun isInternetConnected(activity: Activity): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    private fun showProgress(activity: Activity) {
        if (!activity.isDestroyed) {
            val dialogView = View.inflate(activity, R.layout.progress_dialog, null)
            progressDialog = AlertDialog.Builder(activity)
                .setView(dialogView)
                .setCancelable(false)
                .show()
            progressDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun hideProgress(activity: Activity) {
        if (::progressDialog.isInitialized && progressDialog.isShowing && !activity.isDestroyed) {
            progressDialog.dismiss()
        }
    }

    private fun showInterstitialAd(activity: Activity) {
        when {
            interstitialAd != null -> {
                hideProgress(activity)
                interstitialAd?.show(activity)
            }
            --adLoadWaitingTime > 1 -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    showInterstitialAd(activity)
                }, 1000)
            }
            else -> {
                adLoadWaitingTime = 5
                hideProgress(activity)
            }
        }
    }


}