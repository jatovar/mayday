package com.example.diego.mayday_03;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.provider.EmbeddedExtensionProvider;
import org.jivesoftware.smack.util.Objects;

import java.util.List;
import java.util.Map;

/**
 * Created by jorge on 4/10/16.
 * This class is an extension element for XML packet listener, if the message is not
 * destructive the default value of milliseconds is 0
 */
public class SelfDestructiveReceipt implements ExtensionElement {

    public static final String NAMESPACE = "urn:xmpp:destructive";
    public static final String ELEMENT = "destructive";
    private String milliseconds = "0";

    public SelfDestructiveReceipt(String milliseconds)
    {
        this.milliseconds = milliseconds;
    }
    public SelfDestructiveReceipt(Object milliseconds)
    {
        this.milliseconds = milliseconds.toString();
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
        return "<destructive xmlns='" + NAMESPACE + "' milliseconds='" + this.milliseconds + "'/>";
    }
    public String getMilliseconds(){
        return this.milliseconds;
    }

    public static class Provider extends EmbeddedExtensionProvider
    {

        @Override
        protected ExtensionElement createReturnExtension(String currentElement, String currentNamespace, Map attributeMap, List content) {
            return new SelfDestructiveReceipt(attributeMap.get("milliseconds"));
        }
    }
}

