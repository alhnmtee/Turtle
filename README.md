# Word Game Android Application

## Overview
This is a multiplayer word guessing game built for Android using Kotlin. Players can compete against each other in real-time to guess words, with various game modes and features.

## Features

### Authentication
- Email/Password login system
- Google Sign-In integration
- User registration functionality
- Secure authentication using Firebase

### Game Modes
1. **Normal Mode**
   - Classic word guessing gameplay
   - Players can freely input their guesses

2. **Random Mode**
   - Words are randomly selected from a predefined dictionary
   - Both players try to guess the same word

3. **Letter Mode**
   - Similar to random mode but with a predetermined letter in a specific position
   - Adds an extra strategic element to gameplay

### Word Length Options
- Support for different word lengths (4-7 letters)
- Players can choose their preferred word length before starting a game

### Multiplayer Features
- Real-time gameplay using WebSocket connections
- Player matching system
- Game request and acceptance mechanism
- Score tracking
- In-game chat functionality

### Game Mechanics
- Color-coded feedback system:
  - Green: Correct letter in correct position
  - Yellow: Correct letter in wrong position
  - Gray: Letter not in word
- Score calculation based on correct guesses
- Time limits for each round
- Win/lose conditions tracking

## Technical Stack

### Frontend
- Kotlin
- Android Jetpack Compose for UI
- View Binding
- Navigation Component
- Material Design components

### Backend
- Ktor for WebSocket server
- Firebase Authentication
- Firebase Firestore for user data

### Architecture
- MVVM (Model-View-ViewModel) architecture
- Repository pattern
- Dependency injection using Hilt
- Coroutines for asynchronous operations

## Setup and Installation

1. Clone the repository
2. Configure Firebase:
   - Add your `google-services.json` file
   - Enable Authentication and Firestore
3. Update WebSocket server URL in `KtorRealtimeMessagingClient.kt`
4. Build and run the project

## Project Structure

```
├── app/
│   ├── data/               # Data layer (Repository, API clients)
│   ├── di/                 # Dependency injection modules
│   ├── fields/             # UI components for game fields
│   ├── plugins/            # Ktor server plugins
│   └── ui/                 # UI layer (Activities, Fragments)
```

## Dependencies

- Firebase Auth
- Firebase Firestore
- Ktor Client
- Jetpack Compose
- Hilt
- Navigation Component
- Material Components
- kotlinx.serialization
- kotlinx.coroutines

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.
