package be.cypherke.mua.db;

import be.cypherke.mua.gsonobjects.Teleport;
import be.cypherke.mua.io.FileManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TeleportsDb {
    private final FileManager fileManager;
    private List<Teleport> teleports;

    /**
     * Constructor.
     *
     * @param fileManager the {@link FileManager}
     */
    public TeleportsDb(FileManager fileManager) {
        this.fileManager = fileManager;
        this.teleports = new ArrayList<>();

        loadTeleports();
    }

    private void loadTeleports() {
        if (teleports != null) {
            teleports.clear();
        }

        Type listType = new TypeToken<List<Teleport>>() {
        }.getType();
        String json = fileManager.load(listType);

        if (json != null) {
            Gson gson = new Gson();
            teleports = gson.fromJson(json, listType);
        }
    }

    /**
     * Saves the db to a json.
     */
    public void save() {
        if (teleports != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
            Collections.sort(teleports, (t1, t2) -> t1.getName().compareToIgnoreCase(t2.getName()));
            String json = gson.toJson(teleports);

            fileManager.save(json);
        }
    }

    /**
     * Adds a teleport to the db.
     *
     * @param teleport the {@link Teleport} object to add
     */
    public void add(Teleport teleport) {
        if (this.teleports == null) {
            this.teleports = new ArrayList<>();
        }
        this.teleports.add(teleport);
    }

    /**
     * Gets the teleports of a certain user.
     *
     * @param player the user to search for
     * @return a list of teleports splitted by spaces as a string
     */
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

    /**
     * Gets a teleport from the db.
     *
     * @param player the owner of the teleport
     * @param name   the name of the teleport
     * @return the {@link Teleport} object found
     */
    public Teleport getTp(String player, String name) {
        for (Teleport tp : teleports) {
            if (tp.getName().equalsIgnoreCase(name) && tp.getOwner().equalsIgnoreCase(player)) {
                return tp;
            }
        }
        return null;
    }

    /**
     * Removes teleport from the db.
     *
     * @param player       the owner of the teleport
     * @param teleportName the name of the teleport
     */
    public void removeTeleport(String player, String teleportName) {
        for (Iterator<Teleport> iterator = teleports.iterator(); iterator.hasNext(); ) {
            Teleport tp = iterator.next();
            if (tp.getName().equalsIgnoreCase(teleportName) && tp.getOwner().equalsIgnoreCase(player)) {
                iterator.remove();
            }
        }
    }
}
