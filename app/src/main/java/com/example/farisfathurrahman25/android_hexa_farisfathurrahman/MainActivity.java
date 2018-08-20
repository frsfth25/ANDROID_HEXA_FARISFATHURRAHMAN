package com.example.farisfathurrahman25.android_hexa_farisfathurrahman;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements TaskDelegate{

    EditText editMail;
    EditText editPassword;
    Button buttonLogin;

    private ProgressDialog pDialog;

    String token;
    String photo;
    String fullname;
    String username;
    String email;
    String address;

    String postUrl = "http://hexavara.ip-dynamic.com/androidrec/public/api/login";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Login");  // provide compatibility to all the versions

        editMail = findViewById(R.id.editTextUsername);
        editPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.btnLogin);

        editPassword.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    buttonLogin.performClick();
                    return true;
                }
                return false;
            }
        });

    }

    public void doLogin(View view) {

        if(!internetKontrol()){
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
        }

        String mail = editMail.getText().toString();
        String pass = editPassword.getText().toString();

        String validation = validate(mail, pass);
        if(validation.equals("success")){
            new GetUserDetails().execute(mail, pass);
        }
        else {
            Toast.makeText(this, "Error encountered during credentials validation!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void taskCompletionResult(HashMap<String, String> result) {

        if (result==null || !result.containsKey(Tags.TAG_SUCCESS))
        {
            runOnUiThread(new Runnable() {
                public void run() {
                    // runs on UI thread
                    Toast.makeText(getApplicationContext(),"Error server or connection!", Toast.LENGTH_SHORT).show();

                }
            });
            return;
        }

        if(result.get(Tags.TAG_SUCCESS).equals("1")){

            Intent intent = new Intent(getApplicationContext(), Main2Activity.class);
            intent.putExtra(
                    "token", token
            );
            intent.putExtra(
                    "photo", photo
            );
            intent.putExtra(
                    "fullname", fullname
            );
            intent.putExtra(
                    "username", username
            );
            intent.putExtra(
                    "email", email
            );
            intent.putExtra(
                    "address", address
            );

            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(this, "Login attempt interrupted!", Toast.LENGTH_SHORT).show();
        }
    }


    class GetUserDetails extends AsyncTask<String, String, HashMap<String, String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait, being logged in...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected HashMap doInBackground(String... strings) {
            String success;
            HashMap<String, String> result = new HashMap<>();

            try {
                List<NameValuePair> params = new ArrayList<>();
                params.add(new BasicNameValuePair("username", strings[0]));
                params.add(new BasicNameValuePair("password", strings[1]));

                InputStream postResult = null; String json = null; JSONObject jObj = null;

                // Making HTTP request
                try {
                    // request method is POST
                    // defaultHttpClient
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(postUrl);
                    httpPost.setEntity(new UrlEncodedFormEntity(params));

                    HttpResponse httpResponse = httpClient.execute(httpPost);

                    if(httpResponse.getStatusLine().getStatusCode()==200){
                        //String server_response = EntityUtils.toString(httpResponse.getEntity());
                        //Log.i("Server response", server_response );
                        HttpEntity httpEntity = httpResponse.getEntity();
                        postResult = httpEntity.getContent();
                    } else {
                        final String server_response = EntityUtils.toString(httpResponse.getEntity());
                        Log.i("Server response", server_response );
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(),server_response,Toast.LENGTH_SHORT).show();
                            }
                        });

                        return result;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            postResult, "iso-8859-1"), 8);
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    postResult.close();
                    json = sb.toString();
                } catch (Exception e) {
                    Log.e("Buffer Error", "Error converting result " + e.toString());
                }

                // try parse the string to a JSON object
                try {
                    if (json==null)
                    {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                // runs on UI thread
                                Toast.makeText(getApplicationContext(),"Null data source", Toast.LENGTH_SHORT).show();

                            }
                        });
                        return result;
                    }
                    jObj = new JSONObject(json);
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

                Log.d("User Details", json.toString());

                if (jObj.has("token"))
                    success = "1";
                else
                    success = "0";

                //success = json.getString(Tags.TAG_SUCCESS);
                Log.d("Login status", success);
                result.put(Tags.TAG_SUCCESS, success);

                if (success.equals("1")) {
                    result.put(Tags.TAG_TOKEN, jObj.getString(Tags.TAG_TOKEN));
                    token = jObj.getString(Tags.TAG_TOKEN);
                    result.put(Tags.TAG_USERNAME, jObj.getString(Tags.TAG_USERNAME));
                    username = jObj.getString(Tags.TAG_USERNAME);
                    result.put(Tags.TAG_EMAIL, jObj.getString(Tags.TAG_EMAIL));
                    email = jObj.getString(Tags.TAG_EMAIL);
                    //result.put(Tags.TAG_PASSWORD, userObject.getString(Tags.TAG_PASSWORD));
                    result.put(Tags.TAG_FULLNAME, jObj.getString(Tags.TAG_FULLNAME));
                    fullname = jObj.getString(Tags.TAG_FULLNAME);
                    result.put(Tags.TAG_ADDRESS, jObj.getString(Tags.TAG_ADDRESS));
                    address = jObj.getString(Tags.TAG_ADDRESS);
                    result.put(Tags.TAG_PHOTO, jObj.getString(Tags.TAG_PHOTO));
                    photo = jObj.getString(Tags.TAG_PHOTO);
                }
                else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            // runs on UI thread
                            Toast.makeText(getApplicationContext(),"Failed to fetch data.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }
        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
            pDialog.dismiss();
            taskCompletionResult(result);
        }
    }

    public String validate(String mail, String passwordStr) {
        if(!internetKontrol()){
            Toast.makeText(this, "No internet connection!", Toast.LENGTH_SHORT).show();
            return "No internet connection!";
        }
        if (mail.isEmpty()){
            return "Username cannot be empty!";
        }
        //   if (!isValidMail(mail)){
     //       return "Invalid Username format!";
     //   }
        if (passwordStr.length()<6){
            return "Password cannot be shorter than 6 characters!";
        }
        return "success";
    }

    public  boolean isValidMail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null)
            return false;
        return pat.matcher(email).matches();
    }

    //method to check internet connectivity
    protected boolean internetKontrol() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}

interface TaskDelegate {
    public void taskCompletionResult(HashMap<String, String> result);
}

