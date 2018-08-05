package br.net.fabiozumbi12.UltimateChat.Sponge;

import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.PluginContainer;

public interface UCVHelper {
	Cause getCause(CommandSource src);

    Cause getCause(PluginContainer instance);
	
	StringBuilder getEnchantments(StringBuilder sb, ItemStack item);

	ItemStack getItemInHand(Player sender);

	ItemType getItemName(ItemStack itemStack);
}
