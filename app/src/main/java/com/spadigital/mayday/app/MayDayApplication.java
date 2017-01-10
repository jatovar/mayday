package com.spadigital.mayday.app;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationManager;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.spadigital.mayday.app.Activities.ChatActivity;
import com.spadigital.mayday.app.Entities.ChatMessage;
import com.spadigital.mayday.app.Enum.ChatMessageStatus;
import com.spadigital.mayday.app.Fragments.ConfigFragment;
import com.spadigital.mayday.app.Listeners.MyConnectionListener;
import com.spadigital.mayday.app.Listeners.MyMessageListener;
import com.spadigital.mayday.app.Listeners.MyReceiptReceivedListener;
import com.spadigital.mayday.app.Helpers.DataBaseHelper;
import com.spadigital.mayday.app.PacketExtensions.CustomMessage;
import com.spadigital.mayday.app.PacketExtensions.EmergencyMessageReceipt;
import com.spadigital.mayday.app.PacketExtensions.SelfDestructiveReceipt;
import com.spadigital.mayday.app.PacketExtensions.TransferRequest;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.receipts.DeliveryReceiptManager;

import java.io.IOException;


/**
 * Created by jorge on 12/09/16
 * This is the the main logic where all listeners are initialized and the connection is created
 * with the port and host
 */
public class MayDayApplication extends Application {


    private String log_v = "MayDayApplication: ";
    private String log_e = "MyApplicationERROR: ";

    public static final String INTENT_DISPLAY_ERROR = "ERROR DE CONEXION";

    public static final String DOMAIN    = "mayday";
    private static final String HOST     = "189.206.27.33";
    private static final int PORT        = 5222;
    private static final String RESOURCE = "Android";


    private String mayDayID = "";
    private String password = "";
    private AbstractXMPPConnection connection;
    private Chat chat;
    private NotificationManager notificationMgr;
    private static MayDayApplication instance;
    private AlarmManager alarmManager;


    @Override
    public void onCreate(){
        super.onCreate();
        instance        = this;
        notificationMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        alarmManager    = (AlarmManager) getSystemService(ALARM_SERVICE);

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
        chat                    = chatManager.createChat(contactString);
    }

    public void sendTransferRequest(){
        //TODO
        Message message = new Message();

        try {

            //We need to create a new chat with the desired username
            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String transferUsername =
                    sharedPref.getString(ConfigFragment.PREF_KEY_TRANSFER_ACCOUNT, "0");
            createChat(transferUsername + "@" + DOMAIN);

            TransferRequest sendTransferRequestMessage =
                    new TransferRequest(TransferRequest.TransferStatus.REQUESTING);
            message.addExtension(sendTransferRequestMessage);
            chat.sendMessage(message);

        }catch (SmackException.NotConnectedException e) {
            //CONSTRAINT... USER MUST BE ONLINE.....
            Toast toast = Toast.makeText(this, "You don't have an active connection",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void sendResponseRequest(String resendUsername, TransferRequest.TransferStatus status){
        //TODO
        Message message = new Message();

        try {

            createChat(resendUsername + "@" + DOMAIN);
            TransferRequest sendTransferResponseMessage =
                    new TransferRequest(status);
            message.addExtension(sendTransferResponseMessage);
          //  message.setBody("request");
            chat.sendMessage(message);

        }catch (SmackException.NotConnectedException e) {
            //CONSTRAINT... USER MUST BE ONLINE.....
            Toast toast = Toast.makeText(this, "You don't have an active connection",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public void sendMessage(ChatMessage message, boolean resending) {

        Log.v(log_v, "Send_message");

        try {
            //Message instance for setting its properties
            Message m = new Message();
            m.setBody(message.getMessage());
            if(message.getRedirected()){
                m.setSubject(message.getSubject());
            }

            //To ensure user has received the message (Just in case, not used in this project)
            //DeliveryReceiptRequest.addTo(m);

            //Autodestructive extension
            SharedPreferences sharedPref =
                    PreferenceManager.getDefaultSharedPreferences(this);
            String milliseconds =
                    sharedPref.getString(ConfigFragment.PREF_KEY_DESTROY_RECEIPT, "0");
            SelfDestructiveReceipt selfDestructiveReceipt =
                    new SelfDestructiveReceipt(milliseconds);
            m.addExtension(selfDestructiveReceipt);

            //Emergency message extension
            String isEmergencyMessage =
                    String.valueOf(ChatActivity.getInstance().getIsEmergencyMessage());
            EmergencyMessageReceipt emergencyMessageReceipt =
                    new EmergencyMessageReceipt(isEmergencyMessage);
            m.addExtension(emergencyMessageReceipt);

            //Message sent by smack
            chat.sendMessage(m);
            message.setStatus(ChatMessageStatus.SENT);

            // if connection manager detected there is no communication with server it
            // resend unsent messages labeled in catch of this exception
            // (not sure if its the right way)

            if(resending){
                //Update the database sending messages to sent
                DataBaseHelper db = new DataBaseHelper(this);
                db.updateSendingMessage(message.getId());
                db.close();
                //We have to update to sent icon in the ChatActivity
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
            MyMessageListener listener = new MyMessageListener(this, notificationMgr, alarmManager);
            this.connection.addAsyncStanzaListener(listener, null);
        }
    }

    public void createAnonymousConnection(){
        XMPPTCPConnectionConfiguration.Builder configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setConnectTimeout(3000);
        configBuilder.setServiceName(DOMAIN);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);//
        configBuilder.setResource(RESOURCE);
        configBuilder.setDebuggerEnabled(true);//
        connection = new XMPPTCPConnection(configBuilder.build());

        try {
            connection.connect();

        } catch (SmackException | IOException | XMPPException e) {
            e.printStackTrace();
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

        //Set configBuilder to the actual connection
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

        //Self destructive message extension provider
        ProviderManager.addExtensionProvider(
                SelfDestructiveReceipt.ELEMENT,
                SelfDestructiveReceipt.NAMESPACE,
                new SelfDestructiveReceipt.Provider()
        );

        //Emergency message extension provider
        ProviderManager.addExtensionProvider(
                EmergencyMessageReceipt.ELEMENT,
                EmergencyMessageReceipt.NAMESPACE,
                new EmergencyMessageReceipt.Provider()
        );

        //Transfer request extension provider
        ProviderManager.addExtensionProvider(
                TransferRequest.ELEMENT,
                TransferRequest.NAMESPACE,
                new TransferRequest.Provider()
        );

        //Transfer request extension provider
        ProviderManager.addExtensionProvider(
                CustomMessage.ELEMENT,
                CustomMessage.NAMESPACE,
                new CustomMessage.Provider()
        );



    }


    public void sendCustomMessage(String to, String message) {

        Message m = new Message();

        try {

            createChat(to);
            CustomMessage customMessage = new CustomMessage(message);
            m.addExtension(customMessage);
            chat.sendMessage(m);

        }catch (SmackException.NotConnectedException e) {
            //CONSTRAINT... USER MUST BE ONLINE.....
            Toast toast = Toast.makeText(this, "You don't have an active connection",
                    Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) this.getApplicationContext().getSystemService(AppCompatActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo     = connMgr.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isConnected();
    }


}
