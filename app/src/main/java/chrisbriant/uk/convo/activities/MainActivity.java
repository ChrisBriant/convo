package chrisbriant.uk.convo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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

        //Handle the error message if there is one
        Intent intent = getIntent();
        String socketFailureMessage = "";
        try {
            socketFailureMessage = (String) intent.getExtras().get("socketFailure");
        } catch (Exception e) {
            socketFailureMessage = "";
            e.printStackTrace();
        }


        if(!socketFailureMessage.equals("")) {
            //Re-initiate the connection if it has disconnected
           conn = ServerConn.reconnect(this);
        } else {
            conn = ServerConn.getInstance(this);
        }

        TextView mainTxtErr = findViewById(R.id.mainTxtErr);
        mainTxtErr.setText(socketFailureMessage);

        sharedPrefs = this.getSharedPreferences(this.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

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

            @Override
            public void onSocketClosed() {

            }

            @Override
            public void onPrivateMessage(String sender, String message) {

            }

        });

        //UI
        EditText mainEdtName = findViewById(R.id.mainEdtName);
        mainEdtName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TOUCHED", "NAME");
                if(mainEdtName.getText().toString().equals("Enter Name")) {
                    mainEdtName.setText("");
                }
                return false;
            }
        });
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