# Test Prompt for Local LLM Comparison

## The Prompt (same for cloud and local)

You are working on the London Gems Android project. Here are the project rules:

<paste contents of CLAUDE.md here>

**Task:** Add a WorkManager periodic sync worker that fetches recommendations from Reddit API and upserts them into the Room database. Include Hilt injection, error handling, and retry policy.

## Evaluation Criteria

| # | Criteria | Weight | How to Check |
|---|----------|--------|-------------|
| 1 | Correct package name (`com.londongemsapp`) | CRITICAL | Grep for package declaration |
| 2 | Uses existing architecture (no duplicate classes) | CRITICAL | Count new files — should be ≤4 |
| 3 | Delegates to UseCase (not repo directly) | HIGH | Check what SyncWorker.doWork() calls |
| 4 | Uses project's `DataResult` sealed class | HIGH | Check import for DataResult |
| 5 | Correct entity model (matches existing) | HIGH | No new Entity/DAO files |
| 6 | Uses Kotlinx Serialization (not Moshi/Gson) | MEDIUM | Check imports |
| 7 | `@HiltWorker` + `@AssistedInject` | MEDIUM | Check annotations |
| 8 | NetworkType.CONNECTED constraint | MEDIUM | Check WorkManager constraints |
| 9 | Retry policy with backoff | MEDIUM | Check backoff configuration |
| 10 | Follows naming conventions | LOW | File/class naming |
| 11 | No anti-patterns used | LOW | No Log.d, no Any, no hardcoded strings |

**Scoring**: CRITICAL = 2pts, HIGH = 1.5pts, MEDIUM = 1pt, LOW = 0.5pt. Max = 13pts.

## Models to Test

1. **Qwen 2.5 Coder 32B** — `ollama pull qwen2.5-coder:32b`
2. **DeepSeek Coder V2 16B** — `ollama pull deepseek-coder-v2:16b`
3. **CodeLlama 34B** — `ollama pull codellama:34b`

## How to Test

For each model:
1. Start fresh conversation in Continue.dev (or Ollama CLI)
2. Paste the full CLAUDE.md as system prompt
3. Paste the task prompt
4. Save the generated output to `docs/task4-local-llm/gen-<model-name>/`
5. Score against the criteria table
6. Note: time to generate, quality observations
