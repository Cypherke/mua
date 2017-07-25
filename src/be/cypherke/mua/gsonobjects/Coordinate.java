package be.cypherke.mua.gsonobjects;

public class Coordinate {
    private Double x;
    private Double y;
    private Double z;

    /**
     * Constructor.
     *
     * @param x the x of the coordinate
     * @param y the height of the coordinate
     * @param z the z of the coordinate
     */
    public Coordinate(Double x, Double y, Double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String toString() {
        return x.intValue() + " " + y.intValue() + " " + z.intValue();
    }

    public Double getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public Double getZ() {
        return z;
    }
}
