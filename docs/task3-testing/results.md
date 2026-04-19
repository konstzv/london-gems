# Task 3 Results: Testing

## What Was Done

### Level 1: Unit Tests on Business Logic

Agent scanned the project, found zero test coverage, and wrote tests for 3 modules:

| Test File | Module Tested | Tests | What's Covered |
|-----------|--------------|:-----:|----------------|
| `KeywordCategoryClassifierTest.kt` | domain/classifier | 18 | Subreddit mapping, keyword classification per category, confidence threshold (< 0.3 → UNCATEGORIZED), edge cases (empty text, no matches, priority) |
| `RedditDtoMapperTest.kt` | data/remote | 17 | Deleted/removed filtering, empty body filtering, URL construction, thumbnail validation, field mapping, timestamp conversion |
| `RecommendationRepositoryImplTest.kt` | data/repository | 12 | DAO delegation (category filter), entity↔domain mapping, syncFromReddit error handling, toggle methods |
| **Total** | | **47** | |

**Approach:**
- No mocking frameworks (Mockito/MockK) — hand-written fakes and stubs only
- Tests verify real logic, not mock behavior
- Each test follows naming: `methodName_condition_expectedResult`
- Uses JUnit 4 + kotlinx.coroutines.test (both already in build.gradle.kts)

**To run:**
```bash
./gradlew test
```

### Level 2: UI Smoke Scenarios

5 smoke scenarios written as text descriptions + 1 Compose UI Test file:

| Scenario | Flow | Verifies |
|----------|------|----------|
| 1. Feed loads | Open app → wait → cards appear | Data fetching + rendering |
| 2. Category filter | Tap chip → filtered → clear → all | Filter logic end-to-end |
| 3. Detail + favorite | Tap card → detail → heart → Favorites tab | Navigation + favorite toggle + persistence |
| 4. Mark as done | Detail → mark done → Done filter | Toggle done + filter |
| 5. Open on Reddit | Detail → Open on Reddit → browser | Correct URL (no doubling) |

**Compose UI Test file:** `app/src/androidTest/java/com/londongemsapp/FeedSmokeTest.kt`
- 5 instrumented tests using `createAndroidComposeRule`
- Requires `@HiltAndroidTest` for DI
- Tests the golden path: bottom nav visible, category chips visible, card tap opens detail, favorites navigation

**To run:**
```bash
./gradlew connectedAndroidTest
```

## Files Created

```
app/src/test/java/com/londongemsapp/
├── domain/classifier/KeywordCategoryClassifierTest.kt    (18 tests)
├── data/remote/RedditDtoMapperTest.kt                    (17 tests)
└── data/repository/RecommendationRepositoryImplTest.kt   (12 tests)

app/src/androidTest/java/com/londongemsapp/
└── FeedSmokeTest.kt                                      (5 tests)

docs/task3-testing/
├── smoke-scenarios.md                                     (5 scenarios)
└── results.md                                             (this file)
```

## Limitations

- Unit tests were audited against source but not executed (Gradle daemon TCP issue in sandbox)
- Compose UI tests require a connected device/emulator to run
- No CI integration yet — tests must be run manually
- Smoke scenarios 3-5 depend on network data being available (Reddit API)
