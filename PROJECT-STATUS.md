# Project status

## Completed (2026-11-04)

- CI migrated from GitLab CI to GitHub Actions: `.github/workflows/build.yml` (matrix: SpaceTradersService, SpaceTradersUI; `./gradlew build`). GitLab `.gitlab-ci.yml` files removed.
- `roadmap.md` added (phases 0–7).

## Decisions

- Workflow triggers: push to `main` + every pull request (GitLab ran on every push).
- No Docker-in-Docker service: `ubuntu-latest` has Docker, Testcontainers works directly.
- Gradle caching via `gradle/actions/setup-gradle`, JDK 17 Temurin.

## Next steps

- Faza 1 roadmapy: secure `/api/auth/*` (returns game tokens unauthenticated).
- Commit `SpaceTradersUI/kotlin-js-store/yarn.lock`, replace GitLab README template.
- First workflow run on GitHub has not been observed yet — check Actions tab after push.
