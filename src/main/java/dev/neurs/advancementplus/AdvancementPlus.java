package dev.neurs.advancementplus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class AdvancementPlus extends JavaPlugin {
    @Override
    public void onEnable() {
        AdvancementTrackingDB aDp = new AdvancementTrackingDB();
        if (!aDp.init(getDataFolder(), getLogger())) {
            getServer().getPluginManager().disablePlugin(this);
        }

        getLogger().info("Disabling default advancement announcements.");
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                getServer().dispatchCommand(Bukkit.getConsoleSender(), "gamerule announceAdvancements false");
            }
        };
        runnable.runTaskLater(this, 1L);

        getServer().getPluginManager().registerEvents(new AdvancementsEvents(aDp), this);
    }

    @Override
    public void onDisable() {
    }
}
