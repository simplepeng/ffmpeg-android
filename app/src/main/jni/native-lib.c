#include <jni.h>
#include <string.h>
#include <malloc.h>
#include <libavformat/avformat.h>
#include "libavcodec/avcodec.h"
#include "libavutil/avutil.h"
#include <android/bitmap.h>
#include "libyuv.h"
#include "common.h"
#include "mp4-encoder.h"

JNIEXPORT jstring JNICALL
Java_site_javen_ffmpeg_FFmpegEncoder_listAllCodec(JNIEnv *env,
                                                  jclass type) {
    avcodec_register_all();
    AVCodec *c_temp = av_codec_next(NULL);
    char *info = (char *) malloc(40000);
    memset(info, 0, 40000);
    while (c_temp != NULL) {
        if (c_temp->decode != NULL) {
            strcat(info, "[Decode]");
        } else {
            strcat(info, "[Encode]");
        }
        switch (c_temp->type) {
            case AVMEDIA_TYPE_VIDEO:
                strcat(info, "[Video]");
                break;
            case AVMEDIA_TYPE_AUDIO:
                strcat(info, "[Audio]");
                break;
            default:
                strcat(info, "[Other]");
                break;
        }
        sprintf(info, "%s %10s\n", info, c_temp->name);
        c_temp = c_temp->next;
    }
    return (*env)->NewStringUTF(env, info);
}

/**
 * 获取 Version 版本信息
 * @param env
 * @param type
 * @return
 */
JNIEXPORT jstring JNICALL
Java_site_javen_ffmpeg_FFmpegEncoder_getVersion(JNIEnv *env,
                                                jclass type) {
    int ret = ARGBToI420(0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    LOGD("ret=%d", ret);
    return (*env)->NewStringUTF(env, av_version_info());
}

jint JNICALL
Java_site_javen_ffmpeg_FFmpegEncoder_init(JNIEnv *env,
                                          jobject instance,
                                          jstring audiofile_,
                                          jstring filename_,
                                          jint width,
                                          jint height) {
    const char *audiofile = (*env)->GetStringUTFChars(env, audiofile_, 0);
    const char *filename = (*env)->GetStringUTFChars(env, filename_, 0);
    int ret = init_encoder(filename, audiofile, width, height);
    while (write_audio_frame() != -10000) {}

    (*env)->ReleaseStringUTFChars(env, audiofile_, audiofile);
    (*env)->ReleaseStringUTFChars(env, filename_, filename);
    return ret;
}

/**
 * 写一帧数据
 * @param env
 * @param instance
 * @param bitmap
 * @return
 */
JNIEXPORT jint JNICALL
Java_site_javen_ffmpeg_FFmpegEncoder_writeFrame(JNIEnv *env, jobject instance, jobject bitmap) {
    if (NULL == bitmap) {
        return -1;
    }
    AndroidBitmapInfo info;
    int ret = AndroidBitmap_getInfo(env, bitmap, &info);
    if (ret != ANDROID_BITMAP_RESULT_SUCCESS) {
        return -2;
    }

    unsigned char *addrPtr = NULL;
    ret = AndroidBitmap_lockPixels(env, bitmap, (void **) &addrPtr);
    if (ret != ANDROID_BITMAP_RESULT_SUCCESS) {
        return -3;
    }


    ret = write_video_frame(addrPtr, info);

    AndroidBitmap_unlockPixels(env, bitmap);

    return ret;
}


JNIEXPORT jint JNICALL
Java_site_javen_ffmpeg_FFmpegEncoder_free(JNIEnv *env, jobject instance) {
    return flush_encoder();
}


JNIEXPORT void JNICALL
Java_site_javen_ffmpeg_MainActivity_test(JNIEnv *env, jobject instance) {
}

