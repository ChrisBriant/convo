package chrisbriant.uk.convo.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import chrisbriant.uk.convo.R;
import services.ServerConn;
import services.SockNotifier;

public class MainActivity extends AppCompatActivity {
    ServerConn conn;
    SockNotifier notifier;
    SharedPreferences sharedPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPrefs = this.getSharedPreferences(this.getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();

        conn = ServerConn.getInstance(this);

        notifier = SockNotifier.getInstance();

        notifier.setListener(new SockNotifier.MessageEventListener() {
            @Override
            public void onRegister(String id) {
                Log.d("Registration", id);
                editor.putString("id",id);
                editor.apply();
            }
            @Override
            public void onSetName(String name) {
                Log.d("Setting Name", name);
                editor.putString("name",name);
                editor.apply();
                //Todo Start the rooms activity
            }

        });

        //UI
        EditText mainEdtName = findViewById(R.id.mainEdtName);
        Button mainBtnSend = findViewById(R.id.mainBtnSend);

        mainBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject payload = new JSONObject();
                try {
                    payload.put("type", "name");
                    payload.put("client_id", sharedPrefs.getString("id",""));
                    payload.put("name",mainEdtName.getText());
                    conn.send(payload.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}