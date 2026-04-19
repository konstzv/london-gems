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
./gradlew assembleDebug
```

## Profiles (auto-detect by task type)

Every request is handled through ONE profile. Detect automatically by keywords.

**IMPORTANT**: When you detect a profile, announce it as the FIRST line of your response:
> 🔧 Profile: Bug Fix | Stage 1: Reproduce

Update the stage announcement as you progress through each stage.

| Trigger keywords | Profile |
|-----------------|---------|
| bug, fix, crash, broken, error, не работает, exception, NPE, regression | Bug Fix |
| how does, what uses, explain, where is, which files, покажи, research, trace | Research |
| add, create, implement, new screen, feature, endpoint, интеграция | Feature |

### Profile: Bug Fix

**Stages** (follow in order, do not skip):

1. **Reproduce** — Read the bug description. Search for relevant files by keywords. Identify the code path. Document expected vs actual behavior.

2. **Diagnose** — Trace the full path: UI → ViewModel → UseCase → Repository → Data Source. Check `git log --oneline -10 -- <file>` for recent changes. Identify ROOT CAUSE, not symptoms.

3. **Fix** — Minimal change only. Do NOT refactor, add features, or touch unrelated files. Follow project conventions from this file.

4. **Verify** — Check the fix compiles. Search for other callers of changed code — ensure no regressions. If bug was in data layer, verify Entity ↔ Domain mapping consistency.

5. **Report** — Output:
```
## Bug Fix Report
### Bug: <one-line>
### Root Cause: <file:line — what was wrong>
### Fix: <what changed and why>
### Files Modified: <list>
### Verification: compiles / no regressions / follows conventions
```

**Rules**: MUST read full code path before fixing. MUST NOT change more than 3 files. MUST NOT add comments explaining the fix.

### Profile: Research

**Stages** (follow in order, do not skip):

1. **Understand** — Classify the question: architecture (trace flow), coverage (scan gaps), dependency (trace callers), comparison (read both).

2. **Investigate** — Read files systematically following imports. For architecture questions: trace UI → ViewModel → UseCase → Repository → Data Source. Read at least 5 files before forming conclusions.

3. **Synthesize** — Organize findings. Include file paths and line numbers for every claim. Answer the question directly first, then details.

4. **Report** — Output:
```
## Research Report: <question>
### Answer: <2-3 sentences>
### Detailed Findings: <with file:line references>
### File Map: <files examined and their role>
### Observations: <gaps, risks, inconsistencies found>
```

**Rules**: MUST NOT modify any code. MUST NOT speculate — only report what's verifiable in the code. MUST include file:line for every claim.

### Profile: Feature

**Stages** (follow in order, do not skip):

1. **Research** — Read this CLAUDE.md. Read existing code in the area being modified. Understand the patterns already in use.

2. **Plan** — List the files to create/modify. Verify the plan follows project architecture (correct layer, correct package).

3. **Implement** — Write code following all conventions from this file. Use existing patterns (UseCases, DataResult, UiState). Reuse existing code — do not duplicate.

4. **Verify** — Check all imports resolve. Check DI graph is complete. Check new code integrates with existing navigation/screens.

5. **Report** — Output:
```
## Feature Report
### Feature: <one-line>
### Files Created: <list with purpose>
### Files Modified: <list with what changed>
### Integration: <how it connects to existing code>
```

**Rules**: MUST reuse existing code (UseCases, Repository, DTOs). MUST NOT duplicate existing classes. MUST NOT add dependencies not in libs.versions.toml.

## Key Design Decisions

- **Recommendation, not Place**: data model represents Reddit posts, not physical locations
- **CategoryClassifier interface**: allows swapping keyword-based for LLM-based classification
- **Upsert preserves user flags**: INSERT OR IGNORE + UPDATE of network fields only (never overwrite isFavorite/isDone)
- **OAuth2 installed_client grant**: no client secret in APK, uses device_id
- **Subreddit = category**: r/londonfood → FOOD, r/london → keyword classifier fallback
