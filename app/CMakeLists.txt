# Sets the minimum version of CMake required to build your native library.
# This ensures that a certain set of CMake features is available to
# your build.

cmake_minimum_required(VERSION 3.4.1)
include_directories(src/main/jni/) #添加头文件的位置

#设置查找动态库位置
set(LINK_DIR ${PROJECT_SOURCE_DIR}/src/main/jniLibs/${ANDROID_ABI})
link_directories(${LINK_DIR})

add_library( # Specifies the name of the library.
        native-lib
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        src/main/jni/common.h
        src/main/jni/audio-decoder.c
        src/main/jni/native-lib.c
        src/main/jni/mp4-encoder.c
        )

find_library(log-lib log)

#指定生成目标需要哪些 Library 支持
target_link_libraries(
        native-lib

        libijkffmpeg.so
        libijkplayer.so
        libijksdl.so
        jnigraphics
        ${log-lib})

