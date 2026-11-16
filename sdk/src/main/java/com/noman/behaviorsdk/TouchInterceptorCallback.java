package com.noman.behaviorsdk;

import android.os.SystemClock;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.KeyboardShortcutGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import com.noman.behaviorsdk.model.TouchEventData;

final class TouchInterceptorCallback implements Window.Callback
{
	private final Window.Callback delegate;
	private final EventStore eventStore;

	TouchInterceptorCallback(@NonNull Window.Callback delegate, @NonNull EventStore eventStore)
	{
		this.delegate = delegate;
		this.eventStore = eventStore;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event)
	{
		if (event != null)
		{
			long epochMs = System.currentTimeMillis()
					- (SystemClock.uptimeMillis() - event.getEventTime());

			TouchEventData data = new TouchEventData(
					epochMs,
					event.getActionMasked(),
					event.getX(),
					event.getY(),
					event.getRawX(),
					event.getRawY(),
					event.getPressure(),
					event.getSize(),
					event.getPointerCount(),
					event.getEventTime());

			eventStore.add(data);
		}
		return delegate.dispatchTouchEvent(event);
	}

	// --- Every remaining Window.Callback method delegates to the original ---

	@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		return delegate.dispatchKeyEvent(event);
	}

	@Override
	public boolean dispatchKeyShortcutEvent(KeyEvent event)
	{
		return delegate.dispatchKeyShortcutEvent(event);
	}

	@Override
	public boolean dispatchTrackballEvent(MotionEvent event)
	{
		return delegate.dispatchTrackballEvent(event);
	}

	@Override
	public boolean dispatchGenericMotionEvent(MotionEvent event)
	{
		return delegate.dispatchGenericMotionEvent(event);
	}

	@Override
	public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event)
	{
		return delegate.dispatchPopulateAccessibilityEvent(event);
	}

	@Nullable
	@Override
	public View onCreatePanelView(int featureId)
	{
		return delegate.onCreatePanelView(featureId);
	}

	@Override
	public boolean onCreatePanelMenu(int featureId, @NonNull Menu menu)
	{
		return delegate.onCreatePanelMenu(featureId, menu);
	}

	@Override
	public boolean onPreparePanel(int featureId, @Nullable View view, @NonNull Menu menu)
	{
		return delegate.onPreparePanel(featureId, view, menu);
	}

	@Override
	public boolean onMenuOpened(int featureId, @NonNull Menu menu)
	{
		return delegate.onMenuOpened(featureId, menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, @NonNull MenuItem item)
	{
		return delegate.onMenuItemSelected(featureId, item);
	}

	@Override
	public void onWindowAttributesChanged(WindowManager.LayoutParams attrs)
	{
		delegate.onWindowAttributesChanged(attrs);
	}

	@Override
	public void onContentChanged()
	{
		delegate.onContentChanged();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		delegate.onWindowFocusChanged(hasFocus);
	}

	@Override
	public void onAttachedToWindow()
	{
		delegate.onAttachedToWindow();
	}

	@Override
	public void onDetachedFromWindow()
	{
		delegate.onDetachedFromWindow();
	}

	@Override
	public void onPanelClosed(int featureId, @NonNull Menu menu)
	{
		delegate.onPanelClosed(featureId, menu);
	}

	@Override
	public boolean onSearchRequested()
	{
		return delegate.onSearchRequested();
	}

	@Override
	public boolean onSearchRequested(SearchEvent searchEvent)
	{
		return delegate.onSearchRequested(searchEvent);
	}

	@Nullable
	@Override
	public ActionMode onWindowStartingActionMode(ActionMode.Callback callback)
	{
		return delegate.onWindowStartingActionMode(callback);
	}

	@Nullable
	@Override
	public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type)
	{
		return delegate.onWindowStartingActionMode(callback, type);
	}

	@Override
	public void onActionModeStarted(ActionMode mode)
	{
		delegate.onActionModeStarted(mode);
	}

	@Override
	public void onActionModeFinished(ActionMode mode)
	{
		delegate.onActionModeFinished(mode);
	}

	@Override
	public void onProvideKeyboardShortcuts(
			List<KeyboardShortcutGroup> data, @Nullable Menu menu, int deviceId)
	{
		delegate.onProvideKeyboardShortcuts(data, menu, deviceId);
	}

	@Override
	public void onPointerCaptureChanged(boolean hasCapture)
	{
		delegate.onPointerCaptureChanged(hasCapture);
	}
}
