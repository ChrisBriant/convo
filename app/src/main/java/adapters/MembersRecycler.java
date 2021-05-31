package adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import chrisbriant.uk.convo.R;
import objects.Member;
import services.ServerConn;

public class MembersRecycler extends RecyclerView.Adapter<MembersRecycler.ViewHolder> {
    private Context context;
    private List<Member> members;
    private ServerConn conn;

    public MembersRecycler(Context context, List<Member> members, ServerConn conn) {
        this.context = context;
        this.members = members;
        this.conn = conn;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.member_item,parent,false);
        return new MembersRecycler.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Member member = members.get(position);

        holder.membItmName.setText(member.getName());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView membItmName;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            membItmName = itemView.findViewById(R.id.membItmName);
        }
    }
}
