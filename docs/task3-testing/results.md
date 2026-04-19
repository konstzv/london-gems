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

### Level 2: ADB-Driven Smoke Tests on Real Device

5 smoke scenarios executed by the agent via ADB on a connected Samsung device (1080x2340):

| # | Scenario | Result | Screenshots |
|---|----------|:------:|------------|
| 1 | Feed loads with recommendations | PASS | 01-feed-loaded.png |
| 2 | Category filter (Food & Drinks) | PASS | 02-food-filter.png |
| 3 | Detail screen + Favorite toggle | PASS | 03, 04, 05 |
| 4 | Mark as done + Done filter | PASS | 07, 08, 09 |
| 5 | Open on Reddit (correct URL) | PASS | 10-reddit-browser.png |

**5/5 passed.** Full report with details: `smoke-report.md`

**Method:** Agent used ADB commands to drive the device autonomously:
- `uiautomator dump` → find exact element coordinates (no guessing)
- `input tap X Y` → tap buttons, chips, cards
- `screencap` → capture proof at each step
- `Read` tool → visually verify each screenshot

### Full Cycle Integration

A **Testing profile** was added to CLAUDE.md that ties both levels together:

```
User says: "run tests" / "прогони тесты" / "задеплоил новую фичу"
    ↓
🧪 Profile: Testing activates
    ↓
Stage 1: Unit tests (./gradlew test)
    ↓
Stage 2: Smoke tests via ADB (all scenarios from smoke-scenarios.md)
    ↓
Stage 3: If failures → diagnose (trace code path, identify file:line)
    ↓
Stage 4: Unified report (unit + smoke + failures + recommendations)
```

**Special flow: "I deployed a new feature"**
1. Agent reads recent git changes (`git log`, `git diff HEAD~1`)
2. Identifies new UI features from the diff
3. Adds new smoke scenarios to `smoke-scenarios.md`
4. Runs full cycle (all existing + new scenarios)
5. Reports which scenarios were added and results

### After-PR Flow

When a developer finishes a PR, they say "run tests" and the agent:
1. Runs unit tests → reports pass/fail
2. Installs latest APK on device
3. Runs all smoke scenarios via ADB with screenshots
4. If anything fails → traces the code path and suggests file:line fix
5. Produces a single unified report in `docs/task3-testing/test-report-<date>.md`

## Files Created

```
app/src/test/java/com/londongemsapp/
├── domain/classifier/KeywordCategoryClassifierTest.kt    (18 tests)
├── data/remote/RedditDtoMapperTest.kt                    (17 tests)
└── data/repository/RecommendationRepositoryImplTest.kt   (12 tests)

app/src/androidTest/java/com/londongemsapp/
└── FeedSmokeTest.kt                                      (5 Compose UI tests)

docs/task3-testing/
├── smoke-scenarios.md          (5 scenario definitions)
├── smoke-report.md             (ADB test run report)
├── results.md                  (this file)
└── screenshots/                (10 screenshots from smoke run)
    ├── 01-feed-loaded.png
    ├── 02-food-filter.png
    ├── 03-detail-screen.png
    ├── 04-favorited.png
    ├── 05-favorites-tab.png
    ├── 07-detail-for-done.png
    ├── 08-marked-done.png
    ├── 09-done-filter.png
    └── 10-reddit-browser.png

CLAUDE.md → Testing profile added with:
  - ADB quick reference
  - Full cycle stages
  - Failure diagnosis flow
  - "New feature deployed" scenario
```

## Limitations & Notes

- Unit tests: agent cannot run Gradle from Claude Code sandbox (macOS TCP restriction). User must run `./gradlew test` and paste output, or run from separate terminal
- Smoke tests: agent CAN run these via ADB — fully autonomous
- ADB coordinates are device-specific (1080x2340) — `uiautomator dump` recalculates per device
- No MCP used — ADB approach works without additional setup
- Smoke scenarios 3-5 depend on network data being available (Reddit API)
