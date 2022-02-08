package fr.alkanife.amiria;

import fr.alkanife.botcommons.Lang;
import fr.alkanife.botcommons.Utils;

public class StatusReport {

    private String status = "";

    public StatusReport() {}

    public StatusReport(boolean checkAll) {
        version();
        upday();
        newLine();
        checkTranslations();
    }

    public StatusReport newLine() {
        status += "\n";
        return this;
    }

    public StatusReport version() {
        status += "[Amiria version " + Amiria.getVersion() + "]\n";
        return this;
    }

    public StatusReport upday() {
        status += "[" + Lang.t("logs-status-uptime") + " " + Utils.getUpDays() + "]\n";
        return this;
    }

    public StatusReport checkTranslations() {
        return checkTranslations(false);
    }

    public StatusReport checkTranslations(boolean errorOverride) {
        if (errorOverride) {
            status += " 🛑 " + Lang.t("logs-status-translations-error") + "\n";
            return this;
        }

        if (Lang.getTranslations().size() == 0) {
            status += " ⚠️ " + Lang.t("logs-status-translations-warn") + "\n";
        } else {
            status += " ✅ `" + Lang.getTranslations().size() + "` " + Lang.t("logs-status-translations-loaded") + "\n";
        }

        return this;
    }

    public String getStatus() {
        return status;
    }
}
