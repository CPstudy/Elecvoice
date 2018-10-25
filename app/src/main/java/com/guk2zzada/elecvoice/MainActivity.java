package com.guk2zzada.elecvoice;

import android.Manifest;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    Button btnPlay;
    Button btnMenu;
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

    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> list2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        layGoogle = findViewById(R.id.layGoogle);
        laySettings = findViewById(R.id.laySettings);
        btnPlay = findViewById(R.id.btnPlay);
        btnMenu = findViewById(R.id.btnMenu);
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
                //Sandwich.makeText(getApplicationContext(), edtText.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuAsync ma = new MenuAsync();
                ma.execute();
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

    public String getMenu() {
        try {
            Document doc = Jsoup.connect("http://m.sungkyul.ac.kr/life/sub03_01.do").get();
            Elements contents = doc.select("div#rstr_stu table.sk_table tbody tr div");

            int count = 0;

            for(Element element : contents) {
                list.add(element.text());
                count++;
            }

            contents = doc.select("div#rstr_sam table.sk_table tbody tr div");
            for(Element element : contents) {
                list2.add(element.text());
                count++;
            }

            String[] weeks = {"일", "월", "화", "수", "목", "금", "토"};
            Calendar cal = Calendar.getInstance();
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            int n1 = (dayOfWeek - 2) * 8;
            int n2 = (dayOfWeek - 2) * 4;

            String[] array = {list.get(n1), list.get(n1 + 2), list.get(n1 + 4), list.get(n1 + 6), list2.get(n2), list2.get(n2 + 2)};
            String result = "오늘 학식은 ";
            for(int i = 0; i < array.length; i++) {
                String s;
                try {
                    s = array[i].split("/")[0];
                    int a = s.indexOf('&');
                    if(a >= 0) {
                        System.out.println(s.substring(a - 1, a));
                        s = s.replace("&", getJongsung(s.substring(a - 1, a), "과 ", "와 "));
                        s = s.replace("(뚝)", "뚝배기");
                    }
                } catch (Exception e) {
                    s = "";
                }
                result += s;

                if(i != array.length - 1) {
                    result += ", ";
                }
            }
            result += "입니다.";

            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public String getJongsung(String s, String firstValue, String secondValue) {
        char c = s.charAt(s.length() - 1);

        if(c < 0xAC00 || c > 0xD7A3) {
            return s;
        }

        String selectedValue = (c - 0xAC00) % 28 > 0 ? firstValue : secondValue;

        return selectedValue;
    }

    class MenuAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            edtText.setText(s);
        }

        @Override
        protected String doInBackground(String... strings) {
            return getMenu();
        }
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
