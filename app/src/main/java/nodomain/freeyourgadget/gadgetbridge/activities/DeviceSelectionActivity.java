package nodomain.freeyourgadget.gadgetbridge.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import nodomain.freeyourgadget.gadgetbridge.GBApplication;
import nodomain.freeyourgadget.gadgetbridge.R;
import nodomain.freeyourgadget.gadgetbridge.devices.DeviceManager;
import nodomain.freeyourgadget.gadgetbridge.impl.GBDevice;
import nodomain.freeyourgadget.gadgetbridge.util.GB;

public class DeviceSelectionActivity extends AbstractGBActivity {

    public static final String EXTRA_SELECTED_DEVICE = "selected_device_address";

    private RecyclerView recyclerView;
    private DeviceAdapter adapter;
    private List<GBDevice> devices = new ArrayList<>();
    private String currentSelection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_selection);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.title_select_device);
        }

        recyclerView = findViewById(R.id.device_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DeviceAdapter();
        recyclerView.setAdapter(adapter);

        currentSelection = getIntent().getStringExtra(EXTRA_SELECTED_DEVICE);
        loadDevices();
    }

    private void loadDevices() {
        DeviceManager deviceManager = ((GBApplication) getApplication()).getDeviceManager();
        devices = deviceManager.getDevices();
        adapter.notifyDataSetChanged();

        if (devices.isEmpty()) {
            findViewById(R.id.empty_view).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            findViewById(R.id.empty_view).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void selectDevice(GBDevice device) {
        Intent result = new Intent();
        result.putExtra(EXTRA_SELECTED_DEVICE, device.getAddress());
        setResult(RESULT_OK, result);
        finish();
    }

    private class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.ViewHolder> {

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device_selection, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            GBDevice device = devices.get(position);
            holder.nameText.setText(device.getName());
            holder.addressText.setText(device.getAddress());
            holder.statusText.setText(device.isConnected() ? R.string.device_connected : R.string.device_disconnected);
            holder.radioButton.setChecked(device.getAddress().equals(currentSelection));
            holder.itemView.setOnClickListener(v -> {
                currentSelection = device.getAddress();
                notifyDataSetChanged();
                selectDevice(device);
            });
            holder.radioButton.setOnClickListener(v -> selectDevice(device));
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            RadioButton radioButton;
            TextView nameText;
            TextView addressText;
            TextView statusText;

            ViewHolder(View itemView) {
                super(itemView);
                radioButton = itemView.findViewById(R.id.device_radio);
                nameText = itemView.findViewById(R.id.device_name);
                addressText = itemView.findViewById(R.id.device_address);
                statusText = itemView.findViewById(R.id.device_status);
            }
        }
    }
}
