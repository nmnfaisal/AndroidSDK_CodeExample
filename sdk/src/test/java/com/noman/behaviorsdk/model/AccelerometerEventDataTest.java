package com.noman.behaviorsdk.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class AccelerometerEventDataTest
{
	@Test
	public void constructorSetsAllFields()
	{
		AccelerometerEventData event = new AccelerometerEventData(
				2000L, 1.5f, -2.3f, 9.81f, 5000000000L);

		assertEquals(2000L, event.getTimestamp());
		assertEquals(1.5f, event.getX(), 0.001f);
		assertEquals(-2.3f, event.getY(), 0.001f);
		assertEquals(9.81f, event.getZ(), 0.001f);
		assertEquals(5000000000L, event.getSensorTimestampNs());
	}

	@Test
	public void typeIsAccelerometer()
	{
		AccelerometerEventData event = new AccelerometerEventData(
				2000L, 0f, 0f, 9.8f, 0L);

		assertEquals(EventType.ACCELEROMETER, event.getType());
	}

	@Test
	public void compareToOrdersByTimestampThenByType()
	{
		TouchEventData touch = new TouchEventData(
				100L, 0, 0f, 0f, 0f, 0f, 0f, 0f, 1, 100L);
		AccelerometerEventData accelEarlier = new AccelerometerEventData(
				50L, 0f, 0f, 9.8f, 0L);
		AccelerometerEventData accelSameTime = new AccelerometerEventData(
				100L, 0f, 0f, 9.8f, 0L);

		// Earlier timestamp sorts first
		assertTrue(accelEarlier.compareTo(touch) < 0);
		assertTrue(touch.compareTo(accelEarlier) > 0);

		// Same timestamp: TOUCH (ordinal 0) before ACCELEROMETER (ordinal 1)
		assertTrue(touch.compareTo(accelSameTime) < 0);
		assertTrue(accelSameTime.compareTo(touch) > 0);
	}

	@Test
	public void toStringContainsKeyFields()
	{
		AccelerometerEventData event = new AccelerometerEventData(
				2000L, 1.5f, -2.3f, 9.81f, 5000000000L);

		String str = event.toString();
		assertTrue(str.contains("timestamp=2000"));
		assertTrue(str.contains("x=1.5"));
		assertTrue(str.contains("y=-2.3"));
		assertTrue(str.contains("z=9.81"));
	}
}
