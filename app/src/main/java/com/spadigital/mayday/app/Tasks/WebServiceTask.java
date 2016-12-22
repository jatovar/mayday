package com.spadigital.mayday.app.Tasks;

import android.content.Context;
import android.os.AsyncTask;

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

    private String error;
    private String data[];
    private Context context;

    public WebServiceTask(Context context, String data[]){
        this.context = context;
        this.data = data;
    }

    @Override
    protected String doInBackground(Void... voids) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://189.206.27.33:9090/plugins/passservice/passservice");

        try {
            //add data
            List<NameValuePair> nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("type", "status"));
            nameValuePairs.add(new BasicNameValuePair("username", data[0]));
            nameValuePairs.add(new BasicNameValuePair("email", data[1]));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //execute http post
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity, "UTF-8");
            if(responseString.contains("EMAIL ERROR")) {

            }
            //TODO: catch response and print it ....
            Header header[]  =  response.getAllHeaders();
            StatusLine st = response.getStatusLine();
            for (Header header1 : header) {
                System.out.print(header1);
            }


        } catch (IOException e) {
            error = e.getMessage();
        }
        return error;
    }
}
