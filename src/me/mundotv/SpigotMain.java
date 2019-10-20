package me.mundotv;

import java.sql.SQLException;
import me.mundotv.api.APISpigot;
import me.mundotv.utils.Config;
import net.dv8tion.jda.core.entities.Guild;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotMain extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config.iniciar(new APISpigot(getConfig()), getDescription().getVersion());
        Bukkit.getPluginManager().registerEvents(this, this);
        desativarConsole();
    }

    public void desativarConsole() {
        /*Filter f = new Filter() {
            @Override
            public boolean isLoggable(LogRecord lr) {
                String msg = lr.getMessage();
                return !(msg.contains("logged in with") || msg.contains("Disconnecting") || msg.contains("lost connection"));
            }
        };
        Bukkit.getLogger().setFilter(f);*/
    }

    @Override
    public void onDisable() {
        if (Config.bot != null) {
            Config.bot.stop();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerPreLogin(PlayerPreLoginEvent e) {
        if (Config.erro) {
            e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "§cocorreu um erro ao inicializar o MTDiscordLogin v" + getDescription().getVersion() + " \nverifique o console para mais detalhes \n surpote: §7raspcraft.tk/discord");
            return;
        }
        try {
            String nome = e.getName();
            switch (Config.mysql.checkerIP(nome, e.getAddress().getHostAddress())) {
                case 1:
                    e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, Config.api.getMsg("MessagensKick.SemRegistro").replace("%nick%", nome));
                    return;
                case 2:
                    e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, Config.api.getMsg("MessagensKick.SemLogin").replace("%nick%", nome));
                    return;
                case 3:
                    Guild g;
                    if ((g = Config.bot.jda.getGuildById(Config.api.getString("Discord.IDServer"))) == null) {
                        g = Config.bot.jda.getGuilds().get(0);
                    }
                    if (g.getMemberById(Config.mysql.getDC(nome)) == null) {
                        e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, Config.api.getMsg("MessagensKick.ForaDoDiscord").replace("%nick%", nome));
                    }
                    System.out.println(nome + " se conectou ao servidor");
                    return;
            }
        } catch (SQLException | ClassNotFoundException ex) {
            Bukkit.getConsoleSender().sendMessage("§cERRO! MySQL " + ex.getMessage());
        }
        e.disallow(PlayerPreLoginEvent.Result.KICK_OTHER, "§cERRO!!!");
    }

}
