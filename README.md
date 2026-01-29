# Gotham

An app to track your NYC parking tickets and get notify you when new ones appear.

## Features

- Track up to 5 vehicles
- Background checks every 6 hours
- Push notifications for new tickets
- Offline support with local caching

## Debugging

### Viewing Logs

```bash
adb logcat -c && adb logcat --pid=$(adb shell pidof com.gotham.app) -v color
```

### Creating Test Tickets

Debug builds include a BroadcastReceiver for creating test tickets via ADB. Requires a vehicle created in the app.

```bash
adb shell am broadcast -n com.gotham.app/.debug.DebugTicketReceiver \
  -a com.gotham.app.debug.TEST_TICKET \
  --es plate "ABC1234" --ei ticket_count 2 --ef fine_amount 115.0 --es violation "FIRE HYDRANT"
```

Parameters:

| Param        | Type   | Required | Default                    | Description                          |
|--------------|--------|----------|----------------------------|--------------------------------------|
| plate        | string | Yes      | -                          | License plate of an existing vehicle |
| ticket_count | int    | No       | 1                          | Number of tickets (1-10)             |
| fine_amount  | float  | No       | 65.0                       | Fine amount in dollars               |
| violation    | string | No       | NO PARKING-STREET CLEANING | Violation type                       |

This inserts new tickets into the database for the specified vehicle, then shows a notification.

## Tech Stack

- Kotlin
- Jetpack Compose (UI)
- Room (database)
- Retrofit (networking)
- WorkManager (background tasks)
- Hilt (dependency injection)

## License

MIT
