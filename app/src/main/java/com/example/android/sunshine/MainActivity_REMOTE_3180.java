/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.sunshine;

import android.net.Network;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.android.sunshine.data.SunshinePreferences;
import com.example.android.sunshine.utilities.NetworkUtils;
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils;
import com.example.android.sunshine.utilities.SunshineWeatherUtils;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        /*
         * Using findViewById, we get a reference to our TextView from xml. This allows us to
         * do things like set the text of the TextView.
         */
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);
        loadWeatherData();

    }

    public void loadWeatherData(){
        String preferredLocation = SunshinePreferences.getPreferredWeatherLocation(this);
        URL query = NetworkUtils.buildUrl(preferredLocation);
        new fetchWeatherTask().execute(query);

    }

    public class fetchWeatherTask extends AsyncTask<URL, Void, String[]> {
        @Override
        protected String[] doInBackground(URL... params){
            URL searchUrl = params[0];
            String[] weatherItems = null;
            try {
                String weatherResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
                weatherItems = OpenWeatherJsonUtils.getSimpleWeatherStringsFromJson(MainActivity.this, weatherResults);
            }
            catch(IOException ex){
                ex.printStackTrace();
            }catch (JSONException jsonEx){
                jsonEx.printStackTrace();
            }

            return weatherItems;
        }

        @Override
        protected void onPostExecute(String[] weatherItems){
            if(weatherItems != null && weatherItems.length > 0){
                mWeatherTextView.setText("");
                for(String item: weatherItems){
                    mWeatherTextView.append(item + "\n\n");
                }
            }

        }
    }
}