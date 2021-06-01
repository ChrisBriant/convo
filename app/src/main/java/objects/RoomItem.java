package objects;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class RoomItem {
    private int roomId;
    private String roomName;
    private String owner;
    private boolean secure;
    private int playerCount;
    private ArrayList<String> players;
    private HashMap<String,String> members;

    public RoomItem(int roomId, String roomName, String owner,boolean secure, int playerCount, ArrayList<String> players) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.owner = owner;
        this.secure = secure;
        this.playerCount = playerCount;
        this.players = players;
    }

    public RoomItem(int roomId, String roomName, String owner, boolean secure, int playerCount) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.owner = owner;
        this.secure = secure;
        this.playerCount = playerCount;
        this.players = players;
    }


    public RoomItem(String roomName, String owner, boolean secure, int playerCount, ArrayList<String> players) {
        this.roomName = roomName;
        this.owner = owner;
        this.secure = secure;
        this.playerCount = playerCount;
        this.players = players;
        this.members = new HashMap<String,String>();
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public int getPlayerCount() {
        return playerCount;
    }

    public void setPlayerCount(int playerCount) {
        this.playerCount = playerCount;
    }

    public ArrayList<String> getPlayers() {
        return players;
    }

    public void setPlayers(ArrayList<String> players) {
        this.players = players;
    }

    public void addPlayer(String clientId,String name) {
        members.put(clientId,name);
    }

    public HashMap<String, String> getMembers() {
        return members;
    }

    public boolean isRoomSecure() {
        return secure;
    }

    public void setRoomStatus(boolean secure) {
        this.secure = secure;
    }

    public List<Member> getMembersAsList() {

        ArrayList<Member> memberList = new ArrayList<Member>();
        Set<String> keys = this.members.keySet();
        for (String key : keys) {
            Log.d("KEYENTERED", this.members.get(key));
            memberList.add(new Member(key, this.members.get(key)));
            //memberList.add((Member) this.members.get(key));
        }

        return memberList;
    }

    public void addMembersFromJSONArray(JSONArray members) throws JSONException {
        for(int i=0;i<members.length();i++) {
            JSONObject memberJson = new JSONObject(members.getString(i));
            Member member = new Member(memberJson.getString("id"),memberJson.getString("name"));
            try {
                Log.d("ROOMENTERED",member.getName() );
                this.addPlayer(member.getClientId(),member.getName());
            } catch (Exception e) {
                Log.d("EXCEPTION", "Exception when adding to members");
            }
        }
    }
}
