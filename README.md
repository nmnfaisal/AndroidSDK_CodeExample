# Behavior Sensor Collection SDK

An Android SDK that collects touch and accelerometer events and returns them merged and sorted by time. It's built for behavioral biometrics, where the timing and rhythm of input matters as much as the input itself.

## Approach

I kept this small on purpose. The brief says it's a starting point for a discussion, not a finished product, so I didn't try to build everything.

Two things took most of my thinking time. First, collecting every touch without asking the host app to forward anything. I ended up wrapping the `Window.Callback`, since that's the one spot every touch passes through before any view gets to consume it, and I install it per Activity so the host only has to call two methods. Second, the timestamps. Touch and accelerometer events come from two different system clocks, so I had to convert each one onto a common timeline instead of just stamping them when they arrive. Stamping on arrival would get the timing between events wrong, and for behavioral biometrics that timing is the whole point.

The rest (the store, lifecycle handling, the API) I kept plain and standard. Where I traded something off for simplicity, I wrote down what I'd do differently in production instead of building it now.

## Integration

Add the module as a dependency:

```groovy
dependencies {
    implementation project(':sdk')
}
```

Initialize and start in your `Application`:

```java
public class MyApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        BehaviorSdk.init(this);
        BehaviorSdk.start();
    }
}
```

Read the data whenever you need it, from any thread:

```java
List<SensorDataEvent> events = BehaviorSdk.getCollectedData();
```

## Public API

| Method | Description | Before `init()` |
|--------|-------------|-----------------|
| `init(Application)` | Set up the SDK. Safe to call more than once. | — |
| `start()` | Begin collecting. | throws `IllegalStateException` |
| `stop()` | Stop collecting and unregister listeners. | throws `IllegalStateException` |
| `getCollectedData()` | All events, sorted by time. | returns empty list |
| `getEventCount()` | Number of stored events. | returns 0 |
| `clear()` | Drop all stored events. | throws `IllegalStateException` |
| `isCollecting()` | Whether collection is active. | returns false |

Read methods are lenient (they return sensible defaults before init), because a host querying early shouldn't crash. Lifecycle methods are strict (they throw), because calling them before `init()` is a programmer mistake worth surfacing loudly.

## Data model

```
SensorDataEvent (abstract, Comparable)
├── TouchEventData          action, x, y, rawX, rawY, pressure, size, pointerCount
└── AccelerometerEventData  x, y, z (m/s²)
```

All event classes are immutable. Events sort by timestamp ascending, with touch before accelerometer when two share the same millisecond, so the output is deterministic. Each event also keeps its original hardware timestamp alongside the normalized one, in case the host or a server wants finer precision.

## Design decisions

**Touch collection via `Window.Callback`.** `dispatchTouchEvent` on the window is the one place every touch passes through before any view can consume it. A touch listener on the root view would miss most real touches, since a button or list consumes its own touch and it never reaches the root. The wrapper installs automatically in `onActivityCreated`, with an `instanceof` guard so a configuration change (which recreates the Activity) doesn't wrap it twice. It implements all of `Window.Callback` and passes every call through to the original, so the host app behaves exactly as before. Because a wrapper can’t be safely removed once installed (another library may have wrapped it afterwards), stop() gates recording with a flag rather than restoring the original callback. That also means start() resumes touch collection immediately.

**Timestamp normalization.** Touch events use `uptimeMillis` and sensor events use `elapsedRealtimeNanos`. These are different clocks with different units, so they can't be compared directly. I convert each event's own timestamp to wall-clock epoch millis ("now, minus how long ago this event happened on its own clock"). This keeps the true spacing between events, and because I use each event's own timestamp rather than the moment the callback fired, batched sensor readings keep their real timing instead of collapsing together.

**Thread safety.** Writes come from the main thread (touch and sensor callbacks), but `getCollectedData()` can be called from any thread, so `add()` and the read both lock on the same object. That's required, not a nicety: a background read copying the list while the main thread adds to it would otherwise risk a `ConcurrentModificationException`. The list is sorted on read, not on write, so writes stay cheap and the one sort happens only on the occasional read.

**Foreground-only accelerometer.** I count started/stopped activities to tell whether the app is on screen, and register the sensor only while it is. I use started/stopped rather than resumed/paused because during a screen change the next Activity starts before the previous one stops, so the count never briefly hits zero and the sensor isn't needlessly torn down and re-registered. Sampling is `SENSOR_DELAY_GAME` (~50Hz), enough to capture small movements without the battery cost of the fastest rate.

## Testing

I tested this by hand on an emulator and a real device, mostly checking the cases my own design could break:

- Rotating the screen: touches keep getting captured afterwards, and the event count doesn't double. That was the main thing I wanted to confirm, since recreating the Activity could have wrapped the callback twice.
- Moving between the three screens: the accelerometer stream has no gap during transitions. I checked this by looking at the largest gap between two consecutive accelerometer events, which stays tiny.
- Sending the app to the background: the accelerometer stops (the gap jumps to roughly how long I left it backgrounded) and picks back up when I return.
- Reading from a background thread: I called `getCollectedData()` in a tight loop from another thread while dragging my finger to push a lot of writes through at once, and nothing threw.
- A device with no accelerometer: it still collects touches and doesn't crash.
- The host app still behaves normally: dialogs and the back button work, so the callback wrapping isn't getting in the way.
- Verified that stop() halts both streams and start() resumes them.
There are unit tests for the store (sorting, the tie-break, the defensive copy, concurrent add and read) and the data models.

## What I'd add for production

- **Bounded memory.** The store is an unbounded list today; a ring buffer with a configurable cap would stop it growing over long sessions.
- **Off-main-thread collection and upload.** Move sensor callbacks to a background thread and stream batches to a backend with retry. This is the real product path.
- **Multi-pointer capture.** Currently the primary pointer only; production would iterate all active pointers.
- **Configuration.** A builder for sampling rate, which sensors to collect, and buffer size.

## Dependencies

```groovy
implementation 'androidx.annotation:annotation:1.5.0'
testImplementation 'junit:junit:4.13.2'
```

No UI or lifecycle libraries. Keeping the dependency footprint near zero means nothing to conflict with in the host app.

## Build & run

```bash
./gradlew :sdk:assembleDebug    # build the SDK
./gradlew :sdk:test             # run unit tests
./gradlew assembleDebug         # build the app with the SDK
```