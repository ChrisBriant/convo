package objects;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import services.ServerConn;

public class RoomList extends HashMap {
    private static RoomList single_instance = null;

    public RoomList() {
    }

    public JSONObject getRoom(String key) {
        JSONObject rooms = new JSONObject();

        return rooms;
    }

    //Takes the json string, converts to
    public ArrayList<RoomItem> loadRooms(String roomList) throws JSONException {
        //Clear the current hashtable
        this.clear();

        ArrayList<RoomItem> listOfRooms = new ArrayList<RoomItem>();

        JSONArray roomsJSON = new JSONArray(roomList);

        for(int i=0;i<roomsJSON.length(); i++) {
            JSONObject roomJSON = new JSONObject(roomsJSON.get(i).toString());
            String roomName = roomJSON.getString("name");
            String owner = roomJSON.getString("owner");
            JSONArray members = roomJSON.getJSONArray("members");
            ArrayList<String> userNames = new ArrayList<String>();
            for(int j=0;j<members.length();j++) {
                userNames.add(members.getString(j));
            }
            Log.d("MEMBERS",userNames.toString());
            boolean secure = roomJSON.getBoolean("secure");

            RoomItem room = new RoomItem(roomName,owner,secure,members.length(),userNames);

            //Add to the hash table
            try {
                this.put(roomName,room);
            } catch(Exception e) {
                Log.d("HASH TABLE PUT", "Room alredy exists");
            }
        }

        Log.d("HASH TABLE", this.keySet().toString());
        Set<String> keys = this.keySet();
        for (String key : keys) {
            Log.d("KEY HERE", key);
            listOfRooms.add((RoomItem) this.get(key));
        }

        return listOfRooms;
    }

    public ArrayList<RoomItem> getRoomList() {
        ArrayList<RoomItem> listOfRooms = new ArrayList<RoomItem>();

        Set<String> keys = this.keySet();
        for (String key : keys) {
            Log.d("KEY HERE", key);
            listOfRooms.add((RoomItem) this.get(key));
        }

        return listOfRooms;
    }

    public static RoomList getInstance()
    {
        if (single_instance == null)
            single_instance = new RoomList();

        return single_instance;
    }
}
