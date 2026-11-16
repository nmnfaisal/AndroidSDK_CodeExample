package com.noman.behaviorsdk.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TouchEventDataTest
{
	@Test
	public void constructorSetsAllFields()
	{
		TouchEventData event = new TouchEventData(
				1000L, 1, 10.5f, 20.5f, 100.0f, 200.0f, 0.8f, 0.3f, 2, 500L);

		assertEquals(1000L, event.getTimestamp());
		assertEquals(1, event.getAction());
		assertEquals(10.5f, event.getX(), 0.001f);
		assertEquals(20.5f, event.getY(), 0.001f);
		assertEquals(100.0f, event.getRawX(), 0.001f);
		assertEquals(200.0f, event.getRawY(), 0.001f);
		assertEquals(0.8f, event.getPressure(), 0.001f);
		assertEquals(0.3f, event.getSize(), 0.001f);
		assertEquals(2, event.getPointerCount());
		assertEquals(500L, event.getEventTimeMs());
	}

	@Test
	public void typeIsTouch()
	{
		TouchEventData event = new TouchEventData(
				1000L, 0, 0f, 0f, 0f, 0f, 0f, 0f, 1, 1000L);

		assertEquals(EventType.TOUCH, event.getType());
	}

	@Test
	public void toStringContainsKeyFields()
	{
		TouchEventData event = new TouchEventData(
				1000L, 2, 15.0f, 25.0f, 0f, 0f, 0.5f, 0f, 1, 1000L);

		String str = event.toString();
		assertTrue(str.contains("timestamp=1000"));
		assertTrue(str.contains("action=2"));
		assertTrue(str.contains("x=15.0"));
		assertTrue(str.contains("y=25.0"));
		assertTrue(str.contains("pressure=0.5"));
	}
}
