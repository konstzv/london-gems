# Bug Fix Report: Category filter on Feed screen doesn't work

## Bug Description
Selecting a category chip on the Feed screen (e.g., "Food & Drinks") does not filter recommendations. All recommendations continue to appear regardless of which category is selected.

## Root Cause
**File:** `app/src/main/java/com/londongemsapp/data/repository/RecommendationRepositoryImpl.kt`
**Lines:** 28-34

The `getRecommendations(category)` method had a logic error: both branches of the `if/else` called `dao.getAll()`, ignoring the `category` parameter entirely.

```kotlin
// BEFORE (broken)
val entityFlow = if (category != null) {
    dao.getAll()       // <-- should call dao.getByCategory(category)
} else {
    dao.getAll()
}
```

The DAO already had the correct `getByCategory(category: Category)` method with a proper Room `@Query` filtering by category. It was simply never called.

## Fix Applied
Changed the `category != null` branch to call `dao.getByCategory(category)` instead of `dao.getAll()`.

```kotlin
// AFTER (fixed)
val entityFlow = if (category != null) {
    dao.getByCategory(category)
} else {
    dao.getAll()
}
```

## Code Path Traced
1. `FeedScreen.kt` -- user taps category chip, calls `viewModel.selectCategory(category)`
2. `FeedViewModel.kt` -- `selectCategory()` updates `_selectedCategory` MutableStateFlow, which triggers `flatMapLatest { category -> getRecommendations(category) }`
3. `GetRecommendationsUseCase.kt` -- delegates to `repository.getRecommendations(category)`
4. `RecommendationRepository.kt` -- interface defines `getRecommendations(category: Category?)`
5. `RecommendationRepositoryImpl.kt` -- **BUG HERE** -- was calling `dao.getAll()` for both branches
6. `RecommendationDao.kt` -- `getByCategory(category)` exists with correct Room query: `SELECT * FROM recommendations WHERE category = :category`

## Verification
- Type safety: `dao.getByCategory(category)` receives `Category` (non-null, guaranteed by the `if` check), matching the DAO signature `fun getByCategory(category: Category): Flow<List<RecommendationEntity>>`
- No other callers affected: `getRecommendations` is only called from `GetRecommendationsUseCase`, and the interface contract is unchanged
- Return type unchanged: both `dao.getAll()` and `dao.getByCategory()` return `Flow<List<RecommendationEntity>>`, so the downstream `.map { it.toDomain() }` continues to work

## Files Changed
- `app/src/main/java/com/londongemsapp/data/repository/RecommendationRepositoryImpl.kt` (1 line changed)
