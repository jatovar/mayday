package com.spadigital.mayday.app.Jobs;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;


/**
 * Created by jorge on 7/10/16.
 */
public class SendMessageJob extends Job{

    private String text;

    public SendMessageJob(String text) {
        super(new Params(500).requireNetwork().persist().groupBy("send_message"));//order of tweets matter, we don't want to send two in parallel

        this.text = text;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {

    }

    @Override
    protected void onCancel(int cancelReason, @Nullable Throwable throwable) {

    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(@NonNull Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }
}
