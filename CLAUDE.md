# London Gems — Project Rules

## Stack (locked — do not substitute)

| Layer | Technology |
|-------|-----------|
| Language | Kotlin 2.1.x |
| UI | Jetpack Compose + Material 3 |
| DI | Hilt |
| Network | Retrofit + OkHttp + Kotlinx Serialization |
| Local DB | Room |
| Async | Coroutines + Flow |
| Architecture | MVVM + Clean Architecture (data / domain / presentation) |
| Navigation | Compose Navigation with @Serializable routes |
| Image loading | Coil 3 |
| Background work | WorkManager + hilt-work |
| Build | Gradle KTS + version catalog (libs.versions.toml) |
| Min SDK | 26 / Target SDK 35 |

## Architecture

```
data/
  remote/       → Retrofit API, DTOs, interceptors, mappers
  local/        → Room database, DAOs, entities, converters
  repository/   → Repository implementations
domain/
  model/        → Pure Kotlin data classes + enums (NO Android imports)
  classifier/   → CategoryClassifier interface + implementations
  repository/   → Repository interfaces
  usecase/      → Use case classes with @Inject + operator invoke()
presentation/
  navigation/   → Routes + NavHost
  feed/         → FeedScreen + FeedViewModel
  detail/       → DetailScreen + DetailViewModel
  favorites/    → FavoritesScreen + FavoritesViewModel
  settings/     → SettingsScreen
  components/   → Shared composables (RecommendationCard, CategoryChip, EmptyState)
  theme/        → Color, Type, Theme
di/             → Hilt modules (NetworkModule, DatabaseModule, RepositoryModule, AppModule)
```

## Naming Conventions

- **Packages**: lowercase, no underscores: `com.londongemsapp.data.remote`
- **Files**: PascalCase matching class name: `FeedViewModel.kt`, `RecommendationDao.kt`
- **Composables**: PascalCase function name matching file: `RecommendationCard.kt` → `fun RecommendationCard()`
- **ViewModels**: `<Screen>ViewModel` — `FeedViewModel`, `DetailViewModel`
- **UseCases**: `<Action><Entity>UseCase` — `GetRecommendationsUseCase`, `ToggleFavoriteUseCase`
- **Entities**: `<Name>Entity` for Room, plain name for domain: `RecommendationEntity` vs `Recommendation`
- **DTOs**: `<Name>Dto` — `RedditPostDto`, `RedditListingResponse`
- **State**: `UiState<T>` sealed interface with Loading, Success, Error
- **Routes**: `@Serializable` sealed interface members: `Route.Feed`, `Route.Detail(placeId)`

## Patterns — How We Write Code

### Use Cases: operator invoke, single responsibility
```kotlin
class GetRecommendationsUseCase @Inject constructor(
    private val repository: RecommendationRepository
) {
    operator fun invoke(category: Category? = null): Flow<List<Recommendation>> =
        repository.getRecommendations(category)
}
```

### ViewModel: StateFlow, WhileSubscribed, use cases not repositories directly
```kotlin
@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getRecommendations: GetRecommendationsUseCase,
    private val syncRecommendations: SyncRecommendationsUseCase,
    private val repository: RecommendationRepository
) : ViewModel() {

    val uiState: StateFlow<UiState<List<Recommendation>>> = _selectedCategory
        .flatMapLatest { category -> getRecommendations(category) }
        .map { recommendations ->
            if (recommendations.isEmpty() && lastSyncTimestamp.value == null) {
                UiState.Loading
            } else {
                UiState.Success(recommendations)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = UiState.Loading
        )
}
```

### Composable: Modifier parameter, testTag, stateless
```kotlin
@Composable
fun RecommendationCard(
    recommendation: Recommendation,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("recommendation_card"),
    ) { /* ... */ }
}
```

### DI: @Binds for interfaces, @Provides for third-party
```kotlin
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds @Singleton
    abstract fun bindRecommendationRepository(
        impl: RecommendationRepositoryImpl
    ): RecommendationRepository
}
```

### Repository: offline-first, catch per-source, map entities to domain
```kotlin
override suspend fun syncFromReddit(): DataResult<Int> =
    try {
        val allPosts = buildList {
            addAll(fetchSubreddit("london"))
            addAll(fetchSubreddit("LondonSocialClub"))
        }
        val entities = mapper.mapToRecommendations(allPosts).map { it.toEntity() }
        dao.upsertFromNetwork(entities)
        DataResult.Success(entities.size)
    } catch (e: Exception) {
        DataResult.Error(e)
    }
```

## Anti-Patterns (FORBIDDEN)

### 1. NO `Any` types
```kotlin
// BAD
fun process(data: Any): Any

// GOOD
fun process(data: Recommendation): DataResult<Recommendation>
```

### 2. NO Android imports in domain layer
```kotlin
// BAD — domain/usecase/SyncUseCase.kt
import android.util.Log

// GOOD — domain layer is pure Kotlin, only javax.inject and kotlinx allowed
```

### 3. NO direct repository calls from Composables
```kotlin
// BAD
@Composable
fun FeedScreen(repository: RecommendationRepository) {
    val data by repository.getRecommendations().collectAsState()
}

// GOOD — go through ViewModel
@Composable
fun FeedScreen(viewModel: FeedViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
}
```

### 4. NO println/Log.d in production code
```kotlin
// BAD
Log.d("TAG", "fetched ${posts.size} posts")
println("debug: $result")

// GOOD — use structured error handling via DataResult, or Timber if logging is truly needed
```

### 5. NO hardcoded strings in UI
```kotlin
// BAD
Text(text = "No recommendations found")

// GOOD — use string resources
Text(text = stringResource(R.string.empty_state_message))
```

## File Template

Every new Kotlin file should follow this structure:

```kotlin
package com.londongemsapp.<layer>.<feature>

// 1. Framework imports (android, androidx, compose)
// 2. Third-party imports (dagger, retrofit, coil)
// 3. Project imports (com.londongemsapp.*)
// 4. Kotlin/Java stdlib

// Class/interface/function definition
// - One public class/interface per file
// - Private helpers below the main declaration
// - Extension functions at bottom of file
```

## Build & Run

```bash
JAVA_HOME=$(/usr/libexec/java_home -v 17) ANDROID_HOME=/opt/android_sdk ./gradlew assembleDebug
```

## Key Design Decisions

- **Recommendation, not Place**: data model represents Reddit posts, not physical locations
- **CategoryClassifier interface**: allows swapping keyword-based for LLM-based classification
- **Upsert preserves user flags**: INSERT OR IGNORE + UPDATE of network fields only (never overwrite isFavorite/isDone)
- **OAuth2 installed_client grant**: no client secret in APK, uses device_id
- **Subreddit = category**: r/londonfood → FOOD, r/london → keyword classifier fallback
