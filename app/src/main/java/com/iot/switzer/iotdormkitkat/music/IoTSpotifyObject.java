package com.iot.switzer.iotdormkitkat.music;

import com.iot.switzer.iotdormkitkat.data.IoTSubscriber;
import com.iot.switzer.iotdormkitkat.data.SubscriptionDescription;
import com.iot.switzer.iotdormkitkat.data.entry.IoTSubscriptionEntry;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas Switzer on 7/3/2016.
 */
public class IoTSpotifyObject implements IoTSubscriber,PlayerNotificationCallback, ConnectionStateCallback {

    private Player player;
    public IoTSpotifyObject(Player player)
    {
        this.player = player;
        player.addPlayerNotificationCallback(this);
        player.addConnectionStateCallback(this);
    }

    @Override
    public void onSubscriptionUpdate(IoTSubscriptionEntry entry) {
        player.setShuffle(true);
        player.play(entry.getValAsString());
    }

    @Override
    public List<SubscriptionDescription> getSubscriptions() {
        ArrayList<SubscriptionDescription> out = new ArrayList();
        out.add(new SubscriptionDescription("Playlist URI", SubscriptionDescription.SubscriptionType.STRING));
        return out;
    }

    public void destroy()
    {
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
}
