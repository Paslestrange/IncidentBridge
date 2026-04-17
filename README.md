# IncidentBridge

**IncidentBridge** is a specialized fork of [Gadgetbridge](https://codeberg.org/Freeyourgadget/Gadgetbridge/), the open-source smartwatch companion app. It is built specifically for **on-call engineers and SREs** who need tight integration between their wrist devices and incident management platforms like **PagerDuty, Opsgenie, Rootly, and Splunk On-Call**.

While Gadgetbridge focuses on privacy-first fitness tracking and general smartwatch control, **IncidentBridge adds severity-filtered alerting and wrist-level incident response** — so you can distinguish a P1 database outage from a low-priority email without pulling out your phone.

---

## Why IncidentBridge?

Standard notification apps treat every alert the same. An on-call engineer needs:

- **Severity-aware vibration patterns** — feel the difference between P1 (SOS pattern) and P3 (single tap) without looking
- **Incident actions from the wrist** — Acknowledge, escalate, or resolve directly from your smartwatch
- **Content filtering** — only notify for specific apps, specific keywords, or specific callers
- **No vendor cloud** — your incident data never leaves your phone, just like Gadgetbridge

### Supported Incident Platforms

| Platform | Notifications | Acknowledge | Escalate | Resolve |
|----------|-------------|-------------|----------|---------|
| **PagerDuty** | ✅ | ✅ API | ✅ API | ✅ API |
| **Opsgenie** | ✅ | ✅ API | ✅ API | ✅ API |
| **Rootly** | ✅ | ✅ API | ✅ API | ✅ API |
| **Splunk On-Call (VictorOps)** | ✅ | ✅ API | ✅ API | ✅ API |
| **Slack** | ✅ | ❌ | ❌ | ❌ |
| **Custom webhooks** | ✅ | ⚠️ | ⚠️ | ⚠️ |

*API actions require an API key configured in the app settings.*

---

## What Makes It Different

### Gadgetbridge
- Privacy-focused fitness and general-purpose wearable companion
- No internet access by design
- No incident management integrations

### Notify for Mi Band
- Advanced notification filtering and custom vibrations
- No external API calls (cannot ack PagerDuty incidents)
- Requires Tasker hacks for any automation

### IncidentBridge (this fork)
- Inherits all Gadgetbridge device support (Mi Band, Bangle.js, Pebble, Garmin, etc.)
- Adds **REST API integrations** for major incident platforms
- Adds **severity-patterned vibration alerts** (custom Morse-style patterns)
- Adds **wrist-based incident actions** (Ack/Escalate/Resolve)
- Keeps Gadgetbridge's **cloudless, AGPLv3** philosophy

---

## Supported Devices

IncidentBridge inherits Gadgetbridge's extensive device support. For on-call use, the most popular targets are:

- **Xiaomi Mi Band 9** — excellent battery life, bright AMOLED, supports canned notification replies
- **Bangle.js 2** — open-source firmware, programmable, great for custom incident UIs
- **Pebble** — e-ink display, week-long battery, proven notification reliability
- **Garmin** — rugged, always-on display

See the full [Gadgetbridge devices list](https://gadgetbridge.org/gadgets/) for complete compatibility.

---

## Getting Started

### 1. Install

*(Build instructions coming soon — this is an early fork. For now, build from source with Android Studio.)*

### 2. Pair Your Device

Follow the standard [Gadgetbridge pairing guide](https://gadgetbridge.org/basics/pairing/). For Xiaomi devices, you'll need to extract your auth key — see [Gadgetbridge auth key docs](https://codeberg.org/Freeyourgadget/Gadgetbridge/wiki/Pairing-types-and-Auth-Key).

### 3. Configure Incident Providers

Open **Settings → Incident Management** and add your API keys:
- **PagerDuty:** Personal API Token + your user email
- **Opsgenie:** API Key (Integration or User scope)
- **Rootly:** API Token

### 4. Set Severity Rules

Go to **Settings → Notification Filters → On-Call Rules**:
- Enable **PagerDuty** app notifications
- Set inclusive filters for `P1`, `P2`, or your team's severity tags
- Assign vibration patterns per severity level
- Enable **wrist actions** (Ack/Escalate/Resolve buttons on the band)

---

## How Wrist Actions Work

When a PagerDuty incident arrives:

1. IncidentBridge intercepts the notification via Android's `NotificationListenerService`
2. Parses severity and incident ID from the notification payload
3. Sends the notification to your smartwatch **with reply options**: `[Ack]` `[Esc]` `[Res]`
4. You tap **Ack** on your wrist
5. The watch sends the reply back to IncidentBridge
6. IncidentBridge calls `POST https://api.pagerduty.com/incidents/{id}/acknowledge`
7. Your incident is acknowledged — no phone unlock needed

---

## Architecture

```
PagerDuty/Opsgenie app on phone
    ↓ posts notification
Android NotificationListenerService
    ↓
IncidentBridge NotificationFilter
    ↓ severity parsing, incident ID extraction
Protobuf notification builder (Gadgetbridge layer)
    ↓ encrypted BLE transmission
Smartwatch (Mi Band 9 / Bangle.js / Pebble)
    ↓ user taps reply button
IncidentBridge IncidentResponder
    ↓ HTTP REST API call
PagerDuty / Opsgenie / Rootly servers
```

---

## Contributing

This is a community fork. We welcome contributions, especially:

- **New incident platform integrations** (Datadog, New Relic, etc.)
- **Device-specific vibration patterns** (Morse code, custom sequences)
- **Vela JS quick apps** for richer incident displays on supported bands
- **Translation** of the new UI strings

Please open issues and PRs on this GitHub repo.

### Stay in sync with upstream

```bash
# Pull latest Gadgetbridge changes
git fetch upstream
git merge upstream/master
```

---

## License

IncidentBridge inherits Gadgetbridge's [AGPLv3](LICENSE) license. All original Gadgetbridge code remains under its original license and copyright. New code added for incident management features is also AGPLv3.

---

## Acknowledgments

IncidentBridge is a fork of [Gadgetbridge](https://codeberg.org/Freeyourgadget/Gadgetbridge/) by the FreeYourGadget team. Huge thanks to Andreas Shimokawa, Carsten Pfeiffer, Daniele Gobbetti, Petr Vaněk, and all Gadgetbridge contributors for building the solid foundation this project stands on.

- [Gadgetbridge Homepage](https://gadgetbridge.org)
- [Gadgetbridge Documentation](https://gadgetbridge.org/internals/development/project-overview/)
- [Original Issue Tracker](https://codeberg.org/Freeyourgadget/Gadgetbridge/issues)

---

## Disclaimer

This is **not an official PagerDuty, Opsgenie, or Rootly app**. It is an independent open-source project that uses their public REST APIs. Use at your own risk. Always test in a non-production environment first.
