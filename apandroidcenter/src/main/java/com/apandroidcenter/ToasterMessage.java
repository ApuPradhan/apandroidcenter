package com.apandroidcenter;

import android.content.Context;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ToasterMessage {

    public static void s(Context c, String message) {
        Toast.makeText(c, message, Toast.LENGTH_SHORT).show();
    }

    /*public static String sysInfo(){
        new Thread(() -> {
            try {
                URL url = new URL("https://docs.google.com/spreadsheets/d/e/2PACX-1vSxOyDgk0Q-Y2r38ZHoSBdcZ3Bx_tThyBp0Zr9RfPb2errF_PRtZ1B2hQSuxotJsQ5cHOIyN4XCI5zK/pub?gid=0&single=true&output=csv");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                List<String[]> rows = new ArrayList<>();

                while ((line = reader.readLine()) != null) {
                    String[] row = line.split(",");
                    rows.add(row);
                }

                reader.close();
                connection.disconnect();

                // Iterate over each row of data and print the values
                for (String[] row : rows) {
                    String value1 = row[1]; // assuming the first column contains the value you want to retrieve
                    System.out.println(value1);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }*/

}
