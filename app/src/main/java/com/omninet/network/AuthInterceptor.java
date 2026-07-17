package com.omninet.network;

import android.content.Context;
import com.omninet.core.OmniID;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private Context context;

    public AuthInterceptor(Context context) {
        this.context = context;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer " + OmniID.get())
                .addHeader("User-Agent", "OmniNet-Android/2.0.0")
                .addHeader("Content-Type", "application/json")
                .build();

        return chain.proceed(request);
    }
}
