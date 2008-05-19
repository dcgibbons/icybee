/*
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright (C) 2000-2008 David C. Gibbons
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.nuclearbunny.icybee.ui;

import java.util.*;

public final class UIMessages {
    public static final ResourceBundle messages = ResourceBundle.getBundle("messages");

    public static final String ICYBEE_APP_NAME = "icybee.app.name";

    public static final String OK_ACTION = "ok.action";
    public static final String CANCEL_ACTION = "cancel.action";

    public static final String MENU_COPY = messages.getString("menu.copy");
    public static final String MENU_SELECT_ALL = messages.getString("menu.select.all");

    public static final String PROPERTIES_DIALOG_TITLE = messages.getString("properties.dialog.title");
    public static final String PROPERTIES_CONNECT_ITEM = messages.getString("properties.connect.item");
    public static final String PROPERTIES_CONNECT_OPTIONS_ITEM = messages.getString("properties.connect.options.item");
    public static final String PROPERTIES_DISPLAY_ITEM = messages.getString("properties.display.item");
    public static final String PROPERTIES_DISPLAY_TEXT_ITEM = messages.getString("properties.display.text.item");
    public static final String PROPERTIES_INPUT_ITEM = messages.getString("properties.input.item");
    public static final String PROPERTIES_LOGGING_ITEM = messages.getString("properties.logging.item");
    public static final String PROPERTIES_SCRIPTING_ITEM = messages.getString("properties.scripting.item");
    public static final String PROPERTIES_ADVANCED_ITEM = messages.getString("properties.advanced.item");

    public static final String PROPERTIES_SERVER_DIALOG_TITLE = messages.getString("properties.server.dialog.title");
    public static final String PROPERTIES_ICB_SERVER_MGMT_TITLE = messages.getString("properties.icb.server.mgmt.title");
    public static final String PROPERTIES_USER_INFO_TITLE = messages.getString("properties.icb.server.info.title");

    public static final String PROPERTIES_CONNECT_OPTIONS_TITLE = messages.getString("properties.connect.options.title");
    public static final String PROPERTIES_ECHOBACK_TITLE = messages.getString("properties.echoback.title");
    public static final String PROPERTIES_DISPLAY_BUFFER_TITLE = messages.getString("properties.display.buffer.title");
    public static final String PROPERTIES_DISPLAY_EMOTICONS_TITLE = messages.getString("properties.display.emoticons.title");
    public static final String PROPERTIES_DISPLAY_MESSAGE_TIMESTAMPS_TITLE = messages.getString("properties.display.timestamps.title");
    public static final String PROPERTIES_DISPLAY_TEXT_COMMON_TEXT_TITLE = messages.getString("properties.display.text.common.text.title");
    public static final String PROPERTIES_DISPLAY_TEXT_STYLE_TITLE = messages.getString("properties.display.text.style.title");
    public static final String PROPERTIES_DISPLAY_TEXT_CUSTOM_TEXT_TITLE = messages.getString("properties.display.text.custom.text.title");
    public static final String PROPERTIES_INPUT_TITLE = messages.getString("properties.input.title");
    public static final String PROPERTIES_LOGGING_TITLE = messages.getString("properties.logging.title");
    public static final String PROPERTIES_LOGGING_SELECT_LOGFILE_TITLE = messages.getString("properties.logging.select.logfile.title");
    public static final String PROPERTIES_SCRIPTING_TITLE = messages.getString("properties.scripting.title");
    public static final String PROPERTIES_SCRIPTING_SELECT_SCRIPT_TITLE = messages.getString("properties.scripting.select.script.title");
    public static final String PROPERTIES_PERSONALS_TITLE = messages.getString("properties.personals.title");

    public static final String PROPERTIES_DISPLAY_BUFFER_SIZE_1000_LINES = messages.getString("properties.display.buffer.size.1000");
    public static final String PROPERTIES_DISPLAY_BUFFER_SIZE_2500_LINES = messages.getString("properties.display.buffer.size.2500");
    public static final String PROPERTIES_DISPLAY_BUFFER_SIZE_5000_LINES = messages.getString("properties.display.buffer.size.5000");
    public static final String PROPERTIES_DISPLAY_BUFFER_SIZE_10000_LINES = messages.getString("properties.display.buffer.size.10000");

    public static final String PROPERTIES_DISPLAY_TEXT_SAMPLE_TEXT_LABEL = messages.getString("properties.display.text.sample.text");

    public static final String PROPERTIES_ENCODING_US_ASCII = messages.getString("properties.encoding.us.ascii");
    public static final String PROPERTIES_ENCODING_UTF7 = messages.getString("properties.encoding.utf7");

    public static final String CONNECT_NAME = "connect.name";
    public static final String CONNECT_SHORT_DESCRIPTION = "connect.short.description";

    public static final String FONT_FAMILY_LABEL = "font.family.label";
    public static final String FONT_SIZE_LABEL = "font.size.label";
    public static final String FONT_BORDER_TITLE_LABEL = "font.border.title.label";

    public static final String CHOOSE_BACKGROUND_TITLE = "choose.background.title";
    public static final String CHOOSE_FOREGROUND_TITLE = "choose.foreground.title";

    public static final String MENU_FILE_NAME = "menu.file.name";
    public static final String MENU_EDIT_NAME = "menu.edit.name";
    public static final String MENU_VIEW_NAME = "menu.view.name";
    public static final String MENU_HELP_NAME = "menu.help.name";

    public static final String ACTION_EXIT_NAME = "action.exit.name";
    public static final String ACTION_EXIT_SHORT_DESCRIPTION = "action.exit.short_description";
    public static final String ACTION_COPY_NAME = "action.copy.name";
    public static final String ACTION_COPY_SHORT_DESCRIPTION = "action.copy.short_description";
    public static final String ACTION_PASTE_NAME = "action.paste.name";
    public static final String ACTION_PASTE_SHORT_DESCRIPTION = "action.paste.short_description";
    public static final String ACTION_TOOLBAR_NAME = "action.toolbar.name";
    public static final String ACTION_TOOLBAR_SHORT_DESCRIPTION = "action.toolbar.short_description";
    public static final String ACTION_STATUSBAR_NAME = "action.statusbar.name";
    public static final String ACTION_STATUSBAR_SHORT_DESCRIPTION = "action.statusbar.short_description";
    public static final String ACTION_REPORTBUG_NAME = "action.reportbug.name";
    public static final String ACTION_REPORTBUG_SHORT_DESCRIPTION = "action.reportbug.short_description";

    public static final String ERROR_REPORTBUG_BROWSER = "error.reportbug.browser";
    public static final String ERROR_LAUNCH_URL = "error.launch.url";
    public static final String ERROR_LAUNCH_URL_TITLE = "error.launch.url.title";

    public static final String INIT_SCRIPT_LABEL = "tcl.init.script.label";
    public static final String COMMAND_PREFIX_LABEL = "command.prefix.label";

    public static final String STATUSBAR_WELCOME = "statusbar.welcome";
    public static final String STATUSBAR_LOGGING_ON = "statusbar.logging.on";
    public static final String STATUSBAR_LOGGING_OFF = "statusbar.logging.off";
    public static final String STATUSBAR_SCROLL_ON = "statusbar.scroll.on";
    public static final String STATUSBAR_SCROLL_OFF = "statusbar.scroll.off";
    public static final String STATUSBAR_CONNECTING = "statusbar.connecting";
    public static final String STATUSBAR_CONNECTED = "statusbar.connected";
    public static final String STATUSBAR_CONNECTED_TIME = "statusbar.connected.time";
    public static final String STATUSBAR_DISCONNECTING = "statusbar.disconnecting";
    public static final String STATUSBAR_DISCONNECTED = "statusbar.disconnected";
    public static final String STATUSBAR_IDLE_TIME = "statusbar.idle.time";

    public static final String LOGGING_TIME_FORMAT = "logging.time.format";
    public static final String LOGGING_START = "logging.start";
    public static final String LOGGING_STOP = "logging.stop";
    public static final String LOGGING_OPEN_MSG = "logging.open.msg";
    public static final String LOGGING_PERSONAL_MSG = "logging.personal.msg";
    public static final String LOGGING_STATUS_MSG = "logging.status.msg";
    public static final String LOGGING_BEEP_MSG = "logging.beep.msg";
    public static final String LOGGING_CONNECTED_MSG = "logging.connected.msg";
    public static final String LOGGING_ERROR_MSG = "logging.error.msg";
    public static final String LOGGING_DISCONNECTED_MSG = "logging.disconnected.msg";
    public static final String LOGGING_PRINT_MESSAGE = "logging.print.msg";

    public static final String TCL_EVAL_SCRIPT_ERROR = "tcl.eval.script.error";

    public static final String CLIENT_POPUP_OPEN_PERSONAL = "client.popup.open.personal";
    public static final String CLIENT_POPUP_WHOIS = "client.popup.whois";
    public static final String CLIENT_DISCONNECTED_MSG = "client.disconnected.msg";
}
