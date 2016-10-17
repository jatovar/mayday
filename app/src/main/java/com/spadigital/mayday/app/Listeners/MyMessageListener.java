package com.spadigital.mayday.app.Listeners;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
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
import com.spadigital.mayday.app.Enum.ContactStatus;
import com.spadigital.mayday.app.Fragments.ConfigFragment;
import com.spadigital.mayday.app.Fragments.ContactsFragment;
import com.spadigital.mayday.app.Fragments.ConversationsFragment;
import com.spadigital.mayday.app.Models.DataBaseHelper;
import com.spadigital.mayday.app.PacketExtensions.EmergencyMessageReceipt;
import com.spadigital.mayday.app.PacketExtensions.SelfDestructiveReceipt;
import com.spadigital.mayday.app.R;
import com.spadigital.mayday.app.Tasks.AlarmReceiver;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smackx.bytestreams.ibb.packet.Data;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by jorge on 11/10/16.
 * TODO
 */
public class MyMessageListener implements StanzaListener {

    private final NotificationManager mNotifyMgr;
    private final Context context;
    private final AlarmManager alarmManager;
    private String log_v = "MyMessageListener: ";

    public MyMessageListener(Context context, NotificationManager mNotifyMgr, AlarmManager alarmManager){

        this.mNotifyMgr = mNotifyMgr;
        this.context    = context;
        this.alarmManager = alarmManager;
    }


    @Override
    public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

        try {
            Message message                   = (Message) packet;
            final ChatMessage incomingMessage = new ChatMessage();
            String body                       = message.getBody();

            if(body == null){
                //This is a control message like "composing, pause, etc"
            }
            else
            {
                //Split MayDayID@DOMAIN/RESOURCE into MayDayID(0) and DOMAIN/RESOURCE(1)
                String[] parts = message.getFrom().split("@");
                String from = parts[0];
                Log.d("DEBUGING: ", "FROM: " + from);
                Log.d("DEBUGING: ", "BODY: " + body);

                //set message properties
                incomingMessage.setContactMayDayId(from);
                incomingMessage.setMessage(body);
                incomingMessage.setDatetime(DateFormat.getDateTimeInstance().format(new Date()));
                incomingMessage.setStatus(ChatMessageStatus.UNREAD);
                incomingMessage.setDirection(ChatMessageDirection.INCOMING);

                //Get my custom message extensions
                getSelfDestructiveExtension(message, incomingMessage);
                getEmergencyMessageExtension(message, incomingMessage);

                //find contact in db for name TODO:OPTIMIZE THIS
                Contact contact = null;
                if(ContactsFragment.getInstance() != null)
                    contact = ContactsFragment.getInstance().findContactById(from);
                if(contact != null)
                    incomingMessage.setAuthor(contact.getName());
                else
                    incomingMessage.setAuthor("UNKNOWN");

                //a blocked contact will always exist in db
                DataBaseHelper db    = new DataBaseHelper(this.context);
                ContactStatus status = db.findContactStatus(from);
                db.close();

                if(status != ContactStatus.BLOCKED) {

                    Log.v(log_v, "insertMessageInDb");

                    db = new DataBaseHelper(context);
                    db.messageAdd(incomingMessage);
                    db.close();
                    setNotificationManager(incomingMessage);

                    //if there is a chat activity update it

                    if (ChatActivity.getInstance() != null
                            && from.equals(ChatActivity.getInstance().getCurrentConversationId())
                            && ChatActivity.getInstance().hasWindowFocus()) {

                        Log.v(log_v, "LOADING due to INCOMING message");

                        incomingMessage.setStatus(ChatMessageStatus.READ);
                        db = new DataBaseHelper(context);
                        db.updateReadingMessage(incomingMessage.getId());
                        db.close();

                        ChatActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ChatActivity.getInstance().displayMessage(incomingMessage);

                            }
                        });
                    }

                    //Add message to conversations fragment
                    if (ConversationsFragment.getInstance() != null) {
                        ConversationsFragment.getInstance().getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ConversationsFragment.getInstance().addIncomingMessage(incomingMessage);
                            }
                        });
                    }
                    //Emergency message wake up
                    if(incomingMessage.getIsEmergency()) {
                        PendingIntent alarmIntent;
                        Intent intent = new Intent(context, AlarmReceiver.class);
                        alarmIntent   = PendingIntent.getBroadcast(context, 0, intent, 0);
                        alarmManager.set(AlarmManager.RTC_WAKEUP,
                                System.currentTimeMillis() + 100,
                               alarmIntent);
                    }
                }
            }
        }
        catch(ClassCastException e) {
            Log.v(log_v, "Exception in startListening: "+ e.getMessage());
        }
    }

    private void getEmergencyMessageExtension(Message message, ChatMessage chatMessage) {

        ExtensionElement element = message.getExtension(EmergencyMessageReceipt.NAMESPACE);
        //if there is no extension set in the packet, such a spark client and whatsoever
        if (element != null) {
            String isEmergencyMessage = ((EmergencyMessageReceipt) element).getIsEmergencyMsg();
            if(isEmergencyMessage.equals("true")){
                chatMessage.setIsEmergency(true);
            }
        }
    }

    private void getSelfDestructiveExtension(Message message, ChatMessage chatMessage) {

        ExtensionElement element = message.getExtension(SelfDestructiveReceipt.NAMESPACE);

        //if there is no extension set in the packet, such a spark client and whatsoever
        if (element != null) {
            String milliseconds = ((SelfDestructiveReceipt) element).getMilliseconds();
            //if the package comes with self destructive time we give it priority
            if (!milliseconds.equals("0")) {
                chatMessage.setType(ChatMessageType.SELFDESTRUCTIVE);
                chatMessage.setExpireTime(milliseconds);
            }
            else {
                //extract our preferences
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
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
            //if the message is not self destructive at all (another clients that doesn't support
            //our extension)
            //extract our preferences
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
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
    }

    private void setNotificationManager(ChatMessage chatMessage) {
        //ChatActivity.getInstance().finish();
        //The intent to execute when the expanded status entry is clicked.
        Intent notificationIntent = new Intent(context, ChatActivity.class);
        notificationIntent.putExtra("contact_MayDayID", chatMessage.getContactMayDayID());
        notificationIntent.putExtra("contact_author", chatMessage.getAuthor());
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addNextIntent(notificationIntent);


        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent intent = PendingIntent.getActivity(context, 0,
                notificationIntent, 0);


        NotificationCompat.Builder mBuilder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.send)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(chatMessage.getAuthor() + ": " + chatMessage.getMessage())
                        .setContentIntent(intent)
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true);
        int mNotificationId = chatMessage.getContactMayDayID().hashCode();
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
