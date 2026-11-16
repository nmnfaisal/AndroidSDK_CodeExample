package com.behaviosec.android.sample;

import android.app.Application;

import com.noman.behaviorsdk.BehaviorSdk;

public class SampleApp extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();
		BehaviorSdk.init(this);
		BehaviorSdk.start();
	}
}
