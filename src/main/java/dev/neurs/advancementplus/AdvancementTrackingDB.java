package dev.neurs.advancementplus;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class AdvancementTrackingDB {
    public Connection dbConnection;
    public List<String> earnedAdvancementsCache = new ArrayList<>();
    private boolean initialized = false;

    public boolean init(File dataFolder, Logger logger) {
        if (!dataFolder.exists()) dataFolder.mkdir();

        try {
            this.dbConnection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder.getAbsolutePath() + File.separator + "AdvancementsPlus.db");
            try (PreparedStatement statement = this.dbConnection.prepareStatement("CREATE TABLE IF NOT EXISTS earnedAdvancements (key TEXT PRIMARY KEY NOT NULL)")) {
                statement.executeUpdate();
            }
            try (PreparedStatement statement = this.dbConnection.prepareStatement("SELECT key FROM earnedAdvancements")) {
                ResultSet checkSet = statement.executeQuery();
                while (checkSet.next()) {
                    earnedAdvancementsCache.add(checkSet.getString("key"));
                }
            }
        } catch (SQLException e) {
            logger.warning("The plugin cannot create file to make a database to store advancements.");
            logger.warning(e.getMessage());
            return false;
        }

        this.initialized = true;
        logger.info("Database initialized successfully.");
        return true;
    }

    public void addAdvancement(String key) {
        if (!this.initialized) return;

        try (PreparedStatement statement = this.dbConnection.prepareStatement("INSERT INTO earnedAdvancements (key) VALUES (?)")) {
            statement.setString(1, key);
            statement.executeUpdate();
            earnedAdvancementsCache.add(key);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
