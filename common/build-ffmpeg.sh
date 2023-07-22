#!/system/bin/sh



#pkg upgrade
COMMON_OPTIONS="\
  --target-os=android \
  --enable-cross-compile \
  --disable-static \
  --enable-shared \
  --disable-asm \
  --enable-optimizations \
  --enable-decoders \
  --enable-encoders \
  --enable-hwaccels \
  --enable-muxers \
  --enable-demuxers \
  --enable-parsers \
  --enable-bsfs \
  --enable-protocols \
  --disable-indevs \
  --enable-indev=lavfi \
  --disable-outdevs \
  --enable-filters \
  --enable-doc \
  --enable-htmlpages \
  --disable-debug \
  --enable-gpl \
  --enable-version3 \
  --enable-pic \
  --enable-small \
  --enable-runtime-cpudetect  \
  --enable-swscale-alpha \
  --enable-pixelutils \
  --enable-pthreads \
  --enable-hardcoded-tables \
  --enable-jni \
  --disable-symver \
  --enable-avdevice \
  --enable-avfilter \
  --enable-avformat \
  --enable-avcodec \
  --disable-swresample \
  --enable-avresample \
  --enable-postproc \
  --enable-swscale \
  --enable-avutil \
  --enable-decoder=vorbis \
  --enable-decoder=opus \
  --enable-decoder=flac \
  --enable-decoder=alac \
  --enable-neon \
  "

#ndksupport-1710240003/android-ndk-aide/toolchains/arm-linux-androideabi-4.9/prebuilt/linux-arm/bin/arm-linux-androideabi-

#  --prefix=/data/data/com.termux/files/usr \
#  --extra-libs=-landroid-glob \
#  --enable-cross-compile \
#  --enable-lcms2 \
#  --enable-libaom \
#  --enable-libdav1d \
#  --enable-librav1e \
#  --enable-librsvg \
#  --enable-libxml2 \
#  --enable-libass \
#  --enable-libbluray \
#  --enable-libfreetype \
#  --enable-libgme \
#  --enable-libmp3lame \
#  --enable-libopenjpeg \
#  --enable-libsnappy \
#  --enable-libvo-amrwbenc \
#  --enable-libspeex \
#  --enable-libvpx \
#  --enable-libwebp \
#  --enable-libx264 \
#  --enable-libx265 \
#  --enable-libtheora \
#  --enable-libsoxr \
#  --enable-libxvid \
#  --enable-libvidstab \

export HOME_PATH=~
SOURCE_Z=/sdcard/.aide
cd ${HOME_PATH}
mkdir TMPDIR
chmod 777 ./TMPDIR
export TMPDIR=~/TMPDIR
cp ${SOURCE_Z}/7z-armeabi-v7a .
chmod 777 ./7z-armeabi-v7a
export SZ=~/7z-armeabi-v7a
#${SZ} x ${SOURCE_Z}/ndksupport-1710240003.7z
${SZ} x ${SOURCE_Z}/android-ndk-aide-linux-arm-20160121.tar
#export NDK_PATH=${HOME_PATH}/ndksupport-1710240003/android-ndk-aide
export NDK_PATH=${HOME_PATH}/android-ndk-aide
THUMB=${NDK_PATH}/sources/cxx-stl/stlport/libs/armeabi-v7a/thumb
mkdir ${THUMB}
cp  ${NDK_PATH}/sources/cxx-stl/stlport/libs/armeabi-v7a/libstlport_static.a ${THUMB}
cp  ${NDK_PATH}/sources/cxx-stl/stlport/libs/armeabi-v7a/libstlport_shared.so ${THUMB}
chmod 777 -R ${NDK_PATH}
HOST_PLATFORM="linux-arm"
#${SZ} x ${SOURCE_Z}/ExoPlayer-r2.7.3.tar.gz
#${SZ} x ./ExoPlayer-r2.7.3.tar
export EXOPLAYER_ROOT=${HOME_PATH}/ExoPlayer-r2.7.3
export FFMPEG_EXT_PATH="${EXOPLAYER_ROOT}/extensions/ffmpeg/src/main"
export FFMPEG=ffmpeg-3.3.7
cd "${FFMPEG_EXT_PATH}/jni"
#${SZ} x ${SOURCE_Z}/${FFMPEG}.tar.xz
#${SZ} x ./${FFMPEG}.tar
#cp ${FFMPEG_EXT_PATH}/jni/${FFMPEG}/configure ${SOURCE_Z}/
cp ${SOURCE_Z}/configure ${FFMPEG_EXT_PATH}/jni/${FFMPEG}/
chmod 777 -R ${EXOPLAYER_ROOT}
cd ${FFMPEG}
chmod 777 ./configure
./configure \
    --libdir=android-libs/armeabi-v7a \
    --arch=arm \
    --cpu=armv7-a \
    --cross-prefix="${NDK_PATH}/toolchains/arm-linux-androideabi-4.9/prebuilt/${HOST_PLATFORM}/bin/arm-linux-androideabi-" \
    --sysroot="${NDK_PATH}/platforms/android-9/arch-arm/" \
    --extra-cflags="-march=armv7-a -mfloat-abi=softfp" \
    --extra-ldflags="-Wl,--fix-cortex-a8" \
    --extra-ldexeflags=-pie \
    ${COMMON_OPTIONS} \
    && \
make -j4 && make install-libs && \
make clean



