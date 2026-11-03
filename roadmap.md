# Roadmap — SpaceTradersKMP

## Stan obecny (2026-11-04)

- **SpaceTradersService** — Spring Boot 3.3 (WebFlux + coroutines, JPA/PostgreSQL). Klient SpaceTraders API v2
  generowany z OpenAPI (`openapi-generator`). 8 kontrolerów / ~55 endpointów będących proxy do API gry
  (agents, contracts, factions, fleet, systems, register, status, auth). Testy integracyjne: Testcontainers
  (PostgreSQL) + MockServer.
- **SpaceTradersUI** — Compose Multiplatform 1.7 (desktop JVM + wasmJs). Na razie tylko animowany splash screen
  i przycisk demo `Click me!`. Brak komunikacji z backendem.
- **Infrastruktura** — `docker-compose` z PostgreSQL + pgAdmin, CI na GitHub Actions (build + testy obu modułów).

## Faza 0 — Porządki w repozytorium

- [x] Migracja GitLab CI na GitHub Actions (`.github/workflows/build.yml`).
- [ ] Zastąpić szablonowy README z GitLaba prawdziwym opisem: architektura, uruchomienie lokalne, wymagania.
- [ ] Dodać do repo `SpaceTradersUI/kotlin-js-store/yarn.lock` (powtarzalne buildy wasm).
- [ ] Usunąć z repo `SpaceTradersUI/.fleet/` i zdecydować, co z `.idea/` (najlepiej dodać do `.gitignore`).
- [ ] Rozwiązać TODO w `utils/Constants.kt` („sprawdzić czy w ten sposób nie będzie wywalać błędów”) testem.

## Faza 1 — Bezpieczeństwo i stabilność backendu (priorytet)

- [ ] **Krytyczne:** `/api/auth/email` i `/api/auth/username` zwracają token agenta każdemu, kto zna e-mail / nazwę.
      Dodać uwierzytelnianie (Spring Security, np. sesja/JWT wydawany po rejestracji) i nie zwracać tokenów gry
      na zewnątrz.
- [ ] Szyfrować tokeny w bazie (obecnie plain text w `SpaceTraderUser.token`).
- [ ] Przenieść dane dostępowe DB z `application.yml` do zmiennych środowiskowych / profili.
- [ ] `AuthService` ładuje wszystkich aktywnych użytkowników i filtruje w pamięci — zamienić na zapytania
      repozytorium (`findByEmailAndIsActiveTrue`, `findByNameAndIsActiveTrue`) + unikalny indeks.
- [ ] Flaga `isActive` liczona tylko przy rejestracji — po resecie serwera gry nigdy nie wygasa. Liczyć ją
      z `resetDate` w zapytaniu albo odświeżać schedulerem po resecie.
- [ ] Blokujące wywołania JPA w WebFlux (`RegisterService.save` bez `Dispatchers.IO`) — opakować spójnie
      albo przejść na R2DBC.
- [ ] Obsługa błędów API gry: `ExceptionControllerAdvice` łapie tylko `NotFound`; 400/401/409/429 kończą się 500.
      Mapować `WebClientResponseException` na właściwe statusy i treść błędu z API.
- [ ] Rate limit SpaceTraders (ok. 2 req/s na agenta) — kolejka/limiter + retry z backoffem dla 429.
- [ ] Migracje schematu (Flyway) zamiast `ddl-auto: update`.
- [ ] Przypiąć wersje obrazów `postgres` (docker-compose, Testcontainers) zamiast `latest`.
- [ ] CORS dla klienta wasm.

## Faza 2 — Aktualizacja zależności

- [ ] Spring Boot 3.3 (koniec wsparcia OSS) → aktualna linia; Kotlin 2.0.21 → aktualny 2.x.
- [ ] Compose Multiplatform 1.7 → aktualny; Gradle wrapper 8.8 → aktualny (oba moduły).
- [ ] Rozważyć Java 21 toolchain.
- [ ] Dependabot dla `gradle` i `github-actions`.

## Faza 3 — Fundamenty klienta UI

- [ ] Klient HTTP (Ktor client + kotlinx.serialization) do własnego backendu.
- [ ] Prosta nawigacja między ekranami (Navigation Compose) i ViewModel/stan per ekran.
- [ ] Usunąć demo `Click me!` — splash screen jako ekran startowy z przejściem do logowania.
- [ ] Wspólny moduł modeli (`shared`, KMP) dla DTO backendu i UI — tylko jeśli duplikacja zacznie boleć.
- [ ] Motyw graficzny (kolory/czcionka QuickSilver już są) i obsługa stanów ładowania/błędu.

## Faza 4 — Funkcje gry w UI

- [ ] Rejestracja agenta i logowanie.
- [ ] Dashboard agenta: kredyty, siedziba, frakcja, liczba statków.
- [ ] Kontrakty: lista, akceptacja, dostawa, realizacja.
- [ ] Flota: lista statków, szczegóły, dock/orbit, nawigacja, tankowanie, ładownia.
- [ ] Systemy i waypointy: lista + prosta mapa 2D systemu (Canvas).
- [ ] Rynki i stocznie: ceny, kupno/sprzedaż towarów, zakup statków.
- [ ] Wydobycie/survey/refine dla statków z odpowiednimi modułami.

## Faza 5 — Automatyzacja (boty)

- [ ] Scheduler w backendzie (coroutines) dla powtarzalnych zadań, z poszanowaniem cooldownów i rate limitu.
- [ ] Pętla wydobycia: survey → extract → sprzedaż/dostawa do kontraktu.
- [ ] Automatyczna realizacja kontraktów.
- [ ] Zapisywanie danych rynkowych do bazy i prosty wybór tras handlowych.
- [ ] Podgląd i sterowanie botami z UI.

## Faza 6 — Wdrożenie i dystrybucja

- [ ] Dockerfile dla serwisu (np. `bootBuildImage`) i `docker-compose` z całym stackiem.
- [ ] Publikacja obrazu do GHCR z GitHub Actions.
- [ ] Build wasm i publikacja na GitHub Pages.
- [ ] Paczki desktop (Dmg/Msi/Deb) budowane na tagach jako GitHub Release (macOS/Windows/Linux runners).

## Faza 7 — Jakość i obserwowalność

- [ ] Lint (ktlint lub detekt) jako krok CI.
- [ ] Raport pokrycia (Kover) i upload raportów testów jako artefakt CI.
- [ ] Testy UI (Compose UI test) dla kluczowych ekranów.
- [ ] Metryki Actuator/Micrometer (opóźnienia API gry, 429), structured logging.
