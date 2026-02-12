package be.cypherke.mua.gsonobjects;

/** Represents a Minecraft player. */
public class User {
  private String username;
  private String uuid;
  private String lastseen;
  private Coordinate coordinate;
  private String ip;
  private String entity;
  private boolean online;
  private int numberOfDeaths;

  /**
   * Creates a new user.
   *
   * @param username the player's name
   */
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

  public void setOnline(boolean online) {
    this.online = online;
  }

  public boolean isOnline() {
    return online;
  }

  /** Increments the death counter by one. */
  public void addDeath() {
    this.numberOfDeaths++;
  }

  public int getNumberOfDeaths() {
    return numberOfDeaths;
  }
}
