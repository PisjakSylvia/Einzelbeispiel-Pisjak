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
                /**
                 * Die modifizierte Matrikelnummer wird in neuer String Variable gespeichert,
                 * und dann wird der Text der TextView auf diesen gesetzt.
                 */
                String modifiedMatNRText = getModifiedMatNR();
                modifiedMatNRView.setText(modifiedMatNRText);
            }
        });
    }

    /**
     * getMatBerechnung() dient zur Lösung von Aufgabe 2.1, sie stellt zuerst die
     * Verbindung zum Server her, und gibt dessen Nachricht aus, wenn es sich um
     * eine gültige Matrikelnummer handelt, ansonsten wird eine Nachricht
     * ausgegeben, dass sie nicht richtig war.
     */
    private void getMatBerechnung() {
        final String matrikelnummer = eingabeMatNR.getText().toString();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Eine Socket-Verbindung mit der Server-Domain und dem Port wird hergestellt
                    Socket socket = new Socket(SERVER_Domain, SERVER_Port);
                    //BufferedWriter wird verwendet, um ausgehende Daten zu schreiben, indem es einen OutputStreamWriter verwendet, um den OutputStream des Sockets zu wrappen
                    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                    //BufferedReader wird verwendet, um eingehende Daten zu lesen, indem es einen InputStreamReader verwendet, um den InputStream des Sockets zu wrappen
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                    out.write(matrikelnummer);      //Matrikelnummer wird an Server gesendet
                    out.newLine();      //fügt Zeilenumbruch ein -> um Nachricht an Server zu beenden
                    out.flush();        //leert den Puffer und sendet Daten an Server

                    String response = in.readLine();    //Antwort vom Server wird gelesen und gespeichert
                    /**
                     * runOnUiThread() wird verwendet, um UI-Aktualisierungen im Haupt-Thread auszuführen,
                     * da UI-Operationen nur im Haupt-Thread ausgeführt werden dürfen
                     */
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            serverAntwort.setText(response);    //gespeicherte Serverantword wir in der TextView ausgegeben
                        }
                    });
                    //Verbindungen werden geschlossen, nachdem Kommunikation fertig ist.
                    out.close();
                    in.close();
                    socket.close();
                } catch (Exception e) {
                    //wenn Fehler -> Fehlermeldung
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        /**
                         * Toast ist eine Benachrichtigung -> teilt hier einen Fehler mit.
                         * Startseite.this bezieht sich auf aktuelle Instanz von Startseite,
                         * in welcher der Toast angezeigt werden soll.
                         * Toast.length_short gibt Dauer der Ausgabe an.
                         * show() dient dazu, den Toast am Bildschirm auszugeben.
                         */
                        @Override
                        public void run() {
                            Toast.makeText(Startseite.this, "Fehler beim Senden der Daten", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        thread.start();     //Der Thread wird gestartet, um die Netzwerkanfrage auszuführe
    }
    /**
     * Diese Methode modifiziert die eingegebene Matrikelnummer gemäß den Anforderungen von 2.2.
     * Jede zweite Ziffer wird durch das entsprechende ASCII-Zeichen ersetzt.
     * @return Die modifizierte Matrikelnummer als Zeichenfolge.
     */
    private static String getModifiedMatNR() {
        String matrikelnummer = eingabeMatNR.getText().toString();
        StringBuilder result = new StringBuilder();
        if(matrikelnummer.length() < 8)return "Ungültige Eingabe!";
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
                return "Ungültige Eingabe!";
            }
        }
        return result.toString();
    }
}