package com.example.owledcontroller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.owledcontroller.databinding.ActivityMainBinding;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    private static final String SERVER_IP = "192.168.0.78";
    private static final int SERVER_PORT = 61024;

    private static MongoClient mongoClient;
    private static DB dataBase;
    private static DBCollection dataBaseCollection;

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

    private static ArrayList<String> getEffects(){

        mongoClient = new MongoClient(new MongoClientURI("mongodb+srv://sovyshka:ujdyfdnhzgjxre@cluster0.rpsi7sd.mongodb.net/?retryWrites=true&w=majority"));
        dataBase = mongoClient.getDB("leddb");
        dataBaseCollection = dataBase.getCollection("visualeffects");

        BasicDBObject query = new BasicDBObject("effect", new BasicDBObject("$exists", true));
        DBCursor cursor = dataBaseCollection.find(query);

        ArrayList<String> effects = new ArrayList<>();

        // Перебираем результаты запроса.
        while (cursor.hasNext()) {
            DBObject document = cursor.next();
            String effectValue = (String) document.get("effect");
            effects.add(effectValue);
        }

        cursor.close();
        mongoClient.close();
        return effects;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<String> effectName = getEffects();

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