package me.mundotv.api;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.List;
import me.mundotv.utils.MySQL;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class APIBungee implements API {

    private Configuration conf;

    public APIBungee(Plugin pl) throws IOException {
        File df = pl.getDataFolder();
        if (df.exists() || df.mkdir()) {
            File file;
            if (!(file = new File(df, "config.yml")).exists()) {
                Files.copy(pl.getResourceAsStream("config.yml"), file.toPath(), new CopyOption[0]);
            }
            conf = ConfigurationProvider.getProvider((Class) YamlConfiguration.class).load(file);
        }
    }

    @Override
    public String getMsg(String str) {
        List<String> lstr = conf.getStringList(str);
        String msg;
        if (lstr == null || lstr.isEmpty()) {
            msg = conf.getString(str);
            if (msg != null) {
                msg = msg.replace("&", "§");
            }
        } else {
            msg = lstr.get(0).replace("&", "§");
            for (int i = 1; i < lstr.size(); i++) {
                msg += "\n" + lstr.get(i).replace("&", "§");
            }
        }
        return (msg == null || msg.equals("")) ? "NULL" : msg;
    }

    @Override
    public String getString(String str) {
        return conf.getString(str);
    }

    @Override
    public int getInt(String str) {
        return conf.getInt(str);
    }

    @Override
    public MySQL getMysql() throws SQLException, ClassNotFoundException {
        return new MySQL(getString("MySQL.host"), getString("MySQL.usuario"), getString("MySQL.senha"), getString("MySQL.database"));
    }

    @Override
    public void kickPlayer(String nick, String motivo) {
        ProxiedPlayer pp;
        if ((pp = BungeeCord.getInstance().getPlayer(nick)) != null) {
            pp.disconnect(motivo);
        }

    }

    @Override
    public void sendLog(String str) {
        BungeeCord.getInstance().getLogger().info(str);
    }

    @Override
    public void sendWarm(String str) {
        BungeeCord.getInstance().getLogger().warning(str);
    }

    @Override
    public void stop() {
        BungeeCord.getInstance().stop();
    }

}
