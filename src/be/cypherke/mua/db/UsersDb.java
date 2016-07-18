package be.cypherke.mua.db;

import be.cypherke.mua.gsonobjects.User;
import be.cypherke.mua.io.FileManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UsersDb {
    private final FileManager fileManager;
    private List<User> users;

    public UsersDb(FileManager fileManager) {
        this.fileManager = fileManager;
        this.users = new ArrayList<>();

        loadUsers();
    }

    private void loadUsers() {
        if (users != null) {
            users.clear();
        }

        Type listType = new TypeToken<List<User>>() {
        }.getType();
        String json = fileManager.load(listType);

        if (json != null) {
            Gson gson = new Gson();
            users = gson.fromJson(json, listType);
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
        if (users != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            Collections.sort(users, (u1, u2) -> u1.getUsername().compareToIgnoreCase(u2.getUsername()));
            String json = gson.toJson(users);

            fileManager.save(json);
        }
    }

    public String getUserNames() {
        if (users != null && users.size() > 0) {
            StringBuilder sb = new StringBuilder();
            for (User u : users) {
                sb.append(u.getUsername()).append(" ");
            }
            return sb.toString();
        }
        return null;
    }
}
