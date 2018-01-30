package com.example.dsekar.moviesstageone;

import com.squareup.okhttp.OkHttpClient;

public class OkHttpClientHelper {
    private static OkHttpClient okHttpClient;

    public static synchronized OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            okHttpClient = new OkHttpClient();
        }
        return okHttpClient;
    }
}

