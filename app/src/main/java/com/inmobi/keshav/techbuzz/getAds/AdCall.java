package com.inmobi.keshav.techbuzz.getAds;

import android.util.Log;

import com.inmobi.ads.InMobiAdRequestStatus;
import com.inmobi.ads.InMobiNative;

import java.util.HashMap;

/**
 * Created by keshav.p on 10/14/16.
 */
public class AdCall {
    private HashMap<Integer, InMobiNative> mMobiNativeHashMap = new HashMap<>();

    public void getInmobiNative(final int position, final AdConsumer adConsumer) {
        InMobiNative local = mMobiNativeHashMap.get(position);
        if (null != local) {
            adConsumer.nativeReady(position, local);
        } else {
            InMobiNative inMobiNative = new InMobiNative(1460958725647l, new InMobiNative.NativeAdListener() {
                @Override
                public void onAdLoadSucceeded(InMobiNative inMobiNative) {
                    adConsumer.nativeReady(position, inMobiNative);
                }

                @Override
                public void onAdLoadFailed(InMobiNative inMobiNative, InMobiAdRequestStatus inMobiAdRequestStatus) {

                }

                @Override
                public void onAdDismissed(InMobiNative inMobiNative) {

                }

                @Override
                public void onAdDisplayed(InMobiNative inMobiNative) {

                }

                @Override
                public void onUserLeftApplication(InMobiNative inMobiNative) {

                }
            });
            mMobiNativeHashMap.put(position,inMobiNative);
            inMobiNative.load();
        }
    }

}
