# NotiSiren — Claude working notes

> This file is auto-loaded by Claude Code every session. It is the source of truth for
> *how we work* and *where we are*, so progress survives across machines / fresh chats.
> The product spec is **`NotiSiren_MVP_Gmail_Only.md`** (read it for feature detail).

## What we're building
An **offline** Android app that rings an **alarm** when a **Gmail** notification matches a
user-defined filter (sender / subject contains text). MVP is Gmail-only. Privacy is a hard
requirement: **no `android.permission.INTERNET`** — fully on-device, no analytics/telemetry.
Bilingual at launch: **English + Spanish** (per-locale `strings.xml`, no hardcoded user text).
Priority test devices: Xiaomi (MIUI/HyperOS) and Huawei (EMUI) — aggressive background-killing
is the #1 risk.

## ⚠️ CRITICAL WORKFLOW — the user types the code, not me
The user is learning this codebase for job interviews. **Do NOT use Write/Edit to create or
modify source files (`.kt`, `.xml`, Gradle).** Instead, for each file: give the full path,
the complete contents in a code block, and an explanation of its role + the interview-relevant
concepts. The user types/creates the files themselves, then says "done" and I build to verify.
- I MAY directly edit: this `CLAUDE.md`, the `.md` spec, `.gitignore`, and run throwaway build
  experiments to discover correct config.
- I MAY delete superseded generated boilerplate (e.g. the old `app/.../ui/theme/` files).
- Deliver work in **small, independently-buildable increments**; build after each "done".
- When a typed file has a typo, point to the exact line/char — don't fix it myself.

## Build / verify command
PowerShell shell, but build via Bash tool with the bundled JBR:
```bash
cd "/c/Users/Mateo Rial/AndroidStudioProjects/NotiSiren" \
  && export JAVA_HOME="/c/Program Files/Android/Android Studio/jbr" \
  && export PATH="$JAVA_HOME/bin:$PATH" \
  && ./gradlew :MODULE:assembleDebug --console=plain
```
(On the other machine, `JAVA_HOME` = that install's Android Studio JBR path.)
Modules are enabled **incrementally** in `settings.gradle.kts` — Gradle 9 refuses to configure
an `include()` whose folder doesn't exist yet, so a module's line stays commented until its
files are typed.

## Architecture — multi-module clean architecture
Dependency graph (arrows = "depends on"):
```
:app ──► :feature-main ──► :shared ──► :core
  └──────► :ui-components ◄──┘ (feature modules also depend on :ui-components)
:shared also ──► :core
```
- **:core** — pure Kotlin/Android utils. `AppConstants` (Gmail pkg `com.google.android.gm`),
  `String?.containsIgnoreCase`. ns `com.notisiren.core`.
- **:ui-components** — design system. `NotiSirenTheme` + Color/Type. **`api`-exports Compose +
  Material3** so feature modules get them transitively (they still must apply the
  `kotlin.compose` *plugin* — compiler plugins aren't transitive). ns `com.notisiren.uicomponents`.
- **:shared** — domain + data, **no UI**. domain: `GmailFilter`, `GmailNotification`,
  `FilterRepository` (interface), use cases `GetAllFilters`/`CreateFilter`/`CheckGmailFilter`
  (the core matching logic, a pure fn). data: Room (`FilterEntity`, `FilterDao`,
  `NotiSirenDataBase`*), `FilterRepositoryImpl`, mappers, Hilt modules (`DatabaseModule`
  `@Provides`, `RepositoryModule` `@Binds`). ns `com.notisiren.shared`.
  (*class is spelled `NotiSirenDataBase` — capital B — in file `NotiSirenDatabase.kt`.)
- **:feature-main** — home screen. MVVM + UDF: `MainUiState`, `MainViewModel` (`@HiltViewModel`,
  `StateFlow` via `stateIn`), `MainScreen` (stateful) + `MainContent` (stateless, previewable).
  Own `strings.xml` (en + es). ns `com.notisiren.feature.main`.

Patterns being taught: `implementation` vs `api`, `@Binds` vs `@Provides`, use cases, repository
pattern (interface in domain / impl in data), entity↔domain mappers, Flow for reads + suspend
for writes, state hoisting, KSP vs runtime deps, i18n via `values-xx/strings.xml`.

## AGP 9 gotchas (hard-won — this project is on bleeding-edge AGP 9.2.1)
1. **Built-in Kotlin** — do NOT apply a `kotlin-android` plugin; AGP 9 compiles Kotlin itself.
   It's absent from the version catalog on purpose.
2. **Hilt must be ≥ 2.59.x** for AGP 9 (older versions hit "Android BaseExtension not found").
3. **KSP** needs `android.disallowKotlinSourceSets=false` in `gradle.properties`.
4. **Gradle 9** requires an `include()`d module's directory to exist → enable modules
   incrementally in `settings.gradle.kts`.
Toolchain versions live in `gradle/libs.versions.toml` (agp 9.2.1, kotlin 2.2.10,
ksp 2.2.10-2.0.2, hilt 2.59.2, room 2.8.2, composeBom 2026.02.01, etc.).

## Progress
**Built green:** `:core`, `:ui-components`, `:shared`, `:feature-main`, `:feature-filters`, `:feature-notifications`
(each `assembleDebug` verified). Old `app/.../ui/theme/` boilerplate deleted (theme now lives in `:ui-components`).
`:app` successfully wired with `@HiltAndroidApp`, `MainActivity`, Navigation Compose, and all feature modules.

**MVP Features Complete:**
- ✅ **Filter Management** — Create/edit filters (`:feature-filters` with form validation)
- ✅ **Navigation** — FAB in MainScreen → FilterCreationScreen (Jetpack Navigation Compose)
- ✅ **Notification Listening** — `GmailNotificationListener` intercepts Gmail notifications + `CheckGmailFilter` matching
- ✅ **Alarm Trigger** — `AlarmPlayer` plays sound + vibration on filter match
- ✅ **Permission Detection** — `NotificationListenerStatusChecker` + banner on MainScreen prompts user to enable access

### ⏭️ NEXT ROADMAP ITEMS
`:app` currently will NOT compile: `MainActivity.kt` still imports the deleted
`com.notisiren.ui.theme.NotiSirenTheme`. The following 4 changes were handed to the user but
not yet typed. Re-deliver them (user types), then build `./gradlew :app:assembleDebug`:
1. **`app/build.gradle.kts`** — add `ksp` + `hilt` plugins; replace `dependencies{}` to depend on
   `:core`,`:shared`,`:ui-components`,`:feature-main` + `hilt.android`/`ksp(hilt.compiler)` +
   `core.ktx`,`lifecycle.runtime.ktx`,`activity.compose`, compose BOM/ui/tooling, test deps.
2. **`app/src/main/java/com/notisiren/NotiSirenApp.kt`** (new) — `@HiltAndroidApp class NotiSirenApp : Application()`.
3. **`app/src/main/java/com/notisiren/MainActivity.kt`** (rewrite) — `@AndroidEntryPoint`, import
   `com.notisiren.uicomponents.theme.NotiSirenTheme` + `com.notisiren.feature.main.MainScreen`,
   `setContent { NotiSirenTheme { MainScreen(onAddFilter = { /* TODO nav */ }) } }`.
4. **`app/src/main/AndroidManifest.xml`** — add `android:name=".NotiSirenApp"` to `<application>`.

### Roadmap (after :app runs)
- Create / edit filter screen (`:feature-filters`) + navigation (Navigation Compose) wired to FAB.
- `:feature-settings` — alarm sound/volume, theme, in-app **language picker**
  (`AppCompatDelegate.setApplicationLocales`) + `locales_config.xml`.
- `:feature-notifications` — `NotificationListenerService` (read Gmail notifications),
  permission-status banner on home (IMPORTANT MVP feature: detect listener access enabled),
  full-screen alarm + `mediaPlayback` foreground service, `USE_FULL_SCREEN_INTENT`/`VIBRATE` perms.
- `:testing` module + unit tests (start with `CheckGmailFilter` — pure, trivially testable).
- OEM background-restriction handling (Xiaomi/Huawei autostart guidance).
- Per-feature `strings.xml` always in both `values/` and `values-es/`.

## Git / multi-machine
- Per-module `build/` dirs are git-ignored (added to `.gitignore`).
- `.idea/` is partially tracked by the AS template; harmless. `local.properties` is ignored
  (each machine's SDK path differs — Android Studio regenerates it).
- Current branch: `featuremain`. Main branch: `main`.
