//
// Created by Coder on 2019-07-24.
//

#ifndef FFMPEG_ANDROID_AUDIO_DECODER_H
#define FFMPEG_ANDROID_AUDIO_DECODER_H

#include "common.h"
#include "libavutil/frame.h"

int audio_decoder_init(const char *path);

int audio_decoder_read(AVFrame *frame);

int audio_decoder_release();


#endif //FFMPEG_ANDROID_AUDIO_DECODER_H
