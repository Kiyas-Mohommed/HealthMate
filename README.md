# HealthMate: Personal Health and Wellness Tracker

A native Android app for tracking daily steps, calories burned and water intake.
Built for CI6330 Mobile Application Development, Coursework 1.

## Features
- Add, view, edit and delete daily health records
- Search records by date
- Dashboard showing today's totals
- Input validation and success messages
- Data stored locally with Room (SQLite), kept after the app is closed

## Tech stack
- Kotlin and Android Studio
- Jetpack Compose with Material 3
- MVVM architecture (ViewModel + StateFlow)
- Room (SQLite) for the `health_records` table
- Navigation Compose
- Kotlin Coroutines and Flow

## Project structure
- `data/` : Room entity, DAO, database, repository, dummy data
- `viewmodel/` : HealthViewModel
- `ui/screens/` : Dashboard, Record List, Add/Edit screens
- `ui/theme/` : colours and typography

## Run the app
1. Clone this repository
2. Open it in Android Studio
3. Let Gradle sync, then run on a device or emulator (Android 7.0 / API 24 or higher)

## Author
Kiyas Mohommed, BSc (Hons) Software Engineering, Kingston University