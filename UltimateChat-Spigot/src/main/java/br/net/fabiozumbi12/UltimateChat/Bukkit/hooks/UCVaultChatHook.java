/*
 * Copyright (c) 2012-2025 - @FabioZumbi12
 * Last Modified: 16/07/2025 18:07
 *
 * This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
 *  damages arising from the use of this class.
 *
 * Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 * redistribute it freely, subject to the following restrictions:
 * 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 * use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 * 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 * 3 - This notice may not be removed or altered from any source distribution.
 *
 * Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 * responsabilizados por quaisquer danos decorrentes do uso desta classe.
 *
 * É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 * alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 * 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
 *  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 * 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 * classe original.
 * 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

package br.net.fabiozumbi12.UltimateChat.Bukkit.hooks;

import br.net.fabiozumbi12.UltimateChat.Bukkit.UChat;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class UCVaultChatHook {
    private final Player sender;

    UCVaultChatHook(Player sender) {
        this.sender = sender;
    }

    public String getGroupSuffixes() {
        if (UCVaultCache.gSuffix.containsKey(sender.getName())) {
            return UCVaultCache.gSuffix.get(sender.getName());
        }
        StringBuilder gsuffixes = new StringBuilder();
        for (String g : UCVaultCache.getVaultPerms(sender).getPlayerGroups()) {
            gsuffixes.append(UChat.get().getHooks().getVaultChat().getGroupSuffix(sender.getWorld().getName(), g));
        }
        String gps = gsuffixes.toString();

        setTempCacheGSuffix(sender.getName(), gps);
        return gps;
    }

    private void setTempCacheGSuffix(String sender, String gps) {
        UCVaultCache.gSuffix.put(sender, gps);
        Bukkit.getScheduler().runTaskLater(UChat.get(), () -> UCVaultCache.gSuffix.remove(sender), 40);
    }


    public String getGroupPrefixes() {
        if (UCVaultCache.gPrefix.containsKey(sender.getName())) {
            return UCVaultCache.gPrefix.get(sender.getName());
        }
        StringBuilder gprefixes = new StringBuilder();
        for (String g : UCVaultCache.getVaultPerms(sender).getPlayerGroups()) {
            String pref = UChat.get().getHooks().getVaultChat().getGroupPrefix(sender.getWorld().getName(), g);
            if (pref != null)
                gprefixes.append(UChat.get().getHooks().getVaultChat().getGroupPrefix(sender.getWorld().getName(), g));
        }
        String gps = gprefixes.toString();

        setTempCacheGPrefix(sender.getName(), gps);
        return gps;
    }

    private void setTempCacheGPrefix(String sender, String gps) {
        UCVaultCache.gPrefix.put(sender, gps);
        Bukkit.getScheduler().runTaskLater(UChat.get(), () -> UCVaultCache.gPrefix.remove(sender), 40);
    }


    public String getPlayerSuffix() {
        if (UCVaultCache.playerSuffix.containsKey(sender.getName())) {
            return UCVaultCache.playerSuffix.get(sender.getName());
        }
        String suff = UChat.get().getHooks().getVaultChat().getPlayerSuffix(sender);

        setTempCacheSuffix(sender.getName(), suff);
        return suff;
    }

    private void setTempCacheSuffix(String sender, String suff) {
        UCVaultCache.playerSuffix.put(sender, suff);
        Bukkit.getScheduler().runTaskLater(UChat.get(), () -> UCVaultCache.playerSuffix.remove(sender), 40);
    }


    public String getPlayerPrefix() {
        if (UCVaultCache.playerPrefix.containsKey(sender.getName())) {
            return UCVaultCache.playerPrefix.get(sender.getName());
        }
        String pref = UChat.get().getHooks().getVaultChat().getPlayerPrefix(sender);

        getPlayerPrefix(sender.getName(), pref);
        return pref;
    }

    private void getPlayerPrefix(String sender, String pref) {
        UCVaultCache.playerPrefix.put(sender, pref);
        Bukkit.getScheduler().runTaskLater(UChat.get(), () -> UCVaultCache.playerPrefix.remove(sender), 40);
    }

    public String getPlayerPrefixes() {
        if (UCVaultCache.playerPrefixes.containsKey(sender.getName())) {
            return UCVaultCache.playerPrefixes.get(sender.getName());
        }
        StringBuilder gps = new StringBuilder();
        String[] groups = UCVaultCache.getVaultPerms(sender).getPlayerGroups();
        for (String group : groups) {
            if (UChat.get().getUCConfig().getStringList("general.dont-show-groups").contains(group)) continue;
            String prefix = UChat.get().getHooks().getVaultChat().getGroupPrefix(sender.getWorld(), group);
            if (prefix != null)
                gps.append(UChat.get().getHooks().getVaultChat().getGroupPrefix(sender.getWorld(), group));
        }

        getPlayerPrefixes(sender.getName(), gps.toString());
        return gps.toString();
    }

    private void getPlayerPrefixes(String sender, String prefixes) {
        UCVaultCache.playerPrefixes.put(sender, prefixes);
        Bukkit.getScheduler().runTaskLater(UChat.get(), () -> UCVaultCache.playerPrefixes.remove(sender), 40);
    }

    public String getPlayerSuffixes() {
        StringBuilder gps = new StringBuilder();
        String[] groups = UCVaultCache.getVaultPerms(sender).getPlayerGroups();
        for (String group : groups) {
            String suf = UChat.get().getHooks().getVaultChat().getGroupSuffix(sender.getWorld(), group);
            if (suf != null)
                gps.append(UChat.get().getHooks().getVaultChat().getGroupSuffix(sender.getWorld(), group));
        }

        getPlayerSuffixes(sender.getName(), gps.toString());
        return gps.toString();
    }

    private void getPlayerSuffixes(String sender, String prefixes) {
        UCVaultCache.playerPrefixes.put(sender, prefixes);
        Bukkit.getScheduler().runTaskLater(UChat.get(), () -> UCVaultCache.playerPrefixes.remove(sender), 40);
    }

}