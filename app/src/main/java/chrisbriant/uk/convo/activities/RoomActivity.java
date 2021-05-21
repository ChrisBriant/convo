package chrisbriant.uk.convo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import chrisbriant.uk.convo.R;

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
    }
}