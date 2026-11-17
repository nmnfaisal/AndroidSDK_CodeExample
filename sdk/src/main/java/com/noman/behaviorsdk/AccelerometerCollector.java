package com.noman.behaviorsdk;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.noman.behaviorsdk.model.AccelerometerEventData;

final class AccelerometerCollector implements Application.ActivityLifecycleCallbacks, SensorEventListener {

	private static final String TAG = "AccelerometerCollector";

	private final EventStore eventStore;
	private final SensorManager sensorManager;
	@Nullable
	private final Sensor accelerometer;

	private int startedActivityCount = 0;

	AccelerometerCollector(@NonNull Context context, @NonNull EventStore eventStore) {

		this.eventStore = eventStore;
		this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
		else {
			this.accelerometer = null;
		}

		if (accelerometer == null) {
			Log.w(TAG, "Accelerometer sensor not available on this device");
		}
	}

	// --- SensorEventListener ---

	@Override
	public void onSensorChanged(SensorEvent event) {
		long epochMs = System.currentTimeMillis()
				- (SystemClock.elapsedRealtimeNanos() - event.timestamp) / 1_000_000L;

		AccelerometerEventData data = new AccelerometerEventData(
				epochMs,
				event.values[0],
				event.values[1],
				event.values[2],
				event.timestamp);

		eventStore.add(data);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy)
	{
	}

	//  ActivityLifecycleCallbacks (foreground start/stop counting)

	@Override
	public void onActivityStarted(@NonNull Activity activity)
	{
		startedActivityCount++;
		// Log.d(TAG, "onActivityStarted: " + activity.getClass().getSimpleName()
		//		+ " | visibleCount=" + startedActivityCount);
		if (startedActivityCount == 1)
		{
			// Log.d(TAG, "App entered foreground — registering accelerometer");
			registerSensor();
		}
	}

	@Override
	public void onActivityStopped(@NonNull Activity activity)
	{
		startedActivityCount--;
		// Log.d(TAG, "onActivityStopped: " + activity.getClass().getSimpleName()
		//		+ " | visibleCount=" + startedActivityCount);
		if (startedActivityCount == 0)
		{
			// Log.d(TAG, "App entered background — unregistering accelerometer (battery save)");
			unregisterSensor();
		}
	}

	void startListening()
	{
		if (startedActivityCount > 0) {registerSensor();}
	}

	// Debug logs commented out. SDK internals shouldn't leak to the host app's logcat
	void stopListening()
	{
//		Log.d(TAG, "stopListening called — force unregistering accelerometer");
		unregisterSensor();
	}

	private void registerSensor()
	{
		if (accelerometer != null)
		{
			sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
		}
	}

	private void unregisterSensor()
	{
		if (sensorManager != null)
		{
			sensorManager.unregisterListener(this);
		}
	}

	// --- Unused lifecycle callbacks ---

	@Override
	public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState)
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
	public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState)
	{
	}

	@Override
	public void onActivityDestroyed(@NonNull Activity activity)
	{
	}
}
