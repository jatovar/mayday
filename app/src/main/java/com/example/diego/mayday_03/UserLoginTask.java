package com.example.diego.mayday_03;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.sasl.SASLErrorException;

import java.io.IOException;

/**
 * Created by jorge on 3/10/16.
 * This class handles the asynchronous login with a progress bar, retrieves the error type
 * within the EditText errors.
 */
public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

    private MyApplication app;
    private Context context;
    private View progressView;


    /**
     * Initializes the instance variables and sets the credentials to the corresponding
     * user, it also creates a new connection with the corresponding username and password
     * @param mayDayId the user's maydayId
     * @param password the user's password
     * @param app the Application variables, where the XMPP connection is handled
     * @param context application context
     * @param progressView the progressView control in LoginActivity
     * @param etPassword the password EditText in LoginActivity
     */
    public UserLoginTask(String mayDayId, String password, MyApplication app, Context context,
                         View progressView, EditText etPassword) {

        this.context        = context;
        this.app            = app;
        this.progressView   = progressView;


        this.app.setCredentials(mayDayId, password);
        this.app.createConnection();
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

           app.connection.connect();

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
            Intent principal = new Intent(context, TabberActivity.class);
            principal.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(principal);

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
            app.connection.login();
            result = true;
        }
        catch (XMPPException | SmackException | IOException e){
            Log.v("USER TASK: ", "Exception at connectConnection" + e.getMessage());

        }
        return result;
    }
}

