package com.spadigital.mayday.app;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.spadigital.mayday.app.Activities.ChatActivity;
import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.Enum.ChatMessageStatus;
import com.spadigital.mayday.app.Fragments.ConfigFragment;
import com.spadigital.mayday.app.Listeners.MyConnectionListener;
import com.spadigital.mayday.app.Listeners.MyMessageListener;
import com.spadigital.mayday.app.Listeners.MyReceiptReceivedListener;
import com.spadigital.mayday.app.Models.DataBaseHelper;
import com.spadigital.mayday.app.PacketExtensions.SelfDestructiveReceipt;
import com.spadigital.mayday.app.Tasks.AlarmReceiver;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;


/**
 * Created by jorge on 12/09/16.
 */
public class MayDayApplication extends Application {


    private String log_v = "MayDayApplication: ";
    private String log_e = "MyApplicationERROR: ";

    private static final String DOMAIN   = "jorge-latitude-e5440";
    private static final String HOST     = "192.168.168.67";
    private static final int PORT        = 5222;
    private static final String RESOURCE = "Android";

    /*
        private static final String DOMAIN   = "mayday";
        private static final String HOST     = "10.10.51.155";
        private static final int PORT        = 5222;
        private static final String RESOURCE = "Android";
    */

    private String mayDayID = "";
    private String password = "";
    private AbstractXMPPConnection connection;
    private Chat chat;
    private NotificationManager notificationMgr;
    private static MayDayApplication instance;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;


    @Override
    public void onCreate(){
        super.onCreate();

        instance        = this;
        notificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager    = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent   = new Intent(getApplicationContext(), AlarmReceiver.class);
        alarmIntent     = PendingIntent.getBroadcast(getApplicationContext(), 0, intent, 0);
    }

    public static MayDayApplication getInstance() {
        return instance;
    }

    public AbstractXMPPConnection getConnection(){
        return this.connection;
    }

    public void setCredentials(String mayDayId, String password){

        this.mayDayID = mayDayId;
        this.password = password;
    }

    public void createChat(String contactString){
        ChatManager chatManager = ChatManager.getInstanceFor(connection);
        chat        = chatManager.createChat(contactString);
    }
    public void sendMessage(ChatMessage message, boolean resending) {

        Log.v(log_v, "Send_message");

        try {
            //Message instance for setting its properties
            Message m = new Message();
            m.setBody(message.getMessage());

            //To ensure user has received the message
            DeliveryReceiptRequest.addTo(m);

            //Autodestructive extension
            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String milliseconds =
                    sharedPref.getString(ConfigFragment.PREF_KEY_DESTROY_RECEIPT, "0");
            SelfDestructiveReceipt selfDestructiveReceipt =
                    new SelfDestructiveReceipt(milliseconds);
            m.addExtension(selfDestructiveReceipt);
            //Message sent by smack
            chat.sendMessage(m);
            message.setStatus(ChatMessageStatus.SENT);

            // if connection manager detected there is no communication with server it
            // resend unsent messages labeled in catch of this exception
            // (not sure if its the right way)

            if(resending){
                DataBaseHelper db = new DataBaseHelper(this);
                db.updateSendingMessage(message.getId());
                db.close();
                if(ChatActivity.getInstance() != null)
                    ChatActivity.getInstance().updateSentIcon(message.getId());
            }

        }
        catch(SmackException.NotConnectedException e)
        {
            //Connection lost, the message is labeled with sending status
            message.setStatus(ChatMessageStatus.SENDING);
            Log.d(log_e, "Exception at sendMsg " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void startListening() {
        if (connection != null) {
            MyMessageListener listener = new MyMessageListener(this, notificationMgr);
            this.connection.addAsyncStanzaListener(listener, null);
        }
    }

    public void createConnection() {

        Log.v(log_v,"createConnection");

        //Connection configuration builder
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(mayDayID, password);
        configBuilder.setConnectTimeout(3000);
        configBuilder.setSendPresence(false);
        configBuilder.setServiceName(DOMAIN);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//
        configBuilder.setResource(RESOURCE);
        configBuilder.setDebuggerEnabled(true);//
        connection = new XMPPTCPConnection(configBuilder.build());

        //Connection Listener
        MyConnectionListener listener = new MyConnectionListener(this.getApplicationContext());
        connection.addConnectionListener(listener);

        //This is the event manager for receipt received messages
        MyReceiptReceivedListener receiptRecListener = new MyReceiptReceivedListener();
        DeliveryReceiptManager.getInstanceFor(connection).addReceiptReceivedListener(receiptRecListener);

        //Reconnection handling
        ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();
        ReconnectionManager.getInstanceFor(connection).setReconnectionPolicy(
                ReconnectionManager.ReconnectionPolicy.FIXED_DELAY);
        ReconnectionManager.getInstanceFor(connection).setFixedDelay(3);

        //Self destructive extension
        ProviderManager.addExtensionProvider(
                SelfDestructiveReceipt.ELEMENT,
                SelfDestructiveReceipt.NAMESPACE,
                new SelfDestructiveReceipt.Provider()
        );

    }


}
