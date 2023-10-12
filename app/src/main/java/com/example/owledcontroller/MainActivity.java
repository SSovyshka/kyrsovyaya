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

    private static MongoClient mongoClient;
    private static DB dataBase;
    private static DBCollection dataBaseCollection;

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

    private static ArrayList<Effect> getEffects() throws IOException {
        ArrayList<Effect> list = new ArrayList<>();

        String apiUrl = "http://192.168.0.28:8080/api/example"; // Замените на фактический URL вашего сервера
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
            list = mapper.readValue(String.valueOf(responseText), new TypeReference<ArrayList<Effect>>() {});
        }


        return list;
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        try {
            effects = getEffects();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        ArrayList<String> effectName = new ArrayList<>();
        effects.forEach(e -> {
           effectName.add(e.getEffectName());
        });

        int[] flowerImages = {R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground, R.drawable.ic_launcher_foreground};

        GridAdapter gridAdapter = new GridAdapter(MainActivity.this, effectName, flowerImages);

        binding.gridView.setAdapter(gridAdapter);
        binding.gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                JSONObject jsonObject = new JSONObject();

                try {
                    jsonObject.put("effect", effectName.get(i));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                new NetworkTask().execute(jsonObject);


                // Вывод всплывающего сообщения при нажатии на элемент
                Toast.makeText(MainActivity.this, "You clicked on " + jsonObject.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}