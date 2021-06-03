package chrisbriant.uk.convo.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.ChatRecycler;
import adapters.MembersRecycler;
import adapters.RoomRecycler;
import chrisbriant.uk.convo.R;
import objects.Member;
import objects.RoomItem;
import objects.RoomList;
import objects.RoomMessage;
import services.ServerConn;
import services.SockNotifier;

public class RoomActivity extends AppCompatActivity {
    private ServerConn conn;
    private RecyclerView recyclerView;
    private MembersRecycler membersRecycler;
    private RecyclerView chatRecyclerView;
    private ChatRecycler chatRecycler;
    private SharedPreferences sharedPrefs;
    private String password;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        Context ctx = this;

        Intent intent = getIntent();
        String roomName = (String) intent.getExtras().get("roomName");
        conn = ServerConn.getInstance(this);
        sharedPrefs = this.getSharedPreferences(this.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        password = sharedPrefs.getString("password","");
        Log.d("THISISTHEPASSWORD",password);

        //UI
        TextView chatRmTxtTitle = findViewById(R.id.chatRmTxtTitle);
        chatRmTxtTitle.setText("You are in room " + roomName);
        EditText chatRmEdtMessage = findViewById(R.id.chatRmEdtMessage);

        chatRmEdtMessage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("TOUCHED", "Chat message");
                if(chatRmEdtMessage.getText().toString().equals("Send a chat message...")) {
                    chatRmEdtMessage.setText("");
                }
                return false;
            }
        });

        RoomList roomList = RoomList.getInstance();
        RoomItem roomItem = (RoomItem) roomList.get((Object) roomName);
        //Create the recycler view and load it with the clients
        recyclerView = findViewById(R.id.chatRmRecycMembs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<Member> membersList = (ArrayList<Member>) roomItem.getMembersAsList();
        membersRecycler = new MembersRecycler(this,membersList, conn);
        recyclerView.setAdapter(membersRecycler);
        membersRecycler.notifyDataSetChanged();

        //Create the chat recycler
        chatRecyclerView = findViewById(R.id.chatRmRecycChat);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<RoomMessage> messages = new ArrayList<RoomMessage>();
        chatRecycler = new ChatRecycler(this,messages, conn);
        chatRecyclerView.setAdapter(chatRecycler);
        chatRecycler.notifyDataSetChanged();

        Button chatRmBtnSend = findViewById(R.id.chatRmBtnSend);
        Button chatRmBtnExit = findViewById(R.id.chatRmBtnExit);

        chatRmBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject payload = new JSONObject();
                try {
                    payload.put("type", "message_room");
                    payload.put("client_id", sharedPrefs.getString("id",""));
                    payload.put("name",roomItem.getRoomName());
                    payload.put("message",chatRmEdtMessage.getText());
                    payload.put("password",password);
                    conn.send(payload.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        chatRmBtnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context ctx = v.getContext();
                JSONObject payload = new JSONObject();
                try {
                    payload.put("type", "exit_room");
                    payload.put("client_id", sharedPrefs.getString("id",""));
                    payload.put("name",roomItem.getRoomName());
                    conn.send(payload.toString());
                    Intent intent = new Intent(ctx, RoomListActivity.class);
                    ctx.startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


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
            public void onEnterRoom(String roomName, String clientId, String clientName, JSONArray membersJson) {
                //Add the room member and update the recycler
                roomItem.addPlayer(clientId,clientName);
                try {
                    Log.d("ROOMENTERED", "There's someone at the door.");
                    roomItem.addMembersFromJSONArray(membersJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList<Member> membersList = (ArrayList<Member>) roomItem.getMembersAsList();
                Log.d("ROOMENTERED", String.valueOf(membersList.size()));
                membersRecycler.setMembers(membersList);
                membersRecycler.notifyDataSetChanged();
            }

            @Override
            public void onRoomMessage(String clientId, String clientName, String message) {
                messages.add(new RoomMessage(
                        clientId,
                        clientName,
                        message
                ));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d("ROOMMESSAGE", String.valueOf(messages.size()));
                            chatRecycler.setMessages(messages);
                            chatRecycler.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.d("EXCEPTION", e.toString());
                        }
                    }
                });

            }

            @Override
            public void onSocketClosed() {
                Intent intent = new Intent(ctx, MainActivity.class);
                intent.putExtra("socketFailure","The web socket has closed, please try again.");
                ctx.startActivity(intent);
            }

            @Override
            public void onPrivateMessage(String sender, String message) {
                messages.add(new RoomMessage(
                        sender,
                        message
                ));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            chatRecycler.setMessages(messages);
                            chatRecycler.notifyDataSetChanged();
                        } catch (Exception e) {
                            Log.d("EXCEPTION", e.toString());
                        }
                    }
                });
            }
        });
    }
}