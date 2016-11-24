package com.spadigital.mayday.app;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.android.AndroidSmackInitializer;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by jorge on 22/11/16.
 */
public class AccountRegistrationTest {

    @Test
    public void create(){
        final String DOMAIN   = "mayday";
        final String HOST     = "189.206.27.33";
        final int PORT        = 5222;
        final String RESOURCE = "Android";


        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();

        configBuilder.setServiceName(DOMAIN);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//
        configBuilder.setResource(RESOURCE);

        AbstractXMPPConnection connection = new XMPPTCPConnection(configBuilder.build());

        try {
            new AndroidSmackInitializer().initialize();
            connection.connect();

           // connection.connect();

            Assert.assertTrue(AccountManager.getInstance(connection).supportsAccountCreation());

        } catch (SmackException.NoResponseException | XMPPException.XMPPErrorException | SmackException.NotConnectedException e) {
            e.printStackTrace();
        } catch (XMPPException e) {
            e.printStackTrace();
        } catch (SmackException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
