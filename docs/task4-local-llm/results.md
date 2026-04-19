# Task 4 Results: Local LLM Comparison

## Hardware

| Spec | Value |
|------|-------|
| Chip | Apple M4 Pro |
| Cores | 14 (10P + 4E) |
| RAM | 48 GB |
| Ollama | v0.14.3 |

## Models Tested

| Model | Size | VRAM Usage | Pull Command |
|-------|------|-----------|-------------|
| Qwen 2.5 Coder 32B | ~20GB | fits in 48GB | `ollama pull qwen2.5-coder:32b` |
| DeepSeek Coder V2 16B | ~10GB | fits easily | `ollama pull deepseek-coder-v2:16b` |
| CodeLlama 34B | ~20GB | fits in 48GB | `ollama pull codellama:34b` |

## Test Task

Same prompt used for Day 1 Gen 1 vs Gen 2 comparison:
> Add a WorkManager periodic sync worker that fetches recommendations from Reddit API and upserts them into the Room database. Include Hilt injection, error handling, and retry policy.

Full CLAUDE.md was provided as system prompt context for all models.

## Comparison Table

| Criteria | Claude (cloud) | Qwen 32B | DeepSeek 16B | CodeLlama 34B |
|----------|:-:|:-:|:-:|:-:|
| Correct package name | ✅ | | | |
| Uses existing architecture | ✅ | | | |
| Delegates to UseCase | ✅ | | | |
| Uses DataResult | ✅ | | | |
| Correct entity model | ✅ | | | |
| Kotlinx Serialization | ✅ | | | |
| @HiltWorker + @AssistedInject | ✅ | | | |
| NetworkType.CONNECTED | ✅ | | | |
| Retry with backoff | ✅ | | | |
| Naming conventions | ✅ | | | |
| No anti-patterns | ✅ | | | |
| **Score** | **11/11** | **/11** | **/11** | **/11** |
| **Time to generate** | ~15s | | | |

## Observations

> Fill in after running each model

### Qwen 2.5 Coder 32B
- Quality:
- Speed:
- Context understanding:
- Followed CLAUDE.md rules?

### DeepSeek Coder V2 16B
- Quality:
- Speed:
- Context understanding:
- Followed CLAUDE.md rules?

### CodeLlama 34B
- Quality:
- Speed:
- Context understanding:
- Followed CLAUDE.md rules?

## Verdict

| Use Case | Recommendation |
|----------|---------------|
| Complex features (multi-file, architecture) | Cloud (Claude) |
| Simple completions, boilerplate | Local (best model TBD) |
| Offline / air-gapped | Local (best model TBD) |
| Sensitive code (can't leave machine) | Local |
| Speed-critical autocomplete | Local |

## Continue.dev Setup

Config at `~/.continue/config.json`:
```json
{
  "models": [
    {
      "title": "Qwen 2.5 Coder 32B",
      "provider": "ollama",
      "model": "qwen2.5-coder:32b",
      "systemMessage": "<paste CLAUDE.md contents here>"
    }
  ],
  "tabAutocompleteModel": {
    "title": "Qwen Autocomplete",
    "provider": "ollama",
    "model": "qwen2.5-coder:7b"
  },
  "customCommands": [
    {
      "name": "test",
      "description": "Run the Testing profile",
      "prompt": "Profile: Testing. Run all tests and produce a unified report."
    }
  ]
}
```

**Recommended parameters for code generation:**
- `temperature`: 0.1 (low = deterministic for code)
- `top_p`: 0.9
- `num_ctx`: 32768 (32k context window)
