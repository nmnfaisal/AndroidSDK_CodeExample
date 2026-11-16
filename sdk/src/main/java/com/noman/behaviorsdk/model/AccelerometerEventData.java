package com.noman.behaviorsdk.model;

import androidx.annotation.NonNull;

public final class AccelerometerEventData extends SensorDataEvent
{
	private final float x;
	private final float y;
	private final float z;
	private final long sensorTimestampNs;

	public AccelerometerEventData(
			long timestamp,
			float x,
			float y,
			float z,
			long sensorTimestampNs)
	{
		super(timestamp, EventType.ACCELEROMETER);
		this.x = x;
		this.y = y;
		this.z = z;
		this.sensorTimestampNs = sensorTimestampNs;
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public float getZ()
	{
		return z;
	}

	public long getSensorTimestampNs()
	{
		return sensorTimestampNs;
	}

	@NonNull
	@Override
	public String toString()
	{
		return "AccelerometerEventData{"
				+ "timestamp=" + getTimestamp()
				+ ", x=" + x
				+ ", y=" + y
				+ ", z=" + z
				+ '}';
	}
}
