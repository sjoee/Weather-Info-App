package com.example.weatherinfo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    // must go to manifest file first, set internet permission
    EditText cityName;
    TextView resultTextView;
    // what happen when the user click on the button
    public void findWeather(View view) {

        // get the city name from the textbox and display them in the log
        Log.i("cityName", cityName.getText().toString());

        // hide the keyboard or keypad
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(), 0);


        // try to run the following code, if cannot run go to catch block
        try {
            String encodedCityName = URLEncoder.encode(cityName.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();

            // you can put your own api key here

            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName+ "&appid=4949b4bf62983fd5f0a15f79882d6b68");

            // task.execute("https://samples.openweathermap.org/data/2.5/weather?q=" + encodedCityName+ "&appid=b6907d289e10d714a6e88b30761fae22\n");
            // uses samples only if  actual API call not working

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();

            Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);

        }



    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        cityName = (EditText) findViewById(R.id.cityName);
        resultTextView = (TextView) findViewById(R.id.resultTextView);
    }

    // this is an inner class that extends asynchtask - background process
    public class DownloadTask extends AsyncTask<String, Void, String>
    {
        // run this code in background mode
        @Override
        protected String doInBackground(String... urls)
        {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            // here

            try {
                url = new URL(urls[0]);
                // open the url, the website, request for data
                urlConnection = (HttpURLConnection) url.openConnection();
                // stoed and read incoming data streams
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                // keep reading while there is an incoming data
                // stored in result variables, accumulate all data
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;  // returned the json text

            } catch (Exception e) {  //if cannot connect to website
                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
            }

            return null;  // return nothing



        }

        // what happen after the download process complete, extract the required information from
        // the json text file and displayed them on the apps
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            try {

                String message = "";
                JSONObject jsonObject = new JSONObject(result);
                // extract weather value from json object
                String weatherInfo = jsonObject.getString("weather");
                Log.i("Weather content", weatherInfo);
                // create an array of json type to stored others data also
                JSONArray arr = new JSONArray(weatherInfo);

                for (int i = 0; i < arr.length(); i++) {

                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main = "";
                    String description = "";
                    // get the main and description also from json text
                    main = jsonPart.getString("main");
                    description = jsonPart.getString("description");

                    if (main != "" && description != "") {
                        message += main + ": " + description + "\r\n";
                    }

                }

                if (message != "") {  // display in the textview
                    resultTextView.setText(message);
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);
                }


            } catch (JSONException e) {

                Toast.makeText(getApplicationContext(), "Could not find weather", Toast.LENGTH_LONG);

            }





        }

    }
}