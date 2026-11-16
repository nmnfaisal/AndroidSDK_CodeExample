package com.noman.behaviorsdk.model;

import androidx.annotation.NonNull;

public abstract class SensorDataEvent implements Comparable<SensorDataEvent>
{
	private final long timestamp;
	private final EventType type;

	protected SensorDataEvent(long timestamp, @NonNull EventType type)
	{
		this.timestamp = timestamp;
		this.type = type;
	}

	public long getTimestamp()
	{
		return timestamp;
	}

	@NonNull
	public EventType getType()
	{
		return type;
	}

	@Override
	public int compareTo(@NonNull SensorDataEvent other)
	{
		int cmp = Long.compare(this.timestamp, other.timestamp);
		if (cmp != 0)
		{
			return cmp;
		}
		return Integer.compare(this.type.ordinal(), other.type.ordinal());
	}

	@NonNull
	@Override
	public abstract String toString();
}
