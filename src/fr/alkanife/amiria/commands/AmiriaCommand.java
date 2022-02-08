package fr.alkanife.amiria.commands;

import fr.alkanife.amiria.Amiria;
import fr.alkanife.botcommons.Command;
import fr.alkanife.botcommons.Lang;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import net.dv8tion.jda.api.interactions.components.Component;
import net.dv8tion.jda.api.requests.restaction.interactions.ReplyAction;

import java.util.ArrayList;
import java.util.List;

public class AmiriaCommand {

    @Command(name = "amiria")
    public void sudo(SlashCommandEvent slashCommandEvent) {
        if (!slashCommandEvent.getUser().getId().equals(Amiria.getCreatorID())) {
            slashCommandEvent.reply(Lang.t("amiria-command-no")).setEphemeral(true).queue();
            return;
        }

        List<Button> buttons = new ArrayList<>();

        String buttonTitle1 = Lang.t("amiria-command-button-1-title");
        if (!buttonTitle1.equalsIgnoreCase("unused"))
            buttons.add(Button.link(Lang.t("amiria-command-button-1-link"), buttonTitle1));

        String buttonTitle2 = Lang.t("amiria-command-button-2-title");
        if (!buttonTitle2.equalsIgnoreCase("unused"))
            buttons.add(Button.link(Lang.t("amiria-command-button-2-link"), buttonTitle2));

        String buttonTitle3 = Lang.t("amiria-command-button-3-title");
        if (!buttonTitle3.equalsIgnoreCase("unused"))
            buttons.add(Button.link(Lang.t("amiria-command-button-3-link"), buttonTitle3));

        String buttonTitle4 = Lang.t("amiria-command-button-4-title");
        if (!buttonTitle4.equalsIgnoreCase("unused"))
            buttons.add(Button.link(Lang.t("amiria-command-button-4-link"), buttonTitle4));

        String buttonTitle5 = Lang.t("amiria-command-button-5-title");
        if (!buttonTitle5.equalsIgnoreCase("unused"))
            buttons.add(Button.link(Lang.t("amiria-command-button-5-link"), buttonTitle5)); //actionro;

        slashCommandEvent.reply(Lang.t("amiria-command")).addActionRows(ActionRow.of(buttons)).queue();
    }

}
