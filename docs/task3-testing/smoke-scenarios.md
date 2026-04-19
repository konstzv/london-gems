# UI Smoke Scenarios

## Scenario 1: Feed loads and displays recommendations
1. Open the app
2. Wait for data to load (pull-to-refresh if needed)
3. Verify: recommendation cards appear in the feed
4. Verify: each card shows title, category chip, score, subreddit tag
5. Verify: "Last synced" text appears at bottom

## Scenario 2: Category filtering
1. Open the app, wait for feed to load
2. Tap "Food & Drinks" category chip
3. Verify: only food-related recommendations shown
4. Tap "All" to clear filter
5. Verify: all recommendations shown again

## Scenario 3: Recommendation detail + favorite
1. Open the app, wait for feed to load
2. Tap on any recommendation card
3. Verify: detail screen shows title, body, subreddit, score
4. Tap the heart icon (favorite)
5. Verify: heart icon fills in (is now favorited)
6. Press back
7. Navigate to Favorites tab
8. Verify: the favorited recommendation appears

## Scenario 4: Mark as done
1. Open a recommendation detail
2. Tap "Mark as done"
3. Verify: button changes to "Done" with filled check icon
4. Go to Favorites tab → filter by "Done"
5. Verify: the item appears in Done filter

## Scenario 5: Open on Reddit
1. Open a recommendation detail
2. Tap "Open on Reddit"
3. Verify: browser opens with correct Reddit URL (https://www.reddit.com/r/...)
4. Verify: URL is NOT doubled
