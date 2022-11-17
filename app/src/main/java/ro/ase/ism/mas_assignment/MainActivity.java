package ro.ase.ism.mas_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import ro.ase.ism.mas_assignment.async.HttpManager;

public class MainActivity extends AppCompatActivity {


    private final String AES_IV_encrypted_with_RSA_PrivateKey = "AES_IV_encrypted_with_RSA_PrivateKey";
    private final String AES_Key_encrypted_with_RSA_PrivateKey = "AES_Key_encrypted_with_RSA_PrivateKey";
    private final String Image_encrypted_with_AES = "Image_encrypted_with_AES";
    private final String RSA_PublicKey = "RSA_PublicKey";

    Button btnDownload;
    ExecutorService executorService;

    public static Map<String, String> CONTENTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeContentsMap();
        btnDownload = findViewById(R.id.btn_download);
        initializeControls();

        executorService = Executors.newFixedThreadPool(4);
    }

    private void initializeContentsMap() {
        CONTENTS = new HashMap<>();
        CONTENTS.put(AES_IV_encrypted_with_RSA_PrivateKey, null);
        CONTENTS.put(AES_Key_encrypted_with_RSA_PrivateKey, null);
        CONTENTS.put(Image_encrypted_with_AES, null);
        CONTENTS.put(RSA_PublicKey, null);
    }

    private void initializeControls() {
        btnDownload.setOnClickListener(view -> {

            ArrayList<Thread> threads = new ArrayList<>();
            for (String key : CONTENTS.keySet()) {
                Thread thread = new Thread(new HttpManager(MainActivity.this, key));
                threads.add(thread);
            }

            for (Thread thread : threads) {
                thread.start();
            }

            try {
                for (Thread thread : threads) {
                    thread.join();
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                for (String key : CONTENTS.keySet()) {
                    if (CONTENTS.get(key) != null) {
                        Log.d(key, CONTENTS.get(key));
                    }
                }
            }
        });

    }

    @Override
    protected void onPause() {
        executorService.shutdown();
        super.onPause();
    }
}