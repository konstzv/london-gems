# Task 2 Results: Agent Profiles

## Profiles Created

### 1. Bug Fix
- **Auto-triggers on**: bug, fix, crash, broken, error, exception, NPE, regression
- **Stages**: Reproduce → Diagnose → Fix → Verify → Report
- **Key rules**: must trace full code path, minimal fix only, max 3 files changed

### 2. Research
- **Auto-triggers on**: how does, what uses, explain, where is, which files, trace
- **Stages**: Understand → Investigate → Synthesize → Report
- **Key rules**: read-only, no speculation, file:line references required

### 3. Feature
- **Auto-triggers on**: add, create, implement, new screen, feature, endpoint
- **Stages**: Research → Plan → Implement → Verify → Report
- **Key rules**: reuse existing code, no duplication, follow conventions

## Test Results

### Bug Fix Test
- **Bug injected**: Category filter returns all items regardless of selection
- **Location**: `RecommendationRepositoryImpl.kt:30` — both branches called `dao.getAll()`
- **Result**: PASS — found root cause and fixed in one attempt, one file, one line
- **Log**: `bugfix-test-log.md`

### Research Test
- **Question**: "How does the sync flow work end-to-end?"
- **Result**: PASS — traced full path from FeedViewModel.init through UseCase → Repository → API → DTO mapping → Room → Flow → UI. Identified 3 improvement areas (silent errors, sequential API calls, no WorkManager).
- **Log**: `research-test-log.md`

## What Was Iterated

### First draft → Final version
- Added auto-detection trigger keywords (Gladkov pattern) — removed need to explicitly select profile
- Added Feature profile as third profile (covers the most common daily task)
- Moved profiles into CLAUDE.md instead of separate files — always in context, no manual loading
- Added stage names (Reproduce, Diagnose, Fix, Verify, Report) — forces structured workflow even under pressure

## Approach: CLAUDE.md vs Separate Files

Chose CLAUDE.md integration over separate agent files because:
- Always loaded — no extra commands to type
- Auto-detect by keywords — zero friction
- Same session context — profiles can reference project conventions above them
- Trade-off: can't hard-restrict tools (Research *could* edit, but is instructed not to)
