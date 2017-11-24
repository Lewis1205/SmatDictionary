package com.vany.tulin.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import com.baidu.speech.VoiceRecognitionService;
import com.vany.tulin.interf.ASRserviceResult;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by van元 on 2016/10/26.
 */

public class ASRservice implements RecognitionListener {
    private Context context;

    //语音识别客户端
    private SpeechRecognizer speechRecognizer;
    //通过回调来获取识别的结果
    private ASRserviceResult asRserviceResult;

    public ASRservice(Context context){
        this.context =context;
        initASR();
    }
    /**
     * 开始识别
     */
    public void startASR() {
        if(speechRecognizer!=null)
            speechRecognizer.startListening(paramIntent());
    }
    /**
     * 停止识别
     */
    public void stopASR(){
        if(speechRecognizer!=null)
            speechRecognizer.stopListening();
    }
    /**
     * 语音识别所需的声音参数
     * @return
     */
    public Intent paramIntent(){
        Intent intent =new Intent();
//        intent.putExtra(Constant.EXTRA_SOUND_START, R.raw.bdspeech_recognition_start);
//        intent.putExtra(Constant.EXTRA_SOUND_END, R.raw.bdspeech_speech_end);
//        intent.putExtra(Constant.EXTRA_SOUND_SUCCESS, R.raw.bdspeech_recognition_success);
//        intent.putExtra(Constant.EXTRA_SOUND_ERROR, R.raw.bdspeech_recognition_error);
//        intent.putExtra(Constant.EXTRA_SOUND_CANCEL, R.raw.bdspeech_recognition_cancel);
        return intent;
    }

    /**
     * 初始化语音识别器
     */
    public void initASR(){
        //注册识别器
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context,
                new ComponentName(context, VoiceRecognitionService.class));
        //注册语音监听器
        speechRecognizer.setRecognitionListener(this);
    }

    /**
     * 获取返回识别后的结果
     * @param asRserviceResult
     */
    public void getRecognizerResult(ASRserviceResult asRserviceResult){
        this.asRserviceResult =asRserviceResult;
    }

    /**
     * 获取SpeechRecognizer对象
     * @return
     */
    public SpeechRecognizer getSpeechRecognizer(){
        if (speechRecognizer==null){
            initASR();
            return speechRecognizer;
        }
        return speechRecognizer;
    }

    /**
     * 释放资源
     */
    public void releaseResource(){
        if (speechRecognizer!=null){
            this.speechRecognizer.destroy();
        }
    }

    @Override//只有当此方法回调之后才能开始说话，否则会影响识别结果
    public void onReadyForSpeech(Bundle bundle) {

    }

    @Override//当用户开始说话就会回调此方法
    public void onBeginningOfSpeech() {

    }

    @Override//引擎将对每一帧语音回调一次该方法返回音量值
    public void onRmsChanged(float v) {

    }

    @Override//此方法会被回调多次，buffer是当前帧对应的PCM语音数据，拼接后可得到完整的录音数据
    public void onBufferReceived(byte[] bytes) {

    }

    @Override//当用户停止说话后，将会回调此方法
    public void onEndOfSpeech() {

    }

    @Override//识别出错
    public void onError(int i) {

    }

    @Override//识别最终结果
    public void onResults(Bundle bundle) {
        ArrayList<String> arrayList =bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        String result = Arrays.toString(arrayList.toArray(new String[arrayList.size()]));
        result = result.replaceAll("[\\[|\\]]", "");
//        Toast.makeText(context,"识别结果："+result, Toast.LENGTH_SHORT).show();
        asRserviceResult.getRecResult(result);
    }

    @Override//识别临时结果
    public void onPartialResults(Bundle bundle) {

    }

    @Override//识别事件返回
    public void onEvent(int i, Bundle bundle) {

    }
}
