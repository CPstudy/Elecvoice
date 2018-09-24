package com.guk2zzada.elecvoice;

import android.Manifest;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.guk2zzada.sandwich.Sandwich;

import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Button btnPlay;
    EditText edtText;
    TextView txtSpeed, txtPitch;
    SeekBar skbSpeed, skbPitch;

    View layGoogle;
    FrameLayout laySettings;
    RadioGroup rdoGroupLanguage, rdoGroupFemale, rdoGroupMale;
    RadioButton rdoDefault;
    RadioButton rdoFemale1, rdoFemale2, rdoFemale3;
    RadioButton rdoMale1, rdoMale2, rdoMale3;

    TextToSpeech tts;

    final static int ENGINE_GOOGLE = 0;

    boolean isChecking = true;
    int iEngine = ENGINE_GOOGLE;
    float fSpeed = 1.0f;
    float fPitch = 1.0f;
    int clovaSpeed = 0;
    boolean boolTts = false;
    boolean boolSettings = false;
    String strVoice = "";
    Locale locale = Locale.KOREA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        layGoogle = findViewById(R.id.layGoogle);
        laySettings = findViewById(R.id.laySettings);
        btnPlay = findViewById(R.id.btnPlay);
        edtText = findViewById(R.id.edtText);
        txtSpeed = findViewById(R.id.txtSpeed);
        txtPitch = findViewById(R.id.txtPitch);
        skbSpeed = findViewById(R.id.skbSpeed);
        skbPitch = findViewById(R.id.skbPitch);
        rdoGroupLanguage = findViewById(R.id.rdoGroupLanguage);
        rdoGroupFemale = findViewById(R.id.rdoGroupFemale);
        rdoGroupMale = findViewById(R.id.rdoGroupMale);
        rdoDefault = findViewById(R.id.rdoDefault);
        rdoFemale1 = findViewById(R.id.rdoFemale1);
        rdoFemale2 = findViewById(R.id.rdoFemale2);
        rdoFemale3 = findViewById(R.id.rdoFemale3);
        rdoMale1 = findViewById(R.id.rdoMale1);
        rdoMale2 = findViewById(R.id.rdoMale2);
        rdoMale3 = findViewById(R.id.rdoMale3);

        laySettings.setVisibility(View.GONE);

        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.INTERNET}, MODE_PRIVATE);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                readText(locale, edtText.getText().toString());
                Sandwich.makeText(getApplicationContext(), edtText.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        edtText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    readText(locale, edtText.getText().toString());
                    return true;
                }
                return false;
            }
        });

        edtText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override

            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                switch (actionId) {

                    case EditorInfo.IME_ACTION_DONE:
                        readText(locale, edtText.getText().toString());
                        break;

                    default:
                        return false;

                }

                return true;

            }

        });

        skbSpeed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                fSpeed = (float)i / 10;

                txtSpeed.setText("읽기 속도 = " + fSpeed);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        skbPitch.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                fPitch = (float)progress / 10;
                txtPitch.setText("음 높이 = " + fPitch);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        rdoGroupLanguage.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.rdoKor:
                        locale = Locale.KOREA;
                        rdoGroupFemale.setVisibility(View.VISIBLE);
                        rdoGroupMale.setVisibility(View.VISIBLE);
                        break;

                    case R.id.rdoEng:
                        locale = Locale.US;
                        rdoGroupFemale.setVisibility(View.GONE);
                        rdoGroupMale.setVisibility(View.GONE);
                        break;

                    case R.id.rdoJap:
                        locale = Locale.JAPAN;
                        rdoGroupFemale.setVisibility(View.GONE);
                        rdoGroupMale.setVisibility(View.GONE);
                        break;
                }
            }
        });

        rdoGroupFemale.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    rdoGroupMale.clearCheck();
                }
                isChecking = true;

                switch(checkedId) {

                    case R.id.rdoDefault:
                        strVoice = "";
                        break;

                    case R.id.rdoFemale1:
                        strVoice = "ko-kr-x-ism#female_1-local";
                        break;

                    case R.id.rdoFemale2:
                        strVoice = "ko-kr-x-ism#female_2-local";
                        break;

                    case R.id.rdoFemale3:
                        strVoice = "ko-kr-x-ism#female_3-local";
                        break;
                }
            }
        });

        rdoGroupMale.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != -1 && isChecking) {
                    isChecking = false;
                    rdoGroupFemale.clearCheck();
                }
                isChecking = true;

                switch(checkedId) {

                    case R.id.rdoMale1:
                        strVoice = "ko-kr-x-ism#male_1-local";
                        break;

                    case R.id.rdoMale2:
                        strVoice = "ko-kr-x-ism#male_2-local";
                        break;

                    case R.id.rdoMale3:
                        strVoice = "ko-kr-x-ism#male_3-local";
                        break;
                }
            }
        });
    }

    private void readText(Locale lang, String str) {
        Voice voiceobj = new Voice(strVoice,
                Locale.KOREA, 1, 1, false, null);
        tts.setLanguage(lang);
        tts.setSpeechRate(fSpeed);
        tts.setPitch(fPitch);
        if(!strVoice.equals("") && lang == Locale.KOREA) {
            tts.setVoice(voiceobj);
        }
        boolTts = true;
        tts.speak(str, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu) ;

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings) {
            if(boolSettings) {
                laySettings.setVisibility(View.GONE);
            } else {
                laySettings.setVisibility(View.VISIBLE);
            }
            boolSettings = !boolSettings;

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        try {
            if(tts != null) {
                tts.stop();
                boolTts = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        tts = new TextToSpeech(getApplicationContext(), this, "com.google.android.tts");
    }

    @Override
    public void onInit(int status) {

    }
}
