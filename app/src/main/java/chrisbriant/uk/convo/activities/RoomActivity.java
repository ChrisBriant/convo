package chrisbriant.uk.convo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

import chrisbriant.uk.convo.R;
import objects.RoomItem;
import objects.RoomList;
import services.SockNotifier;

public class RoomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Intent intent = getIntent();
        String roomName = (String) intent.getExtras().get("roomName");

        //UI
        TextView chatRmTxtTitle = findViewById(R.id.chatRmTxtTitle);
        chatRmTxtTitle.setText(roomName);

        RoomList roomList = RoomList.getInstance();
        RoomItem roomItem = (RoomItem) roomList.get((Object) roomName);
        Log.d("ROOMIE", roomItem.getOwner() );
        Log.d("ROOMIE",String.valueOf(roomItem.getMembers().size()));

        SockNotifier notifier = SockNotifier.getInstance();
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
                            Log.d("ROOMLIST", rooms);
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
            public void onEnterRoom(String roomName, String clientId,String clientName) {

            }
        });
    }
}