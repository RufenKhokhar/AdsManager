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
    fun onNativeAdLoaded(nativeAd: NativeAd)
    fun onNativeAdLoadedFail(errorCode: LoadAdError)
}