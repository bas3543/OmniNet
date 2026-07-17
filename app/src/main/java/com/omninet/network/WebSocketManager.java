package com.omninet.network;

import android.content.Context;
import com.omninet.core.OmniID;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class WebSocketManager {
    private static WebSocketManager instance;
    private WebSocketClient webSocketClient;
    private List<WebSocketListener> listeners = new ArrayList<>();
    private Context context;
    private static final String WS_URL = "wss://api.omninet.local:8081/ws";

    public interface WebSocketListener {
        void onMessageReceived(String message);
        void onConnected();
        void onDisconnected();
        void onError(String error);
    }

    public static synchronized WebSocketManager getInstance(Context context) {
        if (instance == null) {
            instance = new WebSocketManager(context);
        }
        return instance;
    }

    private WebSocketManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public void connect() {
        try {
            URI uri = new URI(WS_URL + "?userId=" + OmniID.get());
            webSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    notifyConnected();
                }

                @Override
                public void onMessage(String message) {
                    notifyMessageReceived(message);
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    notifyDisconnected();
                }

                @Override
                public void onError(Exception ex) {
                    notifyError(ex.getMessage());
                }
            };
            webSocketClient.connect();
        } catch (Exception e) {
            notifyError(e.getMessage());
        }
    }

    public void disconnect() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }

    public void sendMessage(String message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
        }
    }

    public void addListener(WebSocketListener listener) {
        listeners.add(listener);
    }

    public void removeListener(WebSocketListener listener) {
        listeners.remove(listener);
    }

    private void notifyMessageReceived(String message) {
        for (WebSocketListener listener : listeners) {
            listener.onMessageReceived(message);
        }
    }

    private void notifyConnected() {
        for (WebSocketListener listener : listeners) {
            listener.onConnected();
        }
    }

    private void notifyDisconnected() {
        for (WebSocketListener listener : listeners) {
            listener.onDisconnected();
        }
    }

    private void notifyError(String error) {
        for (WebSocketListener listener : listeners) {
            listener.onError(error);
        }
    }
}
