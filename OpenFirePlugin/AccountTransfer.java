package org.jivesoftware.openfire.plugin;

import java.io.File;
import java.util.List;

import org.jivesoftware.openfire.MessageRouter;
import org.jivesoftware.openfire.PresenceManager;
import org.jivesoftware.openfire.XMPPServer;
import org.jivesoftware.openfire.container.Plugin;
import org.jivesoftware.openfire.container.PluginManager;
import org.jivesoftware.openfire.interceptor.InterceptorManager;
import org.jivesoftware.openfire.interceptor.PacketInterceptor;
import org.jivesoftware.openfire.interceptor.PacketRejectedException;
import org.jivesoftware.openfire.net.StanzaHandler;
import org.jivesoftware.openfire.session.Session;
import org.jivesoftware.openfire.user.User;
import org.jivesoftware.openfire.user.UserManager;
import org.jivesoftware.openfire.vcard.VCardManager;
import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jivesoftware.openfire.user.UserNotFoundException;
import org.xmpp.packet.Message;
import org.xmpp.packet.Packet;
import org.xmpp.packet.PacketExtension;


public class AccountTransfer implements Plugin, PacketInterceptor {
	
	private static final Logger Log = LoggerFactory.getLogger(AccountTransfer.class);


	private InterceptorManager interceptorManager;
	private VCardManager vCardManager;
	private MessageRouter messageRouter;
	private UserManager userManager;



	public AccountTransfer() {
		interceptorManager = InterceptorManager.getInstance();
		vCardManager = VCardManager.getInstance();
		messageRouter = XMPPServer.getInstance().getMessageRouter();
		userManager = XMPPServer.getInstance().getUserManager();
	}

	@Override
	public void initializePlugin(PluginManager manager, File pluginDirectory) {
		interceptorManager.addInterceptor(this);
	}

	@Override
	public void destroyPlugin() {
		interceptorManager.removeInterceptor(this);
	}

	@Override
	public void interceptPacket(Packet packet, Session session, boolean incoming, boolean processed)
			throws PacketRejectedException {
		
		Log.debug("INTERCEPT PACKET");

		// This variable will store the VCard value (user name) if there is a
		// redirection
		
		String redirectTo = null;
		

		if ((!processed) && (!incoming) && (packet instanceof Message) && (packet.getTo() != null)) {

			Message msg = (Message) packet;

			if (msg.getType() == Message.Type.chat) {
				
				Log.debug("BEFORE TRY Message from:" + packet.getFrom().getNode());
				Log.debug("BEFORE TRY Message to:" + packet.getTo().getNode());
				
				try {
					// The current sender
					User userTo = userManager.getUser(packet.getTo().getNode());
					User userFrom = userManager.getUser(packet.getFrom().getNode());
					boolean found = false;


					if (msg.getBody() != null) {

						// Read the user to redirection name VCard (if there is
						// any)
						redirectTo = vCardManager.getVCardProperty(userTo.getUsername(), "redirectTo");
						Log.debug("Message from:" + packet.getFrom().getNode());
						Log.debug("Message to:" + packet.getTo().getNode());
						Log.debug("User To vCard (redirect to):" + redirectTo);
						Log.debug("UserTo username = " + userTo.getUsername());
						

						if (redirectTo == null || redirectTo.length() == 0 || redirectTo.equals("none")) {
							

							
							
						} else {
							// notification to sender that his messages will be
							// redirected
							Log.debug("Entering to msg router...");
							
							// Message is sent as normal
							List<String> users = (List<String>) userManager.getUsernames();
							for(String user : users){
								String vCardProperty = vCardManager.getVCardProperty(user, "redirectTo");
								if (vCardProperty!= null && vCardProperty.equals(userFrom.getUsername())){
									Log.debug("vCard propertly equals (userFrom.getUsername)");
									Log.debug("vCardpropertly = ",  vCardProperty);
									Log.debug("userFrom = ", userFrom);
								
									found = true;
									User user_disabled = userManager.getUser(user);
									Log.debug("user_disabled =", user_disabled.getUsername());
									
									//TODO: CHANGE SUBJECT
									Message m = 
											createRedirectedMessage(
											//hard coded
											//to
												"jorge_spa" + "@" + packet.getTo().getDomain(),
											//from
												user_disabled.getUsername() + "@" + packet.getFrom().getDomain(),
											//message
												(Message) packet
											);
									messageRouter.route(m);
									break;
								}
							}
							if(!found)
							{
								messageRouter.route(createNotificationToSender(
										packet.getFrom().getNode() + "@" + packet.getFrom().getDomain(),
										packet.getTo().getNode() + "@" + packet.getTo().getDomain(), redirectTo));
								
								// redirection message with new user
								messageRouter.route(createRedirectedMessage(
										redirectTo + "@" + packet.getFrom().getDomain(),
										packet.getTo().getNode() + "@" + packet.getTo().getDomain(),
										(Message) packet));
							}
							
						


						}

					}

				} catch (UserNotFoundException e) {
					e.printStackTrace();

				}
			}

		}

	}

	
	private Message createNotificationToSender(String to, String from, String redirectTo) {
		Message message = new Message();
		message.setTo(to);
		message.setFrom(from);
		message.setSubject("Estoy fuera");
		message.setBody("Estoy fuera todos mis mensajes se mandaran a (" + redirectTo + "). Gracias.");
		return message;
	}
	

	private Message createRedirectedMessage(String to, String from, Message original) {
		Message message = new Message();
		message.setTo(to);
		message.setFrom(from);
		message.setSubject(original.getSubject());
		message.setBody(original.getBody());
		return message;

	}
}

