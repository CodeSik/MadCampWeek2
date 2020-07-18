package com.example.project2;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.example.project2.ui.phonebook.PhoneBookViewModel;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class JsonTaskGet extends AsyncTask<String, String, String> {

    private PhoneBookViewModel phoneBookViewModel;

    @Override
    protected String doInBackground(String... urls) {

        try {

            HttpURLConnection con = null;
            BufferedReader reader = null;

            try{
                URL url = new URL(urls[0]);
                //연결을 함
                con = (HttpURLConnection) url.openConnection();


                con.connect();

                InputStream stream = con.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                //실제 데이터를 받는곳
                StringBuffer buffer = new StringBuffer();
                //line별 스트링을 받기 위한 temp 변수
                String line = "";
                //아래라인은 실제 reader에서 데이터를 가져오는 부분이다. 즉 node.js서버로부터 데이터를 가져온다.
                while((line = reader.readLine()) != null){
                    buffer.append(line);
                }
                //다 가져오면 String 형변환을 수행한다. 이유는 protected String doInBackground(String… urls) 니까
                return buffer.toString();
                //아래는 예외처리 부분이다.




            } catch (MalformedURLException e){

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();

            } finally {

                if(con != null){

                    con.disconnect();

                }

                try {

                    if(reader != null){

                        reader.close();//버퍼를 닫아줌

                    }

                } catch (IOException e) {

                    e.printStackTrace();

                }

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return null;

    }



    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d("printget",result);
    }

}