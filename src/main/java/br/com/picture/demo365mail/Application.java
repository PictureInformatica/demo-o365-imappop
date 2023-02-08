/*
 * Copyright (c) 2023 Picture Soluções em TI - All Rights Reserved
 */

package br.com.picture.demo365mail;

import jakarta.mail.MessagingException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    public static final Settings settings = new Settings();

    public static void main(String[] args) throws MessagingException {
        if(StringUtils.isAnyBlank(settings.getTenant(), settings.getClientId(), settings.getClientSecret(), settings.getMailbox())) {
            logger.error("Invalid configuration");
            System.exit(1);
        }

        // Get OAuth AccessToken for use with IMAP/POP3 XOAUTH2
        // https://learn.microsoft.com/en-us/exchange/client-developer/legacy-protocols/how-to-authenticate-an-imap-pop-smtp-application-by-using-oauth
        logger.info("Requesting oauth access-token from '{}' for tenant '{}'", settings.getOauthUrl(), settings.getTenant());
        OAuthResponse token = OAuth.getToken(settings.getOauthUrl(),
                settings.getClientId(),
                settings.getClientSecret(),
                settings.getOauthScope(),
                settings.getTenant()
        );

        if (token == null || StringUtils.isBlank(token.getAccessToken())) {
            throw new AuthenticationException("Invalid token");
        }

        logger.info("Token lifetime is '{}' seconds", token.getExpiresIn());

        logger.info("Trying IMAP - account '{}'", settings.getMailbox());
        Imap imap = new Imap(token.getAccessToken());
        imap.connect();
        imap.list();

        logger.info("Trying POP3 - account '{}'", settings.getMailbox());
        Pop3 pop3 = new Pop3(token.getAccessToken());
        pop3.connect();
        pop3.list();
    }
}
