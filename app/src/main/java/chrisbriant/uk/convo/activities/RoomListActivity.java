package chrisbriant.uk.convo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import adapters.RoomRecycler;
import chrisbriant.uk.convo.R;
import objects.RoomItem;
import objects.RoomList;
import services.ServerConn;
import services.SockNotifier;

public class RoomListActivity extends AppCompatActivity {
    private ServerConn conn;
    private SockNotifier notifier;
    private RoomList roomList;
    private RecyclerView recyclerView;
    private RoomRecycler roomRecycler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room_list);

        SharedPreferences sharedPrefs = this.getSharedPreferences(this.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        conn = ServerConn.getInstance(this);
        notifier = SockNotifier.getInstance();
        roomList = RoomList.getInstance();

        //Recycler
        Context ctx = this;

        recyclerView = findViewById(R.id.rmRecycRooms);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<RoomItem> roomsForAdapter = new ArrayList<RoomItem>();
        roomRecycler = new RoomRecycler(this,roomList.getRoomList(), conn);
        recyclerView.setAdapter(roomRecycler);
        roomRecycler.notifyDataSetChanged();



        notifier.setListener(new SockNotifier.MessageEventListener() {
            @Override
            public void onRegister(String id) {

            }

            @Override
            public void onSetName(String name) {

            }

            @Override
            public void onRoomList(String rooms) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ArrayList<RoomItem> roomsForAdapter = roomList.loadRooms(rooms);
                            Log.d("ROOMLIST", String.valueOf(roomList.size()));
                            roomRecycler.setRoomList(roomsForAdapter);
                            Log.d("CLASS", this.getClass().toString());
                            roomRecycler.notifyDataSetChanged();
                            Log.d("DID I WORK", "worked");
                        } catch (Exception e) {
                            Log.d("EXCEPTION", e.toString());
                        }
                    }
                });
                Log.d("ROOM LIST", "Room list triggered");

            }

            @Override
            public void onAuthFailed() {

            }

            @Override
            public void onEnterRoom(String roomName) {

            }
        });

        //UI
        EditText rmListEdtRoomName = findViewById(R.id.rmListEdtRoomName);
        CheckBox rmListChkSecure = findViewById(R.id.rmListChkSecure);
        EditText rmListEdtPassword = findViewById(R.id.rmListEdtPassword);
        Button rmListBtnSend = findViewById(R.id.rmListBtnSend);


        rmListBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject payload = new JSONObject();
                try {
                    payload.put("type", "create_room");
                    payload.put("client_id", sharedPrefs.getString("id",""));
                    payload.put("name",rmListEdtRoomName.getText());
                    if(rmListChkSecure.isChecked()) {
                        payload.put("secure",true);
                        payload.put("password", rmListEdtPassword.getText());
                    } else {
                        payload.put("secure",false);
                        payload.put("password", "");
                    }
                    //Todo Tell the room if it is secure or not
                    //Need to change UI to allow password input
                    conn.send(payload.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        rmListEdtRoomName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                rmListChkSecure.setVisibility(View.VISIBLE);
                return false;
            }
        });

        rmListChkSecure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    rmListEdtPassword.setVisibility(View.VISIBLE);
                } else {
                    rmListEdtPassword.setVisibility(View.GONE);
                }
            }
        });

    }
}