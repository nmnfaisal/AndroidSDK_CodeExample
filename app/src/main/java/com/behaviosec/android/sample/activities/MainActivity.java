package com.behaviosec.android.sample.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.behaviosec.android.sample.R;
import com.noman.behaviorsdk.BehaviorSdk;
import com.noman.behaviorsdk.model.EventType;
import com.noman.behaviorsdk.model.SensorDataEvent;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
	private static final String TAG = "MainActivity";

	private AppCompatButton logoutButton;
	private AppCompatButton showDataButton;
	private TextView dataSummaryText;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		logoutButton = findViewById(R.id.logout_button);
		logoutButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				finishActivity();
			}
		});

		showDataButton = findViewById(R.id.show_data_button);
		dataSummaryText = findViewById(R.id.data_summary_text);

		showDataButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				displayCollectedData();
			}
		});
	}

	private void displayCollectedData()
	{
		List<SensorDataEvent> events = BehaviorSdk.getCollectedData();

		int total = events.size();
		int touchCount = 0;
		int accelCount = 0;

		// Largest gap between consecutive accelerometer events. Normal foreground
		// collection stays near the sensor interval (~20ms at 50Hz); a large value
		// means the sensor stopped for a while, e.g. the app was backgrounded.
		long lastAccelMs = -1;
		long maxAccelGapMs = 0;

		for (int i = 0; i < total; i++)
		{
			SensorDataEvent event = events.get(i);

			if (event.getType() == EventType.TOUCH)
			{
				touchCount++;
			}
			else
			{
				accelCount++;

				if (lastAccelMs != -1)
				{
					long gap = event.getTimestamp() - lastAccelMs;
					if (gap > maxAccelGapMs)
					{
						maxAccelGapMs = gap;
					}
				}
				lastAccelMs = event.getTimestamp();
			}
		}

		String timeSpan = "N/A";
		if (total >= 2)
		{
			long firstMs = events.get(0).getTimestamp();
			long lastMs = events.get(total - 1).getTimestamp();
			long spanMs = lastMs - firstMs;
			timeSpan = String.format("%.1fs", spanMs / 1000.0);
		}

		String summary = "Total events: " + total
				+ "\nTouch events: " + touchCount
				+ "\nAccel events: " + accelCount
				+ "\nTime span: " + timeSpan
				+ "\nMax accel gap: " + maxAccelGapMs + "ms";

		dataSummaryText.setText(summary);

		// Log last 5 events for debugging
		int start = Math.max(0, total - 5);
		for (int i = start; i < total; i++)
		{
			Log.d(TAG, "Event[" + i + "]: " + events.get(i).toString());
		}
	}

	@Override
	public void onBackPressed()
	{
		finishActivity();
	}

	private void finishActivity()
	{
		Intent intent = new Intent(this, StartActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		startActivity(intent);
	}
}