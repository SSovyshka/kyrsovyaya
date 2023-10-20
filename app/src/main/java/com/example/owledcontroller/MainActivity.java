package com.example.owledcontroller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.owledcontroller.databinding.ActivityMainBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private static final String SERVER_IP = "192.168.0.78";
    private static final int SERVER_PORT = 61024;

    private ArrayList<Effect> effects;

    private class NetworkTask extends AsyncTask<JSONObject, Void, Void> {
        @Override
        protected Void doInBackground(JSONObject... jsonObjects) {
            try {
                Socket socket = new Socket(SERVER_IP, SERVER_PORT);
                PrintWriter out = new PrintWriter(socket.getOutputStream());
                out.println(jsonObjects[0].toString());
                out.close();
                socket.close();
                Log.d("responseCode", jsonObjects[0].toString());

                String apiUrl = "http://192.168.0.23:8080/api/increasepopularity/"+ jsonObjects[0].get("effect");
                Log.d("responseCode", apiUrl);
                URL url = new URL(apiUrl);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                Log.d("responseCode", String.valueOf(connection.getResponseCode()));


            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    private class FetchEffectsTask extends AsyncTask<Void, Void, ArrayList<Effect>> {
        @Override
        protected ArrayList<Effect> doInBackground(Void... voids) {
            try {
                return getEffects();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        private ArrayList<Integer> getEffectImages() {
            ArrayList<Integer> images = new ArrayList<>();
            effects.forEach(e -> {
                int data = getResources().getIdentifier(e.getGif(), "drawable", getPackageName());
                if(data != 0)
                    images.add(data);
                else
                    images.add(R.drawable.ic_launcher_background);
                Log.d("imgs",e.getGif());
            });
            return images;
        }

        private ArrayList<String> getEffectNames(){
            ArrayList<String> names = new ArrayList<>();
            effects.forEach(e ->{
                names.add(e.getEffectName());
            });
            return names;
        }

        private void sortArrayByPopularity(){
            Comparator<Effect> comparatorByPopularity = (ef1, ef2) ->{
              if (ef1.getPopularity() == ef2.getPopularity()){
                  return 0;
              }else if(ef1.getPopularity() > ef2.getPopularity()){
                  return -1;
              }else {
                  return 0;
              }
            };

            effects.sort(comparatorByPopularity);
        }


        @Override
        protected void onPostExecute(ArrayList<Effect> result) {
            if (result != null) {
                effects = result;
                sortArrayByPopularity();

                ArrayList<String> effectNames = getEffectNames();
                ArrayList<Integer> effectImages = getEffectImages();

                GridAdapter gridAdapter = new GridAdapter(MainActivity.this, effectNames, effectImages);

                binding.gridView.setAdapter(gridAdapter);
                binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("effect", effects.get(i).getEffect());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        new NetworkTask().execute(jsonObject);
                        Toast.makeText(MainActivity.this, "You clicked on " + jsonObject.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(MainActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        new FetchEffectsTask().execute();
    }

    private ArrayList<Effect> getEffects() throws IOException {
        ArrayList<Effect> list = new ArrayList<>();

        String apiUrl = "http://192.168.0.23:8080/api/getalleffects";
        URL url = new URL(apiUrl);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder responseText = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                responseText.append(inputLine);
            }
            in.close();

            ObjectMapper mapper = new ObjectMapper();
            list = mapper.readValue(responseText.toString(), new TypeReference<ArrayList<Effect>>() {});
        }

        return list;
    }
}