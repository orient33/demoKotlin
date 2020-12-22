package com.example.imagetest;

import android.text.TextUtils;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Keep
public class HomeResponse {
    public String apiCode;//"apiCode" -> {Double@10123} 0.0
    ApiData apiData;
    String category;

    static class ApiData {
        String title;//混搭推荐
        String category;//HYBRID
        List<UICard> cards;
    }

    static class UICard {
        String cardType;//TOP_BANNER, HYBRID_RECOMMENDATION_CARD,
        int page;// 0,1,2...
        boolean hasMore;
        List<OneCard> recommends;
    }

    static class OneCard {
        String title;
        String imageUrl;
        Link link;
        int imgWidth;   //490
        int imgHeight;  //868
        String snapshotAspectRatio;// "320:568
        String gifUrl;
        String videoUrl;

        @Nullable
        ImageData toImageData() {
            if (TextUtils.isEmpty(imageUrl) || TextUtils.isEmpty(title)) return null;
            String url = imageUrl;
            if (url.startsWith("http://")) {
                url = url.replaceFirst("http:", "https:");
            }
            float ratio = 0f;
            if (imgWidth >= 0 && imgHeight >= 0) {
                ratio = imgHeight * 1f / imgWidth;
            } else if (snapshotAspectRatio != null && snapshotAspectRatio.contains(":")) {
                String[] wh = snapshotAspectRatio.split(":");
                int w = Integer.parseInt(wh[0]), h = Integer.parseInt(wh[1]);
                ratio = h * 1f / w;
            }
            return new ImageData(title, url, ratio, videoUrl, toString());
        }

        @Override
        public String toString() {
            return "OneCard{" +
                    "title='" + title + '\'' +
                    ", imageUrl='" + imageUrl + '\'' +
                    ", link=" + link +
                    ", imgWidth=" + imgWidth +
                    ", imgHeight=" + imgHeight +
                    ", snapshotAspectRatio='" + snapshotAspectRatio + '\'' +
                    ", gifUrl='" + gifUrl + '\'' +
                    ", videoUrl='" + videoUrl + '\'' +
                    '}';
        }
    }

    static class Link {
        String title;
        String type;// SUBJECT, HREF
        String productType;//WALLPAPER,THEME,AOD,FONT

        @Override
        public String toString() {
            return "Link{" +
                    "title='" + title + '\'' +
                    ", type='" + type + '\'' +
                    ", productType='" + productType + '\'' +
                    '}';
        }
    }

    public List<ImageData> toImageData(boolean ignoreGif) {
        if (apiData == null || apiData.cards == null || apiData.cards.isEmpty())
            return Collections.emptyList();
        List<ImageData> result = new ArrayList<>(40);
        ImageData data;
        for (UICard uicard : apiData.cards) {
            if (uicard.recommends != null && uicard.recommends.size() > 0) {
                for (OneCard card : uicard.recommends) {
                    data = card.toImageData();
                    if (data != null && (!ignoreGif || !data.getImageUrl().contains("gif"))) {
                        result.add(data);
                    }
                }
            }
        }
        return result;
    }
}
