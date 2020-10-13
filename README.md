# Android Coding Task

# App Architecture - MVVM (Model View View Model)
The app is based on **MVVM** Architecture.
The app consists of four layers:
1. **Presentation layer** - The UI layer
2. **Domain layer** - Domain model and Repositories.
3. **Data layer** - Local storage and Network Clients.
4. **Application layer** - Android infrastructure specific components (DI, NetworkStateMonitor).


# Dependencies:

General

1. **Android Framework** - Android API 30 Planform.
2. **Kotlin stdlib** - version 1.4.10.

3-rd party

1. **androidx.appcompat** - Support library for compatibility of different Android SDK versions.
2. **androidx.constraintlayout** - To create more optimized xml layouts.
3. **androidx.recyclerview** - To create lists
4. **androidx.fragment** - To avoid boilerplate code using Fragment (for example lazy viewModel initialization)
5. **androidx.core-ktx** - Useful extensions over the Android Framework
6. **android.room** - SQLite ORM from Google - used to make work with SQLite database more convenient and robust.
7. **google.gson** - Used to make parsing *from* and *to* Json more convenient and robust. 
8. **com.squareup.retrofit2** - A type-safe HTTP client used to make work with network more convenient and robust. 
9. **org.jetbrains.kotlinx:kotlinx-coroutines** - Library from JetBrains for asynchronous and non-blocking programming. Used to simplify and make more efficient work with background tasks and asynchronous jobs 


#Token

To make requests to Twitter API is required to have an Auth token. The token can be obtained after the creation account on https://developer.twitter.com/en. When the token is generated it has to be put in token.properties from where will be injected in the API requests header.

