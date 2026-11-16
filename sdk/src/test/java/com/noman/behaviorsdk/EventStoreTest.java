package com.noman.behaviorsdk;

import com.noman.behaviorsdk.model.AccelerometerEventData;
import com.noman.behaviorsdk.model.EventType;
import com.noman.behaviorsdk.model.SensorDataEvent;
import com.noman.behaviorsdk.model.TouchEventData;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class EventStoreTest
{
	private EventStore store;

	@Before
	public void setUp() {
		store = new EventStore();
		store.setAccepting(true);
	}

	@Test
	public void emptyStoreReturnsEmptyList()
	{
		List<SensorDataEvent> result = store.getAll();
		assertTrue(result.isEmpty());
		assertEquals(0, store.size());
	}

	@Test
	public void addedEventsAreRetrievable()
	{
		store.add(makeTouch(100));
		store.add(makeAccel(200));

		assertEquals(2, store.size());
		List<SensorDataEvent> result = store.getAll();
		assertEquals(2, result.size());
	}

	@Test
	public void eventsAreSortedByTimestamp()
	{
		store.add(makeTouch(300));
		store.add(makeAccel(100));
		store.add(makeTouch(200));

		List<SensorDataEvent> result = store.getAll();
		assertEquals(100, result.get(0).getTimestamp());
		assertEquals(200, result.get(1).getTimestamp());
		assertEquals(300, result.get(2).getTimestamp());
	}

	@Test
	public void sameTimestampTouchSortsBeforeAccelerometer()
	{
		store.add(makeAccel(100));
		store.add(makeTouch(100));

		List<SensorDataEvent> result = store.getAll();
		assertEquals(EventType.TOUCH, result.get(0).getType());
		assertEquals(EventType.ACCELEROMETER, result.get(1).getType());
	}

	@Test
	public void clearRemovesAllEvents()
	{
		store.add(makeTouch(100));
		store.add(makeAccel(200));
		assertEquals(2, store.size());

		store.clear();

		assertEquals(0, store.size());
		assertTrue(store.getAll().isEmpty());
	}

	@Test
	public void getAllReturnsDefensiveCopy()
	{
		store.add(makeTouch(100));

		List<SensorDataEvent> first = store.getAll();
		assertNotSame(first, store.getAll());

		first.clear();
		assertEquals(1, store.size());
	}

	@Test
	public void concurrentAddAndReadDoesNotThrow() throws InterruptedException
	{
		final int writerCount = 4;
		final int eventsPerWriter = 500;
		final CountDownLatch startLatch = new CountDownLatch(1);
		final CountDownLatch doneLatch = new CountDownLatch(writerCount + 1);
		final AtomicBoolean failed = new AtomicBoolean(false);

		for (int w = 0; w < writerCount; w++)
		{
			final int offset = w * eventsPerWriter;
			new Thread(new Runnable()
			{
				@Override
				public void run()
				{
					try
					{
						startLatch.await();
						for (int i = 0; i < eventsPerWriter; i++)
						{
							store.add(makeTouch(offset + i));
						}
					}
					catch (Exception e)
					{
						failed.set(true);
					}
					finally
					{
						doneLatch.countDown();
					}
				}
			}).start();
		}

		// Reader thread
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					startLatch.await();
					for (int i = 0; i < 100; i++)
					{
						List<SensorDataEvent> snapshot = store.getAll();
						// Verify sorted order on each snapshot
						for (int j = 1; j < snapshot.size(); j++)
						{
							if (snapshot.get(j).compareTo(snapshot.get(j - 1)) < 0)
							{
								failed.set(true);
							}
						}
					}
				}
				catch (Exception e)
				{
					failed.set(true);
				}
				finally
				{
					doneLatch.countDown();
				}
			}
		}).start();

		startLatch.countDown();
		doneLatch.await();

		assertTrue("Concurrent add+read should not throw or produce unsorted output",
				!failed.get());
		assertEquals(writerCount * eventsPerWriter, store.size());
	}

	private static TouchEventData makeTouch(long timestamp)
	{
		return new TouchEventData(timestamp, 0, 0f, 0f, 0f, 0f, 0f, 0f, 1, timestamp);
	}

	private static AccelerometerEventData makeAccel(long timestamp)
	{
		return new AccelerometerEventData(timestamp, 0f, 0f, 9.8f, timestamp * 1_000_000L);
	}
}
