package com.noman.behaviorsdk;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

final class TouchCollector implements Application.ActivityLifecycleCallbacks
{
	private final EventStore eventStore;

	TouchCollector(@NonNull EventStore eventStore)
	{
		this.eventStore = eventStore;
	}

	@Override
	public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState)
	{
		Window window = activity.getWindow();
		if (window == null)
		{
			return;
		}

		Window.Callback originalCallback = window.getCallback();
		if (originalCallback instanceof TouchInterceptorCallback)
		{
			return; // already wrapped — double-wrap guard
		}

		window.setCallback(new TouchInterceptorCallback(originalCallback, eventStore));
	}

	@Override
	public void onActivityStarted(@NonNull Activity activity)
	{
	}

	@Override
	public void onActivityResumed(@NonNull Activity activity)
	{
	}

	@Override
	public void onActivityPaused(@NonNull Activity activity)
	{
	}

	@Override
	public void onActivityStopped(@NonNull Activity activity)
	{
	}

	@Override
	public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState)
	{
	}

	@Override
	public void onActivityDestroyed(@NonNull Activity activity)
	{
	}
}
