# Execution Loop — Task Pool

## Format
- [ ] = not started
- [~] = in progress
- [x] = done
- [!] = failed

---

## Bugs

- [x] **T01** [Bug] Double URL in "Open on Reddit" — DetailScreen.kt:148 prepends base URL to already-full URL. Fix: use `recommendation.url` directly. **Done when**: tapping "Open on Reddit" opens correct single URL.

- [x] **T02** [Bug] Feed shows loading spinner forever on first launch with no network — UiState stays Loading even when sync fails. **Done when**: shows error/empty state instead of infinite spinner when offline.

- [x] **T03** [Bug] Category chips don't show "All" option — no way to clear filter and see all categories again. **Done when**: "All" chip appears first in the row and clears the category filter.

- [x] **T04** [Bug] Favorite heart icon in top bar has no ripple/click feedback — looks like it's not clickable. **Done when**: heart icon button shows click ripple effect.

## Small Features

- [x] **T05** [Feature] Add pull-to-refresh indicator to Feed screen — currently no visual feedback during refresh. **Done when**: SwipeRefresh or PullToRefreshBox shows spinner while syncing.

- [x] **T06** [Feature] Show recommendation count in category chips — e.g. "Food & Drinks (12)". **Done when**: each chip shows count of items in that category.

- [x] **T07** [Feature] Add "About" section in Settings screen — show app version, GitHub link. **Done when**: Settings screen shows app version from BuildConfig.

- [x] **T08** [Feature] Add timestamp formatting — show "2h ago" or "3 days ago" instead of raw timestamp. **Done when**: cards show relative time like "2h ago".

## Refactoring

- [x] **T09** [Refactor] Extract hardcoded strings from DetailScreen to strings.xml — "Open on Reddit", "Share", "Mark as done", "Done", "From r/". **Done when**: all user-facing strings use stringResource().

- [x] **T10** [Refactor] Move entity↔domain mapping extensions from RecommendationRepositoryImpl.kt to a separate Mappers.kt file. **Done when**: mappers in their own file, repository imports them.

- [x] **T11** [Refactor] Replace raw SharedPreferences in RecommendationRepositoryImpl with a dedicated SyncPreferences wrapper class. **Done when**: repository uses SyncPreferences instead of raw SharedPreferences.

## Missing Tests

- [x] **T12** [Test] Add unit test for CategoryConverter — test fromCategory and toCategory with all Category enum values. **Done when**: test file exists, covers all 7 enum values.

- [x] **T13** [Test] Add unit test for ToggleFavoriteUseCase and ToggleDoneUseCase — verify they delegate to repository. **Done when**: test files exist with passing tests.

- [x] **T14** [Test] Add edge case tests for KeywordCategoryClassifier — test with very long text, special characters, mixed case keywords. **Done when**: 5+ new edge case tests added.

## Documentation

- [ ] **T15** [Docs] Add KDoc to all public interfaces in domain/repository/RecommendationRepository.kt — document each method's purpose and return type. **Done when**: every public method has a one-line KDoc.

- [ ] **T16** [Docs] Create a README.md for the project — app description, screenshots, build instructions, architecture overview. **Done when**: README.md exists with all 4 sections.

## Advanced

- [ ] **T17** [Feature] Add search bar to Feed screen — local filter by title text using a TextField. **Done when**: search bar filters visible recommendations by title.

- [ ] **T18** [Feature] Add "Clear all favorites" button to Favorites screen with confirmation dialog. **Done when**: button exists, shows confirmation, clears all favorites.

- [ ] **T19** [Bug] RedditAuthInterceptor is still in the codebase but unused after switching to public JSON endpoint — remove dead code. **Done when**: RedditAuthInterceptor.kt deleted, no references remain.

- [ ] **T20** [Refactor] Make sync interval configurable from Settings screen — store preference, pass to WorkManager. **Done when**: Settings has sync interval picker, value persisted.

## UI/Design

- [ ] **T21** [Bug] Thumbnail images are low quality and pixelated — Reddit returns tiny default thumbnails (140px). Fix: parse `preview.images[0].source.url` from Reddit JSON for full-resolution images instead of `thumbnail` field. Update RedditPostDto, RedditDtoMapper, and RecommendationCard to use high-res images. **Done when**: card images are sharp, not pixelated.

- [ ] **T22** [Feature] Modern card design — redesign RecommendationCard with: rounded corners (16dp), subtle gradient overlay on image, title overlaid on bottom of image (white text on dark gradient), category chip as small pill on top-left corner of image, remove separate text section below image. **Done when**: cards look modern like Instagram/Pinterest style with text on image.

- [ ] **T23** [Feature] Add shimmer loading placeholders — show animated shimmer cards while feed is loading instead of plain CircularProgressIndicator. Use Compose animation, no external library. **Done when**: loading state shows 3-4 shimmer card placeholders.

- [ ] **T24** [Feature] Dark/Light theme toggle in Settings — add switch that persists theme choice. Currently follows system only. **Done when**: Settings has theme toggle (System/Light/Dark), choice persists across app restarts.

- [ ] **T25** [Feature] Animated transitions between screens — add shared element transitions or fade/slide animations when navigating from Feed to Detail and back. **Done when**: screen transitions have visible animation instead of instant swap.
