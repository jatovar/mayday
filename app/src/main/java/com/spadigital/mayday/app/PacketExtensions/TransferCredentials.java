package com.spadigital.mayday.app.PacketExtensions;

import org.jivesoftware.smack.packet.PacketExtension;

/**
 * Created by jorge on 25/10/16.
 * TODO: LUL
 */
public class TransferCredentials implements PacketExtension {

    public static final String NAMESPACE = "urn:xmpp:credentials";
    public static final String ELEMENT   = "credentials";

    private String password;

    public TransferCredentials(String password){
        this.password = password;
    }

    public TransferCredentials(Object password) {
        this.password = password.toString();
    }

    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    @Override
    public String getElementName() {
        return ELEMENT;
    }

    @Override
    public CharSequence toXML() {
        return "<credentials xmlns='" + NAMESPACE + "' password='" + this.password + "'/>";
    }
}
