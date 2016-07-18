package be.cypherke.mua.io;

import java.lang.reflect.Type;

public interface FileManager {
    String load(Type type);

    void save(String content);
}
