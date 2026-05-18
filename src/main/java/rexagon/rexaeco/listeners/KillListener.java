package rexagon.rexaeco.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import rexagon.rexaeco.RexaEco;

public class KillListener implements Listener {
    private final RexaEco plugin;

    public KillListener(RexaEco plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer != null) {
            plugin.getKillCoinManager().handleKill(killer, event.getEntity().getType().name());
        }
    }
}