package com.spadigital.mayday.app.PacketExtensions;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by jorge on 4/11/16.
 */
public class CustomMessage implements ExtensionElement {

    public static final String NAMESPACE = "urn:xmpp:custom";
    public static final String ELEMENT   = "custom";
    private String message;

    public CustomMessage(String message){
        this.message = message;
    }

    public CustomMessage(Object message) {
        this.message = message.toString();
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
        return "<custom xmlns='" + NAMESPACE + "' message='" + message + "'/>";
    }

    public String getMessage(){
        return this.message;
    }

    public static class Provider extends EmbeddedExtensionProvider {

        @Override
        protected ExtensionElement createReturnExtension(String currentElement,
                                                         String currentNamespace,
                                                         Map attributeMap,
                                                         List content) {

            return new CustomMessage(attributeMap.get("message"));
        }
    }
}
