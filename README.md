[![License: GPL v3](https://img.shields.io/badge/License-GPL_v3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)

## Demo application for Office 365 IMAP and POP3 using XOAUTH2 ##

### Description ###

This sample is a working application to test and demonstrate how to use OAuth2 authentication with Microsoft Exchange Online mailboxes. The application authenticates using OAuth2 (single tenant), then use the received access-token to connect to imap or pop3 server and list the mailbox.

### Requirements ###
* JDK 17
* Registered application on Azure AD tenant (with a shared secret), see [Quickstart: Register an application with the Microsoft identity platform](https://learn.microsoft.com/en-us/azure/active-directory/develop/quickstart-register-app)
* Permissions POP.AccessAsApp and IMAP.AccessAsApp on the API "Office 365 Exchange Online"
* A working Exchange Online mailbox
* IMAP and POP3 enabled

### Configuration ###

The application.conf needs to be populated with information so the application can connect to the mailbox:

* ClientId (aka ApplicationId)
* ClientSecret
* Tenant domain name
* Mailbox UPN
* OAuth URL (preconfigured with https://login.microsoftonline.com)
* OAuth Scope (preconfigured with https://outlook.office365.com/.default)
* Server (preconfigured with outlook.office365.com)

Some optional configuration are available like SASL support, choice between SSL or TLS and debugging.

### Summary of Exchange Online Setup ###

With the mailbox created, accessible, the application registered with the proper permissions added and consented, a service principal needs to be created so the application has permissions on the mailbox.

* Create the service principal, it will be the identity the application will use to have permissions on the mailbox. Application ID can be found on the Overview page in "App Registrations", or in "Enterprise Applications" on Azure AD. The OBJECT_ID needs attention, because we have two different ids, one in "App Registrations" and another in "Enterprise Applications". ***The OBJECT_ID we need for New-ServicePrincipal cmdlet is the id from "Enterprise Applications", NOT "App Registrations".***.
    ~~~
    New-ServicePrincipal -AppId <APPLICATION_ID> -ServiceId <OBJECT_ID>
    ~~~

* View the created service principal
    ~~~
    Get-ServicePrincipal | fl
    ~~~

* Add permission to the mailbox for the created service principal
    ~~~
    Add-MailboxPermission -Identity "john.smith@contoso.com" -User <SERVICE_PRINCIPAL_ID> -AccessRights FullAccess
    ~~~

At this point, the application should should have permission to access the mailbox.

### Notes ###

* If you still receive authentication error on POP or IMAP, try with another mailbox, Microsoft blocks IMAP and POP3 for some hours if you try to connect too many times with authentication failure.
* For POP3 is important to note the need to use the "two line" format for authentication, the library should support this, or authentication will fail. Like "mail.pop3.auth.xoauth2.two.line.authentication.format" from Jakarta Mail. 
* This demo is using ***client_credentials*** grant type.
* More details on Exchange setup, XOAUTH2 and app registration can be found on the official documentation: [Authenticate an IMAP, POP or SMTP connection using OAuth](https://learn.microsoft.com/en-us/exchange/client-developer/legacy-protocols/how-to-authenticate-an-imap-pop-smtp-application-by-using-oauth)
* This application doesn't change anything in the mailbox, each folder is opened read-only and listed. See MailStore.java.

### Usage ###

* Clean
    ~~~
    ./gradlew clean
    ~~~

* Run
    ~~~
    ./gradlew run
    ~~~
  
* Create distribution (on build/install) 
    ~~~
    ./gradlew installDist
    ~~~

* Create distribution zip (on build/distributions)
    ~~~
    ./gradlew distZip
    ~~~

* Run from build/install
  ~~~
  ./demo-o365-imappop.bat
  ~~~
  or
  ~~~
  java -jar lib/demo-o365-imappop-0.1.jar
  ~~~

When the application is executed, if an application.conf doesn't exist, it will be created from an internal template. See [Configuration](#configuration).

### License ###
Copyright © 2023, [Picture Soluções em TI](https://www.picture.com.br)

This demo application is licensed under [GPLv3](https://www.gnu.org/licenses/gpl-3.0), see [LICENSE](LICENSE).