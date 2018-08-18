package com.example.farisfathurrahman25.android_hexa_farisfathurrahman;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class Main2Activity extends AppCompatActivity{

    String TAG;

    CircleImageView profileImage;

    TextView textFullname;
    TextView textUsername;
    TextView textEmail;
    TextView textAddress;

    private RecyclerView recyclerItems;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ProgressDialog pDialog;

    JSONParser jsonParser = new JSONParser();

    String url = "http://hexavara.ip-dynamic.com/androidrec/public/api/mylist";

    ArrayList<Item> itemList;
    ItemAdapter itemAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        profileImage = (CircleImageView) findViewById(R.id.profile_image);

        textFullname = findViewById(R.id.txtFullname);
        textUsername = findViewById(R.id.txtUsername);
        textEmail = findViewById(R.id.txtEmail);
        textAddress = findViewById(R.id.txtAddress);

        recyclerItems = findViewById(R.id.recyclerView);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
 //       recyclerItems.setHasFixedSize(true);

        // use a linear layout manager
//        mLayoutManager = new LinearLayoutManager(this);
//        recyclerItems.setLayoutManager(mLayoutManager);

        Glide.with(this).load("http://" + getIntent().getStringExtra("photo")).into(profileImage);

        textFullname.setText(getIntent().getStringExtra("fullname"));
        textUsername.setText(getIntent().getStringExtra("username"));
        textEmail.setText(getIntent().getStringExtra("email"));
        textAddress.setText(getIntent().getStringExtra("address"));

        itemList = new ArrayList<>();

        String token = getIntent().getStringExtra("token");
        new GetItemList().execute(token);

        // specify an adapter
//        mAdapter = new ItemAdapter(itemList);
//        recyclerItems.setAdapter(mAdapter);

        Main2Activity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                mAdapter = new ItemAdapter(getApplicationContext(), itemList);
                recyclerItems.setAdapter(mAdapter);
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(Main2Activity.this);
                recyclerItems.setLayoutManager(layoutManager);
                recyclerItems.setHasFixedSize(true);
            }
        });
    }

    class GetItemList extends AsyncTask<String, String, HashMap<String, String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(Main2Activity.this);
            pDialog.setMessage("Please wait, list is being loaded...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected HashMap doInBackground(String... strings) {
            String success;
            HashMap<String, String> result = new HashMap<>();
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("Authorization", strings[0]));

            InputStream is = null; String json = null; JSONArray jArr;

            // request method is GET
            DefaultHttpClient httpClient = new DefaultHttpClient();
            //String paramString = URLEncodedUtils.format(params, "utf-8");
            //url += "?" + paramString;
            HttpGet httpGet = new HttpGet(url);
            httpGet.setHeader("Authorization", strings[0]);

            HttpResponse httpResponse = null;
            try {
                httpResponse = httpClient.execute(httpGet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if(httpResponse.getStatusLine().getStatusCode()==200){
                //String server_response = EntityUtils.toString(httpResponse.getEntity());
                //Log.i("Server response", server_response );
                HttpEntity httpEntity = httpResponse.getEntity();
                try {
                    is = httpEntity.getContent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                String server_response = null;
                try {
                    server_response = EntityUtils.toString(httpResponse.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("Server response", server_response );
                return null;
            }

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
                Log.e("Buffer Error", "Error converting result " + e.toString());
            }

            // try parse the string to a JSON object
            try {
                jArr = new JSONArray(json);

                for (int i = 0; i < jArr.length(); i++) {
                    JSONObject object = jArr.getJSONObject(i);
                    Item item = new Item();

                    Log.i(TAG, object.getString("name"));
                    Log.i(TAG, object.getString("salary"));
                    Log.i(TAG, object.getString("image"));

                    //getting json object values from json array
                    item.setName(object.getString("name"));
                    item.setSalary(Long.parseLong(object.getString("salary")));
                    item.setImageURL("http://" + object.getString("image"));

                    //adding data to the arrayList
                    itemList.add(item);
                }
            } catch (JSONException e) {
                Log.e("JSON Parser", "Error parsing data " + e.toString());
            }

            return result;
        }
        @Override
        protected void onPostExecute(HashMap<String, String> result) {
            super.onPostExecute(result);
            mAdapter.notifyDataSetChanged();
            pDialog.dismiss();
        }
    }
}
