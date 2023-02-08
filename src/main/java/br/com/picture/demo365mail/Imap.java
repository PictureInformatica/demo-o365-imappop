/*
 * Copyright (c) 2023 Picture Soluções em TI - All Rights Reserved
 */

package br.com.picture.demo365mail;

final class Imap extends MailStore {
    Imap(String accessToken) {
        setProtocol("imap");
        setAccessToken(accessToken);
    }

    @Override
    void setup() {
    }
}
