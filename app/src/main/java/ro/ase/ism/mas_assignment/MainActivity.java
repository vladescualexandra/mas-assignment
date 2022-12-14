package ro.ase.ism.mas_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import ro.ase.ism.mas_assignment.async.HttpManager;

public class MainActivity extends AppCompatActivity {

    private final String AES_IV_encrypted_with_RSA_PrivateKey = "AES_IV_encrypted_with_RSA_PrivateKey";
    private final String AES_Key_encrypted_with_RSA_PrivateKey = "AES_Key_encrypted_with_RSA_PrivateKey";
    private final String Image_encrypted_with_AES = "Image_encrypted_with_AES";
    private final String RSA_PublicKey = "RSA_PublicKey";

    byte[] AES_KEY;
    byte[] AES_IV;
    byte[] IMAGE_BYTES;

    Button btnDownload;
    Button btnDecryptAES;
    Button btnDecryptImage;
    Button btnDisplayImage;
    ExecutorService executorService;

    public static Map<String, String> CONTENTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executorService = Executors.newFixedThreadPool(4);

        initializeContentsMap();
        configureDownload();
        configureDecryptAES();
        configureDecryptImage();
        configureDecryptImage();
        configureDisplayImage();
    }

    private void initializeContentsMap() {
        CONTENTS = new HashMap<>();
        CONTENTS.put(AES_IV_encrypted_with_RSA_PrivateKey, null);
        CONTENTS.put(AES_Key_encrypted_with_RSA_PrivateKey, null);
        CONTENTS.put(Image_encrypted_with_AES, null);
        CONTENTS.put(RSA_PublicKey, null);
    }

    private void configureDownload() {
        btnDownload = findViewById(R.id.btn_download);
        btnDownload.setOnClickListener(view -> {
            ArrayList<Thread> threads = new ArrayList<>();
            for (String key : CONTENTS.keySet()) {
                Thread thread = new Thread(new HttpManager(MainActivity.this, key));
                threads.add(thread);
            }

            for (Thread thread : threads) {
                thread.start();
            }
        });
    }

    private void configureDecryptAES() {
        btnDecryptAES = findViewById(R.id.btn_decrypt_aes);
        btnDecryptAES.setOnClickListener(view -> {

            String key = CONTENTS.get(RSA_PublicKey);
            String content = CONTENTS.get(AES_Key_encrypted_with_RSA_PrivateKey);

            if (CONTENTS.get(RSA_PublicKey) == null
                    || CONTENTS.get(AES_Key_encrypted_with_RSA_PrivateKey) == null
                    || CONTENTS.get(AES_IV_encrypted_with_RSA_PrivateKey) == null) {
                Toast.makeText(getApplicationContext(),
                                "Files not downloaded. Please use button [1] DOWNLOAD FILES.",
                                Toast.LENGTH_SHORT)
                        .show();
            } else {
                AES_KEY = decryptRSA(key, content);
                assert AES_KEY != null;
                Log.d("AES_KEY", toHex(AES_KEY));

                content = CONTENTS.get(AES_IV_encrypted_with_RSA_PrivateKey);
                AES_IV = decryptRSA(key, content);
                assert AES_IV != null;
                Log.d("AES_IV", toHex(AES_IV));
            }

        });
    }

    public static byte[] decryptRSA(String key, String input) {
        try {
            X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.getDecoder().decode(key));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(spec);

            byte[] textToDecrypt = Base64.getDecoder().decode(input.getBytes());
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            cipher.update(textToDecrypt);
            return cipher.doFinal();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void configureDecryptImage() {
        btnDecryptImage = findViewById(R.id.btn_decrypt_image);
        btnDecryptImage.setOnClickListener(view -> {

            if (CONTENTS.get(Image_encrypted_with_AES) == null) {
                Toast.makeText(getApplicationContext(),
                                "Files not downloaded. Please use button [1] DOWNLOAD FILES.",
                                Toast.LENGTH_SHORT)
                        .show();
            } else {

                String content = CONTENTS.get(Image_encrypted_with_AES);

                if (AES_KEY == null) {
                    Toast.makeText(getApplicationContext(),
                                    "AES SECRET not available. Please use button [2] DECRYPT AES SECRET AND IV.",
                                    Toast.LENGTH_SHORT)
                            .show();
                } else {
                    int keySize = 32;
                    byte[] aesKey = new byte[32];
                    for (int i = 0; i < keySize; i++) {
                        aesKey[i] = AES_KEY[AES_KEY.length - i - 1];
                    }

                    try {
                        SecretKey key = new SecretKeySpec(aesKey, "AES");
                        Cipher aes = Cipher.getInstance("AES/ECB/NoPadding");
                        aes.init(Cipher.DECRYPT_MODE, key);

                        IMAGE_BYTES = aes.doFinal(content.getBytes(StandardCharsets.UTF_8));
                        Log.d("Decrypted Image", toHex(IMAGE_BYTES));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void configureDisplayImage() {
        if (IMAGE_BYTES == null) {
            Toast.makeText(getApplicationContext(),
                            "Image not decrypted. Please use button [3] DECRYPT IMAGE.",
                            Toast.LENGTH_SHORT)
                    .show();
        } else {
            btnDisplayImage = findViewById(R.id.btn_display_image);
            btnDisplayImage.setOnClickListener(view -> {
                Bitmap bitmap = BitmapFactory.decodeByteArray(IMAGE_BYTES, 0, IMAGE_BYTES.length);
                ImageView ivImage = findViewById(R.id.iv_image);
                ivImage.setImageBitmap(bitmap);
            });
        }
    }

    @Override
    protected void onPause() {
        executorService.shutdown();
        super.onPause();
    }

    public static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }
}