# NotiSiren MVP - Gmail Only Specification

## Overview

**Scope:** Gmail notifications only
**Filter:** Sender OR Title OR Both
**Timeline:** 4-6 weeks to Play Store launch

---

## MVP Core Features

### 1. Read Gmail Notifications
- Listen for notifications from Gmail app
- Extract: Sender, Title/Subject, Preview

### 2. Simple Filter Rules (Gmail Only)
Users can create rules like:
- ✅ Sender contains "boss@company.com" → Ring alarm
- ✅ Title contains "Urgent" → Ring alarm
- ✅ Sender "boss@company.com" AND Title "Meeting" → Ring alarm

### 3. Trigger Alarm
- One system alarm sound
- Plays for 5 seconds
- User can stop with power button or back button

### 4. Basic Settings
- Toggle notifications ON/OFF
- Dark/Light theme
- Language selector (English / Spanish, more later) — see Localization section
- Manual test button (rings alarm)
- **Permission status — IMPORTANT FEATURE:** detect whether the Notification Access
  permission is currently granted and surface it prominently. If it is revoked the app
  is silently useless, so this check is first-class, not cosmetic.

### 5. Core Screens
- **Main:** Status (permission indicator) + List of active rules. **No Test Alarm button here** — the test button lives only in Settings.
- **Create Rule:** Sender field + Subject/Title field (both optional, at least one required). Copy must make it explicit this is **about emails** (e.g. "Email sender", "Email subject"), so users don't expect it to cover other apps.
- **Settings:** Theme switcher + Language selector + Notifications toggle + Test button + Permission status

---

## Simplified Database Schema

```kotlin
@Entity(tableName = "gmail_filters")
data class GmailFilter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val senderPattern: String? = null,      // e.g., "boss@company.com"
    val titleKeyword: String? = null,       // e.g., "Urgent"
    val isEnabled: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
// Note: At least one of senderPattern or titleKeyword must be non-null

@Entity(tableName = "app_settings")
data class AppSetting(
    @PrimaryKey val key: String,
    val value: String
)
// Keys: "theme" (dark/light), "notifications_enabled" (true/false)
```

---

## Filter Logic (Simple)

```kotlin
// shared/domain/usecases/CheckGmailFilter.kt

class CheckGmailFilter {
    operator fun invoke(
        notification: GmailNotification,
        filter: GmailFilter
    ): Boolean {
        // At least one must match
        
        val senderMatches = filter.senderPattern?.let { pattern ->
            notification.sender?.contains(pattern, ignoreCase = true) ?: false
        } ?: true  // If no filter set, consider it a match
        
        val titleMatches = filter.titleKeyword?.let { keyword ->
            notification.title?.contains(keyword, ignoreCase = true) ?: false
        } ?: true  // If no filter set, consider it a match
        
        // Both filters are optional, but at least one must be set
        // If both are set, both must match (AND logic)
        // If one is set, only that one needs to match
        
        val bothSet = !filter.senderPattern.isNullOrEmpty() && 
                      !filter.titleKeyword.isNullOrEmpty()
        
        return if (bothSet) {
            senderMatches && titleMatches  // Both must match
        } else {
            senderMatches || titleMatches  // At least one matches
        }
    }
}
```

---

## Filter Examples

| Sender | Title | Rule Matches | Notes |
|--------|-------|--------------|-------|
| boss@company.com | (empty) | Any email from boss | Sender filter only |
| (empty) | Urgent | Any email with "Urgent" | Title filter only |
| boss@company.com | Urgent | Email from boss WITH "Urgent" | Both must match |
| (empty) | (empty) | ❌ Invalid | Can't create this rule |

---

## Simplified Screens

### Main Screen
```
┌─────────────────────────┐
│  NotiSiren              │
│  Gmail Alerts           │
│  ━━━━━━━━━━━━━━━━━━━   │
│                         │
│  🔴 Permission Required │
│  Tap to enable in       │
│  Settings               │
│                         │
│  Active Rules: 2        │
│  ━━━━━━━━━━━━━━━━━━━   │
│                         │
│  ✓ From: boss@...       │
│                         │
│  ✓ Title: "Urgent"      │
│                         │
│  [+ New Rule]           │
│  [⚙️ Settings]          │
└─────────────────────────┘
(Test Alarm button removed from Main — it lives in Settings only)
```

### Create Rule Screen
```
┌─────────────────────────┐
│  New Gmail Alert        │
│  ━━━━━━━━━━━━━━━━━━━   │
│                         │
│  Sender (optional)      │
│  [___________________]  │
│  Example: boss@c...     │
│                         │
│  Subject/Title          │
│  [___________________]  │
│  Example: Urgent        │
│                         │
│  (Set at least one)     │
│                         │
│  [Create]  [Cancel]     │
└─────────────────────────┘
```

### Settings Screen
```
┌─────────────────────────┐
│  Settings               │
│  ━━━━━━━━━━━━━━━━━━━   │
│                         │
│  Theme                  │
│  ○ Dark   ● Light       │
│  ○ System              │
│                         │
│  Language               │
│  ● English  ○ Español   │
│  ○ System default       │
│                         │
│  Alerts Enabled         │
│  ☑ ON                   │
│                         │
│  ━━━━━━━━━━━━━━━━━━━   │
│  [🔔 Test Alarm]        │
│                         │
│  Gmail Access:          │
│  ✓ Enabled              │
│  Last checked: now      │
│                         │
│  [Settings App]         │
│                         │
│  Version 1.0.0          │
└─────────────────────────┘
```

---

## Notification Detection

### How to Detect Gmail Notifications

Gmail notifications have these characteristics:
```
packageName: "com.google.android.gm"  // Gmail app package

notification.extras.get("android.text") = sender info
notification.extras.get("android.title") = subject/title
notification.extras.get("android.subText") = preview
notification.extras.get("android.summaryText") = thread count
```

### Gmail Notification Data Structure

```kotlin
data class GmailNotification(
    val sender: String?,      // "John Doe <john@example.com>"
    val title: String?,       // Subject line
    val preview: String?,     // Email preview
    val timestamp: Long
)

// Usage:
// Extract from StatusBarNotification in NotificationListenerService
override fun onNotificationPosted(sbn: StatusBarNotification?) {
    if (sbn?.packageName == "com.google.android.gm") {
        val gmail = extractGmailData(sbn.notification)
        checkFilters(gmail)
    }
}
```

---

## Simplified Module Structure (MVP)

```
notisiren/
├── app/
│   ├── src/main/kotlin/com/notisiren/
│   │   ├── MainActivity.kt
│   │   ├── NotiSirenApp.kt
│   │   └── di/
│   │       └── AppModule.kt
│   └── AndroidManifest.xml
│
├── shared/
│   ├── domain/
│   │   ├── models/
│   │   │   ├── GmailFilter.kt
│   │   │   └── GmailNotification.kt
│   │   ├── repositories/
│   │   │   ├── FilterRepository.kt
│   │   │   └── SettingsRepository.kt
│   │   └── usecases/
│   │       ├── CheckGmailFilter.kt
│   │       ├── GetAllFilters.kt
│   │       └── CreateFilter.kt
│   └── data/
│       ├── local/
│       │   ├── NotiSirenDatabase.kt
│       │   ├── FilterDao.kt
│       │   └── SettingsDao.kt
│       └── repositories/
│           ├── FilterRepositoryImpl.kt
│           └── SettingsRepositoryImpl.kt
│
├── core/
│   ├── extensions/
│   │   └── StringExt.kt
│   └── constants/
│       └── AppConstants.kt
│
├── ui-components/
│   └── theme/
│       ├── Theme.kt
│       ├── Color.kt
│       └── Typography.kt
│
├── feature-main/
│   ├── presentation/
│   │   ├── MainScreen.kt
│   │   ├── MainViewModel.kt
│   │   └── MainUiState.kt
│   └── di/
│       └── MainModule.kt
│
├── feature-filters/
│   ├── presentation/
│   │   ├── CreateFilterScreen.kt
│   │   ├── FilterListScreen.kt
│   │   ├── FilterViewModel.kt
│   │   └── FilterUiState.kt
│   └── di/
│       └── FilterModule.kt
│
├── feature-settings/
│   ├── presentation/
│   │   ├── SettingsScreen.kt
│   │   ├── SettingsViewModel.kt
│   │   └── SettingsUiState.kt
│   └── di/
│       └── SettingsModule.kt
│
├── feature-notifications/
│   ├── data/
│   │   ├── GmailNotificationListener.kt
│   │   ├── GmailNotificationService.kt
│   │   └── AlarmPlayer.kt
│   └── di/
│       └── NotificationModule.kt
│
└── testing/
    └── FakeRepositories.kt
```

---

## MVP Permissions (Android Manifest)

> **Deliberately NO `android.permission.INTERNET`.** This is a privacy guarantee, not an
> oversight — without it the app is technically incapable of any network call. See the
> Privacy & Offline section. Trade-off: no remote crash reporting / analytics.

> **Foreground service type corrected:** the original draft used `connectedDevice`, which is
> for companion devices (wearables/Bluetooth) and would draw Play Store review scrutiny. A
> `NotificationListenerService` does **not** itself need a foreground service to receive
> notifications. The only place a foreground service helps is keeping the **alarm** alive
> while it rings — for that, type `mediaPlayback` (or a full-screen-intent activity) is the
> correct fit. Listed below as `mediaPlayback`.

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- NO INTERNET permission — fully offline by design -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_MEDIA_PLAYBACK" />
    <!-- For the full-screen alarm experience on lock screen -->
    <uses-permission android:name="android.permission.USE_FULL_SCREEN_INTENT" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <!-- BIND_NOTIFICATION_LISTENER_SERVICE is declared on the <service>, not requested here -->

    <application
        android:name=".NotiSirenApp"
        ...>

        <service
            android:name=".feature_notifications.data.GmailNotificationListener"
            android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
            android:exported="true">
            <intent-filter>
                <action android:name="android.service.notification.NotificationListenerService" />
            </intent-filter>
        </service>

        <service
            android:name=".feature_notifications.data.GmailNotificationService"
            android:foregroundServiceType="mediaPlayback"
            android:exported="false" />

    </application>

</manifest>
```

---

## Localization (i18n)

**Goal:** English + Spanish at launch, easy to add more (French, Portuguese, …) later.

### How it works
Android selects the right strings automatically from the device locale. We provide one
`strings.xml` per language; `values/` (English) is the required default fallback.

```
ui-components/  (or app)  src/main/res/
├── values/strings.xml        ← default = English  (REQUIRED)
├── values-es/strings.xml     ← Spanish
├── values-fr/strings.xml     ← French      (later)
└── values-pt/strings.xml     ← Portuguese  (later)
```

### Hard rule
**No hardcoded user-facing text anywhere.** Every label/button/message comes from
`stringResource(R.string.xxx)`. This is enforced from day one — retrofitting is expensive.
Strings that are part of detection logic (e.g. the Gmail package name) are NOT translated.

### System vs. in-app selection — we do BOTH
1. **Default:** follow the device language automatically (no code).
2. **In-app Language picker in Settings:** for users whose phone is in one language but who
   want the app in another. Implemented with AppCompat per-app locales:
   - `AndroidManifest` references `@xml/locales_config` (list of supported BCP-47 tags).
   - The picker calls `AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags("es"))`.
   - On Android 13+ this also wires into the system per-app language screen automatically.
   - The chosen tag is persisted in the `app_settings` table (key `"language"`, value `""` = system).

---

## Privacy & Offline (core promise)

The app is **100% offline by design** and this is a headline selling point.

- **No `INTERNET` permission is declared.** The app literally cannot open a network socket.
  This is the strongest possible privacy guarantee and makes the Play Store *Data Safety*
  form trivial: no data collected, no data shared, nothing leaves the device.
- All processing is local: `NotificationListenerService` reads notifications on-device, the
  filter check is pure in-memory logic, and rules/settings live in a local Room database.
- Notification content is **never stored** — it's inspected transiently and discarded. Only
  user-authored filter rules and settings are persisted.

**Accepted trade-off:** no remote crash reporting or analytics (Crashlytics/Firebase require
`INTERNET`). For a privacy-first MVP this is the right call, but it means we rely on thorough
pre-release testing instead of field telemetry.

---

## MVP Code Structure Example

### Core Filter Logic (Shared Domain - Testable)
```kotlin
// shared/domain/usecases/CheckGmailFilter.kt
class CheckGmailFilter {
    operator fun invoke(
        gmail: GmailNotification,
        filter: GmailFilter
    ): Boolean {
        val senderMatches = filter.senderPattern?.let {
            gmail.sender?.contains(it, ignoreCase = true) ?: false
        } ?: true
        
        val titleMatches = filter.titleKeyword?.let {
            gmail.title?.contains(it, ignoreCase = true) ?: false
        } ?: true
        
        val bothSet = !filter.senderPattern.isNullOrEmpty() && 
                      !filter.titleKeyword.isNullOrEmpty()
        
        return if (bothSet) senderMatches && titleMatches 
               else senderMatches || titleMatches
    }
}
```

### Notification Listener (Feature - Android Specific)
```kotlin
// feature-notifications/data/GmailNotificationListener.kt
class GmailNotificationListener : NotificationListenerService() {
    
    @Inject lateinit var checkFilter: CheckGmailFilter
    @Inject lateinit var filterRepository: FilterRepository
    @Inject lateinit var alarmPlayer: AlarmPlayer
    
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn?.packageName != "com.google.android.gm") return
        
        val gmail = extractGmailData(sbn.notification) ?: return
        
        val filters = filterRepository.getEnabledFilters()
        val matched = filters.firstOrNull { filter ->
            checkFilter(gmail, filter)
        }
        
        if (matched != null) {
            alarmPlayer.playAlarm()  // Simple system alarm
        }
    }
    
    private fun extractGmailData(notification: Notification): GmailNotification? {
        val extras = notification.extras
        return GmailNotification(
            sender = extras.get("android.text")?.toString(),
            title = extras.get("android.title")?.toString(),
            preview = extras.get("android.subText")?.toString(),
            timestamp = System.currentTimeMillis()
        )
    }
}
```

### ViewModel (Feature - UI Logic)
```kotlin
// feature-filters/presentation/FilterViewModel.kt
@HiltViewModel
class FilterViewModel @Inject constructor(
    private val createFilter: CreateFilter,
    private val getAllFilters: GetAllFilters
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<FilterUiState>(FilterUiState.Loading)
    val uiState = _uiState.asStateFlow()
    
    fun createFilter(sender: String?, title: String?) {
        if (sender.isNullOrEmpty() && title.isNullOrEmpty()) {
            _uiState.value = FilterUiState.Error("Set at least sender or title")
            return
        }
        
        viewModelScope.launch {
            try {
                createFilter(sender, title)
                _uiState.value = FilterUiState.Success
            } catch (e: Exception) {
                _uiState.value = FilterUiState.Error(e.message ?: "Error")
            }
        }
    }
}

sealed class FilterUiState {
    object Loading : FilterUiState()
    object Success : FilterUiState()
    data class Error(val message: String) : FilterUiState()
}
```

---

## MVP Development Phases (4-6 Weeks)

> Status legend: `[ ]` not started · `[~]` in progress · `[x]` done.
> (Reset to not-started — the project is a fresh Android Studio scaffold.)

### Phase 1: Days 1-5 (Foundation)
- [ ] Create project with modular structure
- [ ] Room database (GmailFilter + AppSettings tables)
- [ ] Repository interfaces
- [ ] Theme setup (dark/light toggle)
- [ ] i18n scaffolding: strings.xml (en) + values-es, locales_config.xml, no hardcoded text

### Phase 2: Days 6-12 (Core Feature)
- [ ] GmailNotificationListener service
- [ ] Extract Gmail sender + title (verify real field mapping — the hard part)
- [ ] Filter matching logic
- [ ] Simple alarm player (full-screen intent + Stop)
- [ ] Permission checks (Notification Access status)

### Phase 3: Days 13-19 (UI - Rules)
- [ ] Create Filter screen (email-specific copy)
- [ ] List Filters screen
- [ ] Edit/Delete functionality
- [ ] Enable/Disable toggle
- [ ] Basic validation

### Phase 4: Days 20-26 (UI - Other Screens)
- [ ] Main screen (status + rules, NO test button)
- [ ] Settings screen (theme + language + test + permission status)
- [ ] In-app language picker (AppCompat per-app locales)
- [ ] Navigation
- [ ] Permission status indicator

### Phase 5: Days 27-32 (Testing & Polish)
- [ ] Unit tests for filter logic
- [ ] OEM background-restriction handling (Xiaomi/Huawei autostart + battery-opt guidance)
- [ ] Manual testing on real devices (incl. Xiaomi & Huawei)
- [ ] Bug fixes
- [ ] Permission monitoring (re-check listener alive on resume)

### Phase 6: Days 33-42 (Release)
- [ ] App signing
- [ ] Privacy policy (emphasize offline / no INTERNET permission)
- [ ] Play Store listing + Data Safety form (no data collected)
- [ ] Screenshots (en + es)
- [ ] PUBLISH! 🚀

---

## Key Differences from Full Plan

| Aspect | Full Plan | MVP |
|--------|-----------|-----|
| Apps Supported | Email, WhatsApp, SMS, others | Gmail only |
| Filter Types | 10+ types | 2: Sender, Title |
| Filter Logic | AND/OR combinations | Simple OR |
| Sound Options | 10+ system sounds | 1 default alarm |
| Sound Testing | Multiple sound previews | Test button only |
| History | Full notification logs | None |
| Settings | 20+ options | 4: Theme, Language, Toggle, Test |
| Database Tables | 3 tables | 2 tables |
| Screens | 5+ screens | 3 screens |

---

## Testing Strategy (MVP)

### Unit Tests
```kotlin
// Test filter logic
class CheckGmailFilterTest {
    @Test
    fun testSenderFilterOnly() { }
    
    @Test
    fun testTitleFilterOnly() { }
    
    @Test
    fun testBothFilters() { }
    
    @Test
    fun testCaseInsensitive() { }
    
    @Test
    fun testNoMatch() { }
}
```

### Manual Testing (Real Devices)
1. Create filter: Sender only
2. Send test email → Alarm should ring
3. Create filter: Title only
4. Send test email → Alarm should ring
5. Create filter: Both
6. Send matching/non-matching emails
7. Test theme toggle
8. Test permission toggle
9. Test alarm stop (power button)

### Device Targets
- Google Pixel (Android 14) — clean AOSP baseline
- Samsung Galaxy (Android 12) — One UI
- OnePlus (Android 13) — OxygenOS / ColorOS
- **Xiaomi (MIUI / HyperOS)** — aggressive background killing, see below
- **Huawei (EMUI)** — aggressive background killing + may lack Google services, see below

### ⚠️ OEM Background-Restriction Risk (HIGH PRIORITY)

This is the single biggest threat to an alarm app. **Xiaomi (MIUI/HyperOS) and Huawei (EMUI)
aggressively kill background services and notification listeners** to save battery. A
`NotificationListenerService` that works flawlessly on Pixel can be silently killed within
minutes on these devices — and a dead listener means no alarm, which users will blame on us.

Required MVP mitigations:
- **Detect these OEMs** and show a guided setup step: enable **Autostart** and **disable
  battery optimization** for NotiSiren. (Reference exact intents from `dontkillmyapp.com`.)
- Re-verify the listener is alive on app resume and re-prompt if it was killed.
- **Huawei caveat:** Huawei phones sold after ~2019 ship **without Google Play Services and
  without the Gmail app** — there is no Gmail notification to listen to on those. "Huawei
  support" therefore means "Huawei devices that still have the Gmail app installed." Scope
  this explicitly in the store listing / FAQ.

---

## Play Store Listing (MVP)

**Title:** "NotiSiren - Gmail Alerts"

**Short Description:** 
"Ring an alarm when important Gmail arrives. Simple. Private. Offline."

**Full Description:**
"Get instant alarm alerts for important Gmail messages.

SET RULES FOR:
✓ Sender (e.g., boss@company.com)
✓ Subject Keywords (e.g., 'Urgent')
✓ Both (combine them)

HOW IT WORKS:
1. Create alert rules in the app
2. App monitors your Gmail silently
3. Alarm sounds when a message matches

FEATURES:
• Filter by sender AND/OR subject
• Simple, focused design
• Dark and light themes
• 100% private (all local, no cloud)
• Offline processing

PERFECT FOR:
- Important work emails
- VIP contacts
- Critical alerts
- Don't miss important messages

Install now and never miss what matters!"

**Screenshots:**
1. Main screen with active rules
2. Create rule screen
3. Settings with test button
4. Permission access screen

---

## What's NOT in MVP

- WhatsApp support
- SMS support
- Outlook/Yahoo/Other email providers
- Sound selection UI
- Notification history
- Advanced filters (regex, date-based)
- Quiet hours
- Rule templates
- Cloud sync
- Widget
- Notification persistence

These are Phase 2, 3, 4... not MVP.

---

## Launch Success Metrics

**MVP Success = App Published + Users Download**

Not perfection, not 100% features.

Just: **Working. Published. Real users.**

---

## Next Steps

1. Create Android Studio project (Gmail-focused)
2. Send me the project
3. I generate all gradle files + folder structure
4. We start Week 1: Database + Theme
5. Week 2: GmailNotificationListener (the hard part)
6. Continue...
7. Launch Week 6

**Ready?** 🚀

