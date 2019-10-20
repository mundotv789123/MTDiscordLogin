package me.mundotv.discord;

import java.sql.SQLException;
import java.util.List;
import me.mundotv.utils.Config;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Comandos extends ListenerAdapter {

    private final String pref;

    public Comandos() {
        this.pref = Config.api.getString("config.prefix") == null ? "m!" : Config.api.getString("config.prefix");
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        MessageChannel mc = event.getChannel();
        if (Config.api.getString("Discord.IDCanal") == null || Config.api.getString("Discord.IDCanal").equals("") || mc.getId().equals(Config.api.getString("Discord.IDCanal"))) {
            String[] cmd = event.getMessage().getContentRaw().split(" ");
            if (cmd[0].startsWith(pref)) {
                String dcid = event.getMember().getUser().getId();
                String dcmen = event.getMember().getAsMention();
                try {
                    switch (cmd[0].substring(pref.length())) {
                        case "registrar":
                        case "add":
                        case "register":
                            if (cmd.length > 1) {
                                if (isValid(cmd[1])) {
                                    if (Config.mysql.getList(dcid).size() < Config.api.getInt("config.limite-contas")) {
                                        switch (Config.mysql.checker(cmd[1], dcid)) {
                                            case 1:
                                                Config.mysql.registrar(cmd[1], dcid);
                                                mc.sendMessage(getMsg("Registrando", dcmen)).complete();
                                                break;
                                            case 2:
                                                mc.sendMessage(getMsg("NickRegistrado", dcmen)).complete();
                                                break;
                                            case 3:
                                                mc.sendMessage(getMsg("JaRegistrado", dcmen)).complete();
                                                break;
                                            default:
                                                mc.sendMessage("???").complete();
                                        }
                                    } else {
                                        mc.sendMessage(getMsg("LimiteExedido", dcmen)).complete();
                                    }
                                } else {
                                    mc.sendMessage(getMsg("NickInvalido", dcmen)).complete();
                                }
                            } else {
                                mc.sendMessage(getMsg("ComandoIncompleto", dcmen)).complete();
                            }
                            break;
                        case "logar":
                        case "login":
                            if (cmd.length > 1) {
                                if (isValid(cmd[1])) {
                                    switch (Config.mysql.checker(cmd[1], dcid)) {
                                        case 1:
                                            mc.sendMessage(getMsg("NickNaoRegistrado", dcmen)).complete();
                                            break;
                                        case 2:
                                            mc.sendMessage(getMsg("NickRegistrado", dcmen)).complete();
                                            break;
                                        case 3:
                                            Config.mysql.resetIP(cmd[1]);
                                            mc.sendMessage(getMsg("Logando", dcmen)).complete();
                                            break;
                                    }
                                } else {
                                    mc.sendMessage(getMsg("NickInvalido", dcmen)).complete();
                                }
                            } else {
                                mc.sendMessage(getMsg("ComandoIncompleto", dcmen)).complete();
                            }
                            break;
                        case "desregistrar":
                        case "unregister":
                        case "remover":
                        case "remove":
                            if (cmd.length > 1) {
                                if (isValid(cmd[1])) {
                                    switch (Config.mysql.checker(cmd[1], dcid)) {
                                        case 1:
                                            mc.sendMessage(getMsg("NickNaoRegistrado", dcmen)).complete();
                                            break;
                                        case 2:
                                            mc.sendMessage(getMsg("NickRegistrado", dcmen)).complete();
                                            break;
                                        case 3:
                                            Config.api.kickPlayer(cmd[1], "§cdesregistrado!!!");
                                            Config.mysql.desregistrar(cmd[1], dcid);
                                            mc.sendMessage(getMsg("Desregistrando", dcmen)).complete();
                                            break;
                                        default:
                                            mc.sendMessage("???").complete();
                                    }
                                } else {
                                    mc.sendMessage(getMsg("NickInvalido", dcmen)).complete();
                                }
                            } else {
                                mc.sendMessage(getMsg("ComandoIncompleto", dcmen)).complete();
                            }
                            break;
                        case "lista":
                        case "list":
                            if (Config.mysql.getList(dcid).size() > 0) {
                                mc.sendMessage(getMsg("Lista", dcid).replace("%lista%", convStringList(Config.mysql.getList(dcid)))).complete();
                            } else {
                                mc.sendMessage(getMsg("ListaVazia", dcmen)).complete();
                            }
                            break;
                        case "ajuda":
                        case "help":
                            mc.sendMessage(getMsg("Ajuda", dcmen)).complete();
                    }
                } catch (SQLException | ClassNotFoundException ex) {
                    mc.sendMessage(getMsg("ErroInterno", dcmen)).complete();
                    Config.api.sendWarm("§cERRO! DiscordChat {0}" + ex.getMessage());
                }
            }
        }
    }

    private boolean isValid(String str) {
        return str.matches("([\\w\\d]{3,16})$");
    }

    private String convStringList(List<String> list) {
        String msg = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            msg += "\n" + list.get(i);
        }
        return msg;
    }

    private String getMsg(String msg, String dcnick) {
        return Config.api.getMsg("MenssagesDiscord." + msg).replace("%prefix%", pref).replace("%discod-nick%", dcnick);
    }

}
