package at.sylvia.softwareengineering2einzelbeispiel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Startseite extends AppCompatActivity {
    //Initialisierungen von Buttons, Eingabefelder und Serverantwort
    private static EditText eingabeMatNR;
    private Button sendButton;
    private Button modifyMatNRButton;
    private TextView serverAntwort;
    private TextView modifiedMatNRView;
    private static final String SERVER_Domain = "se2-submission.aau.at";
    private static final int SERVER_Port = 20080;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startseite);
        /**
         * Mit findViewById() findet man ein View-Element (im XML) mit entsprechender ID
         * und wenn man dies einer Variable zuweißt, dann kann diese verwendet werden,
         * um auf das EditText-Element zuzugreifen. Es wird die Referenz übergeben.
         * R.id = Resource-ID
         */
        eingabeMatNR = findViewById(R.id.eingabeMatNR);
        sendButton = findViewById(R.id.sendButton);
        serverAntwort = findViewById(R.id.serverAntwort);
        /**
         * Ein OnClickListener ist eine Schnittstelle, die es ermöglicht,
         * auf Klickereignisse auf einem View-Element zu reagieren.
         */
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   //Mit klick auf den Button wird getMatBerechnung() aufgerufen -> Aufgabe 2.1
                getMatBerechnung();
            }
        });

        modifyMatNRButton = findViewById(R.id.modifyMatNR);
        modifiedMatNRView = findViewById(R.id.modifiedMatNR);
        modifyMatNRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String modifiedMatNRText = getModifiedMatNR();
                modifiedMatNRView.setText(modifiedMatNRText);
            }
        });


    }

    private void getMatBerechnung() {
        final String matrikelnummer = eingabeMatNR.getText().toString();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(SERVER_Domain, SERVER_Port);
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.write(matrikelnummer);
                    out.newLine();
                    out.flush();

                    String response = in.readLine();

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverAntwort.setText(response);
                        }
                    });

                    out.close();
                    in.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(Startseite.this, "Fehler beim Senden der Daten", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        thread.start();
    }

    private static String getModifiedMatNR() {
        String matrikelnummer = eingabeMatNR.getText().toString();
        StringBuilder result = new StringBuilder();

        for (int i = 0; i < matrikelnummer.length(); i++) {
            char c = matrikelnummer.charAt(i);
            if (Character.isDigit(c)) {
                int digit = Character.getNumericValue(c);
                if (i % 2 == 1) { // Jede zweite Ziffer
                    char asciiChar = (char) ('a' + digit);
                    result.append(asciiChar);
                } else {
                    result.append(c);
                }
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }
}