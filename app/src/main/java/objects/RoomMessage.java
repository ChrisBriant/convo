package objects;

public class RoomMessage {
    private String fromClientId;
    private String fromClientName;
    private String message;
    private Boolean isPrivate;
    private String toClientId;
    private String toClientName;

    public RoomMessage(String fromClientId, String fromClientName, String message, Boolean isPrivate, String toClientId, String toClientName) {
        this.fromClientId = fromClientId;
        this.fromClientName = fromClientName;
        this.message = message;
        this.isPrivate = isPrivate;
        this.toClientId = toClientId;
        this.toClientName = toClientName;
    }

    public RoomMessage(String fromClientId, String fromClientName, String message) {
        this.fromClientId = fromClientId;
        this.fromClientName = fromClientName;
        this.message = message;
        this.isPrivate = false;
    }

    public String getFromClientId() {
        return fromClientId;
    }

    public void setFromClientId(String fromClientId) {
        this.fromClientId = fromClientId;
    }

    public String getFromClientName() {
        return fromClientName;
    }

    public void setFromClientName(String fromClientName) {
        this.fromClientName = fromClientName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getPrivate() {
        return isPrivate;
    }

    public void setPrivate(Boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getToClientId() {
        return toClientId;
    }

    public void setToClientId(String toClientId) {
        this.toClientId = toClientId;
    }

    public String getToClientName() {
        return toClientName;
    }

    public void setToClientName(String toClientName) {
        this.toClientName = toClientName;
    }
}
