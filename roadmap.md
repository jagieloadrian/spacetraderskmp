# Roadmap — SpaceTradersKMP

## Stan obecny (2026-11-04)

- **SpaceTradersService** — Spring Boot 3.3 (WebFlux + coroutines, JPA/PostgreSQL). Klient SpaceTraders API
  generowany z OpenAPI (`openapi-generator`) z lokalnej kopii specu **v2.0.0**. 8 kontrolerów / ~55 endpointów
  będących proxy do API gry. Testy integracyjne: Testcontainers (PostgreSQL) + MockServer.
- **SpaceTradersUI** — Compose Multiplatform 1.7 (desktop JVM + wasmJs). Na razie tylko animowany splash screen
  i przycisk demo `Click me!`. Brak komunikacji z backendem.
- **Infrastruktura** — `docker-compose` z PostgreSQL + pgAdmin, CI na GitHub Actions (build + testy obu modułów).

## Co mówi dokumentacja SpaceTraders (stan na dziś)

Źródła: <https://spacetraders.io/getting-started>, <https://spacetraders.io/openapi>
(`https://spacetraders.io/SpaceTraders.json`), API Guide (authorization, rate limits), Server resets, Changelog.

- **Gra jest „headless”** — nie ma oficjalnego klienta, gracz buduje własny UI / automatyzację. Pomysły z docs:
  skrypt do wydobycia i tras handlowych, aplikacja do zarządzania flotą, śledzenie cen rynkowych, SDK z OpenAPI,
  bot Discord. Ten projekt to „aplikacja do zarządzania flotą + automatyzacja”.
- **Aktualny spec: v2.3.0** (lokalnie v2.0.0). Base URL bez zmian: `https://api.spacetraders.io/v2`.
- **Dwa typy tokenów** (security schemes):
  - `AccountToken` — konto zakładane w dashboardzie gry; służy **tylko** do `POST /register` (tworzenie agentów).
  - `AgentToken` — wydawany przy rejestracji agenta, ważny do najbliższego resetu; wymagany na `/my/**`
    i akcjach; opcjonalny na publicznych (`/`, `/agents`, `/factions`, `/systems/**`).
- **Od 2025-01-25 `POST /register` wymaga `AccountToken`** — rejestracja bez konta już nie działa.
- **Rate limity:** 2 req/s, burst 30 req / 60 s — osobno per IP i per konto (HTTP 429).
  Nagłówki: `x-ratelimit-type`, `-limit`, `-remaining`, `-reset`, `-limit-burst`, `-limit-per-second`.
  Ochrona DDoS zwraca 502.
- **Resety serwera** są regularne (alfa) — wszystkie agenty i tokeny agentów przepadają. Termin następnego resetu
  i częstotliwość są w `GET /` (`resetDate`, `serverResets.next`, `serverResets.frequency`).
- **Zmiany v2.0 → v2.3 istotne dla kodu:**
  - BREAKING: `PATCH /my/ships/{shipSymbol}/nav` zwraca teraz `data: { nav, fuel, events }`, a nie sam `ShipNav`.
  - Nowe endpointy: `GET /market/supply-chain`, `GET /my/ships/{shipSymbol}/modules`,
    `POST /my/ships/{shipSymbol}/modules/install`, `POST /my/ships/{shipSymbol}/modules/remove`
    (nowe kody błędów 4266–4268).
  - `Faction.headquarters` przestało być wymagane; nowe pola `quality` w `ShipEngine/Frame/Reactor`,
    `name` i `constellation` w `System`; nowy typ statku `SHIP_BULK_FREIGHTER`.

## Faza 0 — Porządki w repozytorium

- [x] Migracja GitLab CI na GitHub Actions (`.github/workflows/build.yml`).
- [ ] Zastąpić szablonowy README z GitLaba prawdziwym opisem: architektura, uruchomienie lokalne, wymagania,
      skąd wziąć `AccountToken`.
- [ ] Dodać do repo `SpaceTradersUI/kotlin-js-store/yarn.lock` (powtarzalne buildy wasm).
- [ ] Usunąć z repo `SpaceTradersUI/.fleet/` i zdecydować, co z `.idea/` (najlepiej dodać do `.gitignore`).
- [ ] Rozwiązać TODO w `utils/Constants.kt` („sprawdzić czy w ten sposób nie będzie wywalać błędów”) testem.

## Faza 1 — Zgodność z aktualnym API v2.3.0 (blokujące — część funkcji nie działa z produkcyjnym API)

- [ ] Podmienić `src/main/resources/specification/SpaceTraders.yml` na aktualny spec i przegenerować klienta.
      Testy na MockServerze tego nie złapią — fixtury JSON w `src/test/resources/jsonresponses` są w formacie v2.0,
      trzeba je zaktualizować.
- [ ] **Rejestracja:** `RegisterService` woła `register` bez tokena — produkcyjne API odrzuci to (401).
      Dodać `AccountToken` jako sekret serwisu (zmienna środowiskowa, np. `SPACETRADERS_ACCOUNT_TOKEN`)
      i przekazywać go w `Authorization: Bearer`.
- [ ] **Nawigacja:** dostosować `FleetService.patchShipNav` i kontroler do odpowiedzi `{ nav, fuel, events }`
      (obecny model `ShipNav` nie zdeserializuje nowej odpowiedzi).
- [ ] `Faction.headquarters` opcjonalne — sprawdzić, czy `GET /factions` nie wywala się na frakcjach bez HQ.
- [ ] Nowe endpointy w serwisie: moduły statku (list/install/remove) — analogicznie do mounts,
      z walidacją w `ShipActionValidatorService`; `GET /market/supply-chain`.
- [ ] `nextResetDate` w `/api/register` podawany przez klienta — zastąpić odczytem `serverResets.next` z `GET /`.
- [ ] Mapowanie kodów błędów gry (pole `error.code`, np. 4266–4268) na czytelne odpowiedzi.

## Faza 2 — Bezpieczeństwo i stabilność backendu

- [ ] **Krytyczne:** `/api/auth/email` i `/api/auth/username` zwracają `AgentToken` każdemu, kto zna e-mail / nazwę.
      Dodać uwierzytelnianie użytkowników aplikacji (Spring Security, sesja/JWT) i nie wydawać tokenów gry
      na zewnątrz — serwis trzyma tokeny i sam podpisuje wywołania API gry.
- [ ] Obecnie każdy endpoint `/my/**` przyjmuje token gry w nagłówku `token` od klienta — po wprowadzeniu
      logowania brać token agenta z bazy na podstawie zalogowanego użytkownika.
- [ ] Szyfrować tokeny w bazie (obecnie plain text w `SpaceTraderUser.token`); `AccountToken` tylko w sekretach.
- [ ] Przenieść dane dostępowe DB z `application.yml` do zmiennych środowiskowych / profili.
- [ ] `AuthService` ładuje wszystkich aktywnych użytkowników i filtruje w pamięci — zamienić na zapytania
      repozytorium (`findByEmailAndIsActiveTrue`, `findByNameAndIsActiveTrue`) + unikalny indeks.
- [ ] Flaga `isActive` liczona tylko przy rejestracji — po resecie serwera nigdy nie wygasa. Liczyć ją z
      `resetDate` w zapytaniu albo dezaktywować agentów, gdy `GET /` zwróci nowy `resetDate`.
- [ ] Blokujące wywołania JPA w WebFlux (`RegisterService.save` bez `Dispatchers.IO`) — opakować spójnie
      albo przejść na R2DBC.
- [ ] Obsługa błędów API gry: `ExceptionControllerAdvice` łapie tylko `NotFound`; 400/401/409/422/429/502
      kończą się 500. Mapować `WebClientResponseException` na właściwe statusy i treść błędu z API.
- [ ] **Rate limit** zgodnie z docs: jeden wspólny limiter na konto (2 req/s, burst 30/60 s), bo limit liczy się
      per konto i per IP — wszystkie agenty i boty dzielą tę samą pulę. Retry z backoffem na 429 wg
      `x-ratelimit-reset`, retry na 502.
- [ ] Migracje schematu (Flyway) zamiast `ddl-auto: update`.
- [ ] Przypiąć wersje obrazów `postgres` (docker-compose, Testcontainers) zamiast `latest`.
- [ ] CORS dla klienta wasm.

## Faza 3 — Aktualizacja zależności

- [ ] Spring Boot 3.3 (koniec wsparcia OSS) → aktualna linia; Kotlin 2.0.21 → aktualny 2.x.
- [ ] Compose Multiplatform 1.7 → aktualny; Gradle wrapper 8.8 → aktualny (oba moduły).
- [ ] Rozważyć Java 21 toolchain.
- [ ] Dependabot dla `gradle` i `github-actions`.
- [ ] Job CI (np. cotygodniowy), który porównuje lokalny spec z `https://spacetraders.io/SpaceTraders.json`
      i zgłasza różnice — API zmienia się między resetami.

## Faza 4 — Fundamenty klienta UI

- [ ] Klient HTTP (Ktor client + kotlinx.serialization) do własnego backendu.
- [ ] Prosta nawigacja między ekranami (Navigation Compose) i ViewModel/stan per ekran.
- [ ] Usunąć demo `Click me!` — splash screen jako ekran startowy z przejściem do logowania.
- [ ] Wspólny moduł modeli (`shared`, KMP) dla DTO backendu i UI — tylko jeśli duplikacja zacznie boleć.
- [ ] Motyw graficzny (kolory/czcionka QuickSilver już są) i obsługa stanów ładowania/błędu.
- [ ] Widoczny licznik do następnego resetu serwera (z `GET /`).

## Faza 5 — Funkcje gry w UI (kolejność wg Quickstart z docs)

- [ ] Rejestracja agenta (symbol 3–14 znaków, wybór frakcji) i logowanie.
- [ ] Dashboard agenta: kredyty, siedziba, frakcja, liczba statków.
- [ ] Kontrakty: lista, akceptacja, dostawa, realizacja (pierwsza misja z Quickstartu).
- [ ] Stocznie: znalezienie waypointu z `SHIPYARD`, zakup statku (np. mining drone).
- [ ] Flota: lista statków, szczegóły, dock/orbit, nawigacja i zmiana `flightMode` w locie, tankowanie, ładownia.
- [ ] Wydobycie: survey → extract na asteroidach, jettison, refine/siphon dla statków z modułami.
- [ ] Rynki: ceny, kupno/sprzedaż towarów, supply chain (`/market/supply-chain`).
- [ ] Systemy i waypointy: lista + prosta mapa 2D systemu (Canvas), jump gate / warp, budowa jump gate'ów.
- [ ] Outfitting: mounts i moduły (install/remove).

## Faza 6 — Automatyzacja (boty)

- [ ] Scheduler w backendzie (coroutines) z poszanowaniem cooldownów, czasu przelotu (`route.arrival`)
      i wspólnego limitu zapytań z Fazy 2.
- [ ] Pętla wydobycia: survey → extract → sprzedaż lub dostawa do kontraktu.
- [ ] Automatyczna realizacja kontraktów (accept → zakup/wydobycie → deliver → fulfill → negotiate nowy).
- [ ] Zapisywanie danych rynkowych (ceny, supply/activity) do bazy i wybór tras handlowych.
- [ ] Obsługa resetu: wykrycie nowego `resetDate`, automatyczna rejestracja nowego agenta `AccountToken`em.
- [ ] Podgląd i sterowanie botami z UI.

## Faza 7 — Wdrożenie i dystrybucja

- [ ] Dockerfile dla serwisu (np. `bootBuildImage`) i `docker-compose` z całym stackiem.
- [ ] Publikacja obrazu do GHCR z GitHub Actions; `AccountToken` jako GitHub/deploy secret.
- [ ] Build wasm i publikacja na GitHub Pages.
- [ ] Paczki desktop (Dmg/Msi/Deb) budowane na tagach jako GitHub Release (macOS/Windows/Linux runners).

## Faza 8 — Jakość i obserwowalność

- [ ] Lint (ktlint lub detekt) jako krok CI.
- [ ] Raport pokrycia (Kover) i upload raportów testów jako artefakt CI.
- [ ] Testy UI (Compose UI test) dla kluczowych ekranów.
- [ ] Metryki Actuator/Micrometer: opóźnienia API gry, liczba 429/502, `x-ratelimit-remaining`; structured logging.
