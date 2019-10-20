package me.mundotv.api;

import java.sql.SQLException;
import java.util.List;
import me.mundotv.utils.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class APISpigot implements API {

    private FileConfiguration conf;

    public APISpigot(FileConfiguration config) {
        this.conf = config;
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
        Player pp;
        if ((pp = Bukkit.getPlayer(nick)) != null) {
            pp.kickPlayer(motivo);
        }

    }

    @Override
    public void sendLog(String str) {
        Bukkit.getConsoleSender().sendMessage(str);
    }

    @Override
    public void sendWarm(String str) {
        sendLog(str);
    }

    @Override
    public void stop() {
        Bukkit.getServer().shutdown();
    }
}
