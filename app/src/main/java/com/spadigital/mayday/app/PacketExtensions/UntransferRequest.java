package com.spadigital.mayday.app.PacketExtensions;

import org.jivesoftware.smack.packet.ExtensionElement;

/**
 * Created by jorge on 4/11/16.
 */
public class UntransferRequest implements ExtensionElement {

    public static final String NAMESPACE = "urn:xmpp:untransfer";
    public static final String ELEMENT   = "untransfer";
    private boolean untransfering = false;

    public UntransferRequest(boolean untransfering){
        this.untransfering = untransfering;
    }

    public UntransferRequest(Object untransfering) {
        this.untransfering = (Boolean)untransfering;
    }


    @Override
    public String getNamespace() {
        return null;
    }

    @Override
    public String getElementName() {
        return null;
    }

    @Override
    public CharSequence toXML() {
        return null;
    }
}
