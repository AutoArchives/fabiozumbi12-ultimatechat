package br.net.fabiozumbi12.UltimateChat.Bukkit.Jedis;

import br.net.fabiozumbi12.UltimateChat.Bukkit.UCChannel;
import br.net.fabiozumbi12.UltimateChat.Bukkit.UCPerms;
import br.net.fabiozumbi12.UltimateChat.Bukkit.UCUtil;
import br.net.fabiozumbi12.UltimateChat.Bukkit.UChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import redis.clients.jedis.JedisPubSub;

import java.util.Arrays;

public class ChatChannel extends JedisPubSub {
    private final String[] channels;
    private final String thisId;

    public ChatChannel(String[] channels) {
        this.channels = channels;
        this.thisId = UChat.get().getUCConfig().getString("jedis.server-id").replace("$", "");
    }

    @Override
    public void onMessage(String channel, String message) {
        if (!UChat.get().getUCConfig().getBoolean("debug.messages") && message.split("\\$")[0].equals(this.thisId))
            return;

        if (Arrays.asList(channels).contains(channel)) {
            Bukkit.getScheduler().runTaskAsynchronously(UChat.get(), () -> {
                if (channel.equals("tellresponse")) {
                    String[] tellresp = message.split("@");
                    if (tellresp[0].equals(thisId)) return;
                    if (UChat.get().getJedis().tellPlayers.containsKey(tellresp[1])) {
                        Player sender = Bukkit.getPlayer(UChat.get().getJedis().tellPlayers.get(tellresp[1]));
                        if (sender != null && sender.isOnline()) {
                            if (tellresp[2].equals("false")) {
                                UChat.get().getLang().sendMessage(sender, UChat.get().getLang().get("listener.invalidplayer"));
                            } else {
                                UCUtil.performCommand(sender, Bukkit.getConsoleSender(), "tellraw " + sender.getName() + " " + tellresp[3]);
                            }
                        }
                    }
                    return;
                }

                if (channel.equals("tellsend")) {
                    String[] msgc = message.split("\\$");

                    String id = msgc[0];
                    String tellrec = msgc[1];
                    String messagef = msgc[2];

                    Player play = Bukkit.getPlayer(tellrec);
                    if (play == null) {
                        UChat.get().getJedis().getPool().getResource().publish("tellresponse", thisId + "@" + tellrec + "@false");
                        return;
                    } else {
                        UChat.get().getJedis().getPool().getResource().publish("tellresponse", thisId + "@" + tellrec + "@true@" + messagef.replace("@", ""));
                    }
                    UChat.get().getJedis().tellPlayers.remove(tellrec);
                    Bukkit.getConsoleSender().sendMessage(UCUtil.colorize("&7Private message from server " + id + " to player " + tellrec));

                    //send
                    UCUtil.performCommand(play, Bukkit.getConsoleSender(), "tellraw " + play.getName() + " " + messagef);
                    return;
                }

                if (!channel.equals("generic")) {
                    String[] msgc = message.split("\\$");

                    String id = msgc[0];
                    String messagef = msgc[1];

                    UCChannel ch = UChat.get().getChannel(channel);
                    if (ch == null || !ch.useJedis()) return;

                    if (ch.getDistance() == 0) {
                        if (ch.neeFocus()) {
                            for (String receiver : ch.getMembers()) {
                                if (Bukkit.getPlayer(receiver) != null) {
                                    UCUtil.performCommand(Bukkit.getPlayer(receiver), Bukkit.getConsoleSender(), "tellraw " + receiver + " " + messagef);
                                }
                            }
                        } else {
                            for (Player receiver : Bukkit.getServer().getOnlinePlayers()) {
                                if (UCPerms.channelReadPerm(receiver, ch)) {
                                    UCUtil.performCommand(receiver, Bukkit.getConsoleSender(), "tellraw " + receiver.getName() + " " + messagef);
                                }
                            }
                        }
                    }
                    Bukkit.getConsoleSender().sendMessage(UCUtil.colorize("&7Redis message to channel " + ch.getName() + " from: " + id));
                } else {
                    String[] msgc = message.split("\\$");

                    String id = msgc[0];
                    String messagef = msgc[1];
                    for (Player receiver : Bukkit.getServer().getOnlinePlayers()) {
                        UCUtil.performCommand(receiver, Bukkit.getConsoleSender(), "tellraw " + receiver.getName() + " " + messagef);
                    }
                    Bukkit.getConsoleSender().sendMessage(UCUtil.colorize("&7Raw Message from: " + id));
                }
            });
        }
    }
}
