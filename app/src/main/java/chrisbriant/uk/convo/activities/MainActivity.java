package chrisbriant.uk.convo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import chrisbriant.uk.convo.R;
import objects.RoomItem;
import objects.RoomList;
import services.ServerConn;
import services.SockNotifier;

public class MainActivity extends AppCompatActivity {
    private ServerConn conn;
    private SockNotifier notifier;
    private SharedPreferences sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = this.getSharedPreferences(this.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        conn = ServerConn.getInstance(this);

        notifier = SockNotifier.getInstance();

        Context ctx = this;

        notifier.setListener(new SockNotifier.MessageEventListener() {
            @Override
            public void onRegister(String id) {
                Log.d("Registration", id);
                editor.putString("id",id);
                editor.apply();
            }
            @Override
            public void onSetName(String name) {
                Log.d("Setting Name", name);
                editor.putString("name",name);
                editor.apply();
                //Start the room list activity
                Intent intent = new Intent(ctx, RoomListActivity.class);
                ctx.startActivity(intent);
            }

            @Override
            public void onRoomList(String rooms) {
                RoomList roomList = RoomList.getInstance();
                try {
                    roomList.loadRooms(rooms);
                    ArrayList<RoomItem> roomsForAdapter = roomList.loadRooms(rooms);
                    Log.d("ROOMLIST", String.valueOf(roomList.size()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAuthFailed() {

            }

            @Override
            public void onEnterRoom(String roomName, String clientId, String clientName, JSONArray membersJson) {

            }

            @Override
            public void onRoomMessage(String clientId, String clientName, String message) {

            }

        });

        //UI
        EditText mainEdtName = findViewById(R.id.mainEdtName);
        Button mainBtnSend = findViewById(R.id.mainBtnSend);

        mainBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject payload = new JSONObject();
                try {
                    payload.put("type", "name");
                    payload.put("client_id", sharedPrefs.getString("id",""));
                    payload.put("name",mainEdtName.getText());
                    conn.send(payload.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}