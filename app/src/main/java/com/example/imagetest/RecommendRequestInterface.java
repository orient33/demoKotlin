package com.example.imagetest;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

import static com.example.TestNet.*;

/**
 * 推荐模块请求列表
 */
public interface RecommendRequestInterface {
    String BASE_URL = "https://api.zhuti.xx.com/app/v9/";//v9 url hmm
//          HYBRID
//         THEME
//        WALLPAPER
//        VIDEO_WALLPAPER
//        RINGTONE
//        FONT
//        AOD
//        ICONS

    /**
     * 获取首页列表数据
     *
     * @param category  首页类型。
     * @param cardStart 卡片请求起始页index
     * @param cardCount 卡片请求数量
     */
    @Headers({
            HEADER_REQUEST_FLAG_USER_AD
    })
    @GET("uipages?apiVersion=1")
    Call<HomeResponse> getHomePageList(@Query("pageCategory") String category,
                                       @Query("cardStart") int cardStart,
                                       @Query("cardCount") int cardCount);
}