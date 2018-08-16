package com.example.farisfathurrahman25.android_hexa_farisfathurrahman;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements TaskDelegate{

    EditText editMail;
    EditText editPassword;
    Button buttonLogin;
    TextView textViewError;
    //SessionManager sessionManager;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    String token;
    String photo;
    String fullname;
    String username;
    String email;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editMail = (EditText) findViewById(R.id.editTextUsername);
        editPassword = (EditText) findViewById(R.id.editTextPassword);
        buttonLogin = (Button) findViewById(R.id.btnLogin);

        textViewError = (TextView) findViewById(R.id.tv_error);
        //sessionManager = new SessionManager(getApplicationContext());

    }

    public void doLogin(View view) {

        String mail = editMail.getText().toString();
        String pass = editPassword.getText().toString();

        String validation = validate(mail, pass);
        if(validation.equals("success")){
            textViewError.setVisibility(View.INVISIBLE);
            new GetUserDetails().execute(mail, pass);
        }
        else {
            textViewError.setText(validation);
            textViewError.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void taskCompletionResult(HashMap<String, String> result) {

        if(result.get(Tags.TAG_SUCCESS).equals("1")){
            textViewError.setVisibility(View.INVISIBLE);
            //sessionManager.createLoginSession(result);

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
            textViewError.setText("Username or Password is incorrect!");
            textViewError.setVisibility(View.VISIBLE);
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

                JSONObject json = jsonParser.makeHttpRequest(Tags.URL_LAST_PATH, "POST", params);

                Log.d("User Details", json.toString());

                if (json.has("token"))
                    success = "1";
                else
                    success = "0";

                //success = json.getString(Tags.TAG_SUCCESS);
                Log.d("Login status", success);
                result.put(Tags.TAG_SUCCESS, success);

                if (success.equals("1")) {
                    result.put(Tags.TAG_TOKEN, json.getString(Tags.TAG_TOKEN));
                    token = json.getString(Tags.TAG_TOKEN);
                    result.put(Tags.TAG_USERNAME, json.getString(Tags.TAG_USERNAME));
                    username = json.getString(Tags.TAG_USERNAME);
                    result.put(Tags.TAG_EMAIL, json.getString(Tags.TAG_EMAIL));
                    email = json.getString(Tags.TAG_EMAIL);
                    //result.put(Tags.TAG_PASSWORD, userObject.getString(Tags.TAG_PASSWORD));
                    result.put(Tags.TAG_FULLNAME, json.getString(Tags.TAG_FULLNAME));
                    fullname = json.getString(Tags.TAG_FULLNAME);
                    result.put(Tags.TAG_ADDRESS, json.getString(Tags.TAG_ADDRESS));
                    address = json.getString(Tags.TAG_ADDRESS);
                    result.put(Tags.TAG_PHOTO, json.getString(Tags.TAG_PHOTO));
                    photo = json.getString(Tags.TAG_PHOTO);
                }
                else
                    Toast.makeText(getApplicationContext(),"Either Username or Pass is wrong!", Toast.LENGTH_SHORT);
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
        if (mail.isEmpty()){
            return "Username cannot be empty!";
        }
        //   if (!isValidMail(mail)){
     //       return "Invalid Username format!";
     //   }
        if (passwordStr.length()<6){
            return "Password cannot shorter than 6!";
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
}

interface TaskDelegate {
    public void taskCompletionResult(HashMap<String, String> result);
}

