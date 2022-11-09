package com.example;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.NetAPI.*;

public class TestNet {

    /**
     * 通用参数Flag
     */
    // 上传参数带上客户端环境信息：device, rom, apkVersion
    public static final int REQUEST_FLAG_ENV = 1;
    // 上传参数带上账户信息： user cookie
    public static final int REQUEST_FLAG_AUTH = 1 << 1;
    // 上传参数是否需要加密，签名
    public static final int REQUEST_FLAG_PARAMETER_ENCRYPTION = 1 << 2;
    // 返回结果是否需要解密
    public static final int REQUEST_FLAG_RESULT_DECRYPTION = 1 << 3;
    // 上传参数是否带上广告相关信息(广告参数包括环境参数和imei)
    public static final int REQUEST_FLAG_ADD_AD_INFO = 1 << 4;
    // 无REQUEST FLAG。多用于第三方
    public static final int REQUEST_FLAG_NONE = 0;


    // 请求FLAG选择
    public static final String HEADER_REQUEST_FLAG = "request_flag";
    public static final String HEADER_REQUEST_FLAG_USER_DEFAULT = HEADER_REQUEST_FLAG + ":"
            + (REQUEST_FLAG_ENV | REQUEST_FLAG_AUTH | REQUEST_FLAG_PARAMETER_ENCRYPTION | REQUEST_FLAG_RESULT_DECRYPTION); // 用户通用接口FLAG
    public static final String HEADER_REQUEST_FLAG_USER_AD = HEADER_REQUEST_FLAG + ":"
            + (REQUEST_FLAG_ENV | REQUEST_FLAG_ADD_AD_INFO | REQUEST_FLAG_AUTH | REQUEST_FLAG_PARAMETER_ENCRYPTION | REQUEST_FLAG_RESULT_DECRYPTION); // 用户通用接口FLAG
    public static final String HEADER_REQUEST_FLAG_AD = HEADER_REQUEST_FLAG + ":"
            + (REQUEST_FLAG_ENV | REQUEST_FLAG_ADD_AD_INFO); // 广告类型接口FLAG
    public static final String HEADER_REQUEST_FLAG_ENV = HEADER_REQUEST_FLAG + ":" + REQUEST_FLAG_ENV; // 普通接口FLAG

    public static final String HEADER_REQUEST_ANALYTICS_FLAG = "request_analytics_flag";
    public static final String HEADER_REQUEST_ANALYTICS_FLAG_WITH_SEPARATE = HEADER_REQUEST_ANALYTICS_FLAG + ":";


    public static String avoidNullString() {
        return "Undefined";
    }

    private String getPayload() {
        Map<String, Object> datas = new HashMap<String, Object>();
        datas.put(EVENT_PARAM_THEME_ID, avoidNullString());
        datas.put(EVENT_PARAM_THEME_NAME, avoidNullString());
        datas.put(EVENT_PARAM_WALLPAPER_ID, avoidNullString());
        datas.put(EVENT_PARAM_WALLPAPER_NAME, avoidNullString());
        datas.put(EVENT_PARAM_RINGTONE_ID, avoidNullString());
        datas.put(EVENT_PARAM_RINGTONE_NAME, avoidNullString());
        datas.put(EVENT_PARAM_FONT_ID, avoidNullString());
        datas.put(EVENT_PARAM_FONT_NAME, avoidNullString());
        datas.put(EVENT_PARAM_MIWALLPAPER_ID, avoidNullString());
        datas.put(EVENT_PARAM_MIWALLPAPER_NAME, avoidNullString());
        datas.put(EVENT_PARAM_VIDEO_WALLPAPER_ID, avoidNullString());
        datas.put(EVENT_PARAM_VIDEO_WALLPAPER_NAME, avoidNullString());
        datas.put(EVENT_PARAM_AOD_ID, avoidNullString());
        datas.put(EVENT_PARAM_AOD_NAME, avoidNullString());
        datas.put(EVENT_PARAM_ICON_ID, avoidNullString());
        datas.put(EVENT_PARAM_ICON_NAME, avoidNullString());

//        AnalyticsHelper.insertABTestResult(datas);
        JSONObject jsonObject = new JSONObject();
        for (String key : datas.keySet()) {
            try {
                jsonObject.put(key, datas.get(key));
            } catch (JSONException e) {
            }
        }
        return jsonObject.toString();
    }

    public void testNet(NetResult callback) {
        Retrofit r = new Retrofit.Builder()
                .baseUrl("https://thm.market.xiaomi.com")
                .client(createClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Call<Result> call = r.create(NetAPI.class).uploadDaily("dailyData", getPayload());
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(@NotNull Call<Result> call, @NotNull Response<Result> response) {
                TopUtilKt.log(",response. " + response.body(), "df");
                callback.onComplete(true);
            }

            @Override
            public void onFailure(@NotNull Call<Result> call, @NotNull Throwable t) {
                TopUtilKt.log(call + ",failure. ", "df");
                callback.onComplete(true);
            }
        });
    }

    static class Result {
        String apiCode;//"apiCode" -> {Double@10123} 0.0
        String apiData;//        "apiData" -> "ok"

        @Override
        public String toString() {
            return "Result{" +
                    "apiCode='" + apiCode + '\'' +
                    ", apiData='" + apiData + '\'' +
                    '}';
        }
    }

    public static OkHttpClient createClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        Cache mCache = new Cache(new File(Injector.sContext.getCacheDir(), "response"), 10 * 1024 * 1024); // 设置缓存目录为 /data/data/$package_name/cache/response,缓存大小10M
        return new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS) // 常见超时时间设置
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(new ParamInterceptor()) // 设置参数拦截器；优先级4，每个主题请求都要根据header中设置的request_flag增加公用参数或加解密
                .addInterceptor(loggingInterceptor)
                .cache(mCache)
                .build();
    }
}
