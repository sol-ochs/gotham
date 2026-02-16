# Gotham

Gotham is a native Android app (Kotlin) that tracks NYC parking tickets via the NYC Open Data API. Users can register up to 5 vehicles, receive notifications when new tickets appear, and get reminded about unpaid tickets.

## Features

- Track up to 5 vehicles
- Daily background checks at 8am
- Push notifications for new tickets
- Periodic unpaid reminders
- Payment deadline reminders
- Offline support with local caching

## Build Commands

```bash
./gradlew assembleDebug         # Debug build
./gradlew assembleRelease       # Release build (requires keystore.properties)
./gradlew installDebug          # Build and install on connected device
./gradlew test                  # Run unit tests
```

## Architecture

Clean Architecture with MVVM:

- **domain/** - Business logic: models (Ticket, Vehicle), repository interfaces, use cases organized by feature (vehicle/, ticket/, sync/)
- **data/** - Data layer: Room database (local/), Retrofit API client (remote/), repository implementations, WorkManager workers
- **presentation/** - UI layer: Jetpack Compose screens, ViewModels with State objects, navigation
- **di/** - Hilt dependency injection modules

## Key Technical Details

- **UI**: 100% Jetpack Compose with Material3, dark mode only
- **Database**: Room
- **Networking**: Retrofit + Moshi for NYC Open Data API (30s timeout)
- **Background sync**: WorkManager schedules daily ticket checks
- **Preferences**: DataStore for user settings
- **Release signing**: Configured via `keystore.properties`

## Debugging

View logs for the app:
```bash
adb logcat -c && adb logcat --pid=$(adb shell pidof com.aurox.gotham) -v color
```

Create test tickets (debug builds only):
```bash
adb shell am broadcast -n com.aurox.gotham/.debug.DebugTicketReceiver \
  -a com.aurox.gotham.debug.TEST_TICKET \
  --es plate "ABC1234" --ei ticket_count 2 --ef fine_amount 115.0 --es violation "'FIRE HYDRANT'"
```

Trigger unpaid reminder notification (debug builds only):
```bash
adb shell "am broadcast -n com.aurox.gotham/.debug.DebugTicketReceiver -a com.aurox.gotham.debug.TRIGGER_UNPAID_REMINDER"
```

Trigger deadline reminder notification (debug builds only):
```bash
adb shell "am broadcast -n com.aurox.gotham/.debug.DebugTicketReceiver -a com.aurox.gotham.debug.TRIGGER_DEADLINE_REMINDER"
```

## License

MIT
