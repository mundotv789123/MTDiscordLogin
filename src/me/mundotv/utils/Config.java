package me.mundotv.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import javax.security.auth.login.LoginException;
import me.mundotv.api.API;
import me.mundotv.discord.JDABot;

public class Config {

    public static API api;
    public static MySQL mysql;
    public static JDABot bot;
    public static boolean erro;

    public static void iniciar(API api, String ver) {
        erro = false;
        Config.api = api;
        api.sendLog("§a==============================================================");
        api.sendLog("§3Iniciando plugin MTDiscordLogin v" + ver);

        /* procurar atualizações*/
        api.sendLog("§3procurando atualizações...");
        try {
            URL url = new URL("http://raspcraft.tk/update.php?id=1");
            BufferedReader r = new BufferedReader(new InputStreamReader(url.openStream()));
            String l;
            if ((l = r.readLine()) != null) {
                if (!ver.equals(l)) {
                    api.sendLog("§e" + "v" + l + " foi encontrado...");
                    l = r.readLine();
                    api.sendLog("§eBaixe agora mesmo em: §a" + l);
                    while ((l = r.readLine()) != null) {
                        api.sendLog(l.replace("&", "§"));
                    }
                } else {
                    api.sendLog("§3vc esta usando a versão mais atual");
                }
            }
            r.close();
        } catch (MalformedURLException ex) {
            api.sendWarm("§cERRO! ao procurar por atualizações");
        } catch (IOException ex) {
            api.sendWarm("§cERRO! ao procurar por atualizações");
        }

        /* banco de dados */
        api.sendLog("§3iniciando banco de dados mysql...");
        try {
            mysql = api.getMysql();
        } catch (SQLException | ClassNotFoundException ex) {
            api.sendWarm("§cERRO! [MySQL] " + ex.getMessage());
            erro = true;
        }

        /* download de dependencia */
        api.sendLog("§3verificando dependencias...");
        try {
            Class.forName("net.dv8tion.jda.core.JDABuilder");
        } catch (ClassNotFoundException ex) {
            api.sendLog("§eBaixando dependencias...");
            try {
                wget("https://cdn.discordapp.com/attachments/519753169442570241/596418176099680256/Discord_Bot_API_-_V3.0.jar", new File("./plugins/lib"));
                api.sendLog("§aDependencia baixada com sucesso!");
            } catch (IOException ex1) {
                try {
                    wget("http://raspcraft.tk/downloads/libs/Discord_Bot_API_-_V3.0.jar", new File("./plugins/lib"));
                    api.sendLog("§eDependencia baixada com sucesso!");
                } catch (IOException ex2) {
                    api.sendWarm("§cERRO! Não foi possivel baixar as dependencias automaticamente " + ex.getMessage());
                    erro = true;
                    return;
                }
            }
            api.sendLog("§cReinicie o servidor!");
            api.stop();
            return;
        }

        /* bot do discord */
        api.sendLog("§3iniciando bot do discord...");
        try {
            bot = new JDABot();
        } catch (LoginException ex) {
            api.sendWarm("§cERRO! [DiscordBotJDA] " + ex.getMessage());
            erro = true;
        }

        api.sendLog("§a==============================================================");
    }

    private static void wget(String link, File to) throws MalformedURLException, IOException {
        if (to.exists() || to.mkdir()) {
            URL url = new URL(link);
            URLConnection urlc = url.openConnection();
            InputStream in = urlc.getInputStream();
            FileOutputStream f = new FileOutputStream(new File(to, url.getFile().split("/")[url.getFile().split("/").length - 1]));
            int i;
            byte[] b = new byte[1024];
            while ((i = in.read(b)) > 0) {
                f.write(b, 0, i);
            }
            f.close();
            in.close();
        }
    }

}
