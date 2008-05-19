/*
 * $Id: ICBProperties.java,v 1.9 2003/01/13 04:31:55 dcgibbons Exp $
 *
 * IcyBee - http://www.nuclearbunny.org/icybee/
 * A client for the Internet CB Network - http://www.icb.net/
 *
 * Copyright © 2000-2003 David C. Gibbons, dcg@nuclearbunny.org
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

package org.nuclearbunny.icybee;

import java.awt.*;
import java.util.*;
import javax.swing.text.*;

public class ICBProperties extends Properties {
    private static final String fontFamilyName = ".font.family";
    private static final String fontSizeName = ".font.size";
    private static final String boldName = ".bold";
    private static final String italicName = ".italic";
    private static final String underlineName = ".underline";
    private static final String backgroundColorName = ".background.color";
    private static final String foregroundColorName = ".foreground.color";

	private String lastReleaseUsed = "0.00";
    private int frameXPos = 0;
    private int frameYPos = 0;
    private int frameWidth = 640;
    private int frameHeight= 480;
    private Boolean toolbarVisible = Boolean.TRUE;
    private Boolean statusbarVisible = Boolean.TRUE;
    private Boolean urlGrabberVisible = Boolean.TRUE;
    private String userNick = "nobody";
    private String userAlternativeNick = "somebody";
    private String userID = System.getProperty("user.name");
    private String userGroup = "1";
    private String userPassword = "";
    private Boolean autoConnect = new Boolean(false);
    private String defaultServer = "default.icb.net:7326";
    private String[] servers = { "default.icb.net:7326", "test.icb.net:7326" };
    private Boolean keepConnectionAlive = Boolean.TRUE;
    private int keepConnectionAliveInterval = 300;
    private Boolean personalWindows = Boolean.TRUE;
    private Boolean addIncomingToHistory = Boolean.TRUE;
    private Boolean addOutgoingToHistory = Boolean.TRUE;
    private Boolean ignoreServerEchoback = Boolean.TRUE;
    private int echoback = Echoback.ECHOBACK_VERBOSE_SERVER;
    private String logFileName = "icb.log";
    private Boolean logAutomatically = Boolean.FALSE;
    private Boolean logAppend = Boolean.TRUE;
    private Map styleMap = new HashMap();
    private Boolean executeInitScript = Boolean.FALSE;
    private String initScript = ".icybee.rc";
    private String commandPrefix = "/";
    private Boolean emoticonsEnabled = Boolean.TRUE;
    private Boolean animatedEmoticonsEnabled = Boolean.TRUE;
    private String lookAndFeel = "";
    private Boolean messageTimestampsEnabled = Boolean.FALSE;
    private String messageTimestampsFormat = "HH:mm";
    private Boolean displaySizeLimitEnabled = Boolean.FALSE;
    private int displaySizeLimit = 1000;
    private String textEncoding = "US-ASCII";

    private String defaultFontFamily = "Monospaced";
    private String defaultFontSize = "12";
    private String defaultBold = Boolean.FALSE.toString();
    private String defaultItalic = Boolean.FALSE.toString();
    private String defaultUnderline = Boolean.FALSE.toString();
    private String defaultBackgroundColor = Integer.toString(SystemColor.window.getRGB());
    private String defaultForegroundColor = Integer.toString(SystemColor.windowText.getRGB());

	private String lastReleaseUsedName = "last.release.used";
    private String frameXPosName = "frame.xpos";
    private String frameYPosName = "frame.ypos";
    private String frameWidthName = "frame.width";
    private String frameHeightName = "frame.height";
    private String toolbarVisibleName = "toolbar.visible";
    private String statusbarVisibleName = "statusbar.visible";
    private String urlGrabberVisibleName = "urlgrabber.visible";
    private String userNickName = "user.nick";
    private String userAlternativeNickName = "user.alternative.nick";
    private String userIDName = "user.id";
    private String userGroupName = "user.group";
    private String userPasswordName = "user.passwd";
    private String autoConnectName = "client.autoconnect";
    private String defaultServerName = "servers.default";
    private String serversName = "servers";
    private String keepConnectionAliveName = "connection.keep.alive.enabled";
    private String keepConnectionAliveIntervalName = "connection.keep.alive.interval";
    private String personalWindowsName = "ui.personalwindows";
    private String addIncomingToHistoryName = "history.addincoming";
    private String addOutgoingToHistoryName = "history.addoutgoing";
    private String ignoreServerEchobackName = "echoback.ignore.server";
    private String echobackName = "echoback";
    private String logFileNameName = "log.filename";
    private String logAutomaticallyName = "log.automatically";
    private String logAppendName = "log.append";
    private String executeInitScriptName = "tcl.init.script.enabled";
    private String initScriptName = "tcl.init.script";
    private String commandPrefixName = "command.prefix";
    private String emoticonsEnabledName = "emoticons.enabled";
    private String animatedEmoticonsEnabledName = "animated.emoticons.enabled";
    private String lookAndFeelName = "look.and.feel";
    private String messageTimestampsEnabledName = "message.timestamps.enabled";
    private String messageTimestampsFormatName = "message.timestamps.format";
    private String displaySizeLimitEnabledName = "display.size.limit.enabled";
    private String displaySizeLimitName = "display.size.limit";
    private String textEncodingName = "text.encoding";

    public ICBProperties() {
        super("icb.props", "ICB Client Properties");
        getParameters();
    }

	public String getLastReleaseUsed() {
		return lastReleaseUsed;
	}
	public void setLastReleaseUsed(String lastReleaseUsed) {
		this.lastReleaseUsed = lastReleaseUsed;
	}

    public void setFrameXPos(int frameXPos) {
        this.frameXPos = frameXPos;
    }
    public int getFrameXPos() {
        return frameXPos;
    }

    public void setFrameYPos(int frameYPos) {
        this.frameYPos = frameYPos;
    }
    public int getFrameYPos() {
        return frameYPos;
    }

    public void setFrameWidth(int frameWidth) {
        this.frameWidth = frameWidth;
    }
    public int getFrameWidth() {
        return frameWidth;
    }

    public void setFrameHeight(int frameHeight) {
        this.frameHeight = frameHeight;
    }
    public int getFrameHeight() {
        return frameHeight;
    }

    public void setToolbarVisible(boolean visible) {
        this.toolbarVisible = new Boolean(visible);
    }
    public boolean isToolbarVisible() {
        return toolbarVisible.booleanValue();
    }

    public void setStatusbarVisible(boolean visible) {
        this.statusbarVisible = new Boolean(visible);
    }
    public boolean isStatusbarVisible() {
        return statusbarVisible.booleanValue();
    }

	public boolean isURLGrabberVisible() {
		return urlGrabberVisible.booleanValue();
	}
	
	public void setURLGrabberVisible(boolean visible) {
		this.urlGrabberVisible = new Boolean(visible);
	}
	
    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }
    public String getUserNick() {
        return userNick;
    }

    public void setUserAlternativeNick(String userAlternativeNick) {
        this.userAlternativeNick = userAlternativeNick;
    }
    public String getUserAlternativeNick() {
        return userAlternativeNick;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }
    public String getUserID() {
        return userID;
    }

    public void setUserGroup(String userGroup) {
        this.userGroup = userGroup;
    }

    public String getUserGroup() {
        return userGroup;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = new Boolean(autoConnect);
    }

    public boolean getAutoConnect() {
        return autoConnect.booleanValue();
    }

    public String getDefaultServer() {
        return defaultServer;
    }
    public void setDefaultServer(String defaultServer) {
        if (defaultServer == null) {
            defaultServer = "";
        }
        this.defaultServer = defaultServer;
    }

    public String[] getServers() {
        return servers;
    }
    public void setServers(String[] servers) {
        this.servers = servers;
    }

	public boolean isKeepConnectionAliveEnabled() {
		return keepConnectionAlive.booleanValue();
	}
	public void setKeepConnectionAliveEnabled(boolean enabled) {
		keepConnectionAlive = new Boolean(enabled);
	}
	
	public int getKeepConnectionAliveInterval() {
		return keepConnectionAliveInterval;
	}
	public void setKeepConnectionAliveInterval(int interval) {
		keepConnectionAliveInterval = interval;
	}
	
    public boolean arePersonalWindowsEnabled() {
        return personalWindows.booleanValue();
    }
    public void setPersonalWindowsEnabled(boolean personalWindows) {
        this.personalWindows = new Boolean(personalWindows);
    }

    public boolean isAddIncomingToHistoryEnabled() {
        return addIncomingToHistory.booleanValue();
    }
    public void setAddIncomingToHistoryEnabled(boolean addToHistory) {
        this.addIncomingToHistory = new Boolean(addToHistory);
    }

    public boolean isAddOutgoingToHistoryEnabled() {
        return addOutgoingToHistory.booleanValue();
    }
    public void setAddOutgoingToHistoryEnabled(boolean addToHistory) {
        this.addOutgoingToHistory = new Boolean(addToHistory);
    }

    public boolean isIgnoreServerEchoback() {
        return ignoreServerEchoback.booleanValue();
    }
    public void setIgnoreServerEchoback(boolean ignoreServerEchoback) {
        this.ignoreServerEchoback = new Boolean(ignoreServerEchoback);
    }

    public int getEchoback() {
        return echoback;
    }
    public void setEchoback(int echoback)  {
        // special fix to reset invalid values of echoback from old client version
        if (echoback >= Echoback.ECHOBACK_VERBOSE_SERVER)  {
            echoback = Echoback.ECHOBACK_VERBOSE_SERVER;
        }
        this.echoback = echoback;
    }

    public String getLogFileName() {
        return logFileName;
    }
    public void setLogFileName(String logFileName) {
        this.logFileName = logFileName;
    }

    public boolean getLogAutomatically() {
        return logAutomatically.booleanValue();
    }
    public void setLogAutomatically(boolean logAutomatically) {
        this.logAutomatically = new Boolean(logAutomatically);
    }

    public boolean getLogAppend() {
        return logAppend.booleanValue();
    }
    public void setLogAppend(boolean logAppend) {
        this.logAppend = new Boolean(logAppend);
    }

    public Style getTextStyle(String styleName) {
        Style s = (Style) styleMap.get(styleName);
        if (s != null) {
            return s;
        }

        StyleContext styleContext = StyleContext.getDefaultStyleContext();
        Style defaultStyle = styleContext.getStyle(StyleContext.DEFAULT_STYLE);
        s = styleContext.addStyle(styleName, defaultStyle);

        String fontFamily = properties.getProperty(styleName + fontFamilyName, defaultFontFamily);
        StyleConstants.setFontFamily(s, fontFamily);

        int fontSize = Integer.parseInt(properties.getProperty(styleName + fontSizeName, defaultFontSize));
        StyleConstants.setFontSize(s, fontSize);

        boolean bold = Boolean.valueOf(properties.getProperty(styleName + boldName, defaultBold)).booleanValue();
        StyleConstants.setBold(s, bold);

        boolean italic = Boolean.valueOf(properties.getProperty(styleName + italicName, defaultItalic)).booleanValue();
        StyleConstants.setItalic(s, italic);
        
        boolean underline = Boolean.valueOf(properties.getProperty(styleName + underlineName, defaultUnderline)).booleanValue();
        StyleConstants.setUnderline(s, underline);

        Color background = new Color(Integer.parseInt(
                properties.getProperty(styleName + backgroundColorName, defaultBackgroundColor)));
        StyleConstants.setBackground(s, background);

        Color foreground = new Color(Integer.parseInt(
                properties.getProperty(styleName + foregroundColorName, defaultForegroundColor)));
        StyleConstants.setForeground(s, foreground);

        styleMap.put(styleName, s);
        return s;
    }
    public void setTextStyle(Style style) {
        styleMap.put(style.getName(), style);
    }

	public boolean isExecuteInitScriptEnabled() {
		return executeInitScript.booleanValue();
	}
	public void setExecuteInitScriptEnabled(boolean enabled) {
		executeInitScript = new Boolean(enabled);
	}
	
    public String getInitScript()  {
        return initScript;
    }
    public void setInitScript(String initScript)  {
        this.initScript = initScript;
    }
    
    public String getCommandPrefix() {
    	return commandPrefix;
    }
    public void setCommandPrefix(String commandPrefix) {
    	this.commandPrefix = commandPrefix;
    }

    public boolean areEmoticonsEnabled() {
        return emoticonsEnabled.booleanValue();
    }
    public void setEmoticonsEnabled(boolean emoticonsEnabled) {
    	this.emoticonsEnabled = new Boolean(emoticonsEnabled);
    }
    
    public boolean areAnimatedEmoticonsEnabled() {
    	return animatedEmoticonsEnabled.booleanValue();
    }
    public void setAnimatedEmoticonsEnabled(boolean animatedEmoticonsEnabled) {
    	this.animatedEmoticonsEnabled = new Boolean(animatedEmoticonsEnabled);
    }
    
    public String getLookAndFeel() {
    	return lookAndFeel;
    }
   	public void setLookAndFeel(String lookAndFeelClassName) {
   		this.lookAndFeel = lookAndFeelClassName;
   	}
   	
   	public boolean areMessageTimestampsEnabled() {
   		return messageTimestampsEnabled.booleanValue();
   	}
   	public void setMessageTimestampsEnabled(boolean messageTimestampsEnabled) {
   		this.messageTimestampsEnabled = new Boolean(messageTimestampsEnabled);
   	}
   	
   	public String getMessageTimestampsFormat() {
   		return messageTimestampsFormat;
   	}
   	public void setMessageTimestampsFormat(String messageTimestampsFormat) {
   		this.messageTimestampsFormat = messageTimestampsFormat;
   	}

	public boolean isDisplaySizeLimited() {
		return displaySizeLimitEnabled.booleanValue();
	}
	public void setDisplaySizeLimited(boolean enabled) {
		this.displaySizeLimitEnabled = new Boolean(enabled);
	}
	
	public int getDisplaySizeLimit() {
		return displaySizeLimit;
	}
	public void setDisplaySizeLimit(int displaySizeLimit) {
		this.displaySizeLimit = displaySizeLimit;
	}
	
	public String getTextEncoding() {
		return textEncoding;
	}
	public void setTextEncoding(String encoding) {
		this.textEncoding = encoding;
	}
	
    protected void setDefaults(java.util.Properties defaults) {
    	defaults.put(lastReleaseUsedName, lastReleaseUsed);
        defaults.put(frameXPosName, Integer.toString(frameXPos));
        defaults.put(frameYPosName, Integer.toString(frameYPos));
        defaults.put(frameWidthName, Integer.toString(frameWidth));
        defaults.put(frameHeightName, Integer.toString(frameHeight));
        defaults.put(toolbarVisibleName, toolbarVisible.toString());
        defaults.put(statusbarVisibleName, statusbarVisible.toString());
        defaults.put(urlGrabberVisibleName, urlGrabberVisible.toString());
        defaults.put(userNickName, userNick);
        defaults.put(userAlternativeNickName, userAlternativeNick);
        defaults.put(userIDName, userID);
        defaults.put(userGroupName, userGroup);
        defaults.put(userPasswordName, userPassword);
        defaults.put(autoConnectName, autoConnect.toString());
        defaults.put(defaultServerName, defaultServer);

        defaults.put(serversName, collapseArray(servers));

		defaults.put(keepConnectionAliveName, keepConnectionAlive.toString());
		defaults.put(keepConnectionAliveIntervalName, Integer.toString(keepConnectionAliveInterval));
		
        defaults.put(personalWindowsName, personalWindows.toString());
        defaults.put(addIncomingToHistoryName, addIncomingToHistory.toString());
        defaults.put(addOutgoingToHistoryName, addOutgoingToHistory.toString());
        defaults.put(ignoreServerEchobackName, ignoreServerEchoback.toString());

        defaults.put(echobackName, Integer.toString(echoback));

        defaults.put(logFileNameName, logFileName);
        defaults.put(logAutomaticallyName, logAutomatically);
        defaults.put(logAppendName, logAppend);

        defaults.put(executeInitScriptName, executeInitScript.toString());
        defaults.put(initScriptName, initScript);
        defaults.put(commandPrefixName, commandPrefix);
        defaults.put(emoticonsEnabledName, emoticonsEnabled.toString());
        defaults.put(animatedEmoticonsEnabledName, animatedEmoticonsEnabled.toString());
        defaults.put(lookAndFeelName, lookAndFeel);
        defaults.put(messageTimestampsEnabledName, messageTimestampsEnabled.toString());
        defaults.put(messageTimestampsFormatName, messageTimestampsFormat);
        
        defaults.put(displaySizeLimitEnabledName, displaySizeLimitEnabled.toString());
        defaults.put(displaySizeLimitName, Integer.toString(displaySizeLimit));
        defaults.put(textEncodingName, textEncoding);

        // XXX HACK ALERT!!!
        // This sucks, FIX IT!
        defaults.put(org.nuclearbunny.icybee.ui.ClientPanel.TEXT_OPEN + ".foreground.color",
                Integer.toString(Color.black.getRGB()));
        defaults.put(org.nuclearbunny.icybee.ui.ClientPanel.TEXT_OPEN_NICK + ".foreground.color",
                Integer.toString(Color.blue.getRGB()));
        defaults.put(org.nuclearbunny.icybee.ui.ClientPanel.TEXT_PERSONAL + ".foreground.color",
                Integer.toString(Color.darkGray.getRGB()));
        defaults.put(org.nuclearbunny.icybee.ui.ClientPanel.TEXT_PERSONAL_NICK + ".foreground.color",
                Integer.toString(Color.lightGray.getRGB()));
        defaults.put(org.nuclearbunny.icybee.ui.ClientPanel.TEXT_COMMAND_OUTPUT + ".foreground.color",
                Integer.toString(Color.black.getRGB()));
        defaults.put(org.nuclearbunny.icybee.ui.ClientPanel.TEXT_ERROR + ".foreground.color",
                Integer.toString(Color.red.getRGB()));
        defaults.put(org.nuclearbunny.icybee.ui.ClientPanel.TEXT_ERROR_HEADER + ".foreground.color",
                Integer.toString(Color.red.getRGB()));
        defaults.put(org.nuclearbunny.icybee.ui.ClientPanel.TEXT_STATUS + ".foreground.color",
                Integer.toString(Color.orange.getRGB()));
        defaults.put(org.nuclearbunny.icybee.ui.ClientPanel.TEXT_STATUS_HEADER + ".foreground.color",
                Integer.toString(Color.black.getRGB()));
        defaults.put(org.nuclearbunny.icybee.ui.ClientPanel.TEXT_TIMESTAMP + ".foreground.color",
        		Integer.toString(Color.blue.getRGB()));
    }

    protected void updateSettingsFromProperties() {
    	lastReleaseUsed = properties.getProperty(lastReleaseUsedName);
        frameXPos = Integer.parseInt(properties.getProperty(frameXPosName));
        frameYPos = Integer.parseInt(properties.getProperty(frameYPosName));
        frameWidth = Integer.parseInt(properties.getProperty(frameWidthName));
        frameHeight = Integer.parseInt(properties.getProperty(frameHeightName));
        toolbarVisible = Boolean.valueOf(properties.getProperty(toolbarVisibleName));
        statusbarVisible = Boolean.valueOf(properties.getProperty(statusbarVisibleName));
        urlGrabberVisible = Boolean.valueOf(properties.getProperty(urlGrabberVisibleName));
        userNick = properties.getProperty(userNickName);
        userAlternativeNick = properties.getProperty(userAlternativeNickName);
        userID = properties.getProperty(userIDName);
        userGroup = properties.getProperty(userGroupName);
        userPassword = properties.getProperty(userPasswordName);
        autoConnect = Boolean.valueOf(properties.getProperty(autoConnectName));
        defaultServer = properties.getProperty(defaultServerName);

        servers = expandArray(properties.getProperty(serversName));

		keepConnectionAlive = Boolean.valueOf(properties.getProperty(keepConnectionAliveName));
		keepConnectionAliveInterval = Integer.parseInt(properties.getProperty(keepConnectionAliveIntervalName));

        personalWindows = Boolean.valueOf(properties.getProperty(personalWindowsName));
        addIncomingToHistory = Boolean.valueOf(properties.getProperty(addIncomingToHistoryName));
        addOutgoingToHistory = Boolean.valueOf(properties.getProperty(addOutgoingToHistoryName));
        ignoreServerEchoback = Boolean.valueOf(properties.getProperty(ignoreServerEchobackName));

        echoback = Integer.parseInt(properties.getProperty(echobackName));

        logFileName = properties.getProperty(logFileNameName);
        logAutomatically = Boolean.valueOf(properties.getProperty(logAutomaticallyName));
        logAppend = Boolean.valueOf(properties.getProperty(logAppendName));

		executeInitScript = Boolean.valueOf(properties.getProperty(executeInitScriptName));
        initScript = properties.getProperty(initScriptName);
        commandPrefix = properties.getProperty(commandPrefixName);
        emoticonsEnabled = Boolean.valueOf(properties.getProperty(emoticonsEnabledName));
        animatedEmoticonsEnabled = Boolean.valueOf(properties.getProperty(animatedEmoticonsEnabledName));
        lookAndFeel = properties.getProperty(lookAndFeelName);
        messageTimestampsEnabled = Boolean.valueOf(properties.getProperty(messageTimestampsEnabledName));
        messageTimestampsFormat = properties.getProperty(messageTimestampsFormatName);
        
        displaySizeLimitEnabled = Boolean.valueOf(properties.getProperty(displaySizeLimitEnabledName));
        displaySizeLimit = Integer.parseInt(properties.getProperty(displaySizeLimitName));
        
        textEncoding = properties.getProperty(textEncodingName);
    }

    protected void updatePropertiesFromSettings() {
    	properties.put(lastReleaseUsedName, lastReleaseUsed);
        properties.put(frameXPosName, Integer.toString(frameXPos));
        properties.put(frameYPosName, Integer.toString(frameYPos));
        properties.put(frameWidthName, Integer.toString(frameWidth));
        properties.put(frameHeightName, Integer.toString(frameHeight));
        properties.put(toolbarVisibleName, toolbarVisible.toString());
        properties.put(statusbarVisibleName, statusbarVisible.toString());
        properties.put(urlGrabberVisibleName, urlGrabberVisible.toString());
        properties.put(userNickName, userNick);
        properties.put(userAlternativeNickName, userAlternativeNick);
        properties.put(userIDName, userID);
        properties.put(userGroupName, userGroup);
        properties.put(userPasswordName, userPassword);
        properties.put(autoConnectName, autoConnect.toString());
        properties.put(defaultServerName, defaultServer);

        properties.put(serversName, collapseArray(servers));

		properties.put(keepConnectionAliveName, keepConnectionAlive.toString());
		properties.put(keepConnectionAliveIntervalName, Integer.toString(keepConnectionAliveInterval));

        properties.put(personalWindowsName, personalWindows.toString());
        properties.put(addIncomingToHistoryName, addIncomingToHistory.toString());
        properties.put(addOutgoingToHistoryName, addOutgoingToHistory.toString());
        properties.put(ignoreServerEchobackName, ignoreServerEchoback.toString());

        properties.put(echobackName, Integer.toString(echoback));

        properties.put(logFileNameName, logFileName);
        properties.put(logAutomaticallyName, logAutomatically.toString());
        properties.put(logAppendName, logAppend.toString());

		properties.put(executeInitScriptName, executeInitScript.toString());
        properties.put(initScriptName, initScript);
        properties.put(commandPrefixName, commandPrefix);
        properties.put(emoticonsEnabledName, emoticonsEnabled.toString());
        properties.put(animatedEmoticonsEnabledName, animatedEmoticonsEnabled.toString());
        properties.put(lookAndFeelName, lookAndFeel);
        properties.put(messageTimestampsEnabledName, messageTimestampsEnabled.toString());
        properties.put(messageTimestampsFormatName, messageTimestampsFormat);
        
        properties.put(displaySizeLimitEnabledName, displaySizeLimitEnabled.toString());
        properties.put(displaySizeLimitName, Integer.toString(displaySizeLimit));
        
        properties.put(textEncodingName, textEncoding);

        /* iterate over the styleMap and store all styles */
        Iterator keys = styleMap.keySet().iterator();
        while (keys.hasNext()) {
            Style s = (Style) styleMap.get(keys.next());
            String styleName = s.getName();
            properties.put(styleName + fontFamilyName, StyleConstants.getFontFamily(s));
            properties.put(styleName + fontSizeName, Integer.toString(StyleConstants.getFontSize(s)));
            properties.put(styleName + boldName, new Boolean(StyleConstants.isBold(s)).toString());
            properties.put(styleName + italicName, new Boolean(StyleConstants.isItalic(s)).toString());
            properties.put(styleName + underlineName, new Boolean(StyleConstants.isUnderline(s)).toString());
            properties.put(styleName + backgroundColorName, Integer.toString(StyleConstants.getBackground(s).getRGB()));
            properties.put(styleName + foregroundColorName, Integer.toString(StyleConstants.getForeground(s).getRGB()));
        }
    }

    protected String collapseArray(String[] a) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < a.length; i++) {
            buffer.append(a[i]);
            if (i + 1 < a.length) {
                buffer.append(',');
            }
        }

        return buffer.toString();
    }

    protected String[] expandArray(String s) {
        ArrayList a = new ArrayList();
        int n;
        while ((n = s.indexOf(',')) != -1) {
            String e = s.substring(0, n);
            a.add(e);
            s = s.substring(n+1);
        }
        a.add(s);

        String[] sa = new String[a.size()];
        return (String[]) a.toArray(sa);
    }
}
