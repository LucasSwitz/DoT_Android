package com.iot.switzer.iotdormkitkat.music;

import android.util.Log;

import com.iot.switzer.iotdormkitkat.data.entry.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.iot.switzer.iotdormkitkat.devices.IoTDeviceController;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Lucas Switzer on 7/3/2016.
 */
public class IoTSpotifyObject extends IoTDeviceController implements PlayerNotificationCallback, ConnectionStateCallback {

    static String PLAYLIST_ENTRY_KEY = "Playlist URI";
    private Player player;

    public IoTSpotifyObject(Player player, String token) {
        super("Local", token, 0, new ArrayList<>(Arrays.asList(new SubscriptionDescription(PLAYLIST_ENTRY_KEY, SubscriptionDescription.SubscriptionType.STRING))));
        this.player = player;
        player.addPlayerNotificationCallback(this);
        player.addConnectionStateCallback(this);
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry) {
        player.setShuffle(true);
        Log.d("SPOTIFY", "Playing: " + entry.getValAsString());
        player.play(entry.getValAsString());
    }

    @Override
    public List<SubscriptionDescription> getSubscriptions() {
        return this.getDeviceDescription().subscriptionDescriptions;
    }

    public void destroy() {
        Spotify.destroyPlayer(this);
    }

    @Override
    public void onLoggedIn() {

    }

    @Override
    public void onLoggedOut() {

    }

    @Override
    public void onLoginFailed(Throwable throwable) {

    }

    @Override
    public void onTemporaryError() {

    }

    @Override
    public void onConnectionMessage(String s) {

    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {

    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {

    }

    @Override
    public void write(byte[] out) throws IOException {

    }

    @Override
    protected void stopDevice() {
        destroy();
    }
}
