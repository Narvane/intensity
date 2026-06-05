# Intensity client

Kotlin Multiplatform client (Android + iOS-ready UI) for **Intensity Box**. The backend is **narvane-api** (`../narvane-api`), not this repo.

**What it is:** see [description.md](description.md).

**How this repo fits the vault:** this project lives under `defined_projects/` and is expected to move toward a *defined* state as described in [definition_structure.md](../../definition_structure.md): principles in [defined_principles/](../../defined_principles/), checks in [definition_checker/](../../definition_checker/), and criteria in [definition_of_defined/](../../definition_of_defined/). The client stays a **bounded context**: UI and HTTP client only; persistence and rules belong to the API.

## Modules

| Module | Role |
|--------|------|
| `mobile-shared` | Ktor client, DTOs, shared HTTP setup |
| `mobile-app` | Compose Multiplatform UI (Material 3) |

## API base URL

Production API root (used by both flavors for now):

`https://narvane.com.br/pandora-box/api/v1`

Routes match `defined_projects/narvane-api` (OpenAPI: `app/src/main/resources/META-INF/openapi.yaml`).

Android selects the root via `BuildConfig.PANDORA_API_BASE_URL` per **dev** / **prod** product flavor. iOS currently uses the same production root in code; you can introduce schemes or `xcconfig` later to mirror Android.

## Build (Android)

```bash
./gradlew :mobile-app:assembleDevDebug
./gradlew :mobile-app:assembleProdRelease
```

To point **dev** at a local narvane-api from the emulator, set `PANDORA_API_BASE_URL` in `mobile-app/build.gradle.kts` (dev flavor) to `http://10.0.2.2:8080/pandora-box/api/v1`.

## Endpoints (reference)

### Auth

- `GET .../auth/registered-users`
- `POST .../auth/register`
- `POST .../auth/login/experience`
- `POST .../auth/login/experience`

### Experiences

- `GET .../experiences`
- `POST .../experiences` (experience session)
- `PUT .../experiences/{id}` (owner, experience session)
- `DELETE .../experiences/{id}` (owner, experience session)
- `GET .../experiences/draw` (experience session)

## Rules implemented (product)

- Registration only for emails on the server whitelist.
- **Experience** mode: single-user login.
- **Experience** mode: collective login (all registered users in one request).
- Experience intensity `1..5`.
- Experience text stored encrypted on the server; client sees decrypted content via the API.

## iOS

`mobile-shared` targets iOS; `mobile-app` exposes `MainViewController()` in `iosMain` for embedding in a host Xcode project.
