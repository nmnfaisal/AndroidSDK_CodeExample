package com.noman.behaviorsdk.model;

import androidx.annotation.NonNull;

public final class TouchEventData extends SensorDataEvent
{
	private final int action;
	private final float x;
	private final float y;
	private final float rawX;
	private final float rawY;
	private final float pressure;
	private final float size;
	private final int pointerCount;
	private final long eventTimeMs;

	public TouchEventData(
			long timestamp,
			int action,
			float x,
			float y,
			float rawX,
			float rawY,
			float pressure,
			float size,
			int pointerCount,
			long eventTimeMs)
	{
		super(timestamp, EventType.TOUCH);
		this.action = action;
		this.x = x;
		this.y = y;
		this.rawX = rawX;
		this.rawY = rawY;
		this.pressure = pressure;
		this.size = size;
		this.pointerCount = pointerCount;
		this.eventTimeMs = eventTimeMs;
	}

	public int getAction()
	{
		return action;
	}

	public float getX()
	{
		return x;
	}

	public float getY()
	{
		return y;
	}

	public float getRawX()
	{
		return rawX;
	}

	public float getRawY()
	{
		return rawY;
	}

	public float getPressure()
	{
		return pressure;
	}

	public float getSize()
	{
		return size;
	}

	public int getPointerCount()
	{
		return pointerCount;
	}

	public long getEventTimeMs()
	{
		return eventTimeMs;
	}

	@NonNull
	@Override
	public String toString()
	{
		return "TouchEventData{"
				+ "timestamp=" + getTimestamp()
				+ ", action=" + action
				+ ", x=" + x
				+ ", y=" + y
				+ ", pressure=" + pressure
				+ '}';
	}
}
