package br.net.fabiozumbi12.UltimateChat.Bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class UCPerms {
	
	private static final HashMap<String, Map<String, Boolean>> cachedPerm = new HashMap<>();
	
	public static boolean hasPermission(CommandSender sender, String perm){
		if (cachedPerm.containsKey(sender.getName())){
			Map<String, Boolean> perms = cachedPerm.get(sender.getName());
			if (perms.containsKey(perm)){
				UChat.get().getUCLogger().debug("UCPerms#hasPermission - Get from Cache");
				return perms.get(perm);
			}
		}
		return testPerm(sender, perm);
	}
	
	private static boolean testPerm(CommandSender sender, String perm){
		if (UChat.get().getVaultPerms() != null){
			UChat.get().getUCLogger().debug("UCPerms#hasPermission - Get from Vault");
			return UChat.get().getVaultPerms().has(sender, perm);
		}
		UChat.get().getUCLogger().debug("UCPerms#hasPermission - Get directly from Player");
		return sender.hasPermission(perm);
	}
		
	public static boolean hasSpyPerm(CommandSender receiver, String ch){
		return hasPerm(receiver, "chat-spy."+ch) || hasPerm(receiver, "chat-spy.all");
	}
	
	static boolean cmdPerm(CommandSender p, String cmd){
		return hasPerm(p, "cmd."+cmd);
	}
	
	public static boolean channelReadPerm(CommandSender p, UCChannel ch){
		UCChannel defCh = UChat.get().getDefChannel(p instanceof  Player ? ((Player)p).getWorld().getName() : null);
		return defCh.equals(ch) || hasPerm(p, "channel."+ch.getName().toLowerCase()+".read");
	}
	
	static boolean channelWritePerm(CommandSender p, UCChannel ch){
		UCChannel defCh = UChat.get().getDefChannel(p instanceof  Player ? ((Player)p).getWorld().getName() : null);
		return defCh.equals(ch) || hasPerm(p, "channel."+ch.getName().toLowerCase()+".write");
	}
	
	static boolean canIgnore(CommandSender sender, Object toignore) {
		return (!(toignore instanceof CommandSender) || !isAdmin((CommandSender) toignore)) && !hasPermission(sender, "uchat.cant-ignore." + (toignore instanceof Player ? ((Player) toignore).getName() : ((UCChannel) toignore).getName()));
	}
	
	static boolean hasPerm(CommandSender p, String perm){
		return isAdmin(p) || hasPermission(p, "uchat."+perm);
	}
	
	private static boolean isAdmin(CommandSender p){
		return (p instanceof ConsoleCommandSender) || hasPermission(p, "uchat.admin");
	}
}
