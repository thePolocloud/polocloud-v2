package dev.httpmarco.polocloud.suite.i18n.impl;

import dev.httpmarco.polocloud.suite.PolocloudSuite;
import dev.httpmarco.polocloud.suite.i18n.I18nProvider;
import org.fusesource.jansi.AnsiConsole;

public class I18nPolocloudSuite extends I18nProvider {

    public I18nPolocloudSuite() {
        super("i18n/polocloud-suite");

        AnsiConsole.systemInstall();
        locale(PolocloudSuite.instance().config().language());
    }
}