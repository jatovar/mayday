package com.spadigital.mayday.app.PacketExtensions;

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
    public enum TransferStatus { REQUESTING, ACCEPTING, REJECTING }

    private TransferStatus status;


    public TransferRequest(TransferStatus status){
        this.status = status;
    }

    public TransferRequest(Object status) {
        this.status = TransferStatus.valueOf(status.toString());
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
        return "<transfer xmlns='" + NAMESPACE + "' status='" + String.valueOf(this.status) + "'/>";
    }

    public TransferStatus getStatus(){
        return (this.status);
    }

    public static class Provider extends EmbeddedExtensionProvider{

        @Override
        protected ExtensionElement createReturnExtension(String currentElement,
                                                         String currentNamespace,
                                                         Map attributeMap,
                                                         List content) {

            return new TransferRequest(attributeMap.get("status"));
        }
    }
}
