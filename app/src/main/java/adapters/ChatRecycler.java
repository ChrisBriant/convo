package adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import chrisbriant.uk.convo.R;
import objects.RoomMessage;
import services.ServerConn;

public class ChatRecycler extends RecyclerView.Adapter<ChatRecycler.ViewHolder> {
    private Context context;
    private List<RoomMessage> messages;
    private ServerConn conn;

    public ChatRecycler(Context context, List<RoomMessage> messages, ServerConn conn) {
        this.context = context;
        this.messages = messages;
        this.conn = conn;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_item,parent,false);
        return new ChatRecycler.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(context.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        String myId =  sharedPrefs.getString("id","");
        RoomMessage message = messages.get(position);
        Log.d("BINDING", message.getFromClientId());
        Drawable speachOutgoing = ContextCompat.getDrawable(context, R.drawable.speach_outgoing);
        Drawable speachIncomming = ContextCompat.getDrawable(context, R.drawable.speech_incoming);
        Drawable speachPrivate = ContextCompat.getDrawable(context, R.drawable.speech_private);

        holder.chItmTxt.setText(message.getMessage());
        holder.chItmName.setText(message.getFromClientName());

        if(message.getPrivate()) {
            //Display a private message
            holder.chItmContainer.setBackground(speachPrivate);
            holder.chItmContainer.setPadding(10,6,80,6);
        } else {
            if(message.getFromClientId().equals(myId)) {
                holder.chItmContainer.setBackground(speachOutgoing);
                holder.chItmContainer.setPadding(80,6,0,6);
            } else {
                holder.chItmContainer.setBackground(speachIncomming);
                holder.chItmContainer.setPadding(10,6,80,6);
            }
        }


    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(ArrayList<RoomMessage> messages) {
        this.messages = messages;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView chItmTxt;
        TextView chItmName;
        LinearLayout chItmContainer;


        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            chItmTxt = itemView.findViewById(R.id.chItmTxt);
            chItmName = itemView.findViewById(R.id.chItmName);
            chItmContainer = itemView.findViewById(R.id.chItmContainer);
        }
    }
}
