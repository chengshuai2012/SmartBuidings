package com.aojiexun.smartbuilding.network;

import android.text.TextUtils;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;


/**
 * Created by OFX002 on 2018/7/19.
 */

public class InterceptorUtil {
    public static String TAG="-------";
    private static Request build;

    public static HttpLoggingInterceptor LogInterceptor(){
        return new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                try {
                    String text = URLDecoder.decode(message, "utf-8");
                    Log.e("OKHttp-----", text);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e("OKHttp-----", message);
                }
            }
        }).setLevel(HttpLoggingInterceptor.Level.HEADERS);
    }

    public static Interceptor HeaderInterceptor(){
        return  new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request mRequest=chain.request();
                if("application/force-download".equals(mRequest.header("Content-Type"))){
                    HttpUrl url = mRequest.url();
                    String[] split = url.toString().split("%2F");

                    String newUrl="http://"+split[split.length-2]+"/"+split[split.length-1];
                    Log.e(TAG, "intercept: "+newUrl );
                    HttpUrl newBaseUrl = HttpUrl.parse(newUrl);
                    build = mRequest.newBuilder().url(newBaseUrl).build();
                }else {
                    build = mRequest.newBuilder().build();
                }

                return chain.proceed(build);
            }
        };

    }


}
