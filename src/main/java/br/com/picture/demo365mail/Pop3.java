/*
 * Copyright (c) 2023 Picture Soluções em TI - All Rights Reserved
 */

package br.com.picture.demo365mail;

final class Pop3 extends MailStore {
    Pop3(String accessToken) {
        setProtocol("pop3");
        setAccessToken(accessToken);
    }

    @Override
    void setup() {
        putProperty("mail.pop3.auth.xoauth2.two.line.authentication.format", "true");
        putProperty("mail.pop3.rsetbeforequit", "true");
    }
}
