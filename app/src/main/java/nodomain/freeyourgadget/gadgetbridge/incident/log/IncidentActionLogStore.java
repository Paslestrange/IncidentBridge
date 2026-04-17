package nodomain.freeyourgadget.gadgetbridge.incident.log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.incident.IncidentConstants;

public class IncidentActionLogStore {
    private static final Logger LOG = LoggerFactory.getLogger(IncidentActionLogStore.class);

    public static void addEntry(String provider, String incidentId, String action, String status, long responseTimeMs) {
        try {
            List<IncidentActionLog> logs = getEntries();
            logs.add(new IncidentActionLog(System.currentTimeMillis(), provider, incidentId, action, status, responseTimeMs));
            
            while (logs.size() > IncidentConstants.MAX_LOG_ENTRIES) {
                logs.remove(0);
            }
            
            saveEntries(logs);
        } catch (Exception e) {
            LOG.error("Failed to add action log entry", e);
        }
    }

    public static List<IncidentActionLog> getEntries() {
        List<IncidentActionLog> logs = new ArrayList<>();
        try {
            String json = GBApplication.getPrefs().getString(IncidentConstants.PREF_ACTION_LOG, "[]");
            JSONArray array = new JSONArray(json);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                logs.add(new IncidentActionLog(
                    obj.getLong("timestamp"),
                    obj.getString("provider"),
                    obj.getString("incidentId"),
                    obj.getString("action"),
                    obj.getString("status"),
                    obj.getLong("responseTimeMs")
                ));
            }
        } catch (JSONException e) {
            LOG.error("Failed to parse action log", e);
        }
        return logs;
    }

    public static void clear() {
        GBApplication.getPrefs().getPreferences().edit().putString(IncidentConstants.PREF_ACTION_LOG, "[]").apply();
    }

    private static void saveEntries(List<IncidentActionLog> logs) {
        try {
            JSONArray array = new JSONArray();
            for (IncidentActionLog log : logs) {
                JSONObject obj = new JSONObject();
                obj.put("timestamp", log.timestamp);
                obj.put("provider", log.provider);
                obj.put("incidentId", log.incidentId);
                obj.put("action", log.action);
                obj.put("status", log.status);
                obj.put("responseTimeMs", log.responseTimeMs);
                array.put(obj);
            }
            GBApplication.getPrefs().getPreferences().edit().putString(IncidentConstants.PREF_ACTION_LOG, array.toString()).apply();
        } catch (JSONException e) {
            LOG.error("Failed to save action log", e);
        }
    }
}
