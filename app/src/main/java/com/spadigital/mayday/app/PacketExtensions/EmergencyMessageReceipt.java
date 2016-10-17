package com.spadigital.mayday.app.PacketExtensions;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by jorge on 17/10/16.
 *  This class is an extension element for XML packet listener, if the message is not
 *  an emergency message, the default value is false
 */
public class EmergencyMessageReceipt implements ExtensionElement {

    public static final String NAMESPACE = "urn:xmpp:emergency";
    public static final String ELEMENT   = "emergency";
    private String isEmergencyMsg        = "false";

    public EmergencyMessageReceipt(String isEmergencyMsg) {
        this.isEmergencyMsg = isEmergencyMsg;
    }

    public EmergencyMessageReceipt(Object isEmergencyMsg) {
        this.isEmergencyMsg =  isEmergencyMsg.toString();
    }

    public String getIsEmergencyMsg(){
        return this.isEmergencyMsg;
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
        return "<emergency xmlns='" + NAMESPACE + "' boolean='" + this.isEmergencyMsg + "'/>";
    }

    public static class Provider extends EmbeddedExtensionProvider
    {
        @Override
        protected ExtensionElement createReturnExtension(String currentElement,
                                                         String currentNamespace,
                                                         Map attributeMap,
                                                         List content) {

            return new EmergencyMessageReceipt(attributeMap.get("boolean"));
        }
    }
}
