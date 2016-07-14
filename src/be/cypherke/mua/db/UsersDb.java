package be.cypherke.mua.db;

import be.cypherke.mua.gsonobjects.User;
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

public class UsersDb {
    private List<User> users;
    private String savefile;

    public UsersDb(String savefile) {
        this.users = new ArrayList<>();
        this.savefile = savefile;
        loadUsers();
    }

    private void loadUsers() {
        if (users != null) {
            users.clear();
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
            Type listType = new TypeToken<List<User>>() {
            }.getType();
            users = gson.fromJson(json, listType);
        } else {
            try {
                FileUtils.touch(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public User getUser(String player) {
        if (users != null && users.size() > 0) {
            for (User u : users) {
                if (u.getUsername().equalsIgnoreCase(player)) return u;
            }
        }
        return null;
    }

    public void addUser(User u) {
        if (this.users == null) this.users = new ArrayList<>();
        this.users.add(u);
    }

    public void save() {
        FileWriter file = null;
        try {
            file = new FileWriter(savefile);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        Collections.sort(users, (u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername()));
        String json = gson.toJson(users);
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
