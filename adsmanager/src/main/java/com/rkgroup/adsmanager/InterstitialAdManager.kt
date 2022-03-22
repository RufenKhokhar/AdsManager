package com.rkgroup.adsmanager

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.os.Handler
import android.os.Looper
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

    private var waitForAdLoading: Int = 5

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
     */
    @JvmStatic
    fun loadInterstitialAd(mContext: Context, adID: String, retryOnFailed: Boolean = false) {
        if (interstitialAd == null && !adAlreadyRequesting) {
            adAlreadyRequesting = true
            AdsManager.loadInterstitialAd(mContext, adID,
                object : InterstitialAdLoadCallback() {
                    override fun onAdLoaded(ad: InterstitialAd) {
                        adAlreadyRequesting = false
                        interstitialAd = ad
                        addDefaultFullScreenContentCallback()
                    }

                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        adAlreadyRequesting = false
                        interstitialAd = null
                        if (adError.code !in listOf(ERROR_CODE_NETWORK_ERROR) && retryOnFailed) {
                            Handler(Looper.getMainLooper()).postDelayed({
                                loadInterstitialAd(
                                    mContext,
                                    adID,
                                    retryOnFailed
                                )
                            }, 3000L)
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
        loadInterstitialAd(mContext, adID, retryOnFailed = false)
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

    /**
     * It adds a callback to the interstitial ad.
     *
     */
    private fun addDefaultFullScreenContentCallback() {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                interstitialAd = null

            }

            override fun onAdShowedFullScreenContent() {
                interstitialAd = null
            }

        }
    }


    /* A helper method to show the interstitial ad if it is loaded. */
    @JvmStatic
    fun showInterstitialAdIfLoaded(activity: Activity) {
        showInterstitialAdIfLoaded(activity, 5)
    }

    /**
     *  If you pass fullScreenContentCallbacks to null, it will use the default callbacks
     *   and loads the ad again on dismiss.
     *
     *   if you use your own callbacks, You need to load the AD Manually
     *
     *
     * @param activity: Host Activity
     *
     *@param adLoadWaitingTime: Maximum Time in seconds,by this parameter progressbar will be
     * show, and wait for ad load( and check after each second ) as soon as ad will be loaded, and automatically shows.
     * if ad not loads the given time, progress automatically dismiss. Default time 5 SEC.
     */
    @JvmStatic
    fun showInterstitialAdIfLoaded(
        activity: Activity,
        adLoadWaitingTime: Int = 5,
        fullScreenContentCallbacks: FullScreenContentCallback? = null
    ) {
        if (!activity.isDestroyed && isInternetConnected(activity)) {
            if (interstitialAd != null) {
                if (fullScreenContentCallbacks != null) {
                    addCustomFullScreenContentCallbacks(fullScreenContentCallbacks)
                }
                interstitialAd?.show(activity)
            } else {
                this.waitForAdLoading = adLoadWaitingTime
                showProgress(activity)
                Handler(Looper.getMainLooper()).postDelayed({
                    showInterstitialAd(activity, fullScreenContentCallbacks)
                }, 1000)
            }
        } else {
            fullScreenContentCallbacks?.onAdFailedToShowFullScreenContent(
                AdError(
                    410,
                    "Internet is not connected!",
                    ""
                )
            )
        }
    }

    /**
     *  @param activity: Host Activity
     *
     *  Must call this method on fragment or Activity's OnDestroy() method
     *
     *  it will make sure, progress dialog must be dismiss before activity or fragment is destroying
     *
     *
     */
    @JvmStatic
    fun onDestroy(activity: Activity) {
        hideProgress(activity)
    }

    /**
     * Returns true if the device is connected to the internet, false otherwise
     *
     * @param activity The activity that is requesting the network connection.
     *
     */
    private fun isInternetConnected(activity: Activity): Boolean {
        val cm = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }

    /**
     * It creates a dialog box with a progress bar and a message
     *
     * @param activity The activity that is calling the method.
     */
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

    /**
     * **Hide the progress dialog if it is showing.**
     *
     * @param activity The activity that is calling the method.
     */
    private fun hideProgress(activity: Activity) {
        if (::progressDialog.isInitialized && progressDialog.isShowing && !activity.isDestroyed) {
            progressDialog.dismiss()
        }
    }

    /**
     * If the interstitial ad is loaded, show it. Otherwise, wait for it to load
     *
     * @param activity The activity that will show the ad.
     * @param fullScreenContentCallbacks FullScreenContentCallback? = null
     */
    private fun showInterstitialAd(
        activity: Activity,
        fullScreenContentCallbacks: FullScreenContentCallback? = null
    ) {
        when {
            interstitialAd != null -> {
                hideProgress(activity)
                if (fullScreenContentCallbacks != null) {
                    addCustomFullScreenContentCallbacks(fullScreenContentCallbacks)
                }
                interstitialAd?.show(activity)
            }
            --waitForAdLoading > 1 -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    showInterstitialAd(activity, fullScreenContentCallbacks)
                }, 1000)
            }
            else -> {
                waitForAdLoading = 5
                hideProgress(activity)
                fullScreenContentCallbacks?.onAdFailedToShowFullScreenContent(
                    AdError(404, "Ad not loaded!", "")
                )
            }
        }
    }

    /**
     * It adds a callback to the interstitial ad object.
     *
     * @param fullScreenContentCallbacks The object that implements the FullScreenContentCallbacks
     * interface.
     */
    private fun addCustomFullScreenContentCallbacks(fullScreenContentCallbacks: FullScreenContentCallback) {
        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                interstitialAd = null
                fullScreenContentCallbacks.onAdFailedToShowFullScreenContent(adError)

            }

            override fun onAdShowedFullScreenContent() {
                fullScreenContentCallbacks.onAdShowedFullScreenContent()
            }

            override fun onAdDismissedFullScreenContent() {
                interstitialAd = null
                fullScreenContentCallbacks.onAdDismissedFullScreenContent()
            }

            override fun onAdImpression() {
                fullScreenContentCallbacks.onAdImpression()
            }

            override fun onAdClicked() {
                fullScreenContentCallbacks.onAdClicked()
            }
        }
    }


}