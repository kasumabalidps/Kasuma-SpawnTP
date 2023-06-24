package net.kasumadps.spawntp;

import net.kasumadps.spawntp.commands.SpawnCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.kasumadps.spawntp.commands.SetSpawnCommand;

@Slf4j
public final class SpawnTP extends JavaPlugin implements Listener {
  @Getter
  private static SpawnTP instance;

  @Override
  public void onEnable() {
    instance = this;

    saveDefaultConfig();

    getCommand("spawn").setPermission("spawntp.teleport");
    getCommand("spawn").setExecutor(new SpawnCommand());

    getCommand("setspawn").setPermission("spawntp.admin");
    getCommand("setspawn").setExecutor(new SetSpawnCommand());
  }

  @Override
  public void onDisable() {
    log.info("Plugin disabled.");
  }
}