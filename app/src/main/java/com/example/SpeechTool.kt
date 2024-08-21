package com.example

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Locale


//SpeechRecognizer
const val TAG = "SpeechTool"

object SpeechTool {
    private var mRecognizer: SpeechRecognizer? = null
    private val _outFlow = MutableStateFlow("")

    fun getOutFlow(): StateFlow<String> = _outFlow

    fun startS(context: Context) {
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle) {
                // 准备好开始听写
                Log.i(TAG, "onReadyForSpeech")
                _outFlow.value = "准备好开始听写 onReady\n"
            }

            override fun onBeginningOfSpeech() {
                // 用户开始说话
                Log.i(TAG, "onBeginningOfSpeech")
            }

            override fun onRmsChanged(rmsdB: Float) {
                // 音量变化
                Log.i(TAG, "onRmsChanged")
            }

            override fun onBufferReceived(buffer: ByteArray) {
                // 收到的语音数据
//                Log.i(TAG, "onBufferReceived")
            }

            override fun onEndOfSpeech() {
                // 用户结束说话
                Log.i(TAG, "onEndOfSpeech")
            }

            override fun onError(error: Int) {
                // 发生错误
                Log.i(TAG, "onError. $error")
                _outFlow.value = "发生错误 onError $error\n"
            }

            override fun onResults(results: Bundle) {
                // 识别结果
                val matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (matches != null) {
                    for (result in matches) {
                        Log.d(TAG, "onResults: $result")
                    }
                    _outFlow.value = "$matches\n"
                }
            }

            override fun onPartialResults(partialResults: Bundle) {
                // 部分结果
                Log.i(TAG, "onPartialResults $partialResults")
                _outFlow.value = "$partialResults\n"
            }

            override fun onEvent(eventType: Int, params: Bundle) {
                // 其他事件
                Log.i(TAG, "onEvent $eventType")
                _outFlow.value = "onEvent $eventType\n"
            }
        })
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer.startListening(intent)
        mRecognizer = speechRecognizer
    }

    fun stop(context: Context) {
        val r = mRecognizer ?: return
        r.stopListening()
    }
}