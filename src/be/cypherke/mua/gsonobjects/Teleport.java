package be.cypherke.mua.gsonobjects;

public class Teleport {
    private String name;
    private String owner;
    private String created;
    private Coordinate coordinate;

    /**
     * Constructor.
     *
     * @param name       the name for the teleport
     * @param owner      the one who set the teleport
     * @param created    the datetime when created
     * @param coordinate the {@link Coordinate} of a teleport
     */
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
