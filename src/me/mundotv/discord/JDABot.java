package me.mundotv.discord;

import javax.security.auth.login.LoginException;
import me.mundotv.utils.Config;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;

public class JDABot {

    public JDA jda;

    public JDABot() throws LoginException {
        JDABuilder jdab = new JDABuilder(Config.api.getString("Discord.Token"));
        jdab.setGame(Game.playing(Config.api.getString("config.game-status")));
        jdab.addEventListener(new Comandos());
        jdab.setAutoReconnect(true);
        jda = jdab.buildAsync();
    }

    public void stop() {
        if (jda != null) {
            jda.shutdownNow();
        }
    }
}
