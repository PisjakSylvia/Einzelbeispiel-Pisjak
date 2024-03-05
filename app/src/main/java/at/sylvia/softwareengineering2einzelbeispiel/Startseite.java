package at.sylvia.softwareengineering2einzelbeispiel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Startseite extends AppCompatActivity {
    //Initialisierungen von Button, Eingabefeld und Serverantwort
    private EditText eingabeMatNR;
    private Button sendButton;
    private TextView serverAntwort;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startseite);

        eingabeMatNR = findViewById(R.id.eingabeMatNR);
        sendButton = findViewById(R.id.sendButton);
        //serverAntwort = findViewById(R.id.);
    }
}