package me.samsuik.cannonessentials.listeners;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import me.samsuik.cannonessentials.CannonEssentials;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@NullMarked
public final class MagicSand implements Listener {
    private final Map<Player, PlacedMagicSand> playerPlacedMagicSand = new HashMap<>();
    private final CannonEssentials plugin;

    public MagicSand(final CannonEssentials plugin) {
        this.plugin = plugin;
        MagicSand.Item.createItems(plugin);
    }

    @EventHandler
    public void tickEvent(final ServerTickStartEvent tickEvent) {
        if (tickEvent.getTickNumber() % 2 != 0) {
            return;
        }

        for (final Map.Entry<Player, PlacedMagicSand> entry : this.playerPlacedMagicSand.entrySet()) {
            // Players logging out while a cannon is running with magicsand is a massive cause of lag
            if (!entry.getKey().isOnline()) {
                continue;
            }

            for (final Map.Entry<Location, Material> magicSandEntry : entry.getValue().magicSand.entrySet()) {
                final Location belowLocation = magicSandEntry.getKey().clone().subtract(0, 1, 0);
                final Block block = belowLocation.getBlock();

                if (belowLocation.isChunkLoaded() && block.getType().isAir()) {
                    block.setType(magicSandEntry.getValue());
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void placeItem(final BlockPlaceEvent event) {
        final ItemStack heldItem = event.getItemInHand();
        final Material material = MagicSand.Item.getMaterial(heldItem);

        if (material != null) {
            final Player player = event.getPlayer();
            final PlacedMagicSand placedMagicSand = this.playerPlacedMagicSand.computeIfAbsent(
                    player,
                    p -> new PlacedMagicSand()
            );

            final int magicSandLimit = plugin.getConfig().getInt("magic-sand.limit", 0);
            if (placedMagicSand.magicSand.size() >= magicSandLimit) {
                event.getPlayer().sendRichMessage(plugin.getConfig().getString("magic-sand.limited", ""));
                return;
            }

            final Block block = event.getBlock();
            placedMagicSand.magicSand.put(block.getLocation(), material);
            block.setType(Material.CLAY);

            event.getPlayer().sendRichMessage(plugin.getConfig().getString("magic-sand.placed", ""));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void breakItem(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        final Location location = block.getLocation();

        for (final PlacedMagicSand placedMagicSand : this.playerPlacedMagicSand.values()) {
            if (placedMagicSand.magicSand.remove(location) != null) {
                event.getPlayer().sendRichMessage(plugin.getConfig().getString("magic-sand.broken", ""));
            }
        }
    }

    private static final class PlacedMagicSand {
        private final Map<Location, Material> magicSand = new HashMap<>();
    }

    public static final class Item {
        private static final Map<Material, ItemStack> MATERIAL_TO_ITEM = new HashMap<>();
        private static final Map<ItemStack, Material> ITEM_TO_MATERIAL = new HashMap<>();

        public static @Nullable ItemStack fromMaterial(final @Nullable Material material) {
            return MATERIAL_TO_ITEM.get(material);
        }

        public static @Nullable Material getMaterial(final @Nullable ItemStack itemStack) {
            return ITEM_TO_MATERIAL.get(itemStack);
        }

        public static Collection<Material> getAllMaterials() {
            return ITEM_TO_MATERIAL.values();
        }

        private static void createItems(final CannonEssentials plugin) {
            final String itemName = plugin.getConfig().getString("magic-sand.item-name", "");
            for (final Material material : Material.values()) {
                if (!material.hasGravity()) {
                    continue;
                }

                final ItemStack magicSandItem = new ItemStack(material);
                magicSandItem.editMeta(meta -> {
                    meta.setEnchantmentGlintOverride(true);
                    meta.customName(MiniMessage.miniMessage().deserialize(
                            itemName,
                            Placeholder.unparsed("material", material.name().toLowerCase(Locale.ENGLISH))
                    ));
                });

                MATERIAL_TO_ITEM.put(material, magicSandItem);
                ITEM_TO_MATERIAL.put(magicSandItem, material);
            }
        }
    }
}
