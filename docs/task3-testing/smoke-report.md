# Smoke Test Report — ADB-driven UI Testing

**Date**: 2026-04-19
**Device**: Samsung R3CY408DB0J (1080x2340)
**Method**: ADB commands (input tap, screencap, uiautomator dump)

## Results

| # | Scenario | Result | Screenshot |
|---|----------|:------:|-----------|
| 1 | Feed loads with recommendations | PASS | 01-feed-loaded.png |
| 2 | Category filter (Food & Drinks) | PASS | 02-food-filter.png |
| 3 | Detail screen + Favorite toggle | PASS | 03-detail-screen.png, 04-favorited.png, 05-favorites-tab.png |
| 4 | Mark as done + Done filter | PASS | 07-detail-for-done.png, 08-marked-done.png, 09-done-filter.png |
| 5 | Open on Reddit (correct URL) | PASS | 10-reddit-browser.png |

**5/5 scenarios passed.**

## Scenario Details

### Scenario 1: Feed loads
- Launched app via `adb shell am start`
- Waited 3 seconds for data load
- Verified: recommendation cards visible with titles, category chips, scores, subreddit tags
- "Last synced just now" text visible

### Scenario 2: Category filtering
- Used `uiautomator dump` to find "Food & Drinks" chip bounds: [48,398][483,542]
- Tapped center (265, 470)
- Verified: chip highlighted, only Food & Drinks items shown ("Dishoom", "old restaurant in The Strand")
- Non-food items ("Culture & Museums", "Other") no longer visible

### Scenario 3: Detail + Favorite
- Tapped first card → detail screen opened
- Verified: title, body, subreddit (r/london), score (50), all buttons present
- Tapped heart icon at (996, 278) → icon filled pink
- Pressed back → navigated to Favorites tab
- Verified: "Dishoom special keychain" appeared in Favorites with All/Done/Not Done segments

### Scenario 4: Mark as done
- Opened Dishoom from Favorites
- Tapped "Mark as done" at (579, 1524) → button changed to "Done" with filled check icon
- Pressed back → tapped "Done" filter segment
- Verified: Dishoom appeared under Done filter

### Scenario 5: Open on Reddit
- Opened Dishoom detail
- Tapped "Open on Reddit" at (288, 1704)
- Browser opened: www.reddit.com/r/london with correct "Dishoom special keychain" post
- URL NOT doubled — fix from task 2 bug fix verified

## Approach Notes

- Used `uiautomator dump` to find exact element coordinates (no coordinate guessing)
- Took screenshots at each step via `adb exec-out screencap`
- Agent-driven: all taps and screenshots automated via ADB, no manual interaction
- Limitation: ADB tap uses screen coordinates, which are device-specific (1080x2340)
