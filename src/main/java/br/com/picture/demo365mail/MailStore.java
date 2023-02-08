/*
 * Copyright (c) 2023 Picture Soluções em TI - All Rights Reserved
 */

package br.com.picture.demo365mail;

import jakarta.mail.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

abstract sealed class MailStore permits Imap, Pop3 {
    private static final Logger logger = LoggerFactory.getLogger(MailStore.class);

    private String accessToken;
    private Store store;
    private String protocol;
    private final Properties props = new Properties();

    MailStore() {
        putProperty("mail.debug", Application.settings.isDebug());
        putProperty("mail.debug.auth", Application.settings.isDebugAuth());
    }

    void putProperty(Object key, Object value) {
        props.put(key, value);
    }

    protected String getAccessToken() {
        return accessToken;
    }

    protected void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    void setProtocol(String protocol) {
        this.protocol = protocol;
        logger.info("MailStore: protocol "+protocol);
        init();
    }

    abstract void setup() throws NoSuchProviderException;

    private void init() {
        putProperty(String.format("mail.%s.auth.mechanisms", protocol), "XOAUTH2");
        putProperty(String.format("mail.%s.sasl.mechanisms", protocol), "XOAUTH2");
        putProperty(String.format("mail.%s.auth.login.disable", protocol), "true");
        putProperty(String.format("mail.%s.auth.plain.disable", protocol), "true");
        if (Application.settings.isSasl()) {
            putProperty(String.format("mail.%s.sasl.enable", protocol), "true");
        }

        if (ConnEncryptionType.SSL.equals(Application.settings.getEncryption())) {
            putProperty(String.format("mail.%s.ssl.enable", protocol), "true");
        } else if (ConnEncryptionType.STARTTLS.equals(Application.settings.getEncryption())) {
            putProperty(String.format("mail.%s.starttls.enable", protocol), "true");
        }
    }

    void connect() throws MessagingException {
        if (StringUtils.isBlank(getAccessToken())) {
            throw new MessagingException("Invalid access-token");
        }

        setup();

        Session session = Session.getInstance(props);
        store = session.getStore(protocol);
        logger.info("Connecting to server '{}' (CONN:{};SASL:{})",
                Application.settings.getServer(),
                Application.settings.getEncryption(),
                Application.settings.isSasl()
        );
        store.connect(Application.settings.getServer(), Application.settings.getMailbox(), accessToken);
    }

    Store getStore() {
        return store;
    }

    void list() throws MessagingException {
        logger.info("Listing folders");
        for (Folder f : getStore().getDefaultFolder().list()) {
            f.open(Folder.READ_ONLY);
            logger.info(" ***** Folder: " + f);
            for (Message m : f.getMessages()) {
                logger.info(String.format(" *****     From: %s; To: %s; Subject: %s\n", Arrays.asList(m.getFrom()),
                        Arrays.asList(m.getRecipients(Message.RecipientType.TO)), m.getSubject()));
            }
            f.close();
        }
    }
}
