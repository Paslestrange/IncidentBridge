package nodomain.freeyourgadget.gadgetbridge.incident;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;

public class ContactFilter {

    public static boolean isEscalationContact(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }

        Set<String> contacts = loadEscalationContacts();
        String normalized = normalizeNumber(phoneNumber);

        for (String contact : contacts) {
            if (normalized.equals(normalizeNumber(contact))) {
                return true;
            }
        }
        return false;
    }

    public static Set<String> loadEscalationContacts() {
        String stored = GBApplication.getPrefs().getString(IncidentConstants.PREF_ESCALATION_CONTACTS, "");
        if (stored.isEmpty()) {
            return new HashSet<>();
        }
        return new HashSet<>(Arrays.asList(stored.split(",")));
    }

    public static void saveEscalationContacts(Set<String> contacts) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String contact : contacts) {
            if (!first) sb.append(",");
            sb.append(contact.trim());
            first = false;
        }
        GBApplication.getPrefs().getPreferences().edit()
                .putString(IncidentConstants.PREF_ESCALATION_CONTACTS, sb.toString()).apply();
    }

    private static String normalizeNumber(String number) {
        return number.replaceAll("[^\\d]", "");
    }
}
