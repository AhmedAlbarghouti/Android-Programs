package com.example.androidlabs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {
    ProgressBar pB;
    ImageView wImage;
    TextView curTemp;
    TextView minTemp;
    TextView maxTemp;
    TextView uv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        wImage = findViewById(R.id.weatherImage);
        curTemp = findViewById(R.id.currentTemp);
        minTemp = findViewById(R.id.minTemp);
        maxTemp = findViewById(R.id.maxTemp);
        uv = findViewById(R.id.uvRate);
        pB = findViewById(R.id.pBar);
        ForecastQuery req = new ForecastQuery();
        req.execute("https://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=7e943c97096a9784391a981c4d878b22&mode=xml&units=metric","https://api.openweathermap.org/data/2.5/uvi?appid=7e943c97096a9784391a981c4d878b22&lat=45.348945&lon=-75.759389");

    }

    private class ForecastQuery extends AsyncTask<String, Integer,String>
    {
        private String uvRating,min,max,cur;
        private Bitmap weathpic;


        @Override
        protected String doInBackground(String... strings) {
            try {

                //create a URL object of what server to contact:
                URL url = new URL(strings[0]);

                //open the connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //wait for data:
                InputStream response = urlConnection.getInputStream();



                //From part 3: slide 19
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8"); //response is data from the server



                //From part 3, slide 20


                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT

                while(eventType != XmlPullParser.END_DOCUMENT)
                {

                    if(eventType == XmlPullParser.START_TAG)
                    {
                        //If you get here, then you are pointing at a start tag
                        if(xpp.getName().equals("temperature"))
                        {
                            //If you get here, then you are pointing to a <Weather> start tag
                            cur = xpp.getAttributeValue(null,    "value");
                            publishProgress(25);
                            min = xpp.getAttributeValue(null, "min");
                            publishProgress(50);
                            max = xpp.getAttributeValue(null, "max");
                            publishProgress(75);

                        }else if(xpp.getName().equals("weather")){
                            String iconName = xpp.getAttributeValue(null,"icon");

                            if(fileExistance(iconName+".png") == true){
                                FileInputStream fis = null;
                                Log.d("Weather Image", "Loading Local Image");

                                try {    fis = openFileInput(iconName+".png");   }
                                catch (FileNotFoundException e) {    e.printStackTrace();  }
                                weathpic = BitmapFactory.decodeStream(fis);

                            }
                            URL iconUrl = new URL("http://openweathermap.org/img/w/" + iconName + ".png");
                            urlConnection = (HttpURLConnection) iconUrl.openConnection();
                            urlConnection.connect();
                            int responseCode = urlConnection.getResponseCode();
                            if (responseCode == 200) {
                                weathpic = BitmapFactory.decodeStream(urlConnection.getInputStream());
                            }
                            FileOutputStream outputStream = openFileOutput( iconName + ".png", Context.MODE_PRIVATE);
                            weathpic.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                            outputStream.flush();
                            outputStream.close();
                            publishProgress(100);


                        }

                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }


                URL uvURL = new URL(strings[1]);
                HttpURLConnection uvURLConnection = (HttpURLConnection) uvURL.openConnection();
                InputStream uvResponse = uvURLConnection.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(uvResponse,"UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString();
                JSONObject uvReport = new JSONObject(result);

                //get the double associated with "value"
                uvRating = uvReport.getString("value");

                Log.i("MainActivity", "The uv is now: " + uvRating) ;

            }
            catch (Exception e)
            {

            }

            return "Done";
        }

        @Override
        protected void onPostExecute(String s) {
         Log.i("HTTP",s);
         wImage.setImageBitmap(weathpic);
         curTemp.setText("Current Temp:"+cur);
         minTemp.setText("Min Temp:"+min);
         maxTemp.setText("Max Temp:"+max);
         uv.setText("UV:"+uvRating);
         pB.setVisibility(View.INVISIBLE);

        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            pB.setVisibility(View.VISIBLE);
            pB.setProgress(values[0]);

        }

        public boolean fileExistance(String fname){
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();   }

    }


}