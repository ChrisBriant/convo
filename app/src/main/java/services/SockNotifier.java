package services;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SockNotifier {
    private static SockNotifier single_instance = null;

    public interface MessageEventListener {
        public void onRegister(String id);
        public void onSetName(String name);
        public void onRoomList(String rooms);
        public void onAuthFailed();
        public void onEnterRoom(String roomName,String clientId,String clientName,JSONArray members);
        public void onRoomMessage(String clientId,String clientName,String message);
        public void onSocketClosed();
    }

    // Member variable was defined earlier
    private MessageEventListener listener;

    public SockNotifier() {
        this.listener = null;
    }

    public void setListener(MessageEventListener listener) {
        this.listener = listener;
    }


    public void sendMessage(JSONObject data) throws JSONException {
        Log.d("SEND MESSAGE",data.getString("type"));
        switch (data.getString("type")) {
            case "register":
                listener.onRegister(data.getString("yourid"));
                break;
            case "set_name":
                listener.onSetName(data.getString("message"));
                break;
            case "room_list":
                listener.onRoomList(data.getString("rooms"));
                break;
            case "room_access_denied":
                listener.onAuthFailed();
                break;
            case "room_entrance":
                JSONObject client = new JSONObject(data.getString("client"));
                JSONArray members = new JSONArray(data.getString("members"));
                listener.onEnterRoom(data.getString("name"),client.getString("id"),client.getString("name"),members);
                break;
            case "room_message":
                JSONObject rmMessageclientFrom = new JSONObject(data.getString("client"));
                listener.onRoomMessage(rmMessageclientFrom.getString("id"),rmMessageclientFrom.getString("name"), data.getString("message"));
                break;
            case "socket_closed":
                listener.onSocketClosed();
                break;
        }
    }

    public static SockNotifier getInstance()
    {
        if (single_instance == null)
            single_instance = new SockNotifier();

        return single_instance;
    }


}
