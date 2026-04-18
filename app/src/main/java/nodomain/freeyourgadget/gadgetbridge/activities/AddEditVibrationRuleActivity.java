package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.incident.VibrationRule;
import nodomain.freeyourgadget.gadgetbridge.incident.VibrationRuleStore;
import nodomain.freeyourgadget.gadgetbridge.util.GB;

public class AddEditVibrationRuleActivity extends AbstractGBActivity {

    private EditText nameInput;
    private EditText keywordInput;
    private EditText patternInput;
    private Switch repeatSwitch;
    private TextView patternHelpText;
    private Button saveButton;
    private Button cancelButton;
    private Button testButton;

    private String editingRuleId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_vibration_rule);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        nameInput = findViewById(R.id.input_rule_name);
        keywordInput = findViewById(R.id.input_rule_keyword);
        patternInput = findViewById(R.id.input_rule_pattern);
        repeatSwitch = findViewById(R.id.switch_repeat);
        patternHelpText = findViewById(R.id.text_pattern_help);
        saveButton = findViewById(R.id.btn_save);
        cancelButton = findViewById(R.id.btn_cancel);
        testButton = findViewById(R.id.btn_test_pattern);

        patternHelpText.setText(getString(R.string.vibration_pattern_help));

        String ruleId = getIntent().getStringExtra("rule_id");
        if (ruleId != null) {
            editingRuleId = ruleId;
            loadRuleForEditing(ruleId);
            setTitle(R.string.title_edit_vibration_rule);
        } else {
            setTitle(R.string.title_add_vibration_rule);
        }

        saveButton.setOnClickListener(v -> saveRule());
        cancelButton.setOnClickListener(v -> finish());
        testButton.setOnClickListener(v -> testPattern());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadRuleForEditing(String ruleId) {
        List<VibrationRule> rules = VibrationRuleStore.loadRules();
        for (VibrationRule rule : rules) {
            if (rule.id.equals(ruleId)) {
                nameInput.setText(rule.name);
                keywordInput.setText(rule.keyword);
                patternInput.setText(patternToString(rule.pattern));
                repeatSwitch.setChecked(rule.repeatUntilAcked);
                return;
            }
        }
    }

    private void saveRule() {
        String name = nameInput.getText().toString().trim();
        String keyword = keywordInput.getText().toString().trim();
        String patternStr = patternInput.getText().toString().trim();

        if (name.isEmpty()) {
            nameInput.setError(getString(R.string.error_rule_name_required));
            return;
        }
        if (keyword.isEmpty()) {
            keywordInput.setError(getString(R.string.error_keyword_required));
            return;
        }

        int[] pattern = parsePattern(patternStr);
        if (pattern == null || pattern.length == 0) {
            patternInput.setError(getString(R.string.error_invalid_pattern));
            return;
        }

        boolean repeat = repeatSwitch.isChecked();
        int interval = repeat ? 15000 : 0;

        if (editingRuleId != null) {
            VibrationRule updatedRule = new VibrationRule(editingRuleId, name, keyword, pattern, repeat, interval, true);
            VibrationRuleStore.updateRule(updatedRule);
        } else {
            VibrationRule newRule = new VibrationRule(name, keyword, pattern, repeat, interval, true);
            VibrationRuleStore.addRule(newRule);
        }

        setResult(RESULT_OK);
        finish();
    }

    private void testPattern() {
        String patternStr = patternInput.getText().toString().trim();
        int[] pattern = parsePattern(patternStr);
        if (pattern == null || pattern.length == 0) {
            patternInput.setError(getString(R.string.error_invalid_pattern));
            return;
        }

        testVibrationOnPhone(pattern);
    }

    private void testVibrationOnPhone(int[] pattern) {
        android.os.Vibrator vibrator = (android.os.Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator == null || !vibrator.hasVibrator()) {
            GB.toast(this, R.string.pref_toast_no_vibrator, Toast.LENGTH_SHORT, GB.ERROR);
            return;
        }

        long[] timings = new long[pattern.length];
        for (int i = 0; i < pattern.length; i++) {
            timings[i] = pattern[i];
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(android.os.VibrationEffect.createWaveform(timings, -1));
            } else {
                vibrator.vibrate(timings, -1);
            }
            GB.toast(this, R.string.pref_toast_vibration_tested, Toast.LENGTH_SHORT, GB.INFO);
        } catch (Exception e) {
            GB.toast(this, R.string.pref_toast_vibration_failed, Toast.LENGTH_SHORT, GB.ERROR);
        }
    }

    private int[] parsePattern(String patternStr) {
        if (patternStr.isEmpty()) {
            return null;
        }
        try {
            String[] parts = patternStr.split(",");
            List<Integer> values = new ArrayList<>();
            for (String part : parts) {
                part = part.trim();
                if (!part.isEmpty()) {
                    values.add(Integer.parseInt(part));
                }
            }
            int[] pattern = new int[values.size()];
            for (int i = 0; i < values.size(); i++) {
                pattern[i] = values.get(i);
            }
            return pattern;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private String patternToString(int[] pattern) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < pattern.length; i++) {
            if (i > 0) sb.append(", ");
            sb.append(pattern[i]);
        }
        return sb.toString();
    }
}
