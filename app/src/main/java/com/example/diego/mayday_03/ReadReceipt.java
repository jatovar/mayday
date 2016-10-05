package com.example.diego.mayday_03;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;

import java.util.List;
import java.util.Map;

/**
 * Created by jorge on 5/10/16.
 */
public class ReadReceipt implements ExtensionElement {
    public static final String NAMESPACE = "urn:xmpp:read";
    public static final String ELEMENT = "read";
    private String id; /// original ID of the delivered message


    public ReadReceipt(Object id)
    {
        this.id = id.toString();
    }

    /**
     * Returns the root element XML namespace.
     *
     * @return the namespace.
     */
    @Override
    public String getNamespace() {
        return NAMESPACE;
    }

    /**
     * Returns the root element name.
     *
     * @return the element name.
     */
    @Override
    public String getElementName() {
        return ELEMENT;
    }

    /**
     * Returns the XML representation of this Element.
     *
     * @return the stanza(/packet) extension as XML.
     */
    @Override
    public CharSequence toXML() {
        return "<read xmlns='" + NAMESPACE + "' id='" + id + "'/>";
    }

    public static class Provider extends EmbeddedExtensionProvider
    {
        @Override
        protected ExtensionElement createReturnExtension(String currentElement, String currentNamespace, Map attributeMap, List content) {
            return new ReadReceipt(attributeMap.get("id"));
        }
    }

}
