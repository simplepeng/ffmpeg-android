package site.javen.ffmpeg;

import android.graphics.Bitmap;

/**
 * FFmpeg 工具类
 *
 * @author coder
 */
public class FFmpegEncoder {
    static {
        System.loadLibrary("ijkffmpeg");
        System.loadLibrary("native-lib");
    }

    /**
     * 获取所有编码器信息
     * <p>
     * <p>
     * [Decode][Video]        flv
     * [Decode][Video]       h263
     * [Decode][Video]       h264
     * [Decode][Video]       hevc
     * [Encode][Video]        png
     * [Decode][Video]        vp6
     * [Decode][Video]       vp6f
     * [Decode][Video]        vp8
     * [Decode][Video]        vp9
     * [Decode][Audeo]        aac
     * [Decode][Audeo]   aac_latm
     * [Decode][Audeo]       flac
     * [Decode][Audeo]        mp3
     * [Decode][Audeo]   mp3float
     * [Decode][Audeo]     mp3adu
     * [Decode][Audeo] mp3adufloat
     * [Decode][Audeo]     mp3on4
     * [Decode][Audeo] mp3on4float
     *
     * @return
     */
    public native static String listAllCodec();


    /**
     * 获取 FFmpeg 版本信息
     *
     * @return
     */
    public native static String getVersion();



    /**
     * 初始化编码器
     *
     * @param codecName
     * @param width
     * @param height
     * @return
     */
    public native int init(String codecName, String filename, int width, int height);

    /**
     * 喂一帧数据
     *
     * @param bitmap
     * @return
     */
    public native int writeFrame(Bitmap bitmap);


    /**
     * 释放
     *
     * @return
     */
    public native int free();
}
