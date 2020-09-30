package com.example.job

import android.app.job.JobInfo
import android.app.job.JobParameters
import android.app.job.JobScheduler
import android.app.job.JobService
import android.content.ComponentName
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.example.TestNet
import com.example.log
import java.util.concurrent.TimeUnit

class DemoJobService : JobService() {
    override fun onStartJob(params: JobParameters): Boolean {
        val id = params.jobId;
        if (id == JOB_ID_1) {
            TestNet().testNet {
                jobFinished(params, false)
            }
        }
        return false // true 任务需要时间 待app jobFinish()后系统才解绑服务  / false 任务完成 系统会尽快解绑服务
    }

    override fun onStopJob(params: JobParameters): Boolean {
        return false
    }

    companion object {
        private const val TAG = "DemoJobService"
        private const val JOB_ID_1 = 11
        @RequiresApi(24)
        fun startJob(context: Context) {
            val js =
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
            val info = JobInfo.Builder(
                JOB_ID_1,
                ComponentName(context, DemoJobService::class.java)
            )
                .setPeriodic(TimeUnit.DAYS.toMillis(1), TimeUnit.HOURS.toMillis(4))
                .build()
            if (Build.VERSION.SDK_INT >= 24 && null != js.getPendingJob(JOB_ID_1)) {
                js.cancel(JOB_ID_1)
            }
            val r = js.schedule(info)
            log("startJob. result=$r", "df")
        }
    }
}