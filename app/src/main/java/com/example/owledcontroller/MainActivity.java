package com.example.owledcontroller;

import android.annotation.SuppressLint;
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
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;

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
            } catch (IOException e) {
                e.printStackTrace();
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

        public ArrayList<String> getEffectFunction() {
            ArrayList<String> images = new ArrayList<>();
            effects.forEach(e -> {
                images.add(e.getGif());
            });
            return images;
        }

        @Override
        protected void onPostExecute(ArrayList<Effect> result) {
            if (result != null) {
                effects = result;
                ArrayList<String> effectNames = new ArrayList<>();
                for (Effect effect : effects) {
                    effectNames.add(effect.getEffectName());
                }

                int[] flowerImages = {R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};
                GridAdapter gridAdapter = new GridAdapter(MainActivity.this, effectNames, flowerImages);

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

        String apiUrl = "http://192.168.0.28:8080/api/example";
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