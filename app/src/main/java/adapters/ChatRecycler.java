package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import chrisbriant.uk.convo.R;
import objects.Member;
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
        RoomMessage message = messages.get(position);
        Log.d("BINDING", message.getFromClientId());
        holder.chItmTxt.setText(message.getMessage());
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

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            chItmTxt = itemView.findViewById(R.id.chItmTxt);
        }
    }
}
