package com.moonside.rcc;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.ArrayList;

public class VoiceCtrl extends AppCompatActivity {
    public ImageButton BT_Listen;
    public TextView TX_Voice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_ctrl);
        ImageButton BT_Listen = (ImageButton) findViewById(R.id.Bt_Voice);

        SpeechUtility.createUtility(this, SpeechConstant.APPID +Constants.APPID + SpeechConstant.FORCE_LOGIN +"=true");
        BT_Listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    initSpeech(VoiceCtrl.this);
            }
        });

    }

    public void initSpeech(final Context context) {
        //1.创建RecognizerDialog对象
        RecognizerDialog mDialog = new RecognizerDialog(context, null);
        //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
        //3.设置回调接口
        mDialog.setListener(new RecognizerDialogListener() {
            @Override
            public void onResult(RecognizerResult recognizerResult, boolean isLast) {
                if (!isLast) {
                    String result = parseVoice(recognizerResult.getResultString());
                    TextView TX_Voice = (TextView) findViewById(R.id.TX_voice);
                    TX_Voice.setText(result);
                    if (result.equals("前进"))
                        Home.BTThread.Forward();
                    else if(result.equals("后退"))
                        Home.BTThread.Back();
                    else if (result.equals("向左"))
                        Home.BTThread.TurnLeft();
                    else if(result.equals("向右"))
                        Home.BTThread.TurnRight();
                    else if(result.equals("停止"))
                        Home.BTThread.Stop();
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }
        });
        //4.显示dialog，接收语音输入
        mDialog.show();
    }

    public String parseVoice(String resultString) {
        Gson gson = new Gson();
        Voice voiceBean = gson.fromJson(resultString, Voice.class);

        StringBuffer sb = new StringBuffer();
        ArrayList<Voice.WSBean> ws = voiceBean.ws;
        for (Voice.WSBean wsBean : ws) {
            String word = wsBean.cw.get(0).w;
            sb.append(word);
        }
        return sb.toString();
    }

    public class Voice {

        public ArrayList<WSBean> ws;

        public class WSBean {
            public ArrayList<CWBean> cw;
        }

        public class CWBean {
            public String w;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Home.BTThread.Stop();
    }
}
