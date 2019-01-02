package com.taranga.crimetracker.helper;

import com.taranga.crimetracker.BuildConfig;

/**
 * Created by Niranjan on 20-Feb-18.
 */

public class ServerConstants {

    private static String MAIN_URL_LIVE_DATA = "https://script.google.com/macros/s/";
    public static final String MAIN_URL_LIVE_ID = "AKfycbygukdW3tt8sCPcFDlkMnMuNu9bH5fpt7bKV50p2bM/exec?id=1UPZCHyemY4YqzksZ0j4aLDvylYLWAnjgyTMGYVN6dOA&sheet=Sheet1";
//    public static final String MAIN_URL_LIVE_ID = "AKfycbygukdW3tt8sCPcFDlkMnMuNu9bH5fpt7bKV50p2bM/exec?id=1h2WfLY8zimADhd1CV7b3za89Z-LR53SOD2yg-RZI2s4&sheet=Sheet1";

    private static String TEST_URL_BANGALORE = "https://script.google.com/macros/s/";
    public static final String TEST_URL_BANGALORE_ID = "AKfycbygukdW3tt8sCPcFDlkMnMuNu9bH5fpt7bKV50p2bM/exec?id=1evhk2Gxqk9PM9zkDN0InCjP0MERPZtYChEnvxbBheew&sheet=Sheet1";

    public static String TEST_URL_TUMKUR = "https://script.google.com/macros/s/";
    public static final String TEST_URL_TUMKUR_ID = "AKfycbygukdW3tt8sCPcFDlkMnMuNu9bH5fpt7bKV50p2bM/exec?id=1evhk2Gxqk9PM9zkDN0InCjP0MERPZtYChEnvxbBheew&sheet=Sheet1";

    public static String getMainUrlToTrackCrimes() {
        if (BuildConfig.DEBUG) {
            return TEST_URL_BANGALORE;
        } else {
            return MAIN_URL_LIVE_DATA;
        }
    }
}
