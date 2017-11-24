package com.vany.tulin.controller;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vany.tulin.R;
import com.vany.tulin.common.Constant;
import com.vany.tulin.dao.GreenDaoUtil;
import com.vany.tulin.dao.WordDaoUtil;
import com.vany.tulin.dto.App;
import com.vany.tulin.dto.Sentence;
import com.vany.tulin.dto.TulinTextResult;
import com.vany.tulin.dto.Word;
import com.vany.tulin.interf.ASRserviceResult;
import com.vany.tulin.interf.TulinReturnResultListener;
import com.vany.tulin.service.ASRservice;
import com.vany.tulin.service.TTSservice;
import com.vany.tulin.service.TulinService;
import com.vany.tulin.utils.AudioUtil;
import com.vany.tulin.utils.ConvertUtil;
import com.vany.tulin.utils.FileUtil;
import com.vany.tulin.utils.NetUtil;
import com.vany.tulin.utils.TulinParserUtil;
import com.vany.tulin.utils.XmlParserUtil;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.vany.tulin.R.layout.word;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ASRservice asrService;  //语音识别服务
    private TTSservice ttsService;  //语音合成服务
    private TulinService tulinService;  //图灵机器人服务

    private LinearLayout contentPnl;    //内容面板
    private Button mainBtn;     //主要按钮
    private RelativeLayout robotBtn;    //机器人按钮
    private RelativeLayout historyBtn;  //历史按钮
    private TextView robotbg;   //机器人背景设置
    private TextView historybg; //历史背景设置
    private TextView robotTV;
    private TextView historyTV;
    private boolean flag = false;       //控制显示机器人还是历史
    //单词布局中变量
    private TextView wordtv;
    private TextView enpstv;
    private ImageView enproiv;
    private TextView uspstv;
    private ImageView usproiv;
    private TextView meaningtv;     //单词布局中意义
    private View v; //单词布局
    private ImageButton labaIB;
    private TextView dateTV;
    private ImageView picIV;
    private TextView sentenceTV;
    private TextView meaningTV;     //每日一句中的意义

    private int startX;     //保存每日一句中用户手指按下时的X坐标
    private SimpleDateFormat sf;

    private EditText searchinputET;     //单词搜素输入框
    private boolean isShow = true;     //控制输入框是否显示

    private ObjectAnimator objectAnimator;          //mainBtn按钮动画,该动画相当于进度条
    private AlertDialog dialog;     //没有网络提示框

    private long exitTime;      //记录退出时间，连续按两次返回键2s内则退出

    private List<App> appList = new ArrayList<>();  //封装了手机已安装的所有应用信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dealwithnetwork();      //判断网络是否可用，如果不可用则弹出提示框
        compatible(this, 1);    //判断安卓系统版本，根据不同版本初始化
    }

    @Override
    protected void onResume() {
        super.onResume();
        dealwithnetwork();
    }

    private void dealwithnetwork() {
        if (!NetUtil.isNetAvailable(this)) {
            if (dialog == null) {
                dialog = new AlertDialog.Builder(MainActivity.this).setTitle("温馨提示")
                        .setMessage("世界上最遥远的距离莫过于网络中断啦！")
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                System.out.println("对话框取消啦");
                            }
                        })
                        .show();

            }
        }
    }

    private void init() {
        initView();
        initData(); //初始化单词数据库
    }

    private void initData() {
        WordDaoUtil.getSingleTon().setWordDao(GreenDaoUtil.getDaoSession(this).getWordDao());     //初始化wordDao
        //获取手机上所有应用信息
        PackageManager packageManager = getPackageManager();
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo packageInfo : installedPackages
                ) {
            String appName = packageInfo.applicationInfo.loadLabel(packageManager).toString();
            String packageName = packageInfo.packageName;
            System.out.println("包名：" + packageName + "  应用名称：" + appName);
            appList.add(new App(appName, packageName));
        }

    }

    private void initView() {
        //初始化每日一句布局
        labaIB = (ImageButton) findViewById(R.id.laba_IB);
        dateTV = (TextView) findViewById(R.id.date_TV);
        picIV = (ImageView) findViewById(R.id.pic_IV);
        sentenceTV = (TextView) findViewById(R.id.sentence_TV);
        meaningTV = (TextView) findViewById(R.id.meaning_TV);
        //如果已经保存了今天的每天一句的相关内容，则从SD卡中读出来显示就可以了，否则需要再从网络上加载
        sf = new SimpleDateFormat("yyyy-MM-dd");
        if (FileUtil.isExist() && FileUtil.isSentenceExist(this)) {
            System.out.println("这是从本地上加载的");
            final Sentence st = FileUtil.getSentence(this);
            labaIB.setOnClickListener(new View.OnClickListener() {  //句子发音监听
                @Override
                public void onClick(View v) {
                    labaIB.setBackgroundResource(R.mipmap.voice_open32);
                    AudioUtil.open(st.getPronunce());
                }
            });
            dateTV.setText(st.getDate());
            sentenceTV.setText(st.getSentence());
            meaningTV.setText(st.getMeaning());
            picIV.setImageBitmap(FileUtil.readBitmap());
        } else {
            System.out.println("这是从网络上加载的");
            new SentenceTask().execute(sf.format(new Date()));   //加载每日一句显示
        }


        contentPnl = (LinearLayout) findViewById(R.id.contentPnl);
        mainBtn = (Button) findViewById(R.id.mainBtn);
        robotBtn = (RelativeLayout) findViewById(R.id.robotBtn);
        historyBtn = (RelativeLayout) findViewById(R.id.historyBtn);
        robotbg = (TextView) findViewById(R.id.robotbg_TV);
        historybg = (TextView) findViewById(R.id.historybg_TV);
        robotTV = (TextView) findViewById(R.id.robot_TV);   //机器人文本
        historyTV = (TextView) findViewById(R.id.history_TV);

        mainBtn.setOnClickListener(this);
        robotBtn.setOnClickListener(this);
        historyBtn.setOnClickListener(this);

        searchinputET = (EditText) findViewById(R.id.searchinput_ET);
        ImageView searchIV = (ImageView) findViewById(R.id.search_IV);
        searchIV.setOnClickListener(this);      //单词搜素框点击事件监听
        searchIV.setOnLongClickListener(new View.OnLongClickListener() {//单词搜素框长按事件监听
            @Override
            public boolean onLongClick(View v) {
                if (isShow) {
                    showSearchInput();
                    isShow = false;
                    return true;
                }
                hideSearchInput();
                isShow = true;
                return true;
            }
        });
        ImageView textchatIV = (ImageView) findViewById(R.id.textchat_IV);
        textchatIV.setOnClickListener(this);
        textchatIV.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (isShow) {
                    showSearchInput();
                    isShow = false;
                    return true;
                }
                hideSearchInput();
                isShow = true;
                return true;
            }
        });

        //切换每日一句监听
        LinearLayout sentencecontentLL = (LinearLayout) findViewById(R.id.sentencecontentPanel);
        sentencecontentLL.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = (int) event.getX();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int endX = (int) event.getX();
                        if (endX - startX > 100) {  //加载下一天的数据
                            if (!isNetAvailable()) break;  //如果网络不可用返回
                            nextDay();
                        } else if (endX - startX < -100) {  //加载前一天的数据
                            if (!isNetAvailable()) break;  //如果网络不可用返回
                            preDay();
                        }
                        break;
                }
                return true;
            }
        });

        //初始化单词布局
        v = getLayoutInflater().inflate(word, null);
        wordtv = (TextView) v.findViewById(R.id.word_TV);
        enpstv = (TextView) v.findViewById(R.id.enps_TV);
        enproiv = (ImageView) v.findViewById(R.id.enpro_IV);
        uspstv = (TextView) v.findViewById(R.id.usps_TV);
        usproiv = (ImageView) v.findViewById(R.id.uspro_IV);
        meaningtv = (TextView) v.findViewById(R.id.meaning_TV);

        //初始化服务
        asrService = new ASRservice(this);
        tulinService = new TulinService(this);  //实例化图灵机器人服务
        ttsService = new TTSservice(this);      //实例化语音合成服务
        //申请权限,当时是为了兼容6.0版本以上
//        setBrightnessMode(this, 1);
//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_SETTINGS)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_SETTINGS},
//                    100);
//        } else {
//            asrService = new ASRService2(this);  //实例化语音识别服务
//            ttsService = new TTSService2(this);      //实例化语音合成服务
//            tulinService = new TulinService(this);  //实例化图灵机器人服务
//            System.out.println("申请权限成功啦*********************");
//        }

//        if (ContextCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_SETTINGS)
//                != PackageManager.PERMISSION_GRANTED) {
//
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.WRITE_SETTINGS)) {
//                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
//                intent.setData(Uri.parse("package:" + this.getPackageName()));
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intent);
//            } else {
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.WRITE_SETTINGS},
//                        11);
//            }
//        } else {
//            asrService = new ASRService2(this);  //实例化语音识别服务
//            ttsService = new TTSService2(this);      //实例化语音合成服务
//            tulinService = new TulinService(this);  //实例化图灵机器人服务
//            System.out.println("安卓版本di于6.0@@@@@@@@@@@@");
//        }
    }

    private boolean isNetAvailable() {
        if (!NetUtil.isNetAvailable(this)) {
            Toast.makeText(getApplicationContext(), "网络连接不可用！", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void animformainBtn() {
        objectAnimator = ObjectAnimator.ofFloat(mainBtn, "rotation", 0f, -90f, 0f, 90f, 0f);
        objectAnimator.setDuration(2000);
        objectAnimator.setRepeatMode(ObjectAnimator.REVERSE);   //反转模式
        objectAnimator.setRepeatCount(-1);
    }

    private void showSearchInput() {
        searchinputET.setVisibility(View.VISIBLE);  //显示单词搜素输入框
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(searchinputET, "scaleX", 0, 1);
        objectAnimator.setDuration(1000);
        objectAnimator.start();
    }

    private void hideSearchInput() {
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0f, 1.0f, 0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleAnimation.setDuration(2000);
        searchinputET.startAnimation(scaleAnimation);
        scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchinputET.setVisibility(View.GONE);  //显示单词搜素输入框
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    private void preDay() {
        String date = dateTV.getText() + "";
        try {
            Date d = sf.parse(date);
            new SentenceTask().execute(sf.format(new Date(d.getTime() - 1 * 24 * 60 * 60 * 1000)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void nextDay() {
        String date = dateTV.getText() + "";
        if (date.equals(sf.format(new Date()))) return;
        try {
            Date d = sf.parse(date);
            new SentenceTask().execute(sf.format(new Date(d.getTime() + 1 * 24 * 60 * 60 * 1000)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * 单词异步内部类
     */
    class WordTask extends AsyncTask<String, Void, Word> {
        @Override
        protected Word doInBackground(final String... params) {
            //首先判断用户请求的是否为中文
            if (ConvertUtil.isChinese(params[0])) { //如果用户想要中文转英文，则让图灵机器人处理
                try {
//                    String res = NetUtil.postYiYuan(params[0]);
//                    String result = NetUtil.getDataFromYiYuanAPI(res);
//                    Word w = new Word();
//                    w.setWord(params[0]);
//                    w.setMeaning(result);
//                    return w;
                    //易源API出现了问题了，所以换回金山词霸查中文单词
                    String jsonresult = NetUtil.get(Constant.CB_CHINESEWORD_URL + params[0]);
                    return XmlParserUtil.chineseWordParser(jsonresult);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else { //如果用户想英文转中文，则让金山词霸处理
                try {
                    //首先从本地数据库中搜索该单词是否存在
                    Word wordName = WordDaoUtil.getSingleTon().getWordByWordName(params[0]);
                    if (wordName != null) {
                        System.out.println("这是从数据库中查询的####");
                        return wordName;
                    }
                    //如果本地数据库中不存在就从网络中获取数据
                    System.out.println("这是从网络中查询的******************");
                    String result = NetUtil.get(Constant.CB_WORD_URL + params[0]);
                    return XmlParserUtil.wordParser(result);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return new Word("dimness"); //当用户说话不清楚或没说话的时候标识
        }

        @Override
        protected void onPostExecute(Word w) {
            if (w == null) {
                Toast.makeText(getApplicationContext(), "目前只支持单词翻译哦！", Toast.LENGTH_SHORT).show();
                if (objectAnimator != null)
                    objectAnimator.end();   //结束动画
                return;
            }
            if (w.getWord().equals("dimness")) {//当用户说话不清楚或没说话的时候，执行这句几率很小，除非出错了
                Toast.makeText(getApplicationContext(), "sorry，我没有听清楚哦！", Toast.LENGTH_SHORT).show();
                if (objectAnimator != null)
                    objectAnimator.end();   //结束动画
                return;
            }
            contentPnlAddView(w);
            //将单词保存到数据库中，以便以后查看,这里只保存 英文-->中文
            //判断如果不是中文，并且意义不为空，并且id为空；保证id为空是为了保证在数据库中是不存在的，保证是从网络中获取的
            if (!ConvertUtil.isChinese(w.getWord()) && !w.getMeaning().equals("") && w.getId() == null) {
                WordDaoUtil.getSingleTon().insertWord(w);
            }
            if (objectAnimator != null) {
                objectAnimator.end();   //结束动画
            }
        }
    }

    private void contentPnlAddView(final Word w) {
        wordtv.setText(w.getWord());
        enpstv.setText(w.getEnps());
        if (w.getEnps() == null) {
            enpstv.setVisibility(View.GONE);
            enproiv.setVisibility(View.GONE);
        } else {
            enpstv.setVisibility(View.VISIBLE);
            enproiv.setVisibility(View.VISIBLE);
        }
        enproiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enproiv.setBackgroundResource(R.mipmap.voice_open32);
                AudioUtil.open(w.getEnpronunce());
                Toast.makeText(getApplicationContext(), "英式发音", Toast.LENGTH_SHORT).show();
            }
        });
        uspstv.setText(w.getUsps());
        if (w.getUsps() == null) {
            uspstv.setVisibility(View.GONE);    //隐藏
            usproiv.setVisibility(View.GONE);
        } else {
            uspstv.setVisibility(View.VISIBLE);    //隐藏
            usproiv.setVisibility(View.VISIBLE);
        }
        usproiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usproiv.setBackgroundResource(R.mipmap.voice_open32);
                AudioUtil.open(w.getUspronunce());
                Toast.makeText(getApplicationContext(), "美式发音", Toast.LENGTH_SHORT).show();
            }
        });
        meaningtv.setText(w.getMeaning() == null || w.getMeaning().equals("") ? "无意义！" : w.getMeaning());
        contentPnl.removeAllViews();    //先清空再添加
        enproiv.setBackgroundResource(R.mipmap.voice32);
        usproiv.setBackgroundResource(R.mipmap.voice32);
        contentPnl.addView(v);
    }

    /**
     * 句子异步内部类
     */
    class SentenceTask extends AsyncTask<String, Void, Sentence> {
        @Override
        protected Sentence doInBackground(String... params) {
            String senturl = Constant.CB_DAILYSENTANCE_URL + params[0];
            try {
                String result = NetUtil.get(senturl);
                Sentence sentence = XmlParserUtil.sentenceParser(result);
                return sentence;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(final Sentence sentence) {
            if (sentence == null) return;
            NetUtil.getPic(sentence.getPic(), new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    System.out.println("onFailure");
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    final Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            picIV.setImageBitmap(bitmap);
                        }
                    });
                    FileUtil.saveBitmap(bitmap);//保存图片到SD卡
                }
            });
            labaIB.setOnClickListener(new View.OnClickListener() {  //句子发音监听
                @Override
                public void onClick(View v) {
                    labaIB.setBackgroundResource(R.mipmap.voice_open32);
                    AudioUtil.open(sentence.getPronunce());
                }
            });
            dateTV.setText(sentence.getDate());
            sentenceTV.setText(sentence.getSentence());
            meaningTV.setText(sentence.getMeaning());
            labaIB.setBackgroundResource(R.mipmap.voice32);

            FileUtil.saveSentence(MainActivity.this, sentence);  //第一次将网络上数据保存到SD卡中
        }
    }


    private String dealwithTulinResult(final String json, final String result) {
        final TulinTextResult tulinTextResult = TulinParserUtil.jsonParser(json);
        if (tulinTextResult == null) return "";
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View vinf = getLayoutInflater().inflate(R.layout.tulin2, null);
                TextView chatTimeTV = (TextView) vinf.findViewById(R.id.chatTimeTV);
                TextView robotTextTV = (TextView) vinf.findViewById(R.id.robotTextTV);
                TextView roboturlTV = (TextView) vinf.findViewById(R.id.roboturlTV);
                chatTimeTV.setText(tulinTextResult.getChatTime());
                roboturlTV.setText(tulinTextResult.getUrl());
                robotTextTV.setText(tulinTextResult.getRobotText());
                if (tulinTextResult.getUrl() != null) {     //只有当URL不为空的时候才设置监听，否则只要点击下面都会进行页面跳转
                    roboturlTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), "加载中。。", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, TBSActivity.class);
                            intent.putExtra("url", tulinTextResult.getUrl());
                            startActivity(intent);
                        }
                    });
                }
                contentPnl.removeAllViews();
                contentPnl.addView(vinf);
                //判断如果有菜谱信息，则开启新activity
                if (tulinTextResult.getCookMenuList() != null && tulinTextResult.getCookMenuList().size() > 0) {
                    Intent intent = new Intent(MainActivity.this, CookActivity.class);
                    intent.putParcelableArrayListExtra("cookDatas", tulinTextResult.getCookMenuList());
                    startActivity(intent);
                }
                //判断如果有新闻信息，则开启新activity
                else if (tulinTextResult.getNewsArrayList() != null && tulinTextResult.getNewsArrayList().size() > 0) {
                    Intent intent = new Intent(MainActivity.this, NewsActivity.class);
                    intent.putParcelableArrayListExtra("newsDatas", tulinTextResult.getNewsArrayList());
                    startActivity(intent);
                }
            }
        });
        return tulinTextResult.getRobotText();
    }

    /**
     * 获取两个字符串间最大相同子串，但效率不高，因此不用
     *
     * @param v
     */
//    public String getMaxSubString(String s1,String s2)
//    {
//        String max = "",min = "";
//        max = (s1.length()>s2.length())?s1: s2;
//        min = (max==s1)?s2: s1;
//        for(int x=0; x<min.length(); x++)
//        {
//            for(int y=0,z=min.length()-x; z!=min.length()+1; y++,z++)
//            {
//                String temp = min.substring(y,z);
//                if(max.contains(temp))//if(s1.indexOf(temp)!=-1)
//                    return temp;
//            }
//        }
//        return "";
//    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.mainBtn:
                if (!isNetAvailable()) return;  //如果网络不可用返回
                Toast.makeText(getApplicationContext(), "识别中。。。", Toast.LENGTH_SHORT).show();
                asrService.startASR();  //开始识别
                asrService.getRecognizerResult(new ASRserviceResult() {
                    @Override
                    public void getRecResult(String result) {
                        result = ConvertUtil.convertUper2Lower(result); //将所有大写字母转成小写
                        if (result.contains("9")) {
                            result = ConvertUtil.convert9toz(result);
                        }
                        new WordTask().execute(result);
                    }
                });
                if (objectAnimator != null) {
                    objectAnimator.cancel();
                }
                animformainBtn();   //执行按钮动画
                objectAnimator.start(); //开启按钮动画
                break;
            case R.id.robotBtn:
                if (flag) {
                    historybg.setBackgroundResource(R.mipmap.history32_normal);
                    historyTV.setTextColor(Color.GRAY); //字体恢复正常
                    robotbg.setBackgroundResource(R.mipmap.robot32_gold);
//                    robotTV.setTextColor(getResources().getColor(R.color.colorGold));
                    robotTV.setTextColor(ContextCompat.getColor(this, R.color.colorGold));//替代上面过时方法
                    flag = false;
                }
                if (!isNetAvailable()) return;  //如果网络不可用返回
                Toast.makeText(getApplicationContext(), "请说话。。", Toast.LENGTH_SHORT).show();
                asrService.startASR();  //开始识别
                asrService.getRecognizerResult(new ASRserviceResult() {
                    @Override
                    public void getRecResult(final String result) {
                        Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
                        //判断用户是否要打开其他应用
                        if (result.contains("打开") || result.contains("启动")) {//如果用户有打开或启动的意图
                            String subresult = result.substring(2);
                            //判断用户想打开百度一下，如果是，直接打开百度一下页面
                            if (result.contains("百度一下") ||
                                    result.contains("百度") ||
                                    result.contains("百度页面") ||
                                    result.contains("度娘")) {
                                Intent intent = new Intent(MainActivity.this, TBSActivity.class);
                                intent.putExtra("url", Constant.BDYXURL);
                                startActivity(intent);
                                return;
                            }
                            //首先第一轮是精确匹配
                            for (App app : appList) {
                                if (subresult.equalsIgnoreCase(app.getAppName()))   //这种情况是精确打开APP，不过要求用户要说出完全的应用名称才可以) //这种情况是用户没有把应用名说完全的情况
                                {//如果在应用列表中找到该应用，则启动该应用
                                    Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(app.getPackageName());
                                    if (launchIntentForPackage == null) {
                                        ttsService.speak("很抱歉，暂时无法帮您打开！");
                                        return;
                                    }
                                    launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ttsService.speak(app.getAppName() + "正在启动");
                                    startActivity(launchIntentForPackage);
                                    return;
                                }
                            }
                            //然后模糊匹配
                            for (App app : appList) {
                                if (result.contains(app.getAppName()))      //这种情况是模糊匹配的结果) //这种情况是用户没有把应用名说完全的情况
                                {//如果在应用列表中找到该应用，则启动该应用
                                    Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(app.getPackageName());
                                    if (launchIntentForPackage == null) {
                                        ttsService.speak("很抱歉，暂时无法帮您打开！");
                                        return;
                                    }
                                    launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ttsService.speak(app.getAppName() + "正在启动");
                                    startActivity(launchIntentForPackage);
                                    return;
                                }
                            }
                            //这种情况是用户没有把应用名说完全的情况
                            for (App app : appList) {
                                if ((app.getAppName()).contains(subresult)) {//如果在应用列表中找到该应用，则启动该应用
                                    Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage(app.getPackageName());
                                    if (launchIntentForPackage == null) {
                                        ttsService.speak("很抱歉，暂时无法帮您打开！");
                                        return;
                                    }
                                    launchIntentForPackage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    ttsService.speak(app.getAppName() + "正在启动");
                                    startActivity(launchIntentForPackage);
                                    return;
                                }
                            }
                            ttsService.speak("很抱歉，暂时无法帮您打开！");
                            return;
                        } else if (result.contains("搜索") || result.contains("百度")) {//判断用户是否想使用百度搜素功能
                            String subresult = result.substring(2);
                            Intent intent = new Intent(MainActivity.this, TBSActivity.class);
                            intent.putExtra("url", Constant.BDSEARCHURL + subresult);
                            startActivity(intent);
                            return;
                        }
                        tulinService.sendRequest(result);
                        tulinService.setOnTulinReturnResultListener(new TulinReturnResultListener() {
                            @Override
                            public void onSuccess(final String json) {
                                System.out.println(json);
                                ttsService.speak(dealwithTulinResult(json, result));
                            }

                            @Override
                            public void onFail(int i, String s) {
                            }
                        });
                    }
                });

                break;
            case R.id.historyBtn:
                if (!flag) {
                    robotbg.setBackgroundResource(R.mipmap.robot32_normal);
                    robotTV.setTextColor(Color.GRAY);   //字体恢复正常
                    historybg.setBackgroundResource(R.mipmap.history32_gold);
//                    historyTV.setTextColor(getResources().getColor(R.color.colorGold));
                    historyTV.setTextColor(ContextCompat.getColor(this, R.color.colorGold));//替代上面过时方法
                    flag = true;
                }
                //模拟一些单词数据
//                GreenDaoUtil.getDb(this).execSQL("CREATE TABLE WORD");        //新建表
//                GreenDaoUtil.getDb(this).execSQL("DROP TABLE WORD");        //删除表
//                GreenDaoUtil.getDb(this).execSQL("DELETE FROM WORD");        //清空表
                ArrayList<Word> wordArrayList = (ArrayList<Word>) WordDaoUtil.getSingleTon().getTwentyDatas(0);
                System.out.println(wordArrayList.size() + "#######################");
                Intent intent = new Intent(MainActivity.this, WordActivity.class);
                intent.putParcelableArrayListExtra("wordDatas", wordArrayList);
                startActivity(intent);

                break;
            case R.id.search_IV:
                if (!isNetAvailable()) return;  //如果网络不可用返回
                String s = searchinputET.getText().toString();
                if (s.equals("")) return;
                new WordTask().execute(s);      //异步查询单词
                searchinputET.setText("");      //清空查词输入框，否则会不断发送请求查询该词
                break;
            case R.id.textchat_IV:
                if (!isNetAvailable()) return;  //如果网络不可用返回
                final String text = searchinputET.getText().toString();
                if (text.equals("")) return;
                if (text.contains("搜索") || text.contains("百度")) {//判断用户是否想使用百度搜素功能
                    String subresult = text.substring(2);
                    Intent tbsIntent = new Intent(MainActivity.this, TBSActivity.class);
                    tbsIntent.putExtra("url", Constant.BDSEARCHURL + subresult);
                    startActivity(tbsIntent);
                    searchinputET.setText("");      //清空查词输入框，否则会不断发送请求查询该词
                    return;
                }
                tulinService.sendRequest(text);
                tulinService.setOnTulinReturnResultListener(new TulinReturnResultListener() {
                    @Override
                    public void onSuccess(final String json) {
                        System.out.println(json);
                        dealwithTulinResult(json, text);
                    }

                    @Override
                    public void onFail(int i, String s) {
                    }
                });
                searchinputET.setText("");      //清空查词输入框，否则会不断发送请求查询该词
                break;
        }
    }

    /**
     * 兼容安卓6.0以上
     *
     * @param context
     * @param mode
     */
    private void compatible(Context context, int mode) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//首先判断是否为6.0以上，如果是，执行以下业务逻辑代码
                if (FileUtil.executeOnlyOnce(this)) {   //第一次执行肯定为true，之后都会为false
                    Toast.makeText(getApplicationContext(), "请打开应用所需的所有权限哦！", Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    intent1.setData(Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent1, mode);
                    FileUtil.executeOnlyOnceChangeFalse(this);
                } else {
                    init(); //第一次用户安装完成后获取权限之后就可以直接初始化了
                }
            } else {//如果是6.0以下版本，直接初始化
                init();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            init(); //在安卓6.0以上第一次初始化，获取权限之后调用，只会调用一次
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {

            if ((System.currentTimeMillis() - exitTime) > 2000)  //System.currentTimeMillis()无论何时调用，肯定大于2000
            {
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
