package be.cypherke.muatests.mocks;

import be.cypherke.mua.io.FileManager;

import java.lang.reflect.Type;

public class EmptyFileManager implements FileManager {
    @Override
    public String load(Type type) {
        return null;
    }

    @Override
    public void save(String content) {

    }
}
