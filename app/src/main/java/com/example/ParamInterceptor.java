package com.example;

import android.text.TextUtils;
import android.util.Log;

import com.example.kotlindemo.BuildConfig;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;

import static com.example.TestNet.*;

/**
 * 通用参数拦截器
 */
public class ParamInterceptor implements Interceptor {

    private static final String TAG = "df";

    private static String sCapabilities;

    /**
     * 通用参数key
     */
    private static final String PARAM_DEVICE = "device";
    private static final String PARAM_REGION = "region";
    private static final String PARAM_IS_GLOBAL_BUILD = "isGlobal";
    private static final String PARAM_SYSTEM_TYPE = "system";
    private static final String PARAM_SYSTEM_VERSION = "version";
    private static final String PARAM_MIUI_UI_VERSION = "miuiUIVersion";
    private static final String PARAM_SYSTEM_ALPHA = "alpha";
    private static final String PARAM_LANGUAGE = "language";
    private static final String PARAM_CAPABILITY = "capability";
    private static final String PARAM_APK_VERSION = "apk";
    private static final String PARAM_DEVICE_PIXEL = "devicePixel";
    private static final String PARAM_HWVERSION = "hwVersion";
    private static final String PARAM_IMEI = "imei";
    private static final String PARAM_OAID = "oaid";
    private static final String PARAM_VAID = "vaid";
    private static final String PARAM_PERSONAL = "personal";
    private static final String PARAM_MODEL = "model";
    private static final String PARAM_AD_COUNTRY = "adCountry";
    private static final String PARAM_PACKAGE_NAME = "packageName";
    private static final String PARAM_NETWORK_TYPE = "networkType";
    private static final String PARAM_RESTRICT_IMEI = "restrictImei";
    private static final String PARAM_SIGNATURE = "signature";
    private static final String PARAM_ENTRY_TYPE = "entryType";
    private static final String PARAM_X_REF = "xRef";
    private static final String PARAM_X_PREV_REF = "xPrevRef";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // 默认无请求参数
        int requestFlag = REQUEST_FLAG_NONE;
        if (!TextUtils.isEmpty(request.header(HEADER_REQUEST_FLAG))) {
            try {
                requestFlag = Integer.parseInt(request.header(HEADER_REQUEST_FLAG));
            } catch (Exception e) {
                // do nothing
            }
        }

        HashMap<String, String> finalParamsMap = getParamsFromRequest(request);

        // 是否携带通用设备环境参数
        if ((requestFlag & REQUEST_FLAG_ENV) != 0) {
            finalParamsMap.put(PARAM_DEVICE, "dipper");
            finalParamsMap.put(PARAM_REGION, "cn");
            finalParamsMap.put(PARAM_IS_GLOBAL_BUILD, "false");
            finalParamsMap.put(PARAM_SYSTEM_TYPE, "usereng");
            finalParamsMap.put(PARAM_SYSTEM_VERSION, "9.0");
            finalParamsMap.put(PARAM_MIUI_UI_VERSION, "12");
            finalParamsMap.put(PARAM_SYSTEM_ALPHA, "false");
            finalParamsMap.put(PARAM_LANGUAGE, "zh-cn");
            finalParamsMap.put(PARAM_CAPABILITY, getCapability());
            finalParamsMap.put(PARAM_APK_VERSION, "809");
            finalParamsMap.put(PARAM_DEVICE_PIXEL, "1080x1920");
//            finalParamsMap.put(PARAM_HWVERSION, "");
        }

//        if (!TextUtils.isEmpty(DeviceUtils.getImei(AppContextManager.getContext()))) {
//            finalParamsMap.put(PARAM_IMEI, DeviceUtils.getImei(AppContextManager.getContext()));
//        }
//        if (!TextUtils.isEmpty(DeviceUtils.getOaid())) {
//            finalParamsMap.put(PARAM_OAID, DeviceUtils.getOaid());
//        } else if (!TextUtils.isEmpty(DeviceUtils.getVaid())) {
//            finalParamsMap.put(PARAM_VAID, DeviceUtils.getVaid());
//        }

        // 是否携带广告相关参数
//        if ((requestFlag & ThemeNetworkManager.REQUEST_FLAG_ADD_AD_INFO) != 0) {
//            finalParamsMap.put(PARAM_MODEL, DeviceUtils.getModel());
//            finalParamsMap.put(PARAM_AD_COUNTRY, DeviceUtils.getCountry());
//            finalParamsMap.put(PARAM_PACKAGE_NAME, PackageUtils.getPackageName(AppContextManager.getContext()));
//            finalParamsMap.put(PARAM_NETWORK_TYPE, String.valueOf(NetworkUtils.getNetworkType(AppContextManager.getContext())));
//            finalParamsMap.put(PARAM_RESTRICT_IMEI, String.valueOf(DeviceUtils.isRestrictImei()));
//        }

        // 如果没有说明不加参数，统一加上analysis参数
//        if (requestFlag != ThemeNetworkManager.REQUEST_FLAG_NONE) {
//            finalParamsMap.put(PARAM_ENTRY_TYPE, PageTrackHelper.getForegroundEntryType() != null ? PageTrackHelper.getForegroundEntryType() : "unknow");
//            finalParamsMap.put(PARAM_X_REF, PageTrackHelper.getForegroundRef() != null ? PageTrackHelper.getForegroundRef() : "unknow");
//            finalParamsMap.put(PARAM_X_PREV_REF, PageTrackHelper.getForegroundPrevRef() != null ? PageTrackHelper.getForegroundPrevRef() : "unknow");
//        }

        // TODO: 2020-02-25 临时通用参数，表明是否是 AOD 灰度用户，灰度之后去掉
//        finalParamsMap.put("aodGray", Boolean.toString(UIUtils.isShowAodEntrance()));

        // 是否需要参数加密&签名
        String signature = "";

        // 把参数拼接回request
        request = replaceFinalParamsMapToRequest(request, finalParamsMap, signature);

        // 移除requestFlag
        request = request.newBuilder()
                .removeHeader(HEADER_REQUEST_FLAG)
                .build();

        // 一个关于缓存的特殊处理，见相应注释
        request = filterNoCachedParams(request);

        // 网络请求
        Response response = chain.proceed(request);

        // 一个关于缓存的特殊处理，见相应注释(与上面对应)
        Request responseRequest = response.request();
        response = response.newBuilder()
                .request(recoveryNoCahcedParam(responseRequest))
                .build();

        if (BuildConfig.DEBUG) {
            Log.d(TAG, String.format("intercept:  retrofit %s, %d", request.url().toString(), response.code()));
        }

        return response;
    }

    /**
     * 从request中获取参数map
     */
    private HashMap<String, String> getParamsFromRequest(Request request) {
        HashMap<String, String> params = new HashMap<>();

        //get方法和post方法处理参数情况不一样，需要区分开来
        if ("GET".equals(request.method())) {
            HttpUrl url = request.url();
            for (int i = 0; i < url.querySize(); i++) {
                //取出url中？后的参数
                String key = url.queryParameterName(i);
                String value = url.queryParameterValue(i);

                params.put(key, value);
            }
        } else if ("POST".equals(request.method())) {
            // 这里是表单请求
            if (request.body() instanceof FormBody) {
                FormBody.Builder builder = new FormBody.Builder();
                FormBody oldFormBody = (FormBody) request.body();
                for (int i = 0; i < oldFormBody.size(); i++) {
                    //取出并保存原请求参数
                    params.put(oldFormBody.name(i), oldFormBody.value(i));
                }
            }
        }

        return params;
    }

    /**
     * 获取baseUrl
     */
    private String getBaseUrlFromRequest(Request request) {
        URI originUrl = request.url().uri();
        try {
            return new URI(originUrl.getScheme(),
                    originUrl.getAuthority(),
                    originUrl.getPath(),
                    null).toString();
        } catch (Exception e) {
            Log.e(TAG, "getBaseUrlFromRequest", e);
            return "";
        }
    }

    /**
     * 把参数重新覆盖回request
     */
    private Request replaceFinalParamsMapToRequest(Request request, HashMap<String, String> finalParamsMap, String signature) {
        //get方法和post方法处理参数情况不一样，需要区分开来
        if ("GET".equals(request.method())) {
            HttpUrl url = request.url();
            HttpUrl.Builder newBuilder = url.newBuilder();
            for (Map.Entry<String, String> entry : finalParamsMap.entrySet()) {
                // 替换
                newBuilder.removeAllQueryParameters(entry.getKey());
                newBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
            if (!TextUtils.isEmpty(signature)) {
                newBuilder.addQueryParameter(PARAM_SIGNATURE, signature);
            }

            return request.newBuilder()
                    .url(newBuilder.build())
                    .build();
        } else if ("POST".equals(request.method())) {
            // 这里是表单请求
            if (request.body() instanceof FormBody) {
                FormBody.Builder builder = new FormBody.Builder();
                //添加加密后的参数
                for (Map.Entry<String, String> entry : finalParamsMap.entrySet()) {
                    if (entry.getValue() == null) {
                        Log.w(TAG, "warning!!! value == null for " + entry.getKey());
                        continue;
                    }
                    builder.add(entry.getKey(), entry.getValue());
                }
                // 单独把签名拼接到url后面
                HttpUrl url = request.url();
                HttpUrl.Builder urlBuilder = url.newBuilder();
                if (!TextUtils.isEmpty(signature)) {
                    urlBuilder.addQueryParameter(PARAM_SIGNATURE, signature);
                }

                //构建新的request
                return request.newBuilder()
                        .url(urlBuilder.build())
                        .post(builder.build())
                        .build();
            }
        }

        return request;
    }

    // network参数不参与缓存策略中的key生成计算
    private static final String[] NO_CACHED_PARAMS = {PARAM_NETWORK_TYPE, PARAM_SIGNATURE};
    private static final String HEADER_NO_CACHED_PARAMS = "no_cached_params";

    /**
     * 过滤掉影响缓存的参数
     * 背景：由于okhttp的缓存策略是将整体url作为key，主题请求中会有network参数，会因为网络情况变化导致缓存不可用
     * 处理方法：在ParamInterceptor中（缓存拦截器处理前）将需要过滤的参数从url中临时移至header中，然后再在NoCachedParamInterceptor（缓存拦截器处理后，真正网络请求前）把header中的参数移回url
     */
    static Request filterNoCachedParams(Request request) {
        if ("GET".equals(request.method())) {
            HttpUrl url = request.url();
            HttpUrl.Builder newBuilder = url.newBuilder();

            // 遍历需要过滤的参数key，拼接成临时字符串，并从url中移除
            StringBuilder noCachedParams = new StringBuilder();
            for (String paramKey : NO_CACHED_PARAMS) {
                String paramValue = url.queryParameter(paramKey);
                if (!TextUtils.isEmpty(paramValue)) {
                    //:和&作为分隔的前提是目前加解密使用base64算法，后续如果换算法，需要换分隔符
                    noCachedParams = noCachedParams.append(paramKey)
                            .append(":")
                            .append(paramValue)
                            .append("&");

                    // 移除
                    newBuilder.removeAllQueryParameters(paramKey);
                }
            }
            if (!TextUtils.isEmpty(noCachedParams.toString())) {
                return request.newBuilder()
                        .addHeader(HEADER_NO_CACHED_PARAMS, noCachedParams.toString())
                        .url(newBuilder.build())
                        .build();
            }
        }

        return request;
    }

    /**
     * 上面过滤方法的逆操作
     */
    static Request recoveryNoCahcedParam(Request request) {
        if ("GET".equals(request.method())
                && !TextUtils.isEmpty(request.header(HEADER_NO_CACHED_PARAMS))) {
            HttpUrl url = request.url();
            HttpUrl.Builder newBuilder = url.newBuilder();

            // 对header中临时参数进行分解，拆分回url
            String noCachedParamsStr = request.header(HEADER_NO_CACHED_PARAMS);
            String[] paramMaps = noCachedParamsStr.split("&");

            for (String paramMapStr : paramMaps) {
                if (!TextUtils.isEmpty(paramMapStr)) {
                    //:和&作为分隔的前提是目前加解密使用base64算法，后续如果换算法，需要换分隔符
                    String[] paramMap = paramMapStr.split(":");
                    if (paramMap.length == 2
                            && !TextUtils.isEmpty(paramMap[0])
                            && !TextUtils.isEmpty(paramMap[1])) {
                        newBuilder.addQueryParameter(paramMap[0], paramMap[1]);
                    }
                }
            }

            return request.newBuilder()
                    .removeHeader(HEADER_NO_CACHED_PARAMS)
                    .url(newBuilder.build())
                    .build();
        }

        return request;
    }

    /**
     * 获取客户端能力（主要根据系统版本号做分辨）
     */
    private static String getCapability() {
        if (sCapabilities == null) {
            final Map<String, String> VALUE_CAPABILITIES = new HashMap<String, String>();
            int uiVersion = 9;
            if (uiVersion == 7) {
                uiVersion = 6;
            } else if (uiVersion == 5) {
                uiVersion = 4;
            }
            VALUE_CAPABILITIES.put("v", Integer.toString(uiVersion + 2));
            // aod capability
//            VALUE_CAPABILITIES.put("a", );
            sCapabilities = buildCapability(VALUE_CAPABILITIES);
        }
        return sCapabilities;
    }

    private static String buildCapability(Map<String, String> valueCapabilities) {
        String[] BOOLEAN_CAPABILITIES = {
                "w", // web view support
                "b", // belt list support
                "s", // scroll banner support
                "m", // multiple subject support
                "h5" // multiple subject support
        };

        StringBuilder sb = new StringBuilder();
        for (String booleanCapability : BOOLEAN_CAPABILITIES) {
            sb.append(booleanCapability);
            sb.append(",");
        }
        for (String key : valueCapabilities.keySet()) {
            sb.append(key + ":" + valueCapabilities.get(key));
            sb.append(",");
        }
        return sb.substring(0, sb.length() - 1);
    }
}
