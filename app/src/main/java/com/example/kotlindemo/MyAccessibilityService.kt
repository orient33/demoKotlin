package com.example.kotlindemo

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import com.example.TAG

interface IAS {
    fun onAction(action:Int)
}
// 无障碍 服务访问-- 这个为了 模拟CTS的 action  ：GLOBAL_ACTION_QUICK_SETTINGS 打开quickSettings (MIUI打开了通知栏 非控制中心)
const val ACTION = "df.debug.cts"
class MyAccessibilityService : AccessibilityService(), IAS {
    companion object {
        var isConnect = false
    }

    private val br = object :BroadcastReceiver(){
        override fun onReceive(context: Context, intent: Intent) {
            onAction(GLOBAL_ACTION_QUICK_SETTINGS)
        }
    }
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "onCreate. $this")
        registerReceiver(br, IntentFilter(ACTION), Context.RECEIVER_NOT_EXPORTED)
    }
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // 处理无障碍事件
        Log.d(TAG, "onAccessibilityEvent  $event")
    }

    override fun onInterrupt() {
        // 中断处理
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        isConnect = true
        Log.i(TAG, "onServiceConnected..to System")
    }

    // 执行全局操作的方法
    override fun onAction(action: Int) {
//    override fun performGlobalAction(action: Int): Boolean {
        if (super.performGlobalAction(action)) {
            Log.d(TAG, "Global action performed: $action")
        } else {
            Log.d(TAG, "Failed to perform global action: $action")
        }
    }
}