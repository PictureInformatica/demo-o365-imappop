/*
 * Copyright (c) 2023 Picture Soluções em TI - All Rights Reserved
 */

package br.com.picture.demo365mail;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;

class Settings {
    private static final Logger logger = LoggerFactory.getLogger(Settings.class);
    private final Config config;

    Settings() {
        File configFile = Paths.get(System.getProperty("user.dir"), "application.conf").toFile();
        if (configFile.exists()) {
            logger.info("Loading config file '{}'", configFile);
        } else {
            //try to copy a config template
            try (InputStream templateStream = Settings.class.getResourceAsStream("/application.conf")) {
                if (templateStream != null) {
                    logger.info("Trying to create a new config file at '{}'", configFile.toPath());
                    byte[] templateBytes = templateStream.readAllBytes();
                    Files.write(configFile.toPath(), templateBytes, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                }
            } catch (IOException ignored) {
            }
        }
        config = ConfigFactory.parseFile(configFile);
    }

    String getClientId() {
        return config.getString("azure.client-id");
    }

    String getClientSecret() {
        return config.getString("azure.client-secret");
    }

    String getTenant() {
        return config.getString("azure.tenant");
    }

    String getOauthUrl() {
        return config.getString("azure.oauth-url");
    }

    String getOauthScope() {
        return config.getString("azure.oauth-scope");
    }

    String getMailbox() {
        return config.getString("mail.mailbox");
    }

    String getServer() {
        return config.getString("mail.server");
    }

    ConnEncryptionType getEncryption() {
        String conf = config.getString("mail.encryption");
        if (StringUtils.equalsIgnoreCase("ssl", conf)) {
            return ConnEncryptionType.SSL;
        } else if (StringUtils.equalsIgnoreCase("tls", conf)) {
            return ConnEncryptionType.STARTTLS;
        } else {
            return ConnEncryptionType.NONE;
        }
    }

    boolean isSasl() {
        return config.getBoolean("mail.sasl");
    }

    boolean isDebug() {
        return config.getBoolean("mail.debug");
    }

    boolean isDebugAuth() {
        return config.getBoolean("mail.debug-auth");
    }

}
