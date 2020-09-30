package com.example;


import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NetAPI {

    String EVENT_PARAM_THEME_ID = "theme_id";
    String EVENT_PARAM_THEME_HASH = "theme_hash";
    String EVENT_PARAM_THEME_NAME = "theme_name";
    String EVENT_PARAM_WALLPAPER_ID = "wallpaper_id";
    String EVENT_PARAM_WALLPAPER_NAME = "wallpaper_name";
    String EVENT_PARAM_RINGTONE_ID = "ringtone_id";
    String EVENT_PARAM_RINGTONE_NAME = "ringtone_name";
    String EVENT_PARAM_FONT_ID = "font_id";
    String EVENT_PARAM_FONT_NAME = "font_name";
    String EVENT_PARAM_MIWALLPAPER_ID = "miwallpaper_id";
    String EVENT_PARAM_MIWALLPAPER_NAME = "miwallpaper_name";
    String EVENT_PARAM_VIDEO_WALLPAPER_ID = "video_wallpaper_id";
    String EVENT_PARAM_VIDEO_WALLPAPER_NAME = "video_wallpaper_name";
    String EVENT_PARAM_AOD_ID = "aod_id";
    String EVENT_PARAM_AOD_NAME = "aod_name";
    String EVENT_PARAM_ICON_ID = "icon_id";
    String EVENT_PARAM_ICON_NAME = "icon_name";
//https://thm.market.xiaomi.com/thm/stats
    //source=updateDaily & payload=xxx &通用

    //    @Headers({
//            HEADER_REQUEST_FLAG_ENV
//    })
    @POST("thm/stats")
    Call<TestNet.Result> uploadDaily(@Query("source") String source, @Query("payload") String payload);
}
