# Wizzar - Advanced Weather Forecast Application ⛅️ 🌩️

Wizzar is a feature-rich, modern Android weather application designed to provide accurate, real-time weather forecasts, interactive map locations, and customizable severe weather alerts. It features a beautiful, dynamic glassmorphic UI built entirely with Jetpack Compose.

This project was developed applying **Clean Architecture** principles and the **MVVM** pattern, utilizing the latest Android recommended tech stack.

## 📱 Features

* **Real-Time Weather Tracking**: Get the current weather status, temperature, humidity, pressure, and wind speed for your GPS location.
* **Comprehensive Forecasts**: View detailed hourly forecasts for the next 24 hours and daily forecasts for the next 5 days.
* **Interactive Maps**: Pick specific locations using the interactive map (powered by OSMDroid) or search via auto-complete to add them to your favorites.
* **Favorite Locations**: Save multiple cities and seamlessly switch between them to track their weather conditions.
* **Robust Weather Alerts**: Set custom alarms for specific conditions (rain, snow, extreme temperatures, high winds). Choose between:
    * *Standard Notifications* (powered by `WorkManager`)
    * *Full-Screen Loud Alarms* (powered by `AlarmManager` for severe weather waking)
* **Deep Customization**:
    * **Units**: Switch between Kelvin, Celsius, and Fahrenheit.
    * **Wind Speed**: Choose between meters/sec or miles/hour.
    * **Language**: Full support for English and Arabic with dynamic localized number formatting.
* **Dynamic UI**: The app background dynamically changes between a starry night and a sunny day based on the sunset/sunrise times of the tracked location, complete with smooth glassmorphic UI components.

## 🛠 Tech Stack & Architecture

Wizzar is built using cutting-edge Android development tools and libraries:

* **Language**: [Kotlin](https://kotlinlang.org/) (100%)
* **UI Toolkit**: [Jetpack Compose](https://developer.android.com/jetpack/compose)
* **Architecture**: MVVM (Model-View-ViewModel) + Clean Architecture (Presentation, Domain, Data layers)
* **Dependency Injection**: [Dagger Hilt](https://dagger.dev/hilt/)
* **Asynchrony**: [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & [Flow](https://kotlin.org/api/kotlinx.coroutines/kotlinx-coroutines-core/kotlinx.coroutines.flow/-flow/)
* **Networking**: [Retrofit 2](https://square.github.io/retrofit/) + OkHttp3 Logging Interceptor
* **Local Persistence**: 
    * [Room Database](https://developer.android.com/training/data-storage/room) (Caching forecasts, favorites, and alerts)
    * [Preferences DataStore](https://developer.android.com/topic/libraries/architecture/datastore) (User settings)
* **Background Processing**: [WorkManager](https://developer.android.com/topic/libraries/architecture/workmanager) & `AlarmManager`
* **Location Services**: Google Play Services (FusedLocationProviderClient)
* **Maps**: [OSMDroid](https://github.com/osmdroid/osmdroid)

## 🧪 Testing

The application includes a comprehensive suite of Unit and Integration tests to ensure stability and accuracy:
* **Frameworks**: JUnit4, [MockK](https://mockk.io/) (for mocking dependencies).
* **Flow Testing**: [Turbine](https://github.com/cashapp/turbine) for elegant testing of StateFlows and SharedFlows.
* **Assertions**: [Google Truth](https://truth.dev/) for readable assertions.
* **Database Testing**: In-memory Room databases for isolating DAO tests.
* **Coroutines Testing**: `kotlinx-coroutines-test` and custom `MainDispatcherRule`.

## 🚀 Getting Started

To build and run this project locally, follow these steps:

### Prerequisites
1. Android Studio (Latest version recommended).
2. An OpenWeatherMap API Key. You can get a free key from [OpenWeatherMap](https://openweathermap.org/api).

### Installation
1. Clone the repository:
   ```bash
   git clone [https://github.com/MinaWagdy228/Weather-App.git](https://github.com/MinaWagdy228/Weather-App.git)
2. Open the project in Android Studio.
3. Create a file named local.properties in the root directory of the project (if it doesn't already exist).
4. Add your OpenWeather API key to the local.properties file:
   WEATHER_API_KEY=your_api_key_here
   (Note: The build.gradle.kts file is configured to automatically inject this key into the BuildConfig during compilation).
5. Sync the Gradle files and run the app on an emulator or physical device running Android 7.0 (API 24) or higher.

📂 Project Structure Overview:

com.example.wizzar
│
├── core/             # Schedulers (AlarmManager/WorkManager), Workers, Notifications, Receivers
├── data/             # Repositories implementations, Local (Room, DataStore), Remote (Retrofit, DTOs), Mappers
├── di/               # Dagger Hilt Modules (NetworkModule, DatabaseModule, RepositoryModule, etc.)
├── domain/           # UseCases, Domain Models, Repository Interfaces, Utilities (Result, UnitConverter)
└── presentation/     # Jetpack Compose Screens, ViewModels, UI Themes, Navigation Graph, Common Utils

✔️ MVVM and Clean Architecture properly implemented.
✔️ Integrated with OpenWeatherMap data/2.5/forecast API.
✔️ Utilized Retrofit, Room, Coroutines, and WorkManager.
✔️ Complete Unit Testing coverage for DAOs, ViewModels, and Repositories.
✔️ High usability, stable performance, and user-friendly UX.

👤 Author
Mina Wagdy

GitHub: @MinaWagdy228
