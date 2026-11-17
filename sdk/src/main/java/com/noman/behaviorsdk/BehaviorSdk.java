package com.noman.behaviorsdk;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import com.noman.behaviorsdk.model.SensorDataEvent;
import java.util.Collections;
import java.util.List;

public final class BehaviorSdk {

	private static final String TAG = "BehaviorSdk";

	private static Application sApplication;
	private static EventStore sEventStore;
	private static TouchCollector sTouchCollector;
	private static AccelerometerCollector sAccelerometerCollector;
	private static boolean sCollecting = false;

	private BehaviorSdk() { }

	public static void init(@NonNull Application application) {

		if (sApplication != null) {
			Log.w(TAG, "BehaviorSdk already initialized — ignoring duplicate init()");
			return;
		}

		sApplication = application;
		sEventStore = new EventStore();
		sTouchCollector = new TouchCollector(sEventStore);
		sAccelerometerCollector = new AccelerometerCollector(application, sEventStore);
	}

	public static void start() {

		if (sApplication == null) {
			throw new IllegalStateException("BehaviorSdk.init() must be called before start()");
		}

		if (sCollecting) {
			Log.w(TAG, "BehaviorSdk already collecting — ignoring duplicate start()");
			return;
		}

		sCollecting = true;
		sEventStore.setAccepting(true);
		sApplication.registerActivityLifecycleCallbacks(sTouchCollector);
		sApplication.registerActivityLifecycleCallbacks(sAccelerometerCollector);
		sAccelerometerCollector.startListening();
	}

	public static void stop() {

		if (sApplication == null) {
			throw new IllegalStateException("BehaviorSdk.init() must be called before stop()");
		}

		if (!sCollecting) {
			Log.w(TAG, "BehaviorSdk is not collecting — ignoring stop()");
			return;
		}

		sCollecting = false;
		sEventStore.setAccepting(false);
		sApplication.unregisterActivityLifecycleCallbacks(sTouchCollector);
		sApplication.unregisterActivityLifecycleCallbacks(sAccelerometerCollector);
		sAccelerometerCollector.stopListening();
	}

	@NonNull
	public static List<SensorDataEvent> getCollectedData() {

		if (sEventStore == null) {
			return Collections.emptyList();
		}

		return sEventStore.getAll();
	}

	public static int getEventCount() {
		if (sEventStore == null) {
			return 0;
		}

		return sEventStore.size();
	}

	public static void clear() {

		if (sApplication == null) {
			throw new IllegalStateException("BehaviorSdk.init() must be called before clear()");
		}

		sEventStore.clear();
	}

	public static boolean isCollecting() {
		return sCollecting;
	}
}
