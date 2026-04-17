# IncidentBridge Implementation Plan

## Overview

This plan implements incident management features for the IncidentBridge fork of Gadgetbridge, enabling severity-filtered alerts and wrist-based incident response for on-call engineers.

## Features to Implement

### Feature 1: Severity-Patterned Vibration Alerts
**Goal**: Parse incident notifications for severity keywords and map them to distinct vibration patterns on the smartwatch.

**Implementation**:
- Extend `NotificationSpec` to include `severity` field
- Add severity parser in `NotificationListener.java` that inspects notification title/body for patterns like `P1`, `P2`, `P3`, `CRITICAL`, `HIGH`, `SEV1`, `SEV2`
- Map severity levels to vibration pattern arrays
- Modify `XiaomiNotificationService.java` to send custom vibration sequences after notification

**Files to modify**:
- `app/src/main/java/.../model/NotificationSpec.java` (add severity field)
- `app/src/main/java/.../externalevents/NotificationListener.java` (add severity parsing)
- `app/src/main/java/.../service/devices/xiaomi/services/XiaomiNotificationService.java` (send vibration patterns)

**Acceptance criteria**:
- P1 incident triggers SOS vibration pattern (200ms on, 100ms off, 200ms on, 100ms off, 200ms on, 100ms off, 600ms on, 100ms off, 600ms on, 100ms off, 600ms on)
- P2 triggers Morse R pattern (400ms on, 100ms off, 200ms on, 100ms off, 400ms on)
- P3 triggers single short buzz (100ms)
- Non-incident notifications use default behavior

---

### Feature 2: Incident Detection and Filtering
**Goal**: Identify incident management app notifications and extract incident metadata.

**Implementation**:
- Create `IncidentAppConfig` enum/class with known incident apps and their notification patterns
- Add incident ID extraction logic that parses notification extras/text
- Create whitelist/blacklist filter for incident apps in settings
- Add content-based inclusive filters (only forward if contains specific keywords)

**Known incident app packages**:
- `com.pagerduty.android`
- `com.opsgenie.app`
- `com.rootly.mobile`
- `com.slack`

**Files to create**:
- `app/src/main/java/.../incident/IncidentAppConfig.java`
- `app/src/main/java/.../incident/IncidentParser.java`

**Files to modify**:
- `app/src/main/java/.../externalevents/NotificationListener.java`

**Acceptance criteria**:
- Correctly identifies notifications from PagerDuty, Opsgenie, Rootly
- Extracts incident ID from notification payload when available
- Respects user-configured app whitelist
- Filters by content keywords (configurable)

---

### Feature 3: Wrist-Based Reply Actions
**Goal**: Show Acknowledge/Escalate/Resolve buttons on the smartwatch for incident notifications.

**Implementation**:
- Modify `XiaomiNotificationService.java` to detect incident notifications
- For incident notifications, set `repliesAllowed=true` and add reply strings: `["Ack", "Esc", "Res"]`
- Store mapping: notification key → incident ID → provider type
- Handle reply callback from band (Command type 7, subtype 13)
- Route reply action to IncidentResponder

**Files to modify**:
- `app/src/main/java/.../service/devices/xiaomi/services/XiaomiNotificationService.java`

**Files to create**:
- `app/src/main/java/.../incident/IncidentMapping.java` (stores notification-to-incident mappings)

**Acceptance criteria**:
- Incident notifications show 3 reply options on Mi Band 9
- Tapping "Ack" sends acknowledge action
- Tapping "Esc" sends escalate action
- Tapping "Res" sends resolve action
- Non-incident notifications unchanged

---

### Feature 4: IncidentResponder (API Integration)
**Goal**: HTTP client that calls PagerDuty/Opsgenie/Rootly REST APIs for incident actions.

**Implementation**:
- Create `IncidentResponder.java` with provider-agnostic interface
- Implement `PagerDutyResponder`, `OpsgenieResponder`, `RootlyResponder`
- Use Android `HttpURLConnection` or OkHttp (check if already in dependencies)
- Support API key authentication per provider
- Handle network errors gracefully (retry once, then fail silently)

**Files to create**:
- `app/src/main/java/.../incident/responder/IncidentResponder.java` (interface)
- `app/src/main/java/.../incident/responder/PagerDutyResponder.java`
- `app/src/main/java/.../incident/responder/OpsgenieResponder.java`
- `app/src/main/java/.../incident/responder/RootlyResponder.java`
- `app/src/main/java/.../incident/responder/ResponderFactory.java`

**API endpoints**:
- PagerDuty: `PUT /incidents/{id}` (status: acknowledged/resolved)
- Opsgenie: `POST /v2/alerts/{id}/acknowledge`, `POST /v2/alerts/{id}/close`
- Rootly: `PATCH /v1/incidents/{id}` (status updates)

**Acceptance criteria**:
- Successfully calls PagerDuty API with valid token
- Successfully calls Opsgenie API with valid key
- Handles 401/403 auth errors
- Network timeout after 10 seconds
- Non-blocking (async execution)

---

### Feature 5: Settings UI for Incident Management
**Goal**: Allow users to configure API keys, select providers, and set filtering rules.

**Implementation**:
- Add new preference screen under main settings
- Add `IncidentManagementPreferencesActivity.java`
- Add preference keys for:
  - Enable/disable incident management
  - Provider selection (PagerDuty/Opsgenie/Rootly/Custom)
  - API key/token input (secure text field)
  - PagerDuty user email (for From header)
  - App whitelist (multi-select of installed apps)
  - Severity filter (which severities to forward)
  - Time-based rules (on-call hours)
- Add preference XML layout
- Add strings for all new UI text

**Files to create**:
- `app/src/main/java/.../activities/IncidentManagementPreferencesActivity.java`
- `app/src/main/res/xml/prefs_incident_management.xml`

**Files to modify**:
- `app/src/main/res/xml/preferences.xml` (add link to new screen)
- `app/src/main/res/values/strings.xml` (add UI strings)

**Acceptance criteria**:
- Settings screen accessible from main Settings menu
- API keys persist across app restarts
- Provider selection updates available fields
- Time picker for on-call hours works
- Changes take effect immediately

---

### Feature 6: Enable Internet Permission
**Goal**: Allow the app to make HTTP API calls.

**Implementation**:
- Remove `tools:node="remove"` from INTERNET permission in manifest
- Or add build flavor with INTERNET_ACCESS=true (like banglejs)

**Files to modify**:
- `app/src/main/AndroidManifest.xml`

**Acceptance criteria**:
- App can make HTTPS requests to PagerDuty API
- Network operations don't crash the app

---

## Implementation Order

1. **Phase 1: Foundation**
   - Enable INTERNET permission
   - Create `IncidentAppConfig` and `IncidentParser`
   - Add severity parsing to NotificationListener
   - Test severity vibration patterns

2. **Phase 2: Core Logic**
   - Create `IncidentResponder` interface and implementations
   - Add reply button injection to XiaomiNotificationService
   - Create incident mapping storage
   - Wire reply handler to responder

3. **Phase 3: Settings UI**
   - Create preference activity and XML
   - Add to main settings navigation
   - Wire settings to responder configuration

4. **Phase 4: Polish**
   - Add error handling and retry logic
   - Add logging for API calls
   - Test end-to-end flow
   - Update README with actual implementation details

## Testing Strategy

- Unit tests for `IncidentParser` (various notification formats)
- Unit tests for `Responder` implementations (mock HTTP)
- Integration test: send test notification, verify vibration pattern
- Manual test: pair with Mi Band 9, send PagerDuty notification, verify reply buttons

## Risks and Mitigations

| Risk | Mitigation |
|------|-----------|
| PagerDuty notification format changes | Make parser flexible with regex fallback |
| Network failures during API calls | Implement retry with exponential backoff |
| Auth token exposure | Store in Android Keystore (future enhancement) |
| Build time increases | Keep changes modular, don't touch core BLE code |
| Merge conflicts with upstream | Isolate changes in `incident/` package |
