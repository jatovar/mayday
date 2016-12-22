package com.spadigital.mayday.app.Tasks;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialog;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by jorgetovar on 9/12/16.
 */

public class WebServiceTask extends AsyncTask <Void, Void, String>{

    private Context context;
    private List<NameValuePair> nameValuePairs = new ArrayList<>(2);


    public WebServiceTask(Context context, String data[]){

        this.context = context;

        nameValuePairs.add(new BasicNameValuePair("type", "status"));
        nameValuePairs.add(new BasicNameValuePair("username", data[0]));
        nameValuePairs.add(new BasicNameValuePair("email", data[1]));
    }

    @Override
    protected String doInBackground(Void... voids) {

        String responseString;
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost     = new HttpPost("http://189.206.27.33:9090/plugins/passservice/passservice");

        try {
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity     = response.getEntity();
            responseString        = EntityUtils.toString(entity, "UTF-8");

        } catch (IOException e) {
            responseString = e.getMessage();
        }


        return responseString;
    }

    @Override
    protected void onPostExecute(String responseString) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        if(responseString.contains("EMAIL ERROR")) {
            builder.setTitle("Error en el usuario y/o correo eléctronico");
            builder.setMessage("El usuario y/o correo electronico no concuerdan con nuestros registros");
            builder.setPositiveButton("OK", null);
            builder.setIcon(android.R.drawable.ic_dialog_alert);

        }else{
            if(responseString.contains("SQL ERROR")){
                builder.setTitle("Error en el servidor");
                builder.setMessage("Favor de contactar a soporte para recuperar su cuenta.");
                builder.setPositiveButton("OK", null);
                builder.setIcon(android.R.drawable.ic_dialog_alert);

            }else{
                if(responseString.contains("SUCCESS")) {
                    builder.setTitle("Email de confirmación");
                    builder.setMessage("La confirmación de reinicio de contraseña ha sido enviada a su correo");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ((AppCompatActivity) context).finish();
                                }
                            }
                    );
                    builder.setIcon(android.R.drawable.ic_dialog_info);
                }else{
                    builder.setTitle("Internal Exception");
                    builder.setMessage(responseString);
                    builder.setPositiveButton("OK", null);
                    builder.setIcon(android.R.drawable.ic_dialog_alert);
                }
            }
        }

        builder.show();
    }
}
