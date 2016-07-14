package be.cypherke.mua.gsonobjects;

public class User {
    private String username;
    private String uuid;
    private String lastseen;
    private Coordinate coordinate;
    private String ip;
    private String entity;

    public User(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setEntity(String entity) {
        this.entity = entity;
    }

    public void setCoordinate(Coordinate coordinate) {
        this.coordinate = coordinate;
    }

    public void setLastseen(String lastseen) {
        this.lastseen = lastseen;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }
}
