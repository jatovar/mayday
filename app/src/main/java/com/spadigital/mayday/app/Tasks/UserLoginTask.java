package com.spadigital.mayday.app.Tasks;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.spadigital.mayday.app.Activities.RetrieveTransferActivity;
import com.spadigital.mayday.app.Activities.TaberActivity;
import com.spadigital.mayday.app.MayDayApplication;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.vcardtemp.VCardManager;
import org.jivesoftware.smackx.vcardtemp.packet.VCard;

import java.io.IOException;

/**
 * Created by jorge on 3/10/16.
 * This class handles the asynchronous login with a circular_progress_bar bar, retrieves the error type
 * within the EditText errors.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private Context context;
    private View progressView;


    /**
     * Initializes the instance variables and sets the credentials to the corresponding
     * user, it also creates a new connection with the corresponding username and password
     * @param mayDayId the user's maydayId
     * @param password the user's password
     * @param context application context
     * @param progressView the progressView control in LoginActivity
     */
    public UserLoginTask(String mayDayId, String password, Context context, View progressView) {
        this.context        = context;
        this.progressView   = progressView;
        MayDayApplication.getInstance().setCredentials(mayDayId, password);
        MayDayApplication.getInstance().createConnection();
        MayDayApplication.getInstance().startListening();
    }

    /**
     * This method tries to connect to Openfire server within the AsyncTask
     * @param arg0 void param
     * @return true if connected
     * @see AsyncTask
     */
    @Override
    protected Boolean doInBackground(Void... arg0) {
        Boolean result = false;
        try {

           MayDayApplication.getInstance().getConnection().connect();

            if(login())
                result = true;

        }
        catch (XMPPException | SmackException | IOException e){

            Log.v("USER TASK: ", "Exception at connectConnection " + e.getMessage());
        }
        return result;
    }

    /**
     *
     */
    @Override
    protected void onPreExecute(){
        progressView.setVisibility(View.VISIBLE);
        progressView.requestFocus();
    }



    @Override
    protected void onPostExecute(Boolean success){
        progressView.setVisibility(View.GONE);

        if (success) {

            Presence presence = new Presence(Presence.Type.available);

            try {
                //noinspection deprecation
                MayDayApplication.getInstance().getConnection().sendPacket(presence);
                VCard vCard = VCardManager.getInstanceFor(
                        MayDayApplication.getInstance().getConnection()).loadVCard();
                if (vCard != null &&  vCard.getField("redirectTo") != null && vCard.getField("redirectTo").length()>0) {
                    Intent retrieve = new Intent(context, RetrieveTransferActivity.class);
                    retrieve.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(retrieve);
                    //Toast toast = Toast.makeText(context, "Ya tienes tu cuenta transferida", Toast.LENGTH_LONG);
                    //toast.show();
                }else{
                    Intent principal = new Intent(context, TaberActivity.class);
                    principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(principal);
                }


                //VCard vCard = new VCard();
               // vCard.setField("redirectTo","redirect_username");

                //try {
                //    VCardManager.getInstanceFor(MayDayApplication.getInstance().getConnection()).saveVCard(vCard);
                //}catch (SmackException.NoResponseException |
                 //       XMPPException.XMPPErrorException   |
                 //       SmackException.NotConnectedException e) {
                 //   e.printStackTrace();
                //}

            } catch (SmackException.NotConnectedException e) {
                e.printStackTrace();
            } catch (XMPPException.XMPPErrorException e) {
                e.printStackTrace();
            } catch (SmackException.NoResponseException e) {
                e.printStackTrace();
            }


        } else {
            Toast toast = Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_LONG);
            toast.show();
            progressView.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onProgressUpdate(Void ...arg0){
        progressView.animate();
    }

    private Boolean login(){
        Boolean result = false;

        try {
            MayDayApplication.getInstance().getConnection().login();
            result = true;
        }
        catch (XMPPException | SmackException | IOException e){
            Log.v("USER TASK: ", "Exception at connectConnection" + e.getMessage());

        }
        return result;
    }
}

