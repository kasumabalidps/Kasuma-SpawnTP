package net.kasumadps.spawntp.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import javax.management.openmbean.InvalidOpenTypeException;

import net.kasumadps.spawntp.SpawnTP;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SetSpawnCommand implements CommandExecutor, TabCompleter {
  private final JavaPlugin plugin = SpawnTP.getInstance();
  private final FileConfiguration config = this.plugin.getConfig();

  private static String locationPath = "general.location";

  @Override
  public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args) {
    // only allow commands from players
    if (!(sender instanceof Player)) return false;
    Player player = (Player) sender;
    List<String> argsList = Arrays.asList(args);

    // permission check
    if (!player.hasPermission(command.getPermission())) {
      player.sendMessage(this.config.getString("locale.missingPermission", "You are lacking the required permissions."));
      return true;
    }

    // if unset
    if (argsList.contains("unset")) { // NOSONAR - intended
      this.config.set(locationPath, null);
      this.plugin.saveConfig();
      player.sendMessage(this.config.getString("locale.spawnSet", "Spawn location updated."));
      return true;
    }

    // get location
    Location location = player.getLocation();

    // normalize view if necessary
    if (argsList.contains("normalize-view")) {
      location.setYaw((float)this.getClosestDegree(location.getYaw(), new double[] { -180, -90, 0, 90, 180 }));
      location.setPitch((float)this.getClosestDegree(location.getPitch(), new double[] { -180, -90, 0, 90, 180 }));
    }

    // normalize position if necessary
    if (argsList.contains("normalize-position")) {
      location.setX(location.getX() > location.blockX() ? location.blockX() + .5 : location.blockX() - .5);
      location.setZ(location.getZ() > location.blockZ() ? location.blockZ() + .5 : location.blockZ() - .5);
      location.setY(location.blockY()); // no normalizing needed
    }
    
    // store location
    this.config.set(locationPath, location);
    this.plugin.saveConfig();
    player.sendMessage(this.config.getString("locale.spawnSet", "Spawn location updated."));
    return true;
  }

  @Override
  public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String commandLabel, @NotNull String[] args) {
    List<String> completes = new ArrayList<>();
    List<String> argsList = Arrays.asList(args);

    if (argsList.contains("unset")) return completes;

    completes.add("normalize-view");
    completes.add("normalize-position");
    completes.add("unset");

    return completes.stream().filter(c -> !argsList.contains(c)).toList();
  }
  
  private double getClosestDegree(double degree, double[] possibleDegrees) {
    if (possibleDegrees.length == 0) throw new InvalidOpenTypeException("You need to specify at least one possible degree");

    return Arrays.stream(possibleDegrees).boxed()
      .min(Comparator.comparing(d -> Math.abs(d - degree)))
      .orElse(possibleDegrees[0]);
  }
}
