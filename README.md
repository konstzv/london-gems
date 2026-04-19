# London Gems

An Android app that curates London recommendations from Reddit. It aggregates posts from subreddits like r/london and r/LondonSocialClub, classifies them into categories (Food & Drinks, Events, Parks & Nature, Culture & Museums, Nightlife, Hidden Gems), and presents them in a browsable feed.

## Features

- Reddit-sourced recommendations with automatic category classification
- Offline-first architecture with Room database
- Favorite and mark-as-done tracking
- Pull-to-refresh with background sync via WorkManager
- Category filtering with recommendation counts
- Relative timestamps and Reddit deep links

## Build

```bash
./gradlew assembleDebug
```

Requires Android SDK with min API 26 (Android 8.0).

## Architecture

The project follows MVVM + Clean Architecture with three layers:

```
domain/          Pure Kotlin business logic (models, use cases, repository interfaces)
data/            Data sources (Reddit API via Retrofit, Room database, mappers)
presentation/    Jetpack Compose UI (screens, view models, theme, components)
di/              Hilt dependency injection modules
```

Key technologies: Kotlin, Jetpack Compose, Material 3, Hilt, Retrofit, Room, Coroutines + Flow, Coil 3, WorkManager.
