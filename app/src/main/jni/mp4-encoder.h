//
// Created by Coder on 2019-07-24.
//

#ifndef FFMPEG_MP4_ENCODER_H
#define FFMPEG_MP4_ENCODER_H

#include <stdlib.h>
#include <libswscale/swscale.h>
#include <libswresample/swresample.h>
#include <android/bitmap.h>
#include "libavformat/avformat.h"
#include "libavcodec/avcodec.h"
#include "libavutil/frame.h"
#include "libyuv.h"

#define STREAM_FRAME_RATE 25 /* 25 images/s */
#define STREAM_PIX_FMT    AV_PIX_FMT_YUV420P /* default pix_fmt */
#define SCALE_FLAGS SWS_BICUBIC


// a wrapper around a single output AVStream
typedef struct OutputStream {
    AVStream *st;
    AVCodecContext *enc;

    /* pts of the next frame that will be generated */
    int64_t next_pts;
    int64_t samples_count;

    AVFrame *frame;
    AVFrame *tmp_frame;

    float t, tincr, tincr2;

    struct SwsContext *sws_ctx;
    struct SwrContext *swr_ctx;
} OutputStream;


/**
 * 初始化编码器
 * @param path 路径
 * @param _width  宽度
 * @param _height 高度
 * @return
 */
int init_encoder(const char *path, const char *audiopath, int _width, int _height);

/**
 * 写入 argb 数据
 * @param data  数据
 * @return
 */
int write_video_frame(uint8_t *data, AndroidBitmapInfo param);

/**
 * 写入音频
 * @return
 */
int write_audio_frame();


/**
 * 清空缓存
 * @return
 */
int flush_encoder();

#endif //FFMPEG_MP4_ENCODER_H
