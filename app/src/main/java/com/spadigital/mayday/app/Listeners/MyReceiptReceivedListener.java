package com.spadigital.mayday.app.Listeners;

import android.util.Log;

import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

/**
 * Created by jorge on 14/10/16.
 */
public class MyReceiptReceivedListener implements ReceiptReceivedListener {
    @Override
    public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
        Log.d("PACKET", "onReceiptReceived: from: " + fromJid + " to: " + toJid
                + " deliveryReceiptId: " + receiptId + " stanza: " + receipt);
    }
}
