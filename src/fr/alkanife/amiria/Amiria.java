package fr.alkanife.amiria;

import fr.alkanife.amiria.commands.Handler;
import fr.alkanife.botcommons.Lang;
import fr.alkanife.botcommons.Utils;
import fr.alkanife.botcommons.YMLReader;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.util.HashMap;
import java.util.Random;

public class Amiria {

    private static String version = "prod-3.0.1";

    private static Logger logger;

    private static HashMap<String, Object> configurationValues;

    private static String token;
    private static String creatorID;

    private static Guild enyxia;

    private static Role people;

    private static TextChannel hrp;
    private static TextChannel logs;

    private static JDA jda;
    private static Handler handler;

    public static void main(String[] args) {
        Utils.clearTerminal();

        logger = LoggerFactory.getLogger(Amiria.class);
        handler = new Handler();

        logger.info("––––––––––––––––––––––––––––––––––––––––––––");
        logger.info("Starting Amiria " + version);

        try {
            logger.info("Reading configuration.yml file");

            configurationValues = YMLReader.read("configuration");

            if (configurationValues == null) {
                logger.error("Configuration file not found");
                return;
            }

            Object token = configurationValues.get("token");

            if (token == null) {
                logger.error("Invalid token");
                return;
            }

            Amiria.token = String.valueOf(token);

            Object creatorID = configurationValues.get("creator-id");

            if (creatorID == null) {
                logger.error("creator-id not found");
                return;
            }

            Amiria.creatorID = String.valueOf(creatorID);

            logger.info("Starting JDA");
            JDABuilder jdaBuilder = JDABuilder.createDefault(Amiria.token);
            jdaBuilder.setRawEventsEnabled(true);
            jdaBuilder.setStatus(OnlineStatus.ONLINE);
            jdaBuilder.enableIntents(GatewayIntent.GUILD_MEMBERS);
            jdaBuilder.enableIntents(GatewayIntent.GUILD_VOICE_STATES);
            jdaBuilder.setMemberCachePolicy(MemberCachePolicy.ALL);
            jdaBuilder.setActivity(Activity.watching("Enyxia"));
            jdaBuilder.addEventListeners(new Events());
            jda = jdaBuilder.build();

        } catch (Exception exception) {
            logger.error("Failed to start", exception);
        }
    }

    public static Handler getHandler() {
        return handler;
    }

    public static String getVersion() {
        return version;
    }

    public static Logger getLogger() {
        return logger;
    }

    public static HashMap<String, Object> getConfigurationValues() {
        return configurationValues;
    }

    public static String getToken() {
        return token;
    }

    public static Guild getEnyxia() {
        return enyxia;
    }

    public static void setEnyxia(Guild enyxia) {
        Amiria.enyxia = enyxia;
    }

    public static JDA getJda() {
        return jda;
    }

    public static Role getPeople() {
        return people;
    }

    public static void setPeople(Role people) {
        Amiria.people = people;
    }

    public static TextChannel getHrp() {
        return hrp;
    }

    public static void setHrp(TextChannel hrp) {
        Amiria.hrp = hrp;
    }

    public static TextChannel getLogs() {
        return logs;
    }

    public static void setLogs(TextChannel logs) {
        Amiria.logs = logs;
    }

    public static String getCreatorID() {
        return creatorID;
    }

    public static void broadLog(EmbedBuilder embedBuilder) {
        broadLog(embedBuilder.build());
    }

    public static void broadLog(MessageEmbed messageEmbed) {
        Amiria.getLogs().sendMessage(messageEmbed).queue();
    }

    public static void broadLog(MessageBuilder messageBuilder) {
        Amiria.getLogs().sendMessage(messageBuilder.build()).queue();
    }

    public static void broadLog(Message message) {
        Amiria.getLogs().sendMessage(message).queue();
    }

    public static void broadcastError(Exception exception) {
        String message = "";

        if (exception != null)
            if (exception.getCause() != null)
                if (exception.getCause().getMessage() != null)
                    message = exception.getCause().getMessage();

        broadcastError(message);
    }

    public static void broadcastError(String message) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Lang.t("error-title"));

        String[] errorMessages = Lang.t("error-messages").split("\n");
        int random = new Random().nextInt(errorMessages.length);
        String errorMessage = errorMessages[random].replaceAll("nl", "\n");

        embedBuilder.setDescription("*" + errorMessage + "*\n\n`" + message + "`");
        embedBuilder.setColor(new Color(122, 0, 0));
        embedBuilder.setThumbnail(Lang.t("error-gif"));

        MessageBuilder messageBuilder = new MessageBuilder("<@!" + Amiria.getCreatorID() + ">");
        messageBuilder.setEmbed(embedBuilder.build());

        broadLog(messageBuilder);
    }
}
