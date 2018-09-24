package com.guk2zzada.elecvoice;

import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;

public class ClovaSpeech {
    ClovaSpeech() {

    }

    public boolean getVoice(String msg, String voice, int speed) {
        Log.e("Speech", "getVoice");
        if(msg.equals("")) {
            Log.e("Speech", "Stoped");
            return false;
        }
        String clientId = "qvjSMwSY_KWhk7qAzjIM";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "AOSw0X3E33";//애플리케이션 클라이언트 시크릿값";
        try {
            Log.e("Speech", "try");
            String text = URLEncoder.encode(msg, "UTF-8"); // 13자
            String apiURL = "https://openapi.naver.com/v1/voice/tts.bin";
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            // post request
            String postParams = "speaker=" + voice + "&speed=" + speed + "&text=" + text;
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(postParams);
            wr.flush();
            wr.close();
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                InputStream is = con.getInputStream();
                int read = 0;
                byte[] bytes = new byte[1024];

                File dir = new File(Environment.getExternalStorageDirectory() + "/", "ClovaTTS");

                if(!dir.exists()) {
                    dir.mkdir();
                }

                // 랜덤한 이름으로 mp3 파일 생성
                //String tempname = Long.valueOf(new Date().getTime()).toString();
                String tempname = "clovatemp";
                File f = new File(Environment.getExternalStorageDirectory() + File.separator, "ClovaTTS/" + tempname + ".mp3");
                f.createNewFile();
                OutputStream outputStream = new FileOutputStream(f);
                while ((read =is.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, read);
                }
                is.close();

                String path_to_file = Environment.getExternalStorageDirectory() + File.separator + "ClovaTTS/" + tempname + ".mp3";
                MediaPlayer mp = new MediaPlayer();
                mp.setDataSource(path_to_file);
                mp.prepare();
                mp.start();

            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                Log.e("Speech", response.toString());
            }
            Log.e("Speech", "finished");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
