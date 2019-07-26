//
// Created by Coder on 2019-07-24.
//
#include <libavformat/avformat.h>
#include "audio-decoder.h"

int ret = 0;

AVFormatContext *fmt_ctx;
int stream_index = 0;
AVStream *audio_stream;
AVCodec *codec;
AVCodecContext *codec_context;
AVPacket *packet;

//初始化
int audio_decoder_init(const char *path) {

    ret = avformat_open_input(&fmt_ctx, path, NULL, NULL);
    if (ret < 0) {
        return -1;
    }

    ret = avformat_find_stream_info(fmt_ctx, NULL);
    if (ret < 0) {
        return -2;
    }

    ret = av_find_best_stream(fmt_ctx, AVMEDIA_TYPE_AUDIO, -1, -1, NULL, 0);
    if (ret < 0) {
        return -3;
    }
    stream_index = ret;
    audio_stream = fmt_ctx->streams[stream_index];
    codec = avcodec_find_decoder(audio_stream->codecpar->codec_id);
    if (!codec) {
        return -4;
    }

    codec_context = avcodec_alloc_context3(codec);
    if (!codec_context) {
        return -5;
    }


    ret = avcodec_parameters_to_context(codec_context, audio_stream->codecpar);
    if (ret < 0) {
        return -6;
    }

    ret = avcodec_open2(codec_context, codec, NULL);
    if (ret < 0) {
        return -7;
    }



    return 0;
}

int audio_decoder_read(AVFrame *frame) {
    if (packet == NULL) {
        packet = av_packet_alloc();
    }
    av_init_packet(packet);
    av_frame_get_buffer(frame, 0);
    packet->data = NULL;
    packet->size = 0;
    int got_frame = 0;
    while (av_read_frame(fmt_ctx, packet) >= 0) {
        if (packet->stream_index != stream_index) {
            continue;
        }
        ret = avcodec_decode_audio4(codec_context, frame, &got_frame, packet);
        if (ret < 0) {
            return -1;
        }
        if (got_frame) {
            return 0;
        }
    }
    return -2;
}

int audio_decoder_release() {
    avcodec_free_context(&codec_context);
}
