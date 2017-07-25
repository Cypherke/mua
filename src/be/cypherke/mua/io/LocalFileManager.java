package be.cypherke.mua.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;

public class LocalFileManager implements FileManager {
    private final String fileName;

    public LocalFileManager(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String load(Type type) {
        Path path = FileSystems.getDefault().getPath(fileName);
        File file = path.toFile();
        if (file.isFile() && file.canRead()) {
            try {
                return new String(Files.readAllBytes(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                FileUtils.touch(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override
    public void save(String content) {
        FileWriter file = null;
        try {
            file = new FileWriter(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (file != null) {
            try {
                file.write(content);
                file.flush();
                file.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
