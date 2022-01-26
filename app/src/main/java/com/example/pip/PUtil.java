package com.example.pip;

import android.media.MediaCodec;
import android.media.MediaCodecList;
import android.media.MediaFormat;

import androidx.annotation.NonNull;

import com.example.TopUtilKt;

import java.io.IOException;

public class PUtil {
    public static MediaCodec createCodec(@NonNull MediaFormat format) throws IOException {

        //        MediaCodec cc = MediaCodec.createDecoderByType(format.getString("mime"));

        //        MediaCodecInfo.CodecCapabilities.FEA
        //        format.setFeatureEnabled("", false);
        MediaCodecList list = new MediaCodecList(MediaCodecList.REGULAR_CODECS);
        String d = list.findDecoderForFormat(format);
        MediaCodec dd = MediaCodec.createByCodecName(d);
        //        TopUtilKt.log("cc = " + cc.getName() + ",dd=" + dd.getName(), "df");
        return dd;
    }

    //获取 第 frameIndex 的帧率时间  其中帧率为frameRate
    public static long getFrameTime(int frameIndex, int frameRate) {
        return (frameIndex * 1000000L) / frameRate;
    }
    //定位到固定帧的方法
    //1 根据 帧率计算出时间 frameTime
    //2 MediaExtractor.seekTo(frameTime, MediaExtractor.SEEK_TO_CLOSEST_SYNC)
    //3 从MediaCodec申请ByteBuffer 再用MediaExtractor.readSampleData()填充buffer
    //  最后调用MediaCodec.queueInputBuffer(bi, 0, dataSize, time, flags)
}
