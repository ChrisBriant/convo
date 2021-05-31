package adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import chrisbriant.uk.convo.R;
import chrisbriant.uk.convo.activities.RoomActivity;
import chrisbriant.uk.convo.activities.RoomListActivity;
import objects.RoomItem;
import objects.RoomList;
import services.ServerConn;
import services.SockNotifier;

public class RoomRecycler extends RecyclerView.Adapter<RoomRecycler.ViewHolder> {
    private Context context;
    private List<RoomItem> roomList;
    private ServerConn conn;

    public RoomRecycler(Context context, List<RoomItem> roomList, ServerConn conn) {
        this.context = context;
        this.roomList = roomList;
        this.conn = conn;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_item,parent,false);
        return new RoomRecycler.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RoomItem room = roomList.get(position);

        holder.rmItmName.setText(room.getRoomName());
        //holder.rmOwner.setText(room.getOwner());
        holder.rmNoPlayers.setText(String.valueOf(room.getPlayerCount()));
        //Log.d("ROOM STATUS IN HOLDER", String.valueOf(room.isRoomStatus()));
//        if(room.isRoomStatus()) {
//            holder.rmStatus.setText("Locked");
//        } else {
//            holder.rmStatus.setText("Open");
//        }
        if(room.isRoomSecure()) {
            holder.rmItmSecure.setText("secure");
        } else {
            holder.rmItmSecure.setText("open");
        }

        Log.d("BINDING",room.getRoomName());
    }

    @Override
    public int getItemCount() {
        return roomList.size();
    }

    public void setRoomList(List<RoomItem> roomList) {
        this.roomList = roomList;
        Log.d("ROOM LIST SET", "I am setting the room list");
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private SharedPreferences sharedPrefs;
        private TextView rmItmName;
        //private TextView rmOwner;
        private TextView rmNoPlayers;
        private TextView rmItmSecure;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            sharedPrefs = ctx.getSharedPreferences(ctx.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            rmItmName = itemView.findViewById(R.id.rmItmName2);
            ///rmOwner = itemView.findViewById(R.id.rmItmOwner);
            rmNoPlayers = itemView.findViewById(R.id.rmItmNoPlayers);
            rmItmSecure = itemView.findViewById(R.id.rmItmSecure);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            RoomItem r = roomList.get(getAdapterPosition());
            if(r.isRoomSecure()) {
                Context ctx = v.getContext();
                //Launch dialog which asks for password
                //Create alert builder
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

                LayoutInflater inflater = LayoutInflater.from(ctx);
                View view = inflater.inflate(R.layout.diag_password,null);
                TextView pwDiagTxtErr = view.findViewById(R.id.pwDiagTxtErr);
                EditText pwDiagEdtPassword = view.findViewById(R.id.pwDiagEdtPassword);
                Button pwDiagBtnSend = view.findViewById(R.id.pwDiagBtnSend);
                Button pwDiagBtnCancel = view.findViewById(R.id.pwDiagBtnCancel);

                alertDialogBuilder.setView(view);
                final AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
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

                    }

                    @Override
                    public void onAuthFailed() {
                        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pwDiagTxtErr.setVisibility(View.VISIBLE);
                            }
                        });

                    }

                    @Override
                    public void onEnterRoom(String roomName,String clientId,String clientName) {
                        Log.d("ROOMENTERED",roomName);
                        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                pwDiagTxtErr.setVisibility(View.GONE);
                                RoomList rooms = new RoomList((ArrayList<RoomItem>) roomList);
                                RoomItem roomItem = (RoomItem) rooms.get((Object) roomName);
                                roomItem.addPlayer(clientId,clientName);
                                //Log.d("ROOMENTERED",roomItem.getOwner());
                                //Go to the room if the client id matches
                                if(sharedPrefs.getString("id","").equals(clientId)) {
                                    Intent intent = new Intent(ctx, RoomActivity.class);
                                    intent.putExtra("roomName",roomName);
                                    ctx.startActivity(intent);
                                }
                            }
                        });
                    }
                });

                pwDiagBtnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pwDiagTxtErr.setVisibility(View.GONE);
                        alertDialog.dismiss();
                    }
                });

                pwDiagEdtPassword.setOnKeyListener(new View.OnKeyListener() {
                    @Override
                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        pwDiagTxtErr.setVisibility(View.GONE);
                        return false;
                    }
                });

                pwDiagBtnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject payload = new JSONObject();
                        try {
                            payload.put("type", "enter_room");
                            payload.put("client_id", sharedPrefs.getString("id",""));
                            payload.put("name",r.getRoomName());
                            payload.put("secure", r.isRoomSecure());
                            payload.put("password",pwDiagEdtPassword.getText());
                            conn.send(payload.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            } else {
                //Go straight to the room
                JSONObject payload = new JSONObject();
                try {
                    payload.put("type", "enter_room");
                    payload.put("client_id", sharedPrefs.getString("id",""));
                    payload.put("name",r.getRoomName());
                    payload.put("secure", r.isRoomSecure());
                    payload.put("password","");
                    conn.send(payload.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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

                    }

                    @Override
                    public void onAuthFailed() { }

                    @Override
                    public void onEnterRoom(String roomName,String clientId,String clientName) {
                        Log.d("ROOMENTERED",roomName);
                        ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(sharedPrefs.getString("id","").equals(clientId)) {
                                    RoomList rooms = new RoomList((ArrayList<RoomItem>) roomList);
                                    RoomItem roomItem = (RoomItem) rooms.get((Object) roomName);
                                    roomItem.addPlayer(clientId,clientName);
                                    Intent intent = new Intent(context, RoomActivity.class);
                                    intent.putExtra("roomName",roomName);
                                    context.startActivity(intent);
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}