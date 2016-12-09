package com.spadigital.mayday.app.Tasks;

import android.os.AsyncTask;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.data;

/**
 * Created by jorgetovar on 9/12/16.
 */

public class PostTask extends AsyncTask <String, String, String>{

    @Override
    protected String doInBackground(String... data) {

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost("http://189.206.27.33:9090/plugins/passservice/passservice");

        try {
            //add data
            List<NameValuePair> nameValuePairs = new ArrayList<>(1);
            nameValuePairs.add(new BasicNameValuePair("username", data[0]));
            nameValuePairs.add(new BasicNameValuePair("email", data[1]));

            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //execute http post
            HttpResponse response = httpclient.execute(httppost);
            Header header[]  =  response.getAllHeaders();


        } catch (ClientProtocolException e) {

        } catch (IOException e) {

        }
        return "";
    }
}
