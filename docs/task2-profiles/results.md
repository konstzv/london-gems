# Task 2 Results: Agent Profiles

## What Was Done

### Step 1: Created Bug Fix Profile
- Defined a 5-stage workflow: Reproduce → Diagnose → Fix → Verify → Report
- Each stage has strict instructions on what to do and what NOT to do
- Key constraint: must trace full code path before fixing, max 3 files changed, no refactoring

### Step 2: Created Research Profile
- Defined a 4-stage workflow: Understand → Investigate → Synthesize → Report
- Strictly read-only — cannot modify any code
- Must include file:line references for every claim, no speculation allowed

### Step 3: Created Feature Profile (third custom profile)
- Defined a 5-stage workflow: Research → Plan → Implement → Verify → Report
- Must reuse existing code (UseCases, Repository, DTOs), no duplication allowed

### Step 4: Integrated into CLAUDE.md with Auto-Detection
Instead of separate agent files, profiles were added directly to CLAUDE.md with keyword triggers:

| Trigger keywords | Profile |
|-----------------|---------|
| bug, fix, crash, broken, error, exception, NPE, regression | Bug Fix |
| how does, what uses, explain, where is, which files, trace | Research |
| add, create, implement, new screen, feature, endpoint | Feature |

This means: open any Claude Code session in the project → type your task naturally → Claude auto-detects the right profile from keywords. Zero friction, no special commands needed.

### Step 5: Tested Bug Fix on Real Bug
- **Injected bug**: swapped `dao.getByCategory(category)` to `dao.getAll()` in `RecommendationRepositoryImpl.kt:30` — category filter silently returns all items instead of filtering
- **Bug report given**: "Category filter on Feed screen doesn't work — tapping Food & Drinks still shows ALL recommendations"
- **Result**: PASS on first attempt
  - Agent traced: FeedScreen → FeedViewModel → GetRecommendationsUseCase → RecommendationRepository → RecommendationRepositoryImpl → RecommendationDao
  - Found root cause: both branches of `if (category != null)` called `dao.getAll()`
  - Fixed: one line, one file
  - Verified: checked all callers, no regressions

### Step 6: Tested Research on Real Question
- **Question**: "How does the sync flow work end-to-end? Trace from app launch to data on screen."
- **Result**: PASS on first attempt
  - Agent read 15+ files across all layers
  - Traced complete flow: FeedViewModel.init → SyncRecommendationsUseCase → RecommendationRepositoryImpl.syncFromReddit → RedditApi (4 API calls) → RedditDtoMapper → RecommendationDao.upsertFromNetwork → Room Flow emission → FeedScreen UI update
  - Identified 3 improvement areas: silent error handling, sequential API calls, missing WorkManager
  - All findings backed by file:line references

### Step 7: Tested Bug Fix on Real Bug (user-run, separate session)
- **Bug**: "When I click Open on Reddit it opens the browser but the URL is wrong — it has the base address doubled"
- **Prompt given**: exact bug description, no hints
- **Result**: PASS on first attempt
  - Agent searched for "Open on Reddit" in codebase
  - Found `DetailScreen.kt:148` prepends `https://www.reddit.com` to `recommendation.url`
  - Found `RedditDtoMapper.kt:36` already stores the full URL with base in `recommendation.url`
  - Result: double URL `https://www.reddit.comhttps://www.reddit.com/r/...`
  - Fix: changed `val redditUrl = "https://www.reddit.com${recommendation.url}"` to `val redditUrl = recommendation.url`
  - One line, one file
- **Profile detection**: Claude activated the Bug Fix workflow automatically (detected "broken" / "wrong" keywords) but did NOT display the stage announcements we added — it skipped the emoji prefix format

### Observation: Stage Announcements
After Step 7 we noticed Claude doesn't reliably announce profile stages with the `🔧 Profile: Bug Fix | Stage 1: Reproduce` format. The profile was followed correctly but the visual indicator was inconsistent. This is a limitation of text-based instructions — Claude may compress or skip formatting it considers non-essential.

## Test Logs
- `bugfix-test-log.md` — full Bug Fix agent output (injected bug test)
- `research-test-log.md` — full Research agent output

## How to Use Profiles

Open a new Claude Code session in the project:
```bash
cd ~/projects/london-gems
claude
```

Then just describe your task naturally:
- "The app crashes when I tap a recommendation with no thumbnail" → Bug Fix profile auto-activates
- "How does the favorites flow work?" → Research profile auto-activates
- "Add a search bar to the Feed screen" → Feature profile auto-activates

To force a specific profile, prefix your message:
- "Profile: Bug Fix. <description>"

## What Was Iterated After First Attempt

1. **Added auto-detection keywords** — first version required manually selecting profile. After testing, added keyword trigger table to CLAUDE.md so detection is automatic
2. **Added Feature profile** — originally only had Bug Fix + Research. Added Feature as the third profile because it covers the most common daily task
3. **Moved from separate files to CLAUDE.md** — profiles in separate .md files required manual loading. Embedding in CLAUDE.md means they're always in context
4. **Added stage names** — naming each stage (Reproduce, Diagnose, Fix, Verify, Report) forces structured workflow and prevents skipping steps

## Approach Decision: CLAUDE.md vs Separate Agent Files

| Criteria | CLAUDE.md (chosen) | Separate .claude/agents/ |
|----------|:------------------:|:------------------------:|
| Auto-detection | YES — keyword triggers | NO — must select manually |
| Always loaded | YES | NO — must invoke |
| Can restrict tools | NO | YES — Research can't edit |
| Friction | Zero | Extra step each time |
| Setup | Add text to existing file | Create new files + config |

**Verdict**: CLAUDE.md for daily use. Separate agents only when you need hard tool restrictions.
