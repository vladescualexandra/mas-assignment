package ro.ase.ism.mas_assignment.async;

import static android.os.Looper.getMainLooper;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import ro.ase.ism.mas_assignment.MainActivity;

public class HttpManager implements Runnable {

    private final String SERVER = "https://student.ism.ase.ro/access/content/group/Y2S1_MAS/AndroidEncrypt/";

    Context context;
    String FILE_NAME;

    public HttpManager(Context context, String fileName) {
        this.FILE_NAME = fileName;
        this.context = context;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        StringBuilder result = new StringBuilder();

        try {
            URL url = new URL(SERVER + FILE_NAME);
            connection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line;

            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        new Handler(getMainLooper()).post(() ->
                MainActivity.CONTENTS.replace(FILE_NAME, String.valueOf(result)));

    }
}
