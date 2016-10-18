package com.inmobi.keshav.techbuzz.getAds;

import com.inmobi.ads.InMobiNative;

/**
 * Created by keshav.p on 10/14/16.
 */
public interface AdConsumer {

    void nativeReady(int position, InMobiNative inMobiNative);
}
