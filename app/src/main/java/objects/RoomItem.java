package objects;

import java.util.ArrayList;

public class RoomItem {
    private int roomId;
    private String roomName;
    private String owner;
    private boolean secure;
    private int playerCount;
    private ArrayList<String> players;

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

    public boolean isRoomStatus() {
        return secure;
    }

    public void setRoomStatus(boolean secure) {
        this.secure = secure;
    }

}
