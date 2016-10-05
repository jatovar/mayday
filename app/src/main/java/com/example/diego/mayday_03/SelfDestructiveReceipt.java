package com.example.diego.mayday_03;

import org.jivesoftware.smack.packet.ExtensionElement;

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
        return "<destructive xmlns='" + NAMESPACE + "' ms='" + this.milliseconds + "'/>";
    }
    public String getMilliseconds(){
        return this.milliseconds;
    }
}
