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

package br.com.devpaulo.legendchat.api.events;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe apenas para compatibilidade com o LegendChat para setar tags.
 * Só o método 'setTagValue' funciona nessa classe.
 *
 * @author Fabio
 */
public class ChatMessageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final HashMap<String, String> tags;
    private final CommandSender sender;
    private boolean cancel = false;
    private String message;

    public ChatMessageEvent(CommandSender sender, HashMap<String, String> tags, String message) {
        super(true);
        this.sender = sender;
        this.tags = tags;
        this.message = message;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Player getSender() {
        if (this.sender instanceof Player) {
            return (Player) this.sender;
        }
        return null;
    }

    public boolean setTagValue(String tagname, String value) {
        addTag(tagname, value);
        return true;
    }

    public String getTagValue(String tag) {
        return tags.get(tag);
    }

    public void addTag(String tagname, String value) {
        tags.put(tagname, value);
    }

    public HashMap<String, String> getTagMap() {
        return tags;
    }

    public List<String> getTags() {
        return new ArrayList<>(tags.keySet());
    }

    @Override
    public boolean isCancelled() {
        return this.cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
