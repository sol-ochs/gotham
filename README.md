# This Is Fine

Android app that monitors NYC parking tickets for your vehicles and notifies you when new ones appear.

## Features

- Track up to 5 vehicles
- Background checks every 6 hours
- Push notifications for new tickets
- Offline support with local caching

## Setup

1. Clone and open in Android Studio
2. (Optional) Get a free API token at [NYC Open Data](https://data.cityofnewyork.us/) for higher rate limits. Add to `local.properties`:
   ```
   NYC_API_TOKEN=your_token_here
   ```
3. Build and run

## Debugging

### Viewing Logs

```bash
adb logcat -c && adb logcat --pid=$(adb shell pidof com.gotham.app) -v color
```

### Sending Test Notifications

Debug builds include a BroadcastReceiver for testing notifications via ADB:

```bash
adb shell am broadcast -n com.gotham.app/.debug.DebugNotificationReceiver \
  -a com.gotham.app.debug.TEST_NOTIFICATION \
  --ei ticket_count 2 --ef fine_amount 115.0 --es violation "FIRE HYDRANT"
```
Optional parameters:

| Param        | Type   | Default                    | Description              |
|--------------|--------|----------------------------|--------------------------|
| ticket_count | int    | 1                          | Number of tickets (1-10) |
| fine_amount  | float  | 65.0                       | Fine amount in dollars   |
| violation    | string | NO PARKING-STREET CLEANING | Violation type           |

## Tech Stack

- Kotlin
- Jetpack Compose (UI)
- Room (database)
- Retrofit (networking)
- WorkManager (background tasks)
- Hilt (dependency injection)

## License

MIT
