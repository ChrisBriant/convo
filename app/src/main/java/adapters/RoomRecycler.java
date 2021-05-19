package adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONObject;

import java.util.List;

import chrisbriant.uk.convo.R;
import objects.RoomItem;
import services.ServerConn;

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
        holder.rmOwner.setText(room.getOwner());
        holder.rmNoPlayers.setText(String.valueOf(room.getPlayerCount()));
        Log.d("ROOM STATUS IN HOLDER", String.valueOf(room.isRoomStatus()));
//        if(room.isRoomStatus()) {
//            holder.rmStatus.setText("Locked");
//        } else {
//            holder.rmStatus.setText("Open");
//        }

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
        private TextView rmItmName;
        private TextView rmOwner;
        private TextView rmNoPlayers;
        private TextView rmItmSecure;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            rmItmName = itemView.findViewById(R.id.rmItmName2);
            rmOwner = itemView.findViewById(R.id.rmItmOwner);
            rmNoPlayers = itemView.findViewById(R.id.rmItmNoPlayers);
            rmItmSecure = itemView.findViewById(R.id.rmItmSecure);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            RoomItem r = roomList.get(getAdapterPosition());
            if(r.isRoomStatus()) {
                Toast.makeText(context,"Sorry room is not available.",Toast.LENGTH_SHORT).show();
            } else {
                //Enter room here
                SharedPreferences sharedPrefs = context.getSharedPreferences(
                        context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("current_room",r.getRoomName());
                editor.apply();
                JSONObject payload = new JSONObject();
                try {
                    payload.put("type", "enter_room");
                    payload.put("client_id", sharedPrefs.getString("id",""));
                    payload.put("name", r.getRoomName());
                    conn.send(payload.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("AUTH", "Exception Fired");
                }
                //Intent intent = new Intent(context, RoomActivity.class);
                //context.startActivity(intent);
            }
        }
    }
}