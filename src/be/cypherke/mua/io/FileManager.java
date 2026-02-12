package be.cypherke.mua.io;

import java.lang.reflect.Type;

/** Interface for loading and saving file-backed data. */
public interface FileManager {
  /**
   * Loads content from the file.
   *
   * @param type the type of data to load
   * @return the file content as a String, or null if not found
   */
  String load(Type type);

  /**
   * Saves content to the file.
   *
   * @param content the content to save
   */
  void save(String content);
}
