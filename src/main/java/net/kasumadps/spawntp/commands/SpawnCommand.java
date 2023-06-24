package net.kasumadps.spawntp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.kasumadps.spawntp.SpawnTP;

public class SpawnCommand implements CommandExecutor, TabCompleter {
  private final FileConfiguration config = SpawnTP.getInstance().getConfig();

  private static String locationPath = "general.location";

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args) {
    // only allow commands from players
    if (!(sender instanceof Player)) return false;
    Player player = (Player) sender;

    // permission check
    if (!player.hasPermission(command.getPermission())) {
      player.sendMessage(this.config.getString("locale.missingPermission", "You are lacking the required permissions."));
      return true;
    }

    // get location
    Location location = this.config.getLocation(locationPath);
    if (location == null) {
      player.sendMessage(this.config.getString("locale.spawnNotSet", "Server spawn not set."));
      return true;
    }

    // teleport
    player.teleport(location, TeleportCause.COMMAND);
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args) {
    return new ArrayList<>();
  }
}