package com.rkgroup.myapplication

import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.rkgroup.adsmanager.InterstitialAdManager

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnShowFullScreenAd: Button = findViewById(R.id.btnShowFullScreenAd)
        InterstitialAdManager.loadInterstitialAd(this)
        btnShowFullScreenAd.setOnClickListener {
            InterstitialAdManager.showInterstitialAdIfLoaded(this,
                fullScreenContentCallbacks = object : FullScreenContentCallback() {
                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        Log.d(TAG, "onAdFailedToShowFullScreenContent: ${adError.message}")
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "onAdShowedFullScreenContent: ")
                    }

                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "onAdDismissedFullScreenContent: ")
                    }

                    override fun onAdImpression() {
                        Log.d(TAG, "onAdImpression: ")
                    }

                    override fun onAdClicked() {
                        Log.d(TAG, "onAdClicked: ")
                    }
                })
        }


    }

    companion object{
        private const val TAG = "MainActivity"
    }
}