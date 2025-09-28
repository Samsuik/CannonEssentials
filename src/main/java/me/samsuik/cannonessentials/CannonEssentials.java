package me.samsuik.cannonessentials;

import me.samsuik.cannonessentials.commands.MagicSandCommand;
import me.samsuik.cannonessentials.commands.MagicSandRefillCommand;
import me.samsuik.cannonessentials.commands.TntFillCommand;
import me.samsuik.cannonessentials.listeners.ItemDrops;
import me.samsuik.cannonessentials.listeners.MagicSand;
import me.samsuik.cannonessentials.listeners.TntProtection;
import org.bukkit.plugin.java.JavaPlugin;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class CannonEssentials extends JavaPlugin {
    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        final MagicSand magicSand = new MagicSand(this);
        this.registerCommand("tntfill", new TntFillCommand(this));
        this.registerCommand("magicsand", new MagicSandCommand(this));
        this.registerCommand("refill", new MagicSandRefillCommand(this, magicSand));
        this.getServer().getPluginManager().registerEvents(new TntProtection(this), this);
        this.getServer().getPluginManager().registerEvents(magicSand, this);
        this.getServer().getPluginManager().registerEvents(new ItemDrops(this), this);
    }
}
