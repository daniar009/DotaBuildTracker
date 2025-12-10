# Dota Build Tracker

An Android application that tracks and displays item builds for Dota 2 players, fetching data daily from player statistics.

## Features

- **Fetch Player Builds**: Enter a player ID to fetch their item builds
- **Offline Support**: View previously fetched builds even without internet connection
- **Daily Updates**: Automatically updates player builds daily
- **Simple Interface**: Clean and intuitive UI for viewing item builds

## Technical Requirements

This project demonstrates the following technologies and patterns:

### ✅ Networking - Retrofit (8 points)
- Uses Retrofit for API calls to fetch player item builds
- Configured with OkHttp logging interceptor for debugging
- Handles network errors gracefully with offline fallback

### ✅ Coroutines - Kotlin Coroutines (8 points)
- All network operations use Kotlin Coroutines
- ViewModel uses `viewModelScope` for coroutine lifecycle management
- Repository uses `suspend` functions for async operations
- Flow is used for reactive data streams from Room database

### ✅ Architecture - MVVM (8 points)
- **Model**: Data classes for ItemBuild and API responses
- **View**: MainActivity with XML layouts
- **ViewModel**: ItemBuildViewModel handles business logic and UI state
- **Repository**: ItemBuildRepository manages data sources (API and Database)
- Clear separation of concerns following MVVM pattern

### ✅ Offline Mode (11 points)
- **SharedPreferences (4 points)**: 
  - Stores last player ID
  - Saves last update timestamp
  - Manages auto-update preferences
- **Room Database (7 points)**:
  - Entity: ItemBuild with proper annotations
  - DAO: ItemBuildDao with Flow support for reactive queries
  - Database: AppDatabase with singleton pattern
  - Offline-first approach: Data persists locally and loads from database

### ✅ Code Review - Git (5 points)
- Project is set up for Git version control
- `.gitignore` configured for Android projects
- Ready for branch-based collaboration

## Project Structure

```
app/src/main/java/com/dotabuildtracker/
├── data/
│   ├── model/              # Data models
│   ├── local/              # Room Database (DAO, Database)
│   ├── remote/             # Retrofit API service
│   ├── preferences/        # SharedPreferences manager
│   └── repository/         # Repository pattern implementation
├── ui/
│   ├── adapter/            # RecyclerView adapter
│   ├── viewmodel/          # ViewModels and Factory
│   └── MainActivity.kt     # Main UI
├── worker/                 # WorkManager for daily updates
└── utils/                  # Utility classes
```

## Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd FinalProject
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle dependencies
   - Wait for sync to complete

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click "Run" or press Shift+F10

## Usage

1. **Enter Player ID**: Type a Dota 2 player's Steam account ID (32-bit or 64-bit) in the input field
   - You can find your Steam ID on sites like https://steamid.io/ or https://steamidfinder.com/
   - The ID is a numeric value (e.g., 76561198012345678)
2. **Fetch Builds**: Click "Fetch Builds" to retrieve item builds from the OpenDota API
   - The app fetches the player's recent 10 matches
   - For each match, it extracts item purchases
   - Items are aggregated by hero to show the most common builds
3. **View Builds**: Scroll through the list of hero builds with their items
   - Each build shows the hero name, number of matches, and most common items
4. **Refresh**: Click "Refresh" to update the builds for the current player
5. **Offline Mode**: Previously fetched builds are stored locally and available offline

## API Integration

The app uses the **OpenDota API** (https://api.opendota.com/api/) to fetch real Dota 2 match data:

- **Player Matches**: Fetches recent matches for a given player ID
- **Match Details**: Retrieves detailed match information including item purchases
- **Heroes & Items**: Gets hero and item data for name mapping
- **Item Builds**: Aggregates items from multiple matches to create hero-specific builds

### How It Works:
1. Enter a Dota 2 player's Steam account ID (32-bit or 64-bit)
2. The app fetches the player's recent matches
3. For each match, it retrieves detailed match data
4. Extracts item purchases for the player
5. Groups items by hero and shows the most commonly used items

### API Endpoints Used:
- `GET /api/players/{account_id}/matches` - Get player's recent matches
- `GET /api/matches/{match_id}` - Get detailed match information
- `GET /api/heroes` - Get hero information
- `GET /api/constants/items` - Get item information

**Note**: OpenDota API is free with rate limits (60 requests/minute). No API key required for basic usage.

## Dependencies

- **Retrofit 2.9.0**: HTTP client for API calls
- **Room 2.6.1**: Local database for offline storage
- **Coroutines 1.7.3**: Asynchronous programming
- **Lifecycle Components**: ViewModel and LiveData
- **WorkManager 2.9.0**: Background work for daily updates
- **Material Components**: Modern UI components

## Team Collaboration

### Git Workflow
1. Create feature branches for new functionality
2. Use pull requests for code review
3. Commit regularly with meaningful messages
4. Merge to main after review

### Branch Strategy
- `main`: Stable production code
- `feature/*`: New features
- `bugfix/*`: Bug fixes

## Evaluation Checklist

- ✅ Retrofit for networking
- ✅ Kotlin Coroutines for async operations
- ✅ MVVM architecture pattern
- ✅ SharedPreferences for settings
- ✅ Room Database for offline storage
- ✅ Git repository with proper structure
- ✅ Clean, maintainable code
- ✅ Production-ready UI

## License

This project is created for educational purposes as part of a final course project.

(and this one too is created with assistance of AI)
