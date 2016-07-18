package be.cypherke.mua.gsonobjects;

public class Teleport {
    private String name;
    private String owner;
    private String created;
    private Coordinate coordinate;

    public Teleport(String name, String owner, String created, Coordinate coordinate) {
        this.name = name;
        this.owner = owner;
        this.created = created;
        this.coordinate = coordinate;
    }

    public String getName() {
        return name;
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public String getOwner() {
        return owner;
    }
}
