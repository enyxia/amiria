package fr.alkanife.amiria;

import fr.alkanife.amiria.commands.AmiriaCommand;
import fr.alkanife.botcommons.Lang;
import fr.alkanife.botcommons.Utils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.MessageDeleteEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.MessageUpdateEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Events extends ListenerAdapter {

    public static List<LoggedMessage> sentMessages = new ArrayList<>();

    @Override
    public void onReady(ReadyEvent readyEvent) {
        Amiria.getLogger().info("Connected to Discord");
        Amiria.getLogger().info("Fetching Discord informations");

        JDA jda = readyEvent.getJDA();


        //search enyxia
        Guild enyxia = jda.getGuildById("341997146519633922");

        if (enyxia == null) {
            Amiria.getLogger().error("Enyxia was not found");
            jda.shutdown();
            return;
        }

        Amiria.setEnyxia(enyxia);
        Amiria.getLogger().info("Enyxia was found, " + enyxia.getMemberCount() + " members");


        //search people role
        Object peopleID = Amiria.getConfigurationValues().get("people-id");

        if (peopleID == null) {
            Amiria.getLogger().error("Invalid configuration: people-id is null");
            jda.shutdown();
            return;
        }

        Role people = enyxia.getRoleById(String.valueOf(peopleID));

        if (people == null) {
            Amiria.getLogger().error("People not found");
            jda.shutdown();
            return;
        }

        Amiria.setPeople(people);
        Amiria.getLogger().info(people.getName() + " role was found");


        //search #hrp channel
        Object hrpID = Amiria.getConfigurationValues().get("hrp-id");

        if (hrpID == null) {
            Amiria.getLogger().error("Invalid configuration: hrp-id is null");
            jda.shutdown();
            return;
        }

        TextChannel hrp = enyxia.getTextChannelById(String.valueOf(hrpID));

        if (hrp == null) {
            Amiria.getLogger().error("#hrp was not found");
            jda.shutdown();
            return;
        }

        Amiria.setHrp(hrp);
        Amiria.getLogger().info("#" + hrp.getName() + " channel was found");


        //search #logs channel
        Object logsID = Amiria.getConfigurationValues().get("logs-id");

        if (logsID == null) {
            Amiria.getLogger().error("Invalid configuration: logs-id is null");
            jda.shutdown();
            return;
        }

        TextChannel logs = enyxia.getTextChannelById(String.valueOf(logsID));

        if (logs == null) {
            Amiria.getLogger().error("#logs was not found");
            jda.shutdown();
            return;
        }

        Amiria.setLogs(logs);
        Amiria.getLogger().info("#" + logs.getName() + " channel was found");


        StatusReport statusReport = new StatusReport().version().newLine();

        Amiria.getLogger().info("Loading translations");
        try {
            Lang.load();
            statusReport.checkTranslations();
        } catch (Exception exception) {
            statusReport.checkTranslations(true);
            Amiria.getLogger().error("Failed to load translations", exception);
        }
        Amiria.getLogger().info(Lang.getTranslations().size() + " loaded translations");

        Amiria.getLogger().info("Setting up amiria command");
        Amiria.getHandler().registerCommands(new AmiriaCommand());

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Lang.t("logs-power-on-title"));
        embedBuilder.setDescription("**" + Lang.t("logs-power-on-status") + "**\n" + statusReport.getStatus());

        String[] okMemes = Lang.t("ok-memes").split("\n");
        int random = new Random().nextInt(okMemes.length);
        String okmeme = okMemes[random];

        embedBuilder.setThumbnail(okmeme);
        embedBuilder.setColor(new Color(150, 224, 136));
        Amiria.broadLog(embedBuilder.build());

        Amiria.getLogger().info("Ready!");

        //Amiria.getEnyxia().updateCommands().addCommands(new CommandData("amiria", "Commande principale")).queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent messageReceivedEvent) {
        if (!messageReceivedEvent.getChannelType().equals(ChannelType.PRIVATE))
            return;

        if (!messageReceivedEvent.getAuthor().getId().equalsIgnoreCase(Amiria.getCreatorID()))
            return;

        String message = messageReceivedEvent.getMessage().getContentRaw().toLowerCase(Locale.ROOT);

        switch (message) {
            case "reboot":
                messageReceivedEvent.getMessage().reply("Redémarrage").queue(message1 -> {
                    EmbedBuilder embedBuilder = new EmbedBuilder();
                    embedBuilder.setTitle(Lang.t("logs-power-off"));
                    embedBuilder.setImage(Lang.t("logs-power-off-image"));
                    embedBuilder.setColor(new Color(141, 61, 61));

                    Amiria.broadLog(embedBuilder.build());
                    Amiria.getLogger().info("Shutting down");
                    Amiria.getJda().shutdown();
                    System.exit(0);
                });
                break;

            case "status":
                messageReceivedEvent.getMessage().reply(new MessageBuilder("État des services :")
                        .setEmbed(new EmbedBuilder().setDescription(new StatusReport(true).getStatus()).build()).build()).queue();
                break;

            case "reload":
                Amiria.getLogger().info("Reloading translations");

                StatusReport statusReport = new StatusReport();

                try {
                    Lang.load();
                    statusReport.checkTranslations();
                } catch (Exception exception) {
                    statusReport.checkTranslations(true);
                    Amiria.getLogger().error("Failed to load translations", exception);
                }

                messageReceivedEvent.getMessage().reply(new MessageBuilder("Rechargement des traductions effectué. Résultat :")
                        .setEmbed(new EmbedBuilder().setDescription(statusReport.getStatus()).build()).build()).queue();
                Amiria.getLogger().info(Lang.getTranslations().size() + " (re)loaded translations");
                break;

            default:
                messageReceivedEvent.getMessage().reply("Commandes administratives :\n" +
                        "> `reboot`, `status`, `reload`").queue();
                break;
        }
    }

    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        Amiria.getHandler().handle(event);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent guildMessageReceivedEvent) {
        if (guildMessageReceivedEvent.getAuthor().isBot())
            return;

        if (sentMessages.size() >= 20)
            sentMessages.remove(0);

        sentMessages.add(new LoggedMessage(guildMessageReceivedEvent.getMessageIdLong(), guildMessageReceivedEvent.getMessage().getContentDisplay(), guildMessageReceivedEvent.getAuthor().getIdLong()));
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent guildMemberJoinEvent) {
        String[] welcomeMessages = Lang.t("welcome-messages", guildMemberJoinEvent.getMember().getAsMention()).split("\n");

        int random = new Random().nextInt(welcomeMessages.length);

        String message = welcomeMessages[random];

        Amiria.getHrp().sendMessage(message).queue();

        guildMemberJoinEvent.getGuild().modifyMemberRoles(guildMemberJoinEvent.getMember(), Amiria.getPeople()).queue();

        User user = guildMemberJoinEvent.getMember().getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Lang.t("logs-member-join"));
        embedBuilder.setThumbnail(user.getAvatarUrl());
        embedBuilder.setColor(new Color(93, 154, 74));
        embedBuilder.addField(Lang.t("logs-member"), user.getAsTag() + " (" + user.getAsMention() + ")", true);

        Amiria.broadLog(embedBuilder.build());
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent guildMemberRemoveEvent) {
        User user = guildMemberRemoveEvent.getUser();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Lang.t("logs-member-left"));
        embedBuilder.setThumbnail(user.getAvatarUrl());
        embedBuilder.setColor(new Color(141, 61, 61));
        embedBuilder.addField(Lang.t("logs-member"), user.getAsTag() + " (" + user.getAsMention() + ")", true);

        Amiria.broadLog(embedBuilder.build());
    }

    @Override
    public void onMessageUpdate(MessageUpdateEvent messageUpdateEvent) {
        String beforeMessage = null;

        for (LoggedMessage sentMessage : sentMessages)
            if (sentMessage.getId() == messageUpdateEvent.getMessageIdLong())
                beforeMessage = Utils.limit(sentMessage.getContent(), 1000);

        if (beforeMessage == null)
            beforeMessage = Lang.t("logs-unknown");

        User user = messageUpdateEvent.getAuthor();
        TextChannel textChannel = messageUpdateEvent.getTextChannel();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(Lang.t("logs-message-edited"));
        embedBuilder.setThumbnail(user.getAvatarUrl());
        embedBuilder.setColor(new Color(61, 141, 132));
        embedBuilder.setDescription("[" + Lang.t("logs-message") + "](" + messageUpdateEvent.getMessage().getJumpUrl() + ")");
        embedBuilder.addField(Lang.t("logs-member"), user.getAsTag() + " (" + user.getAsMention() + ")", true);
        embedBuilder.addField(Lang.t("logs-channel"), textChannel.getName() +  " (" + textChannel.getAsMention() + ")", true);
        embedBuilder.addField(Lang.t("logs-message-edited-before"), beforeMessage, false);
        embedBuilder.addField(Lang.t("logs-message-edited-after"), Utils.limit(messageUpdateEvent.getMessage().getContentDisplay(), 1000), false);

        Amiria.broadLog(embedBuilder.build());
    }

    @Override
    public void onMessageDelete(MessageDeleteEvent messageDeleteEvent) {

        TextChannel textChannel = messageDeleteEvent.getTextChannel();

        LoggedMessage loggedMessage = null;

        for (LoggedMessage sentMessage : sentMessages)
            if (sentMessage.getId() == messageDeleteEvent.getMessageIdLong())
                loggedMessage = sentMessage;


        if (loggedMessage == null) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(Lang.t("logs-message-deleted"));
            embedBuilder.setColor(new Color(141, 61, 61));
            embedBuilder.addField(Lang.t("logs-message-deleted-author"), Lang.t("logs-unknown"), true);
            embedBuilder.addField(Lang.t("logs-channel"), textChannel.getName() +  " (" + textChannel.getAsMention() + ")", true);
            embedBuilder.addField(Lang.t("logs-message"), Lang.t("logs-unknown"), false);
            Amiria.broadLog(embedBuilder.build());
        } else {
            Member member = Amiria.getEnyxia().getMemberById(loggedMessage.getAuthor());

            String author = loggedMessage.getAuthor() + " (" + Lang.t("logs-message-deleted-author-notamember") + ")";

            EmbedBuilder embedBuilder = new EmbedBuilder();

            if (member != null) {
                author = member.getUser().getAsTag() + " (" + member.getAsMention() + ")";
                embedBuilder.setThumbnail(member.getUser().getAvatarUrl());
            }

            embedBuilder.setTitle(Lang.t("logs-message-deleted"));
            embedBuilder.setColor(new Color(141, 61, 61));
            embedBuilder.addField(Lang.t("logs-message-deleted-author"), author, true);
            embedBuilder.addField(Lang.t("logs-channel"), textChannel.getName() +  " (" + textChannel.getAsMention() + ")", true);
            embedBuilder.addField(Lang.t("logs-message"), Utils.limit(loggedMessage.getContent(), 1000), false);
            Amiria.broadLog(embedBuilder.build());
        }
    }

}
