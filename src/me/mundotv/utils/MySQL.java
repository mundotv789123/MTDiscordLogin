package me.mundotv.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class MySQL {

    private Connection conn;
    private Timer timer;
    private final String host, user, pass, db;
    
    public MySQL(String host, String user, String pass, String db) throws SQLException, ClassNotFoundException {
        this.host = host;
        this.user = user;
        this.pass = pass;
        this.db = db;
        timer = new Timer(this);
        getConn().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS mtdiscordlogin(nick varchar(16) not null unique, dcid varchar(18) not null, ip varchar(16) default NULL, ban boolean default false)");
        timer.start();
    }
    
    // 1 = nick não registrado, 2 = nick registrado porem em outro dc, 3 nick registrado no msm dc
    public int checker(String nick, String dcid) throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getConn().prepareStatement("SELECT * FROM mtdiscordlogin WHERE nick=?");
        ps.setString(1, nick);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            if (rs.getString("dcid").equals(dcid)) {
                ps.close();
                return 3;
            }
            ps.close();
            return 2;
        }
        ps.close();
        return 1;
    }

    public void desregistrar(String nick, String dcid) throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getConn().prepareStatement("DELETE FROM mtdiscordlogin WHERE nick=? AND dcid=?");
        ps.setString(1, nick);
        ps.setString(2, dcid);
        ps.executeUpdate();
        ps.close();
    }

    // 1 = nick não registrado, 2 = nick registrado com ip errado, 3 nick com ip correto
    public int checkerIP(String nick, String ip) throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getConn().prepareStatement("SELECT * FROM mtdiscordlogin WHERE nick=?");
        ps.setString(1, nick);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String ipd;
            if ((ipd = rs.getString("ip")) == null || ipd.equals("NULL")) {
                ps.close();
                ps = getConn().prepareStatement("UPDATE mtdiscordlogin SET ip=? WHERE nick=?");
                ps.setString(1, ip);
                ps.setString(2, nick);
                ps.executeUpdate();
                ps.close();
                return 3;
            }
            if (ipd.equals(ip)) {
                return 3;
            }
            return 2;
        }
        return 1;
    }

    public void resetIP(String nick) throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getConn().prepareStatement("UPDATE mtdiscordlogin SET ip=NULL WHERE nick=?");
        ps.setString(1, nick);
        ps.executeUpdate();
        ps.close();
    }

    public String getDC(String nick) throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getConn().prepareStatement("SELECT dcid FROM mtdiscordlogin WHERE nick=?");
        ps.setString(1, nick);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return rs.getString("dcid");
        }
        return null;
    }

    public void registrar(String nick, String dcid) throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getConn().prepareStatement("INSERT INTO mtdiscordlogin (nick, dcid) VALUES (?, ?)");
        ps.setString(1, nick);
        ps.setString(2, dcid);
        ps.executeUpdate();
    }

    public List<String> getList(String dcid) throws SQLException, ClassNotFoundException {
        PreparedStatement ps = getConn().prepareStatement("SELECT nick FROM mtdiscordlogin WHERE dcid=?");
        ps.setString(1, dcid);
        ResultSet rs = ps.executeQuery();
        List<String> ls = new ArrayList();
        while (rs.next()) {
            ls.add(rs.getString("nick"));
        }
        ps.close();
        return ls;
    }
    
    public Connection getConn() throws SQLException, ClassNotFoundException {
        if (isClosed()) {
            Class.forName("com.mysql.jdbc.Driver");
            this.conn = DriverManager.getConnection("jdbc:mysql://" + host + "/" + db + "?useSSL=false", user, pass);
        }
        timer.restartTime();
        return conn;
    }

    public void close() throws SQLException {
        if (!isClosed()) {
            conn.close();
            conn = null;
        }
    }

    public boolean isClosed() throws SQLException {
        return conn == null || conn.isClosed();
    }

    public static class Timer extends Thread {

        private int tempo;
        private MySQL mysql;

        public Timer(MySQL mysql) {
            this.mysql = mysql;
        }

        public void restartTime() {
            tempo = 15;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    if (!this.mysql.isClosed()) {
                        if (this.tempo-- <= 0) {
                            this.mysql.close();
                        }
                    }
                    sleep(1000);
                } catch (InterruptedException | SQLException ex) {
                }
            }
        }
    }
}
