package com.iot.switzer.iotdormkitkat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.activities.AddPresetActivity;
import com.iot.switzer.iotdormkitkat.activities.EditPresetsActivity;
import com.iot.switzer.iotdormkitkat.data.entry.IoTPresetButton;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceController;
import com.iot.switzer.iotdormkitkat.music.IoTSpotifyObject;
import com.iot.switzer.iotdormkitkat.network.IoTManager;
import com.iot.switzer.iotdormkitkat.network.IoTNetworkListener;
import com.iot.switzer.iotdormkitkat.network.IoTNetworkStateData;
import com.iot.switzer.iotdormkitkat.presets.PresetButtonGroup;
import com.iot.switzer.iotdormkitkat.presets.PresetManager;
import com.iot.switzer.iotdormkitkat.services.DeviceDiscoveryService;
import com.iot.switzer.iotdormkitkat.ui.DeviceBlock;
import com.iot.switzer.iotdormkitkat.ui.DeviceUITable;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

public class MainActivity extends AppCompatActivity implements IoTNetworkListener {

    private static final int SPOTIFY_REQUEST_CODE = 1337;
    private static final String CLIENT_ID = "08eb8a2785a945ccba2717f4c191131a";
    private static final String REDIRECT_URI = "DotMusicService://callback";
    private ViewGroup presetScrollView;
    private DeviceUITable table;

    private IoTSpotifyObject spotifyObject;

    private PresetButtonGroup bg;

    private ViewGroup statusBlock;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PresetManager.getInstance().setContext(getApplicationContext());

        Log.d("START", "Start of program!");

        setContentView(R.layout.activity_main);

        initToolbar();
        initPresetScrollView();
        initDeviceUITable();
        initStatusBlock();
        loadPresets();
        startDiscoveryService();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initPresetScrollView() {
        presetScrollView = (ViewGroup) findViewById(R.id.presetScrollView).findViewById(R.id.presetLinearLayout);
        bg = new PresetButtonGroup(presetScrollView.getContext());
    }

    private void initDeviceUITable() {
        ViewGroup layout = (ViewGroup) findViewById(R.id.main_layout);
        ScrollView tableScrollView = (ScrollView) layout.findViewById(R.id.tableScrollView);
        table = new DeviceUITable(tableScrollView.getContext(), this);
        IoTManager.getInstance().addListener(this);
        tableScrollView.addView(table);
    }

    private void initStatusBlock() {
        statusBlock = (ViewGroup) findViewById(R.id.status_block);
    }

    private void startDiscoveryService() {

        IoTManager.getInstance().searchForDevices(this);
    }

    private void loadPresets() {
        presetScrollView.removeAllViews();
        bg.reload();

        loadPresetButtons();
    }

    private void loadPresetButtons()
    {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(20, 0, 20, 0);

        for (IoTPresetButton p : bg.getButtons()) {
            presetScrollView.addView(p, layoutParams);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent msgIntent = new Intent(this, DeviceDiscoveryService.class);
        stopService(msgIntent);
        IoTManager.getInstance().destroy();

        if (spotifyObject != null)
            spotifyObject.destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.discover_devices_menu_item:
                startDiscoveryService();
                return true;
            case R.id.add_preset_menu_item:
                launchAddPresetActivity();
                return true;
            case R.id.login_spotify_menu_item:
                launchSpotifyLogin();
                return true;
            case R.id.edit_presets_menu_item:
                launchEditPresetsActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void launchSpotifyLogin() {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request);
    }

    private void launchAddPresetActivity() {
        Intent intent = new Intent(this, AddPresetActivity.class);
        startActivityForResult(intent, AddPresetActivity.CREATE_NEW_PRESET);
    }

    private void launchEditPresetsActivity() {
        Intent intent = new Intent(this, EditPresetsActivity.class);
        startActivityForResult(intent, EditPresetsActivity.EDIT_PRESETS);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AddPresetActivity.CREATE_NEW_PRESET) {
            handleAddPresetReturn(requestCode, resultCode);
        } else if (requestCode == SPOTIFY_REQUEST_CODE) {
            handleSpotifyAuthenticationReturn(resultCode, data);
        } else if (requestCode == EditPresetsActivity.EDIT_PRESETS) {
            handleEditPresetsReturn(requestCode,resultCode);
        }
    }

    private void handleAddPresetReturn(int requestCode, int resultCode) {
        if (requestCode == AddPresetActivity.CREATE_NEW_PRESET) {
            if (resultCode == AddPresetActivity.PRESET_ADDED) {
                loadPresets();
            } else {

            }
        }
    }

    private void handleSpotifyAuthenticationReturn(int resultCode, Intent data)
    {
        AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
        if (response.getType() == AuthenticationResponse.Type.TOKEN) {

            Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
            Player player = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                @Override
                public void onInitialized(Player player) {
                    signalSuccessfulSpotifyAuthenitcation(player);
                }
                @Override
                public void onError(Throwable throwable) {

                    Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                }
            });
        } else {
            Log.d("MAINACTIVITY", "No Token: " + response.getType().name() + " " + response.getError());
        }
    }

    private void signalSuccessfulSpotifyAuthenitcation(Player player)
    {
        Toast.makeText(getApplicationContext(), "Spotify Login Successful", Toast.LENGTH_SHORT).show();

        spotifyObject = new IoTSpotifyObject(player, "Spotify Player");
        IoTManager.getInstance().addDevice(spotifyObject);
    }

    private void handleEditPresetsReturn(int requestCode, int resultCode)
    {
        if (resultCode == EditPresetsActivity.PRESETS_EDITED) {
            loadPresets();
        }
    }


    @Override
    public void onDeviceAdd(IoTDeviceController d) {
        table.add(new DeviceBlock(getApplicationContext(), d));
    }

    @Override
    public void onNetworkStateChange(IoTNetworkStateData data) {
        drawStatus(data.message);
        drawProgress(data.progress);
    }

    private void drawStatus(final String status) {
        final TextView tv = (TextView) statusBlock.findViewById(R.id.state_text_view);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(status);
            }
        });
    }

    private void drawProgress(final int progress) {
        final ProgressBar pb = (ProgressBar) statusBlock.findViewById(R.id.process_progress_bar);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                pb.setProgress(progress);
                pb.refreshDrawableState();
            }
        });

    }
}

