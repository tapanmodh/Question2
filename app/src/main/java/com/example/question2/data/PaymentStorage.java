package com.example.question2.data;

import android.content.Context;

import androidx.core.util.Consumer;

import com.example.question2.model.Payment;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PaymentStorage {

    private static final String FILE_NAME = "LastPayment.txt";
    private static final Gson gson = new Gson();

    public static void save(Context context, List<Payment> payments) {
        new Thread(() -> {
            try {
                Payment[] arr = payments.toArray(new Payment[0]);
                String json = gson.toJson(arr);

                try(FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE)) {
                    fos.write(json.getBytes());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void load(Context context, Consumer<List<Payment>> callback) {
        new Thread(() -> {
            try {
                File file = new File(context.getFilesDir(), FILE_NAME);
                if (!file.exists()) {
                    callback.accept(new ArrayList<>());
                    return;
                }

                StringBuilder builder = new StringBuilder();
                try(FileInputStream fis = context.openFileInput(FILE_NAME);
                BufferedReader br = new BufferedReader(new InputStreamReader(fis))) {

                    String line;
                    while ((line = br.readLine()) != null) builder.append(line);
                }

                Payment[] arr = gson.fromJson(builder.toString(), Payment[].class);
                callback.accept(Arrays.asList(arr));

            } catch (Exception e) {
                e.printStackTrace();
                callback.accept(new ArrayList<>());
            }
        }).start();
    }
}
