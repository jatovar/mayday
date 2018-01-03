package com.spadigital.mayday.app.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.spadigital.mayday.app.Helpers.AlertsHelper;
import com.spadigital.mayday.app.MayDayApplication;
import com.spadigital.mayday.app.R;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

/**
 * Created by jorge on 24/11/16.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{


    private MayDayApplication myApp;
    private XMPPConnection connection;
    private final static int MAX_LENGTH = 8;
    private static final String ALLOWED_CHARACTERS ="QWERTYUIOPASDFGHJKLZXCVBNM";
    private static final String ALLOWED_NUMBERS = "0123456789";
    private String username = "";


    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registernew);

        myApp = MayDayApplication.getInstance();

        Button buttonCancel = (Button) findViewById(R.id.btn_cancel_register);
        buttonCancel.setOnClickListener(this);

        Button buttonRegister = (Button) findViewById(R.id.btn_register_new);
        buttonRegister.setOnClickListener(this);

    }

    public void clickNewAccount(){
       // Log.mVibrator(log_v, "even_login");
        EditText newEmail = (EditText) findViewById(R.id.et_email);
        EditText newPassword = (EditText) findViewById(R.id.et_newpassword);
        final String email     = newEmail.getText().toString();
        final String password     = newPassword.getText().toString();

        if(!isValidEmail(email))
            newEmail.setError("escriba un correo eléctronico válido");
        else if (TextUtils.isEmpty(password)) {
            newPassword.setError("escriba su contraseña");
            newPassword.requestFocus();
            //TODO: MATCH CONFIRMATION PASSWORD
        }else {
            final ProgressDialog pd = ProgressDialog.show(this, "Registrando cuenta",
                    "Por favor espere...", true);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {

                    myApp.createAnonymousConnection();
                    connection = myApp.getConnection();
                    try {
                        if(AccountManager.getInstance(connection).supportsAccountCreation()){

                            if(createNewAccount(email, password)) {
                                pd.dismiss();

                                Intent registerNew = new Intent(getApplicationContext(), GenerateActivity.class);
                                registerNew.putExtra("newUserName", username);
                                registerNew.putExtra("newPassword", password);
                                startActivity(registerNew);
                            }
                            else{

                                //TODO:HANDLE THE ERROR
                            }
                        }
                    } catch (SmackException.NoResponseException |
                            XMPPException.XMPPErrorException |
                            SmackException.NotConnectedException e) {
                        e.printStackTrace();
                    }
                }
            });
            t.start();

        }

    }

    private boolean createNewAccount(String email, String password) {

        Map<String, String> mapAttributes = new HashMap<>();
        mapAttributes.put("email", email);

        username = randomUser();


        try {
            AccountManager accountManager =  AccountManager.getInstance(connection);
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(username, password, mapAttributes);
        } catch (SmackException.NoResponseException |
                XMPPException.XMPPErrorException |
                SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    private boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private String randomUser() {

        final Random random    = new Random();
        final StringBuilder sb = new StringBuilder(MAX_LENGTH);
        sb.append("MD");
        for(int i = 0; i < 3; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));

        for(int i = 0; i < 3; ++i)
            sb.append(ALLOWED_NUMBERS.charAt(random.nextInt(ALLOWED_NUMBERS.length())));
        return sb.toString();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_register_new:
                if(myApp.isConnected())
                    clickNewAccount();
                else{
                    AlertsHelper.register(this);
                    AlertsHelper.displayError(this, "No tienes una conexión activa a internet");
                }
                break;
            case R.id.btn_cancel_register:
                finish();
                break;

        }
    }
}
