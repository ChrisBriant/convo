package services;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SockNotifier {
    private static SockNotifier single_instance = null;

    public interface MessageEventListener {
        public void onRegister(String id);
        public void onSetName(String name);
        public void onRoomList(String rooms);
        public void onAuthFailed();
        public void onEnterRoom(String roomName);
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
            case "room_entrance":
                listener.onEnterRoom(data.getString("name"));
        }
    }

    public static SockNotifier getInstance()
    {
        if (single_instance == null)
            single_instance = new SockNotifier();

        return single_instance;
    }


}
