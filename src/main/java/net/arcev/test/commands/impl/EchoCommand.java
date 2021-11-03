package net.arcev.test.commands.impl;


import cloud.commandframework.annotations.CommandDescription;
import cloud.commandframework.annotations.CommandMethod;
import cloud.commandframework.annotations.CommandPermission;
import net.arcev.test.commands.CommandManager;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import static net.kyori.adventure.text.Component.text;

public class EchoCommand {

    public CommandManager manager;
    private BukkitAudiences bukkitAudiences;

    @CommandMethod("echo")
    @CommandDescription("Echo a message back to yourself")
    @CommandPermission("command.echo")
    private void echo(
            final @NonNull Player player,
            final @NonNull String message) {
        bukkitAudiences.player(player).sendMessage(Identity.nil(), text(message, NamedTextColor.GOLD));
    }

}
