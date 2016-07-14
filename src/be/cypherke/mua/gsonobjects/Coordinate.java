package be.cypherke.mua.gsonobjects;

public class Coordinate {
    private Double x;
    private Double y;
    private Double z;

    public Coordinate(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString() {
        return x.intValue() + " " + y.intValue() + " " + z.intValue();
    }
}
