package be.cypherke.mua.gsonobjects;

/** Represents a 3D coordinate in the Minecraft world. */
public class Coordinate {
  private Double xcoordinate;
  private Double ycoordinate;
  private Double zcoordinate;

  /**
   * Constructor.
   *
   * @param x the x of the coordinate
   * @param y the height of the coordinate
   * @param z the z of the coordinate
   */
  public Coordinate(Double x, Double y, Double z) {
    this.xcoordinate = x;
    this.ycoordinate = y;
    this.zcoordinate = z;
  }

  /** {@inheritDoc} */
  @Override
  public String toString() {
    return xcoordinate.intValue() + " " + ycoordinate.intValue() + " " + zcoordinate.intValue();
  }

  public Double getXcoordinate() {
    return xcoordinate;
  }

  public Double getYcoordinate() {
    return ycoordinate;
  }

  public Double getZcoordinate() {
    return zcoordinate;
  }
}
