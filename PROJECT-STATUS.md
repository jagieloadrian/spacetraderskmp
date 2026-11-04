# Project status

## Completed (2026-11-04)

- CI migrated from GitLab CI to GitHub Actions: `.github/workflows/build.yml` (matrix: SpaceTradersService, SpaceTradersUI; `./gradlew build`). GitLab `.gitlab-ci.yml` files removed.
- `roadmap.md` added, then revised against official docs (getting-started, OpenAPI v2.3.0, rate limits, changelog): phases 0–8.

## Decisions

- Workflow triggers: push to `main` + every pull request (GitLab ran on every push).
- No Docker-in-Docker service: `ubuntu-latest` has Docker, Testcontainers works directly.
- Gradle caching via `gradle/actions/setup-gradle`, JDK 17 Temurin.

## Next steps

- Roadmap phase 1: update local spec v2.0.0 to v2.3.0 — `POST /register` now needs `AccountToken`, `PATCH /my/ships/{shipSymbol}/nav` response changed (both broken against live API).
- Roadmap phase 2: secure `/api/auth/*` (returns game tokens unauthenticated).
- Commit `SpaceTradersUI/kotlin-js-store/yarn.lock`, replace GitLab README template.
- First workflow run on GitHub has not been observed yet — check Actions tab after push.
