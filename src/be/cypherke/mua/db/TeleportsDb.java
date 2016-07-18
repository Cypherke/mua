package be.cypherke.mua.db;

import be.cypherke.mua.gsonobjects.Teleport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeleportsDb {
    private List<Teleport> teleports;
    private String savefile;

    public TeleportsDb(String savefile) {
        this.teleports = new ArrayList<>();
        this.savefile = savefile;
        loadTeleports();
    }

    private void loadTeleports() {
        if (teleports != null) {
            teleports.clear();
        }
        Path path = FileSystems.getDefault().getPath(savefile);
        File file = path.toFile();
        if (file.isFile() && file.canRead()) {
            String json = null;
            try {
                json = new String(Files.readAllBytes(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Gson gson = new Gson();
            Type listType = new TypeToken<List<Teleport>>() {
            }.getType();
            teleports = gson.fromJson(json, listType);
        } else {
            try {
                FileUtils.touch(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        if (teleports != null) {
            FileWriter file = null;
            try {
                file = new FileWriter(savefile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            Collections.sort(teleports, (t1, t2) -> t1.getName().compareToIgnoreCase(t2.getName()));
            String json = gson.toJson(teleports);
            if (file != null) {
                try {
                    file.write(json);
                    file.flush();
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void add(Teleport teleport) {
        if (this.teleports == null) this.teleports = new ArrayList<>();
        this.teleports.add(teleport);
    }

    public String getUserTps(String player) {
        if (teleports != null && teleports.size() > 0) {
            StringBuilder tps = new StringBuilder();
            for (Teleport tp : teleports) {
                if (tp.getOwner().equalsIgnoreCase(player)) {
                    tps.append(tp.getName()).append(" ");
                }
            }
            return tps.toString();
        }
        return null;
    }

    public Teleport getTp(String name) {
        for (Teleport tp : teleports) {
            if (tp.getName().equalsIgnoreCase(name)) {
                return tp;
            }
        }
        return null;
    }
}
