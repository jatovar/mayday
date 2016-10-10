package com.spadigital.mayday.app;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.support.v7.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import com.spadigital.mayday.app.Activities.ChatActivity;
import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.Entities.Contact;
import com.spadigital.mayday.app.Enum.ChatMessageDirection;
import com.spadigital.mayday.app.Enum.ChatMessageStatus;
import com.spadigital.mayday.app.Enum.ChatMessageType;
import com.spadigital.mayday.app.Fragments.ConfigFragment;
import com.spadigital.mayday.app.Fragments.ContactsFragment;
import com.spadigital.mayday.app.Fragments.ConversationsFragment;
import com.spadigital.mayday.app.Models.DataBaseHelper;
import com.spadigital.mayday.app.Listeners.MyConnectionListener;
import com.spadigital.mayday.app.PacketExtensions.SelfDestructiveReceipt;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.util.ArrayBlockingQueueWithShutdown;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;
import org.jivesoftware.smackx.receipts.DeliveryReceiptRequest;
import org.jivesoftware.smackx.receipts.ReceiptReceivedListener;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by jorge on 12/09/16.
 */
public class MayDayApplication extends Application implements ReceiptReceivedListener{


    private String log_v = "MayDayApplication: ";
    private String log_e = "MyApplicationERROR: ";

    private static final String DOMAIN   = "jorge-latitude-e5440";
    private static final String HOST     = "192.168.168.67";
    private static final int PORT        = 5222;
    private static final String RESOURCE = "Android";



  /*  private static final String DOMAIN   = "mayday";
    private static final String HOST     = "10.10.51.155";
    private static final int PORT        = 5222;
    private static final String RESOURCE = "Android";
    */
    private String mayDayID = "";
    private String password = "";
    private AbstractXMPPConnection connection;
    private Chat chat;
    private NotificationManager mNotifyMgr;
    private static MayDayApplication instance;


    @Override
    public void onCreate(){
        super.onCreate();
        instance   = this;
        mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
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
    public void sendMessage(ChatMessage message) {

        Log.v(log_v, "Send_message");

        try {

                Message m = new Message();
                m.setBody(message.getMessage());

                DeliveryReceiptRequest.addTo(m);

                SharedPreferences sharedPref =
                        PreferenceManager.getDefaultSharedPreferences(this);

                String milliseconds =
                        sharedPref.getString(ConfigFragment.PREF_KEY_DESTROY_RECEIPT, "0");

                SelfDestructiveReceipt selfDestructiveReceipt =
                        new SelfDestructiveReceipt(milliseconds);

                m.addExtension(selfDestructiveReceipt);
                chat.sendMessage(m);
                message.setStatus(ChatMessageStatus.SENT);



        }
        catch(SmackException.NotConnectedException e)
        {
            message.setStatus(ChatMessageStatus.SENDING);
            Log.d(log_e, "Exception at sendMsg " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void startListening() {
        if (connection != null) {
            this.connection.addAsyncStanzaListener(new StanzaListener() {
                @Override
                public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

                        try {

                            Message message = (Message) packet;
                            final ChatMessage chatMessage = new ChatMessage();
                            String body = message.getBody();
                            if(body == null){
                                //This is a control message like "composing, pause, etc"
                            }
                            else {
                                //Split MayDayID@DOMAIN/RESOURCE into MayDayID(0) and DOMAIN/RESOURCE(1)
                                String[] parts = message.getFrom().split("@");
                                String from = parts[0];
                                Log.d("DEBUGING: ", "FROM: " + from);
                                Log.d("DEBUGING: ", "BODY: " + body);
                                //TODO: -Extract TYPE from incoming message
                                //TODO: -Get the author from db
                                chatMessage.setContactMayDayId(from);
                                chatMessage.setMessage(body);
                                chatMessage.setDatetime(DateFormat.getDateTimeInstance()
                                        .format(new Date()));
                                getSelfDestructiveExtension(message, chatMessage);
                                chatMessage.setStatus(ChatMessageStatus.UNREAD);
                                chatMessage.setDirection(ChatMessageDirection.INCOMING);
                                Contact contact = null;
                                if(ContactsFragment.getInstance() != null)
                                    contact = ContactsFragment.getInstance().findContactById(from);
                                if(contact != null)
                                    chatMessage.setAuthor(contact.getName());
                                else
                                    chatMessage.setAuthor("UNKNOWN");
                                insertMessageInDb(chatMessage);
                                setNotificationManager(message, chatMessage);

                                if(ChatActivity.getInstance() != null
                                        && from.equals(ChatActivity.getInstance().getCurrentConversationId())
                                        && ChatActivity.getInstance().hasWindowFocus())
                                {
                                    Log.v(log_v, "LOADING due to INCOMING message");
                                    chatMessage.setStatus(ChatMessageStatus.READ);
                                    ChatActivity.getInstance().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ChatActivity.getInstance().displayMessage(chatMessage);

                                        }
                                    });
                                }
                                if(ContactsFragment.getInstance() != null)
                                    ConversationsFragment.getInstance().getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ConversationsFragment.getInstance().addIncomingMessage(chatMessage);
                                        }
                                    });

                            }

                        }
                        catch(ClassCastException e) {

                            Log.v(log_v, "Exception in startListening: "+ e.getMessage());
                        }
                }
            }, null);


        }
    }

    private void setNotificationManager(Message message, ChatMessage chatMessage) {
        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.send)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(chatMessage.getAuthor()+": "+chatMessage.getMessage())
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true);
        int mNotificationId = message.getStanzaId().hashCode();

        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }

    private void getSelfDestructiveExtension(Message message, ChatMessage chatMessage) {

        ExtensionElement element = message.getExtension(SelfDestructiveReceipt.NAMESPACE);

        //if there is no extension set in the packet, such a spark client and whatsoever
        if (element != null) {

            String milliseconds = ((SelfDestructiveReceipt) element).getMilliseconds();

            //if the package comes with self destructive time we give it priority
            if (!milliseconds.equals("0"))
            {
                chatMessage.setType(ChatMessageType.SELFDESTRUCTIVE);
                chatMessage.setExpireTime(milliseconds);

            }
            else
            {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                String millisecondsMine =
                        sharedPref.getString(ConfigFragment.PREF_KEY_DESTROY_MINE, "0");

                //if there is a actual configuration, its going to destroy
                if(!millisecondsMine.equals("0")) {
                    chatMessage.setType(ChatMessageType.SELFDESTRUCTIVE);
                    chatMessage.setExpireTime(millisecondsMine);
                }else{
                    //no autodestruction was configured, the message is normal
                    chatMessage.setType(ChatMessageType.NORMAL);
                }



            }


        }else{
            chatMessage.setType(ChatMessageType.NORMAL);
        }
    }

    private void insertMessageInDb(ChatMessage chatMessage){

        Log.v(log_v, "insertMessageInDb");

        DataBaseHelper db = new DataBaseHelper(this);
        db.getWritableDatabase();
        db.messageAdd(chatMessage);
        db.close();
    }

    public void createConnection() {

        Log.v(log_v,"createConnection");

        XMPPTCPConnectionConfiguration.Builder configBuilder
                = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(mayDayID, password);
        configBuilder.setSendPresence(false);
        configBuilder.setServiceName(DOMAIN);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        //TODO: CHANGE to enable
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setResource(RESOURCE);
        //TODO: CHANGE to false
        configBuilder.setDebuggerEnabled(true);
        connection = new XMPPTCPConnection(configBuilder.build());
        MyConnectionListener listener = new MyConnectionListener(this.getApplicationContext());
        connection.addConnectionListener(listener);
        //This is the event manager for receipt received messages
        DeliveryReceiptManager.getInstanceFor(connection).addReceiptReceivedListener(this);

        ProviderManager.addExtensionProvider(
                SelfDestructiveReceipt.ELEMENT,
                SelfDestructiveReceipt.NAMESPACE,
                new SelfDestructiveReceipt.Provider()
        );



    }



    @Override
    public void onReceiptReceived(String fromJid, String toJid, String receiptId, Stanza receipt) {
        Log.d("PACKET", "onReceiptReceived: from: " + fromJid + " to: " + toJid
                + " deliveryReceiptId: " + receiptId + " stanza: " + receipt);
    }
}
