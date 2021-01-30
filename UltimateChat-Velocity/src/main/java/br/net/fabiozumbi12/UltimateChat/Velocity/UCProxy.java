/*
 Copyright @FabioZumbi12

 This class is provided 'as-is', without any express or implied warranty. In no event will the authors be held liable for any
  damages arising from the use of this class.

 Permission is granted to anyone to use this class for any purpose, including commercial plugins, and to alter it and
 redistribute it freely, subject to the following restrictions:
 1 - The origin of this class must not be misrepresented; you must not claim that you wrote the original software. If you
 use this class in other plugins, an acknowledgment in the plugin documentation would be appreciated but is not required.
 2 - Altered source versions must be plainly marked as such, and must not be misrepresented as being the original class.
 3 - This notice may not be removed or altered from any source distribution.

 Esta classe é fornecida "como está", sem qualquer garantia expressa ou implícita. Em nenhum caso os autores serão
 responsabilizados por quaisquer danos decorrentes do uso desta classe.

 É concedida permissão a qualquer pessoa para usar esta classe para qualquer finalidade, incluindo plugins pagos, e para
 alterá-lo e redistribuí-lo livremente, sujeito às seguintes restrições:
 1 - A origem desta classe não deve ser deturpada; você não deve afirmar que escreveu a classe original. Se você usar esta
  classe em um plugin, uma confirmação de autoria na documentação do plugin será apreciada, mas não é necessária.
 2 - Versões de origem alteradas devem ser claramente marcadas como tal e não devem ser deturpadas como sendo a
 classe original.
 3 - Este aviso não pode ser removido ou alterado de qualquer distribuição de origem.
 */

package br.net.fabiozumbi12.UltimateChat.Velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import org.slf4j.Logger;

import java.io.*;
import java.util.stream.Collectors;

@Plugin(
        id = "ultimatechat_velocity",
        name = "UltimateChat Velocity",
        version = "1.0-SNAPSHOT",
        authors = {"FabioZumbi12"}
)
public class UCProxy {

    private static final String UCHAT_CHANNEL = "bungee:uchat";

    @Inject
    private Logger logger;

    @Inject
    private ProxyServer proxy;

    private final MinecraftChannelIdentifier ci = MinecraftChannelIdentifier.from(UCHAT_CHANNEL);

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        proxy.getChannelRegistrar().register(ci);
    }

    @Subscribe
    public void onPluginMessage(PluginMessageEvent e) {

        if (!UCHAT_CHANNEL.equals(e.getIdentifier().getId())) {
            return;
        }

        ByteArrayInputStream stream = new ByteArrayInputStream(e.getData());
        DataInputStream in = new DataInputStream(stream);
        String id = "";
        String ch = "";
        String json = "";
        try {
            id = in.readUTF();
            ch = in.readUTF();
            json = in.readUTF();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        sendMessage(ch, json, id);
    }

    public void sendMessage(String ch, String json, String id) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(stream);
        try {
            out.writeUTF(id);
            out.writeUTF(ch);
            out.writeUTF(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (RegisteredServer si : proxy.getAllServers().stream().filter(si -> !si.getPlayersConnected().isEmpty()).collect(Collectors.toList())) {
            si.sendPluginMessage(ci, stream.toByteArray());
        }
    }
}