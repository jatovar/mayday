package com.spadigital.mayday.app.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.spadigital.mayday.app.MayDayApplication;
import com.spadigital.mayday.app.R;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.iqregister.AccountManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by jorge on 24/11/16.
 */
public class RegisterActivity extends AppCompatActivity {


    private MayDayApplication myApp;
    private XMPPConnection connection;
    private final static int MAX_LENGTH = 8;
    private static final String ALLOWED_CHARACTERS ="0123456789QWERTYUIOPASDFGHJKLZXCVBNM";
    private String username = "";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registernew);

        myApp = MayDayApplication.getInstance();
        Button button = (Button) findViewById(R.id.btn_cancel_register);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void click_new_account(View v){
       // Log.v(log_v, "even_login");
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
        /*ProgressDialog pd;
        pd = ProgressDialog.show(this, "Registrando cuenta",
                "Por favor espere...", true);
                */
        //Intent registerNew = new Intent(getApplicationContext(), GenerateActivity.class);
        //startActivity(registerNew);
    }

    private boolean createNewAccount(String email, String password) {

        Map<String, String> mapAttributes = new HashMap<>();
        mapAttributes.put("email", email);

        username = randomUser();

        /*
        try {
            AccountManager.getInstance(connection).createAccount(username, password, mapAttributes);

        } catch (SmackException.NoResponseException |
                XMPPException.XMPPErrorException |
                SmackException.NotConnectedException e) {
            e.printStackTrace();
            return false;
        }
        */
        return true;

    }

    private boolean isValidEmail(CharSequence target) {
        return target != null && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private String randomUser() {

        final Random random    = new Random();
        final StringBuilder sb = new StringBuilder(MAX_LENGTH - 2);
        sb.append("MD");
        for(int i = 0; i < MAX_LENGTH - 2; ++i)
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        return sb.toString();
    }

}
