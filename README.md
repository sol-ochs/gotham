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

## Tech Stack

- Kotlin
- Jetpack Compose (UI)
- Room (database)
- Retrofit (networking)
- WorkManager (background tasks)
- Hilt (dependency injection)

## License

MIT
