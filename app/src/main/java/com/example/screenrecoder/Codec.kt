package com.example.screenrecoder

//avc规格: https://zh.wikipedia.org/wiki/H.264/MPEG-4_AVC
val ENCODER_PARAM_TABLE = arrayOf(
    // width,  height,  bitrate, framerate  /* level */
    intArrayOf(9999, 9999, 14000000, 30),   //to be replace Use device height, width,
    intArrayOf(1920, 1080, 14000000, 30),   //custom
    intArrayOf(1280, 720, 14000000, 30),    /* AVCLevel31 */
    intArrayOf(720, 480, 10000000, 30),     /* AVCLevel3  */
    intArrayOf(720, 480, 4000000, 15),      /* AVCLevel22  */
    intArrayOf(352, 576, 4000000, 25)       /* AVCLevel21  */
)