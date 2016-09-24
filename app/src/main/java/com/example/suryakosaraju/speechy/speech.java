package com.example.suryakosaraju.speechy;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class speech extends AppCompatActivity {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1001;
    private EditText Hint;
    private ListView lvmatch;
    private Spinner msmatch;
    private ImageButton btnspeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        Hint = (EditText) findViewById(R.id.text);
        lvmatch = (ListView) findViewById(R.id.matchtext);
        msmatch = (Spinner) findViewById(R.id.matches);
        btnspeech = (ImageButton) findViewById(R.id.btnspeak);

    }

    public void SpeechCheck() {
        PackageManager pm = getPackageManager();
        List<ResolveInfo> task = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        if (task.size() == 0) {
            btnspeech.setEnabled(false);
            Toast.makeText(this, "Device not supported...", Toast.LENGTH_SHORT).show();
        }
    }

    public void speech1(View v) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, Hint.getText().toString());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH);

        if (msmatch.getSelectedItemPosition() == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select no of matches from Spinner", Toast.LENGTH_SHORT).show();
            return;
        }
        int noofmatches = Integer.parseInt(msmatch.getSelectedItem().toString());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, noofmatches);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);

    }


    protected void onActivityResult(int reqtcode, int restcode, Intent text) {
        if (restcode == RESULT_OK) {
            ArrayList<String> textmatchlist = text.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!textmatchlist.isEmpty()) {

                if (textmatchlist.get(0).contains("search")) {
                    String search = textmatchlist.get(0).replace("search", " ");
                    Intent search1 = new Intent(Intent.ACTION_WEB_SEARCH);
                    search1.putExtra(SearchManager.QUERY, search);
                    startActivity(search1);
                } else {
                    lvmatch.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, textmatchlist));
                }
            }

        } else if (restcode == RecognizerIntent.RESULT_AUDIO_ERROR) {
            showToast("Audio Error");
        } else if (restcode == RecognizerIntent.RESULT_CLIENT_ERROR) {
            showToast("Client Error");
        } else if (restcode == RecognizerIntent.RESULT_NETWORK_ERROR) {
            showToast("Network Errror");
        } else if (restcode == RecognizerIntent.RESULT_NO_MATCH) {
            showToast("No Match");
        } else if (restcode == RecognizerIntent.RESULT_SERVER_ERROR) {
            showToast("Server Error");
        }

        super.onActivityResult(reqtcode, restcode, text);


    }

    void showToast(String mesg) {
        Toast.makeText(this, mesg, Toast.LENGTH_SHORT).show();
    }
}

