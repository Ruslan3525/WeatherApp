package com.example.finalexam;

import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.AsyncTaskLoader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MapActivity extends AppCompatActivity {

    private EditText edit;
    private Button checkWeather;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        edit = findViewById(R.id.edit); //find edittext layout
        checkWeather = findViewById(R.id.checkWeather); //find button which checks weather
        result = findViewById(R.id.result); //find textview layout with result

        checkWeather.setOnClickListener(new View.OnClickListener() { //add method to button
            @Override
            public void onClick(View view) {
                if (edit.getText().toString().trim().equals(""))// checks that string is empty
                    Toast.makeText(MapActivity.this, R.string.no_text, Toast.LENGTH_LONG).show();// Class toast shows window with string value
                else {
                    String city = edit.getText().toString();
                    String api_key = "f9d25dd70a5989d11ae802ed42f4a8f1";
                    String api_url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + api_key + "&units=metric";
                    new GetUrlData().execute(api_url);
                }
            }
        });


    }

    private class GetUrlData extends AsyncTask<String, String, String> { //class that extends class AsyncTask

        protected void onPreExecute() { //runs before doInBackground, accesses the UI, and displays some text
            super.onPreExecute(); //calling a method in a parent class AsyncTask
            result.setText("Please wait...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null; // create base on HttpURLConnection
            BufferedReader reader = null; //create object base on BufferReader

            try {  // defines a block of code in which an exception can occur
                URL url = new URL(strings[0]); //open url connection
                connection = (HttpURLConnection) url.openConnection(); //open http connection
                connection.connect();

                InputStream stream = connection.getInputStream(); // Object base on InputeStream for reading data
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer(); //
                String line = ""; // empty string

                while ((line = reader.readLine()) != null) // cycles through the reading lines until the reading is exactly zero
                    buffer.append(line).append("\n"); // adds one read line and performs a newline

                return buffer.toString();

            } catch (MalformedURLException e) { //defines the block of code in which the exception is handled
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally { // defines a block of code that is optional but, if present, is executed anyway, regardless of the results of the try block
                if (connection != null) // checks if a http connection is open, and is non-zero
                    connection.disconnect(); // stop connection

                try {
                    if (reader != null) // checks if a reader is open, and is non-zero
                        reader.close(); // closes reader
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result_one) { // executed after doInBackground (does not fire if the AsyncTask has been canceled
            super.onPostExecute(result_one);

            try {
                JSONObject jsonObject = new JSONObject(result_one); // The object that handles the json object is the result
                result.setText("Temperature: " + jsonObject.getJSONObject("main").getDouble("temp"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
