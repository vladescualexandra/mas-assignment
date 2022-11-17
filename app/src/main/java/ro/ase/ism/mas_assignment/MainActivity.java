package ro.ase.ism.mas_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private final String SERVER = "https://student.ism.ase.ro/access/content/group/Y2S1_MAS/AndroidEncrypt/";
    private final String AES_IV_encrypted_with_RSA_PrivateKey = "AES_IV_encrypted_with_RSA_PrivateKey";
    private final String AES_Key_encrypted_with_RSA_PrivateKey = "AES_Key_encrypted_with_RSA_PrivateKey";
    private final String Image_encrypted_with_AES = "Image_encrypted_with_AES";
    private final String RSA_PublicKey = "RSA_PublicKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    private void readFileContent(String path) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open(path)));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                builder.append(mLine);            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}