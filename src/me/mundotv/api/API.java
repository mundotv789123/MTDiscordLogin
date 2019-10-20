package me.mundotv.api;

import java.sql.SQLException;
import me.mundotv.utils.MySQL;

public interface API {

    public abstract void kickPlayer(String nick, String motivo);

    public abstract void sendLog(String nick);

    public abstract void sendWarm(String nick);

    public abstract void stop();

    public abstract String getMsg(String str);

    public abstract String getString(String str);

    public abstract int getInt(String str);

    public abstract MySQL getMysql() throws SQLException, ClassNotFoundException;
}
