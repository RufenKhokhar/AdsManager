package com.rkgroup.adsmanager

import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd

/**
 * @author Rufen Khokhar
 *
 *  This Interface handles the
 * Native Ad Load Callbacks
 */
interface NativeAdLoadListener {
    /**
     * This function is called when the native ad is loaded
     *
     * @param nativeAd The native ad object.
     */
    fun onNativeAdLoaded(nativeAd: NativeAd)
    /**
     * A function that is called when the native ad fails to load.
     *
     * @param errorCode The error code that identifies the reason for the failure.
     */
    fun onNativeAdLoadedFail(errorCode: LoadAdError)
}