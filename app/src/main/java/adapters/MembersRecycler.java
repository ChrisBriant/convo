package adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;

import chrisbriant.uk.convo.R;
import objects.Member;
import objects.RoomItem;
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
        Log.d("BINDING", member.getName());
        holder.membItmName.setText(member.getName());
    }

    @Override
    public int getItemCount() {
        return members.size();
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView membItmName;

        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);

            membItmName = itemView.findViewById(R.id.membItmName);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Member m = members.get(getAdapterPosition());

            Context ctx = v.getContext();
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);

            LayoutInflater inflater = LayoutInflater.from(ctx);
            View view = inflater.inflate(R.layout.diag_personal_message,null);
            TextView pmDiagTxtErr = view.findViewById(R.id.pmDiagTxtErr);
            TextView pmDiagTitle = view.findViewById(R.id.pmDiagTitle);
            pmDiagTitle.setText("Send a personal message to " + m.getName());
            EditText pmDiagEdtPassword = view.findViewById(R.id.pmDiagEdtPassword);
            Button pmDiagBtnSend = view.findViewById(R.id.pmDiagBtnSend);
            Button pmDiagBtnCancel = view.findViewById(R.id.pmDiagBtnCancel);

            alertDialogBuilder.setView(view);
            final AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();

            pmDiagBtnSend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            pmDiagBtnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pmDiagTxtErr.setVisibility(View.GONE);
                    alertDialog.dismiss();
                }
            });
        }
    }
}
