package me.samsuik.cannonessentials.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import me.samsuik.cannonessentials.CannonEssentials;
import me.samsuik.cannonessentials.listeners.MagicSand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;

import java.util.Collection;
import java.util.Locale;

@NullMarked
public final class MagicSandCommand implements BasicCommand {
    private final CannonEssentials plugin;

    public MagicSandCommand(final CannonEssentials plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(final CommandSourceStack stack, final String[] args) {
        if (!(stack.getExecutor() instanceof Player player)) {
            return;
        }

        final String requestedMaterial = args.length < 1 ? "sand" : args[0];
        final Material material = Material.matchMaterial(requestedMaterial);
        final ItemStack itemStack = MagicSand.Item.fromMaterial(material);

        if (itemStack != null) {
            player.give(itemStack);
            player.sendRichMessage(plugin.getConfig().getString("magic-sand.give", ""));
        }
    }

    @Override
    public Collection<String> suggest(final CommandSourceStack stack, final String[] args) {
        return MagicSand.Item.getAllMaterials().stream()
                .map(material -> material.name().toLowerCase(Locale.ENGLISH))
                .toList();
    }

    @Override
    public String permission() {
        return  "me.samsuik.cannonessentials.magicsand";
    }
}
