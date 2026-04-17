package nodomain.freeyourgadget.gadgetbridge.incident;

public final class IncidentConstants {
    private IncidentConstants() {}

    public static final String ACTION_ACK = "Ack";
    public static final String ACTION_ESC = "Esc";
    public static final String ACTION_RES = "Res";

    public static final String SEV_P1 = "P1";
    public static final String SEV_P2 = "P2";
    public static final String SEV_P3 = "P3";
    public static final String SEV_P4 = "P4";
    public static final String SEV_CRITICAL = "CRITICAL";
    public static final String SEV_HIGH = "HIGH";
    public static final String SEV_MEDIUM = "MEDIUM";
    public static final String SEV_LOW = "LOW";
    public static final String SEV_SEV1 = "SEV1";
    public static final String SEV_SEV2 = "SEV2";
    public static final String SEV_SEV3 = "SEV3";

    public static final String PKG_PAGERDUTY = "com.pagerduty.android";
    public static final String PKG_OPSGENIE = "com.opsgenie.app";
    public static final String PKG_ROOTLY = "com.rootly.mobile";

    public static final String PREF_INCIDENT_MANAGEMENT_ENABLED = "pref_incident_management_enabled";
    public static final String PREF_PAGERDUTY_API_TOKEN = "pagerduty_api_token";
    public static final String PREF_PAGERDUTY_USER_EMAIL = "pagerduty_user_email";
    public static final String PREF_OPSGENIE_API_KEY = "opsgenie_api_key";
    public static final String PREF_ROOTLY_API_TOKEN = "rootly_api_token";
    public static final String PREF_TEST_PAGERDUTY_CONNECTION = "test_pagerduty_connection";
    public static final String PREF_TEST_OPSGENIE_CONNECTION = "test_opsgenie_connection";
    public static final String PREF_TEST_ROOTLY_CONNECTION = "test_rootly_connection";
    public static final String PREF_INCIDENT_APP_WHITELIST = "incident_app_whitelist";
    public static final String PREF_INCIDENT_CONTENT_FILTER = "incident_content_filter";

    public static final String EXTRA_INCIDENT_ID = "incident_id";

    public static final String LOG_TAG = "IncidentBridge";
    public static final int MAX_LOG_ENTRIES = 500;
    public static final String PREF_ACTION_LOG = "incident_action_log";

    public static final String PREF_VIBRATION_P1 = "vibration_pattern_p1";
    public static final String PREF_VIBRATION_P2 = "vibration_pattern_p2";
    public static final String PREF_VIBRATION_P3 = "vibration_pattern_p3";
    public static final String PREF_VIBRATION_P4 = "vibration_pattern_p4";

    public static final String PREF_ONCALL_SCHEDULE_ENABLED = "oncall_schedule_enabled";
    public static final String PREF_ONCALL_START_TIME = "oncall_start_time";
    public static final String PREF_ONCALL_END_TIME = "oncall_end_time";

    public static final String PREF_WEBHOOK_URL = "webhook_url";
    public static final String PREF_WEBHOOK_ENABLED = "webhook_enabled";

    public static final String PREF_ESCALATION_CONTACTS = "escalation_contacts";

    public static final String URL_PAGERDUTY_API = "https://api.pagerduty.com/incidents";
    public static final String URL_OPSGENIE_API = "https://api.opsgenie.com/v2/alerts";
    public static final String URL_ROOTLY_API = "https://api.rootly.com/v1/incidents";
    public static final String URL_PAGERDUTY_API_TEST = "https://api.pagerduty.com/users/me";
    public static final String URL_OPSGENIE_API_TEST = "https://api.opsgenie.com/v2/users";
    public static final String URL_ROOTLY_API_TEST = "https://api.rootly.com/v1/incidents?limit=1";
}
