package com.rkgroup.adsmanager

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

private const val TAG = "AdsManager"

object AdsManager {
    /**
     *
     * Populate your NativeAdView with NativeAd
     *
     * @param nativeAd: Native that currently loaded
     * @param adView: Where you want to populate
     */
    @JvmStatic
    fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
        // Set the media view. Media content will be automatically populated in the media view once
        // Set other ad assets
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        val mediaView: MediaView? = adView.findViewById(R.id.ad_media)
        adView.mediaView = mediaView
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // The headline is guaranteed to be in every NativeAd.
        if (nativeAd.headline == null && adView.headlineView != null) {
            adView.headlineView!!.visibility = View.INVISIBLE
        } else if (adView.headlineView != null) {
            adView.headlineView!!.visibility = View.VISIBLE
            (adView.headlineView as TextView).text = nativeAd.body
            adView.headlineView!!.isSelected = true
        }

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.body == null && adView.bodyView != null) {
            adView.bodyView!!.visibility = View.INVISIBLE
        } else if (adView.bodyView != null) {
            adView.bodyView!!.visibility = View.VISIBLE
            (adView.bodyView as TextView).text = nativeAd.body
            adView.bodyView!!.isSelected = true
        }
        if (nativeAd.callToAction == null && adView.callToActionView != null) {
            adView.callToActionView!!.visibility = View.INVISIBLE
        } else if (adView.callToActionView != null) {
            adView.callToActionView!!.visibility = View.VISIBLE
            (adView.callToActionView as Button).text = nativeAd.callToAction
        }
        if (nativeAd.icon == null && adView.iconView != null) {
            if (nativeAd.images.isNotEmpty()) {
                adView.iconView!!.visibility = View.VISIBLE
                (adView.iconView as ImageView).setImageDrawable(nativeAd.images[0]!!.drawable)
            } else {
                adView.iconView!!.visibility = View.GONE
            }
        } else if (adView.iconView != null) {
            adView.iconView!!.visibility = View.VISIBLE
            (adView.iconView as ImageView).setImageDrawable(nativeAd.icon!!.drawable)
        }
        if (nativeAd.price == null && adView.priceView != null) {
            adView.priceView!!.visibility = View.INVISIBLE
        } else if (adView.priceView != null) {
            adView.priceView!!.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null && adView.storeView != null) {
            adView.storeView!!.visibility = View.INVISIBLE
        } else if (adView.storeView != null) {
            adView.storeView!!.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null && adView.starRatingView != null) {
            adView.starRatingView!!.visibility = View.INVISIBLE
        } else if (adView.starRatingView != null) {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView!!.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null && adView.advertiserView != null) {
            adView.advertiserView!!.visibility = View.INVISIBLE
        } else if (adView.advertiserView != null) {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView!!.visibility = View.VISIBLE
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd)
    }

    /**
     *
     *
     * @param adContainer: Ad Container where do you want to show AD
     * @param adUnit: AD Unit ID
     * @param adSize: Target ad AD size
     *@param adContainerVisibility: set adContainer Visibility on ad load Failed
     * @see View.VISIBLE
     * @see View.GONE
     * @see View.INVISIBLE
     * @return AdView that currently loading
     *
     */
    @JvmStatic
    fun loadBannerAd(
        adContainer: ViewGroup,
        adUnit: String,
        adSize: AdSize,
        adContainerVisibility: Int = View.GONE
    ): AdView {
        val mAdView = AdView(adContainer.context)
        mAdView.adSize = adSize
        mAdView.adUnitId = adUnit
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                adContainer.removeAllViews()
                adContainer.addView(mAdView)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                adContainer.visibility = adContainerVisibility
            }

        }
        mAdView.loadAd(AdRequest.Builder().build())
        return mAdView
    }

    /**
     *
     *
     * @param adContainer: Ad Container where do you want to show AD
     * @param adSize: Target ad AD size
     *
     * This method loads Test Ad
     *
     * @return AdView that currently loading
     *
     */
    @JvmStatic
    fun loadBannerAd(
        adContainer: ViewGroup,
        adSize: AdSize,
        adContainerVisibility: Int = View.GONE
    ): AdView {
        return loadBannerAd(
            adContainer,
            adContainer.context.getString(R.string.banner_test_ad_id),
            adSize,
            adContainerVisibility
        )
    }

    /**
     *
     *
     *
     * @param activity: Host Activity
     * @param adContainer: Ad Container where do you want to show AD
     * @param adUnit: AD UNit ID
     *
     * This method loads Test Ad
     *
     * @return AdView that currently loading
     *
     */
    @JvmStatic
    fun loadAdaptiveBannerAd(
        activity: Activity,
        adContainer: ViewGroup,
        adUnit: String,
        adContainerVisibility: Int = View.GONE
    ): AdView {
        return loadBannerAd(
            adContainer,
            adUnit,
            getAdaptiveBannerAdSize(activity),
            adContainerVisibility
        )
    }

    /**
     *
     *
     *
     * @param fragment: Host Fragment
     * @param adContainer: Ad Container where do you want to show AD
     * @param adUnit: AD UNit ID
     *
     * This method loads Test Ad
     *
     * @return AdView that currently loading
     *
     */
    @JvmStatic
    fun loadAdaptiveBannerAd(
        fragment: Fragment,
        adContainer: ViewGroup,
        adUnit: String,
        adContainerVisibility: Int = View.GONE
    ): AdView {
        return loadBannerAd(
            adContainer,
            adUnit,
            getAdaptiveBannerAdSize(fragment.requireActivity()), adContainerVisibility
        )

    }

    /**
     *
     *
     *
     * @param activity: Host Activity
     * @param adContainer: Ad Container where do you want to show AD
     *
     * This method loads Test Ad
     *
     * @return AdView that currently loading
     *
     */
    @JvmStatic
    fun loadAdaptiveBannerAd(
        activity: Activity,
        adContainer: ViewGroup,
        adContainerVisibility: Int = View.GONE
    ): AdView {
        return loadBannerAd(adContainer, getAdaptiveBannerAdSize(activity), adContainerVisibility)
    }

    /**
     *
     *
     *
     * @param fragment: Host Activity
     * @param adContainer: Ad Container where do you want to show AD
     *
     * This method loads Test Ad
     *
     * @return AdView that currently loading
     *
     */
    @JvmStatic
    fun loadAdaptiveBannerAd(
        fragment: Fragment,
        adContainer: ViewGroup,
        adContainerVisibility: Int = View.GONE
    ): AdView {
        return loadBannerAd(
            adContainer,
            getAdaptiveBannerAdSize(fragment.requireActivity()),
            adContainerVisibility
        )
    }

    /**
     * @param mContext:The Context object for your activity or application
     * @param adUnit: AD unit id
     * @param nativeAdOptions: set your own native ad options
     * @see NativeAdOptions
     * @param nativeAdLoadListener:  Ad Load callbacks
     *
     */

    @JvmStatic
    fun loadNativeAd(
        mContext: Context,
        adUnit: String,
        nativeAdOptions: NativeAdOptions,
        nativeAdLoadListener: NativeAdLoadListener
    ) {
        val adLoader = AdLoader.Builder(mContext, adUnit).forNativeAd { nativeAd: NativeAd ->
            Log.d(TAG, "getAdLoader: nativeAd is  loaded")
            nativeAdLoadListener.onNativeAdLoaded(nativeAd)
        }.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: LoadAdError) {
                Log.d(TAG, "onAdFailedToLoad: unifiedNativeAd is not loaded")
                nativeAdLoadListener.onNativeAdLoadedFail(errorCode)
            }
        }).withNativeAdOptions(nativeAdOptions).build()

        adLoader.loadAd(AdRequest.Builder().build())
    }

    /**
     * @param mContext:The Context object for your activity or application
     *@param adUnit: AD unit id
     * @param nativeAdLoadListener:  Ad Load callbacks
     *
     */

    @JvmStatic
    fun loadNativeAd(
        mContext: Context,
        adUnit: String,
        nativeAdLoadListener: NativeAdLoadListener
    ) {
        val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
        val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions)
            .setRequestMultipleImages(true)
            .build()
        loadNativeAd(mContext, adUnit, adOptions, nativeAdLoadListener)
    }

    /**
     *@param adContainer: Where do you want to show AD
     *@param adUnit: AD unit id
     *@param layoutRes: native Ad view
     *@param adContainerVisibility: set adContainer Visibility on ad load Failed
     * @see View.VISIBLE
     * @see View.GONE
     * @see View.INVISIBLE
     *
     */
    @JvmStatic
    fun loadNativeAd(
        adContainer: ViewGroup,
        adUnit: String,
        @LayoutRes layoutRes: Int,
        adContainerVisibility: Int = View.GONE
    ) {
        loadNativeAd(adContainer.context, adUnit, object : NativeAdLoadListener {
            override fun onNativeAdLoaded(nativeAd: NativeAd) {
                adContainer.visibility = View.VISIBLE
                val nativeAdView: NativeAdView =
                    View.inflate(adContainer.context, layoutRes, null) as NativeAdView
                adContainer.removeAllViews()
                adContainer.addView(nativeAdView)
                populateNativeAdView(nativeAd, nativeAdView)
            }

            override fun onNativeAdLoadedFail(errorCode: LoadAdError) {
                adContainer.visibility = adContainerVisibility
            }
        })
    }

    /**
     *@param adContainer: Where do you want to show AD
     *@param layoutRes: native Ad view
     *@param adContainerVisibility: set adContainer Visibility on ad load Failed
     * @see View.VISIBLE
     * @see View.GONE
     * @see View.INVISIBLE
     * This loads Test ads for you
     *
     */

    @JvmStatic
    fun loadNativeAd(
        adContainer: ViewGroup,
        @LayoutRes layoutRes: Int,
        adContainerVisibility: Int = View.GONE
    ) {
        loadNativeAd(adContainer.context, object : NativeAdLoadListener {
            override fun onNativeAdLoaded(nativeAd: NativeAd) {
                adContainer.visibility = View.VISIBLE
                val nativeAdView: NativeAdView =
                    View.inflate(adContainer.context, layoutRes, null) as NativeAdView
                adContainer.removeAllViews()
                adContainer.addView(nativeAdView)
                populateNativeAdView(nativeAd, nativeAdView)
            }

            override fun onNativeAdLoadedFail(errorCode: LoadAdError) {
                adContainer.visibility = adContainerVisibility
            }
        })
    }

    /**
     * This method will load test ads
     *
     *@param mContext: The Context object for your activity or application
     *@param nativeAdLoadListener:  Ad Load callbacks
     *
     */
    @JvmStatic
    fun loadNativeAd(mContext: Context, nativeAdLoadListener: NativeAdLoadListener) {
        loadNativeAd(mContext, mContext.getString(R.string.native_test_ad_id), nativeAdLoadListener)
    }

    /**
     * @param mContext:The Context object for your activity or application
     *@param adUnit: AD unit id
     * @param interstitialAdLoadCallback: Load callbacks
     *
     */
    @JvmStatic
    fun loadInterstitialAd(
        mContext: Context,
        adUnit: String,
        interstitialAdLoadCallback: InterstitialAdLoadCallback
    ) {
        InterstitialAd.load(
            mContext,
            adUnit,
            AdRequest.Builder().build(),
            interstitialAdLoadCallback
        )
    }

    /**
     * This method will load test ads
     *
     * @param mContext:The Context object for your activity or application
     * @param interstitialAdLoadCallback: Load callbacks
     *
     */
    @JvmStatic
    fun loadInterstitialAd(
        mContext: Context,
        interstitialAdLoadCallback: InterstitialAdLoadCallback
    ) {
        loadInterstitialAd(
            mContext,
            mContext.getString(R.string.interstitial_test_ad_id),
            interstitialAdLoadCallback
        )
    }


    private fun getAdaptiveBannerAdSize(activity: Activity): AdSize {
        // Determine the screen width (less decorations) to use for the ad width.
        val display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()
        // Get adaptive ad size and return for setting on the ad view.
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }


}