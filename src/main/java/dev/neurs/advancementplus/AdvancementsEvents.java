package dev.neurs.advancementplus;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.advancement.AdvancementDisplayType;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class AdvancementsEvents implements Listener {
    private final AdvancementTrackingDB aDp;

    public AdvancementsEvents(AdvancementTrackingDB aDp) {
        this.aDp = aDp;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        int count = 0;
        for (String advancementRawKey : this.aDp.earnedAdvancementsCache) {
            NamespacedKey advancementKey = NamespacedKey.fromString(advancementRawKey);

            assert advancementKey != null;
            Advancement advancement = Bukkit.getAdvancement(advancementKey);

            assert advancement != null;
            if (!player.getAdvancementProgress(advancement).isDone()) {
                AdvancementProgress progress = player.getAdvancementProgress(advancement);
                for(String criteria : progress.getRemainingCriteria())
                    progress.awardCriteria(criteria);
                count++;
            }
        }
        player.sendMessage("There are " + ChatColor.GREEN + count + ChatColor.WHITE + " advancements made since your last join.");
    }

    @EventHandler
    public void onPlayerAdvancement(PlayerAdvancementDoneEvent event) {
        Advancement advancement = event.getAdvancement();
        String advancementKey = advancement.getKey().getKey();

        if (this.aDp.earnedAdvancementsCache.contains(advancementKey)) {
            return;
        }

        AdvancementDisplay display = advancement.getDisplay();
        if (display == null) return;

        Player player = event.getPlayer();

        AdvancementDisplayType advancementType = display.getType();

        this.aDp.addAdvancement(advancementKey);

        for (Player other : Bukkit.getServer().getOnlinePlayers()) {
            if (!other.getAdvancementProgress(advancement).isDone()) {
                AdvancementProgress progress = other.getAdvancementProgress(advancement);
                for(String criteria : progress.getRemainingCriteria())
                    progress.awardCriteria(criteria);
                if (advancementType == AdvancementDisplayType.CHALLENGE) {
                    other.getWorld().playSound(other.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 10F, 1F);
                }
            }
        }

        ChatColor advancementColor;
        if (advancementType == AdvancementDisplayType.TASK) {
            advancementColor = ChatColor.GREEN;
        } else if (advancementType == AdvancementDisplayType.GOAL) {
            advancementColor = ChatColor.AQUA;
        } else {
            advancementColor = ChatColor.DARK_PURPLE;
        }

        Bukkit.broadcastMessage(player.getName() + " has unlocked the advancement " + advancementColor + "[" + display.getTitle() + "]" + ChatColor.WHITE + "\n Added this advancement to all players!");
    }
}
