package com.spadigital.mayday.app.Tasks;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by jorge on 21/10/16.
 */
public class TransferRequest implements ExtensionElement {

    public static final String NAMESPACE = "urn:xmpp:transfer";
    public static final String ELEMENT   = "transfer";
    private String transferringUser = "";

    public TransferRequest(String transferringUser){
        this.transferringUser = transferringUser;
    }
    public TransferRequest(Object transferringUser) {
        this.transferringUser = transferringUser.toString();
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
        return "<destructive xmlns='" + NAMESPACE + "' transferringUser='" + this.transferringUser + "'/>";
    }

    public String getTransferringUser(){
        return this.transferringUser;
    }

    public static class Provider extends EmbeddedExtensionProvider{

        @Override
        protected ExtensionElement createReturnExtension(String currentElement,
                                                         String currentNamespace,
                                                         Map attributeMap,
                                                         List content) {

            return new TransferRequest(attributeMap.get("transferringUser"));
        }
    }
}
