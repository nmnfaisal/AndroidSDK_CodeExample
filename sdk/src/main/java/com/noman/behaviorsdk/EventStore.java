package com.noman.behaviorsdk;

import androidx.annotation.NonNull;

import com.noman.behaviorsdk.model.SensorDataEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

final class EventStore
{
	private final Object lock = new Object();
	private final List<SensorDataEvent> events = new ArrayList<>();
	private boolean accepting = false;			// to stop collecting when stop button is pressed


	synchronized void setAccepting(boolean value) {
		this.accepting = value;
	}

	void add(@NonNull SensorDataEvent event) {
		if (!accepting) {
			return;
		}
		synchronized (lock) {
			events.add(event);
		}
	}

	@NonNull
	List<SensorDataEvent> getAll() {
		synchronized (lock)
		{
			List<SensorDataEvent> copy = new ArrayList<>(events);
			Collections.sort(copy);
			return copy;
		}
	}

	int size() {
		synchronized (lock) {
			return events.size();
		}
	}

	void clear() {
		synchronized (lock) {
			events.clear();
		}
	}
}
