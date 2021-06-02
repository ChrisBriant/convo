package services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class ServerConn {

    private static ServerConn single_instance = null;

    private OkHttpClient client;
    private SharedPreferences sharedPrefs;
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");
    private static final String URL = "wss://conversation.chrisbriant.uk:8081/ws";
    private WebSocket sock;

    private ServerConn(Context ctx) {
        //Create share preferences
        //sharedPrefs = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key),Context.MODE_PRIVATE);
        SSLContext sslContext = null;

        //Add notifier
        SockNotifier notifier = SockNotifier.getInstance();

        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        if (sslContext != null) {
            // Create a trust manager that does not validate certificate chains
            final X509TrustManager trustManager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                }

                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[]{};
                }
            };
            try {
                sslContext.init(null, new TrustManager[]{trustManager}, null);
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            Log.d("HELLO", "Hello");
            final Request request = new Request.Builder().url(URL)
                    .build();

            client = new OkHttpClient.Builder()
                    .sslSocketFactory(sslSocketFactory, trustManager)
                    .build();

            WebSocketListener webSocketListener = new WebSocketListener() {
                @Override
                public void onOpen(@NotNull WebSocket webSocket, @NotNull Response response) {
                    //super.onOpen(webSocket, response);
                    Log.d("Connecting", response.message());
                    sock = webSocket;
                }

                @Override
                public void onClosing(@NotNull WebSocket webSocket, int code, @NotNull String reason) {
                    super.onClosing(webSocket, code, reason);
                }

                @Override
                public void onFailure(@NotNull WebSocket webSocket, @NotNull Throwable t, @Nullable Response response) {
                    //super.onFailure(webSocket, t, response);
                    Log.d("Failure", "The websocket has failed");
                    JSONObject data = new JSONObject();
                    try {
                        data.put("type","socket_closed");
                        notifier.sendMessage(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onMessage(@NotNull WebSocket webSocket, @NotNull String text) {
                    //super.onMessage(webSocket, text);
                    Log.d("Message", text);
                    try {
                        JSONObject data = new JSONObject(text);
                        notifier.sendMessage(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }

            };

            client.newWebSocket(request, webSocketListener);
            client.dispatcher().executorService().shutdown();
        }
    }


    public static ServerConn getInstance(Context ctx)
    {
        if (single_instance == null)
            single_instance = new ServerConn(ctx);

        return single_instance;
    }

    public void send(String json) {
        Log.d("Sending", json);
        sock.send(json);
    }

}
