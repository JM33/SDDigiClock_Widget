package com.sd.sddigiclock;

import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.os.Build;
import android.util.Log;

/**
 * Created by Brian on 6/17/2019.
 */

public class ClockJobScheduler extends JobService {
    private static final String TAG = ClockJobScheduler.class.getSimpleName();
    private static final int JOB_ID = 201;
    boolean isWorking = false;
    boolean jobCancelled = false;

    @Override
    public boolean onStartJob(final JobParameters jobParameters) {
        Log.d(TAG,"Running service now..");
        //Small or Long Running task with callback
        //Call Job Finished when your job is finished, in callback
        jobFinished(jobParameters, false );

        //Reschedule the Service before calling job finished
        if(android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            scheduleRefresh();



        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        return false;
    }

    private void scheduleRefresh() {
        JobScheduler mJobScheduler = (JobScheduler) getApplicationContext()
                .getSystemService(JOB_SCHEDULER_SERVICE);
        JobInfo.Builder mJobBuilder =
                new JobInfo.Builder(JOB_ID,
                        new ComponentName(getPackageName(),
                                UpdateWidgetService.class.getName()));

  /* For Android N and Upper Versions */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            mJobBuilder
                    .setMinimumLatency(15 * 60 * 1000) //YOUR_TIME_INTERVAL
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY);
        }
    }
}
