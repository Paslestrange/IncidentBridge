package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.incident.VibrationRule;
import nodomain.freeyourgadget.gadgetbridge.incident.VibrationRuleStore;
import nodomain.freeyourgadget.gadgetbridge.util.GB;
import android.widget.Toast;

public class CustomVibrationRulesActivity extends AbstractGBActivity {

    private static final int REQUEST_ADD_RULE = 1;
    private static final int REQUEST_EDIT_RULE = 2;

    private RecyclerView recyclerView;
    private VibrationRuleAdapter adapter;
    private List<VibrationRule> rules = new ArrayList<>();
    private TextView emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_vibration_rules);

        recyclerView = findViewById(R.id.vibration_rules_list);
        emptyView = findViewById(R.id.empty_view);
        FloatingActionButton fab = findViewById(R.id.fab_add_rule);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new VibrationRuleAdapter();
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> {
            Intent intent = new Intent(CustomVibrationRulesActivity.this, AddEditVibrationRuleActivity.class);
            startActivityForResult(intent, REQUEST_ADD_RULE);
        });

        loadRules();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRules();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            loadRules();
        }
    }

    private void loadRules() {
        rules = VibrationRuleStore.loadRules();
        adapter.notifyDataSetChanged();
        updateEmptyView();
    }

    private void updateEmptyView() {
        if (rules.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void testOnDevice(VibrationRule rule) {
        DeviceManager deviceManager = ((GBApplication) getApplication()).getDeviceManager();
        List<GBDevice> devices = deviceManager.getDevices();
        GBDevice targetDevice = null;
        for (GBDevice device : devices) {
            if (device.isConnected() && device.isInitialized()) {
                targetDevice = device;
                break;
            }
        }
        if (targetDevice == null) {
            GB.toast(this, R.string.pref_toast_no_device_connected, Toast.LENGTH_SHORT, GB.ERROR);
            return;
        }
        Intent intent = new Intent("nodomain.freeyourgadget.gadgetbridge.TEST_VIBRATION");
        intent.putExtra("device_address", targetDevice.getAddress());
        intent.putExtra("pattern", rule.pattern);
        sendBroadcast(intent);
        GB.toast(this, R.string.pref_toast_testing_on_device, Toast.LENGTH_SHORT, GB.INFO);
    }

    private class VibrationRuleAdapter extends RecyclerView.Adapter<VibrationRuleAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vibration_rule, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            VibrationRule rule = rules.get(position);
            holder.nameText.setText(rule.name);
            holder.keywordText.setText(getString(R.string.pref_summary_vibration_rule_keyword, rule.keyword));
            holder.patternText.setText(getString(R.string.pref_summary_test_vibration, Arrays.toString(rule.pattern)));
            holder.repeatText.setVisibility(rule.repeatUntilAcked ? View.VISIBLE : View.GONE);
            holder.enabledSwitch.setChecked(rule.enabled);
            holder.enabledSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                rule.enabled = isChecked;
                VibrationRuleStore.updateRule(rule);
            });
            holder.testButton.setOnClickListener(v -> testOnDevice(rule));
            holder.editButton.setOnClickListener(v -> {
                Intent intent = new Intent(CustomVibrationRulesActivity.this, AddEditVibrationRuleActivity.class);
                intent.putExtra("rule_id", rule.id);
                startActivityForResult(intent, REQUEST_EDIT_RULE);
            });
            holder.deleteButton.setOnClickListener(v -> {
                VibrationRuleStore.deleteRule(rule.id);
                rules.remove(position);
                notifyItemRemoved(position);
                updateEmptyView();
            });
        }

        @Override
        public int getItemCount() {
            return rules.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView nameText;
            TextView keywordText;
            TextView patternText;
            TextView repeatText;
            Switch enabledSwitch;
            ImageButton testButton;
            ImageButton editButton;
            ImageButton deleteButton;

            ViewHolder(View itemView) {
                super(itemView);
                nameText = itemView.findViewById(R.id.rule_name);
                keywordText = itemView.findViewById(R.id.rule_keyword);
                patternText = itemView.findViewById(R.id.rule_pattern);
                repeatText = itemView.findViewById(R.id.rule_repeat);
                enabledSwitch = itemView.findViewById(R.id.rule_enabled);
                testButton = itemView.findViewById(R.id.btn_test);
                editButton = itemView.findViewById(R.id.btn_edit);
                deleteButton = itemView.findViewById(R.id.btn_delete);
            }
        }
    }
}
