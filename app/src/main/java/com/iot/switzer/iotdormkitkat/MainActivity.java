package com.iot.switzer.iotdormkitkat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.iot.switzer.iotdormkitkat.activities.AddPresetActivity;
import com.iot.switzer.iotdormkitkat.activities.EditPresetsActivity;
import com.iot.switzer.iotdormkitkat.data.entry.IoTPresetButton;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.data.entry.IoTVariablesBase;
import com.iot.switzer.iotdormkitkat.music.IoTSpotifyObject;
import com.iot.switzer.iotdormkitkat.network.IoTManager;
import com.iot.switzer.iotdormkitkat.presets.Preset;
import com.iot.switzer.iotdormkitkat.presets.PresetButtonGroup;
import com.iot.switzer.iotdormkitkat.presets.PresetManager;
import com.iot.switzer.iotdormkitkat.services.DeviceDiscoveryService;
import com.iot.switzer.iotdormkitkat.ui.DeviceUITable;
import com.iot.switzer.iotdormkitkat.ui.EntryValueUITable;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

public class MainActivity extends AppCompatActivity{

    ViewGroup presetScrollView;
    private static final int SPOTIFY_REQUEST_CODE = 1337;

    private static final String CLIENT_ID = "08eb8a2785a945ccba2717f4c191131a";

    private static final String REDIRECT_URI = "DotMusicService://callback";

    private IoTSpotifyObject spotifyObject;

    private  PresetButtonGroup bg;

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent msgIntent = new Intent(this, DeviceDiscoveryService.class);
        stopService(msgIntent);
        IoTManager.getInstance().destroy();

        if(spotifyObject != null)
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PresetManager.getInstance().setContext(getApplicationContext());

        Log.d("START", "Start of program!");

        startDiscoveryService();
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        presetScrollView = (ViewGroup) findViewById(R.id.presetScrollView).findViewById(R.id.presetLinearLayout);
        bg = new PresetButtonGroup(presetScrollView.getContext());
        loadPresets();


        ViewGroup layout = (ViewGroup) findViewById(R.id.main_layout);
        ScrollView scrollingView = (ScrollView) layout.findViewById(R.id.tableScrollView);

        DeviceUITable table = new DeviceUITable(scrollingView.getContext());
        IoTManager.getInstance().addListener(table);
        scrollingView.addView(table);
    }

    private void startDiscoveryService() {
        Intent msgIntent = new Intent(this, DeviceDiscoveryService.class);
        msgIntent.putExtra(DeviceDiscoveryService.PARAM_IN_MSG, "START");
        startService(msgIntent);
    }

    private void loadPresets()
    {
        presetScrollView.removeAllViews();
        bg.reload();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(20,0,20,0);

        for(IoTPresetButton p : bg.getButtons())
        {
            presetScrollView.addView(p,layoutParams);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == AddPresetActivity.CREATE_NEW_PRESET)
        {
            if(resultCode == AddPresetActivity.PRESET_ADDED)
            {
                loadPresets();
            }
            else
            {

            }
        }
        else if(requestCode == SPOTIFY_REQUEST_CODE)
        {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {

                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Player player = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                    @Override
                    public void onInitialized(Player player) {
                        Toast.makeText(getApplicationContext(),"Spotify Login Successful",Toast.LENGTH_SHORT).show();

                        spotifyObject = new IoTSpotifyObject(player, "Spotify Player");
                       IoTManager.getInstance().addDevice(spotifyObject);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
            else
            {
                Log.d("MAINACTIVITY", "No Token: "+response.getType().name() + " "+response.getError());
            }
        }
        else if(requestCode == EditPresetsActivity.EDIT_PRESETS)
        {
            if(resultCode == EditPresetsActivity.PRESETS_EDITED)
            {
                loadPresets();
            }
        }
    }

    private void launchSpotifyLogin()
    {
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, SPOTIFY_REQUEST_CODE, request);
    }

    private void launchAddPresetActivity()
    {
        Intent intent = new Intent(this, AddPresetActivity.class);
        startActivityForResult(intent,AddPresetActivity.CREATE_NEW_PRESET);
    }

    private void launchEditPresetsActivity()
    {
        Intent intent = new Intent(this, EditPresetsActivity.class);
        startActivityForResult(intent,EditPresetsActivity.EDIT_PRESETS);
    }
}

