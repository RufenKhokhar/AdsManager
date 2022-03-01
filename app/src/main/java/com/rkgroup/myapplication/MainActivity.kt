package com.rkgroup.myapplication

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.rkgroup.adsmanager.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val adContainer:AdContainer = findViewById(R.id.adContainer)
        InterstitialAdManager.showInterstitialAdIfLoaded(this, fullScreenContentCallbacks = object :FullScreenContentCallback(){
            override fun onAdFailedToShowFullScreenContent(
                adError: AdError
            ) {
                super.onAdFailedToShowFullScreenContent(adError)
            }

            override fun onAdShowedFullScreenContent() {
                super.onAdShowedFullScreenContent()
            }

            override fun onAdDismissedFullScreenContent() {
                super.onAdDismissedFullScreenContent()
            }

            override fun onAdImpression() {
                super.onAdImpression()
            }

            override fun onAdClicked() {
                super.onAdClicked()
            }
        })




    }
}