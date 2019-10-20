package me.mundotv;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Filter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import me.mundotv.api.APIBungee;
import me.mundotv.utils.Config;
import net.dv8tion.jda.core.entities.Guild;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class BungeeMain extends Plugin implements Listener {

    @Override
    public void onEnable() {
        try {
            Config.iniciar(new APIBungee(this), getDescription().getVersion());
        } catch (IOException ex) {
            getLogger().log(Level.WARNING, "§cERRO! Config {0}", ex.getMessage());
            Config.erro = true;
        }
        BungeeCord.getInstance().getPluginManager().registerListener(this, this);
        desativarConsole();
    }

    public void desativarConsole() {
        Filter f = new Filter() {
            @Override
            public boolean isLoggable(LogRecord lr) {
                String msg = lr.getMessage();
                return !(msg.contains("has connected") || msg.contains("has disconnected"));
            }
        };
        BungeeCord.getInstance().getLogger().setFilter(f);
    }

    @Override
    public void onDisable() {
        if (Config.bot != null) {
            Config.bot.stop();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPreLogin(PreLoginEvent e) {
        if (Config.erro) {
            e.getConnection().disconnect("§cocorreu um erro ao inicializar o MTDiscordLogin v" + getDescription().getVersion() + " \nverifique o console para mais detalhes \n surpote: §7raspcraft.tk/discord");
            return;
        }
        try {
            String nome = e.getConnection().getName();
            switch (Config.mysql.checkerIP(nome, e.getConnection().getAddress().getHostString())) {
                case 1:
                    e.getConnection().disconnect(Config.api.getMsg("MessagensKick.SemRegistro").replace("%nick%", nome));
                    return;
                case 2:
                    e.getConnection().disconnect(Config.api.getMsg("MessagensKick.SemLogin").replace("%nick%", nome));
                    return;
                case 3:
                    Guild g;
                    if ((g = Config.bot.jda.getGuildById(Config.api.getString("Discord.IDServer"))) == null) {
                        g = Config.bot.jda.getGuilds().get(0);
                    }
                    if (g.getMemberById(Config.mysql.getDC(nome)) == null) {
                        e.getConnection().disconnect(Config.api.getMsg("MessagensKick.ForaDoDiscord").replace("%nick%", nome));
                    }
                    System.out.println(nome + " se conectou ao servidor");
                    return;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            getLogger().log(Level.WARNING, "§cERRO! MySQL {0}", ex.getMessage());
        }
        e.getConnection().disconnect("§cERRO!");
    }
}
