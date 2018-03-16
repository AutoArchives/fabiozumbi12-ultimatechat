package br.net.fabiozumbi12.UltimateChat.Bukkit.Jedis;

import br.net.fabiozumbi12.UltimateChat.Bukkit.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class UCJedisLoader {
	private JedisPool pool;
	private String[] channels;
	private ChatChannel channel;
	protected HashMap<String, String> tellPlayers = new HashMap<>();
	private String thisId;
    private String ip;
    private int port;
    private String auth;
    private JedisPoolConfig poolCfg;
	
	protected JedisPool getPool(){
		return this.pool;		
	}
	
	public UCJedisLoader(String ip, int port, String auth, List<UCChannel> channels){
		this.thisId = UChat.get().getUCConfig().getString("jedis.server-id").replace("$", "");
        this.ip = ip;
        this.port = port;
        this.auth = auth;

		channels.add(new UCChannel("generic"));
		channels.add(new UCChannel("tellsend"));
		channels.add(new UCChannel("tellresponse"));
		
		String[] newChannels = new String[channels.size()];
		for (int i = 0; i < channels.size(); i++){
			newChannels[i] = channels.get(i).getName().toLowerCase();
		}
		
		this.channels = newChannels;
		channel = new ChatChannel(newChannels);
		poolCfg = new JedisPoolConfig();
		poolCfg.setTestOnBorrow(true);

		//connect
        if (!connectPool()){
            return;
        }

	    UChat.get().getUCLogger().info("REDIS conected.");
	}

    private boolean connectPool(){
        if (this.pool == null || this.pool.isClosed()){
            if (auth.isEmpty()){
                this.pool = new JedisPool(poolCfg, ip, port, 0);
            } else {
                this.pool = new JedisPool(poolCfg, ip, port, 0, auth);
            }

            try {
                Jedis jedis = this.pool.getResource();
                jedis.configSet("timeout", "0");
                new Thread(() -> {
                    try {
                        jedis.subscribe(channel, channels);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (JedisConnectionException e){
                e.printStackTrace();
                UChat.get().getLogger().warning("REDIS not conected! Try again with /chat reload, or check the status of your Redis server.");
                return false;
            }
        }
        return true;
    }

	public void sendTellMessage(CommandSender sender, String tellReceiver, String msg){
		UltimateFancy fancy = new UltimateFancy();
		fancy.textAtStart(ChatColor.translateAlternateColorCodes('&', this.thisId));
		
		//spy
		if (!UCPerms.hasPermission(sender, "uchat.chat-spy.bypass")){
			for (Player receiver:UChat.get().getServer().getOnlinePlayers()){			
				if (!receiver.getName().equals(tellReceiver) && !receiver.equals(sender) && 
						UChat.get().isSpy.contains(receiver.getName()) && UCPerms.hasSpyPerm(receiver, "private")){
					String spyformat = UChat.get().getUCConfig().getString("general.spy-format");
					
					spyformat = spyformat.replace("{output}", ChatColor.stripColor(UCMessages.sendMessage(sender, tellReceiver, msg, new UCChannel("tell"), true).toOldFormat()));
					receiver.sendMessage(ChatColor.translateAlternateColorCodes('&', spyformat));
				}
			}
		}
		
		fancy.appendString(UCMessages.sendMessage(sender, tellReceiver, msg, new UCChannel("tell"), false).toString());
		tellPlayers.put(tellReceiver, sender.getName());
		
		if (Arrays.asList(channels).contains("tellsend")){
			Bukkit.getScheduler().runTaskAsynchronously(UChat.get(), () -> {
                try {
                    connectPool();
                    Jedis jedis = pool.getResource();
                    jedis.publish("tellsend", thisId+"$"+tellReceiver+"$"+fancy.toString());
                    jedis.quit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
		}
	}
	
	public void sendRawMessage(UltimateFancy value){		
		if (Arrays.asList(channels).contains("generic")){
			Bukkit.getScheduler().runTaskAsynchronously(UChat.get(), () -> {
                try {
                    connectPool();
                    Jedis jedis = pool.getResource();
                    jedis.publish("generic", thisId+"$"+value.toString());
                    jedis.quit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
		}		
	}
	
	public void sendMessage(String channel, UltimateFancy value){
			
		if (Arrays.asList(channels).contains(channel)){
			Bukkit.getScheduler().runTaskAsynchronously(UChat.get(), () -> {
                try {
                    connectPool();
                    Jedis jedis = pool.getResource();
                    jedis.publish(channel, thisId+"$"+value.toString());
                    jedis.quit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
		}		
	}

	public void closePool(){
		UChat.get().getUCLogger().info("Closing REDIS...");
		if (this.channel.isSubscribed()){
			this.channel.unsubscribe();
		}
		try {
            this.pool.getResource().close();
        } catch (Exception ignored){}
		this.pool.close();
		this.pool.destroy();
		UChat.get().getUCLogger().info("REDIS closed.");
	}
}
