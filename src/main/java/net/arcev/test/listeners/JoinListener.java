package net.arcev.test.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void OnJoin(PlayerJoinEvent e) {
        e.setJoinMessage("Welcome Traveler");
    }
}
