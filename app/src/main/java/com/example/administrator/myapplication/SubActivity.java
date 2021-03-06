package com.example.administrator.myapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.icu.util.TimeZone;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016-07-30.
 */
public class SubActivity extends Activity {

    Uri ringtoneUri;
    int hour;
    int minute;
    int requestCode;
    int sun1, mon2, tue3, wed4, thu5, fri6, sat7;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sub_activity);

        Button timeBtn = (Button) findViewById(R.id.timeBtn);
        Button dayBtn = (Button) findViewById(R.id.dayBtn);
        Button btn = (Button) findViewById(R.id.button2);
        Button btn2 = (Button) findViewById(R. id. button3);
        Button ringBtn = (Button) findViewById(R.id. ringBtn);

        // 지난번 저장해놨던 사용자 입력값을 꺼내서 보여주기
        SharedPreferences sharedPreferences = getSharedPreferences("CodeNumber", MODE_PRIVATE);
        int requestCode1 = sharedPreferences.getInt("number",696969); // 키값으로 꺼냄, 뒤에것은 디폴트값
        requestCode = requestCode1;

        //시간 설정 버튼 처음에 보여주는 값을 현재 시간으로 설정하기
        Calendar calendar = Calendar.getInstance();
        TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
        calendar.setTimeZone(timeZone);//한국시간으로 TimeZone설정
        int hour1 = calendar.get(Calendar.HOUR_OF_DAY);
        int minute1 = calendar.get(Calendar.MINUTE);
        if(hour1<12)
        {//0시부터 ~ 11시라면 앞에 "오전"을 나타낸다
            if(minute1==0)
            {//0분이라는 표기값 없애주기 위한 설정
                timeBtn.setText("오전 "+hour1+"시");
            }
            else
                timeBtn.setText("오전 "+hour1+"시 "+minute1+"분");
        }
        else if(hour1>11)
        {//12시부터 23시라면 앞에 "오후"를 나타낸다.
            if(hour1==12)
            {
                if(minute1==0)
                {//0분이라는 표기값 없애주기 위한 설정
                    timeBtn.setText("오후 12시");
                }
                else
                    timeBtn.setText("오후 12시 " + minute1 + "분");
            }
            else {
                for (int i = 13; i < 24; i++) {//24시간제를 12시간제로 표시하기 위해 반복문을 돌려서 13시부터 24시까지 바꿔준다
                    if (hour1 == i) {
                        if(minute1==0)
                        {
                            timeBtn.setText("오후 " + (i - 12) + "시");
                        }
                        else
                            timeBtn.setText("오후 " + (i - 12) + "시 " + minute1 + "분");
                    }
                }
            }
        }

        //시간 설정 버튼 클릭 구현
        timeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubActivity.this, DialogActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        //요일 설정 버튼 클릭 구현
        dayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubActivity.this, DialogActivity2.class);
                startActivityForResult(intent, 2);
            }
        });


        //취소버튼 클릭 구현
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent inten = new Intent(SubActivity.this, MainActivity.class);
                setResult(RESULT_OK, inten);

                finish();
                overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
            }
        });

        //확인 버튼 클릭 구현
        btn2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Button timeBtn = (Button) findViewById(R.id.timeBtn);
                Button dayBtn = (Button) findViewById(R.id.dayBtn);
                EditText labelText = (EditText)findViewById(R.id.editText5);//라벨
                String text = timeBtn.getText().toString();
                String text2 = dayBtn.getText().toString();
                String label = labelText.getText().toString();

                requestCode++;//매번 새로운 알람 생성을 위해 requestCode값을 변경함

                Intent inten = new Intent(SubActivity.this, MainActivity.class);
                inten.putExtra("입력한 text", text);
                inten.putExtra("입력한 text2", text2);
                inten.putExtra("리퀘스트코드", requestCode);//생성된 리스트뷰에 해당 RequestCode값 보내기

                setResult(RESULT_OK, inten);//리스트뷰로 텍스트 값 보내기 완료

                //실제 알람 구현은 여기부터

                Calendar calendar = Calendar.getInstance();
                Calendar currentTime = Calendar.getInstance();//레알 현재시간

                TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                calendar.setTimeZone(timeZone);//한국시간으로 TimeZone설정
                currentTime.setTimeZone(timeZone);

                if(hour==0&&minute==0){ //TimePicker를 사용하지 않고 바로 확인 버튼을 누른다면
                    calendar.set(Calendar.SECOND, 0);//현재시간에서 초만 0으로 셋팅
                }
                else {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);  //TimePicker에서 시간과 분값 받아서 셋팅하기
                    calendar.set(Calendar.SECOND, 0);
                }

                Date date = calendar.getTime();

                Intent intent = new Intent(SubActivity.this, AlarmDialogActivity.class);
                intent.putExtra("리잉톤", ringtoneUri); //링톤Uri 값을 인텐트에 태워 보낸댜
                intent.putExtra("리퀘코드", requestCode);
                intent.putExtra("라벨", label);
                //호출된 AlarmDialogActivity에서 해당 요일이 아니면 바로 끄기 위해 요일 체크여부 값을 보냄
                intent.putExtra("일욜", sun1); intent.putExtra("월욜", mon2); intent.putExtra("화욜", tue3);intent.putExtra("수욜", wed4);
                intent.putExtra("목욜", thu5); intent.putExtra("금욜", fri6); intent.putExtra("토욜", sat7);
                PendingIntent pIntent = PendingIntent.getActivity(getApplicationContext(), requestCode, intent, 0);

                Intent intent2 = new Intent(SubActivity.this, AlarmDialogActivity.class); // 단발성 알람을 위해 새로운 인텐트 생성
                intent2.putExtra("리잉톤", ringtoneUri); //링톤Uri 값을 인텐트에 태워 보낸댜
                intent2.putExtra("리퀘코드", requestCode);
                intent2.putExtra("라벨", label);
                PendingIntent pIntent2 = PendingIntent.getActivity(getApplicationContext(), requestCode, intent2, 0);

                AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

                //반복여부가 '반복안함'일 경우
                if(sun1!=1&&mon2!=2&&tue3!=3&&wed4!=4&&thu5!=5&&fri6!=6&&sat7!=7)
                {
                    if(calendar.getTimeInMillis()<currentTime.getTimeInMillis()) //설정시간이 현재시간보다 작을 경우
                    {//이전시간일 때 알람이 바로 울리는걸 방지하기위해 다음 날로 셋팅한다.
                        calendar.add(Calendar.DATE, 1);
                        Date date1 = calendar.getTime(); //하루를 추가했으므로 Date값을 새로 생성
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent2);
                        Log.e("로그찍어 설정한 시간(setExact)", ""+date1+"  requestCode : "+requestCode);
                    }
                    else
                    {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pIntent2);
                        Log.e("로그찍어 설정한 시간(setExact)", ""+date+"  requestCode : "+requestCode);
                    }
                }   //알람 한번만 실행
                else //반복 여부에 요일이 하나 이상 들어있을 경우
                {
                    if(calendar.getTimeInMillis()<currentTime.getTimeInMillis()) //설정시간이 현재시간보다 작을 경우
                    {//이전시간일 때 알람이 바로 울리는걸 방지하기위해 다음 날로 셋팅한다.
                        calendar.add(Calendar.DATE, 1);
                        Date date1 = calendar.getTime(); //하루를 추가했으므로 Date값을 새로 생성
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);
                        Log.e("로그찍어 설정한 시간(setRepeating)", "" + date1 + "  requestCode : " + requestCode);
                    }
                    else
                    {
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pIntent);
                        Log.e("로그찍어 설정한 시간(setRepeating)", "" + date + "  requestCode : " + requestCode);
                    }
                }

                finish();
                overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
                Toast.makeText(SubActivity.this, "알람이 추가되었습니다", Toast.LENGTH_SHORT).show();
            }
        });

        //벨소리 설정 버튼 구현
        ringBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
                startActivityForResult(intent, 0);
            }
        });

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        Button ringBtn = (Button) findViewById(R.id. ringBtn);

        if(requestCode==0) {

            //벨소리 인텐트 받기
            ringtoneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            Ringtone ringtone = RingtoneManager.getRingtone(this, ringtoneUri);
            String name = ringtone.getTitle(this);

            ringBtn.setText(name);
        }

        if(requestCode==1) //시간 설정 인텐트 받기
        {
            Button timeBtn = (Button) findViewById(R.id.timeBtn);

           hour = data.getIntExtra("시", 2);
           minute = data.getIntExtra("분", 3);

            if(hour<12)
            {//0시부터 ~ 11시라면 앞에 "오전"을 나타낸다
                if(minute==0)
                {//0분이라는 표기값 없애주기 위한 설정
                    timeBtn.setText("오전 "+hour+"시");
                }
                else
                timeBtn.setText("오전 "+hour+"시 "+minute+"분");
            }
            else if(hour>11)
            {//12시부터 23시라면 앞에 "오후"를 나타낸다.
                if(hour==12)
                {
                    if(minute==0)
                    {//0분이라는 표기값 없애주기 위한 설정
                        timeBtn.setText("오후 12시");
                    }
                    else
                    timeBtn.setText("오후 12시 " + minute + "분");
                }
                else {
                    for (int i = 13; i < 24; i++) {//24시간제를 12시간제로 표시하기 위해 반복문을 돌려서 13시부터 24시까지 바꿔준다
                        if (hour == i) {
                            if(minute==0)
                            {
                                timeBtn.setText("오후 " + (i - 12) + "시");
                            }
                            else
                            timeBtn.setText("오후 " + (i - 12) + "시 " + minute + "분");
                        }
                    }
                }
            }
        }

        if(requestCode==2) //요일 설정 인텐트 받기
        {
            Button dayBtn = (Button) findViewById(R.id.dayBtn);

            String mon = data.getStringExtra("월요일");
            String tue = data.getStringExtra("화요일");
            String wed = data.getStringExtra("수요일");
            String thu = data.getStringExtra("목요일");
            String fri = data.getStringExtra("금요일");
            String sat = data.getStringExtra("토요일");
            String sun = data.getStringExtra("일요일");

            sun1 = data.getIntExtra("일요일1", 6999);
            mon2 = data.getIntExtra("월요일2", 6999);
            tue3 = data.getIntExtra("화요일3", 6999);
            wed4 = data.getIntExtra("수요일4", 6999);
            thu5 = data.getIntExtra("목요일5", 6999);
            fri6 = data.getIntExtra("금요일6", 6999);
            sat7 = data.getIntExtra("토요일7", 6999);

            if(mon==null)//체크 안됬을 시
                mon="";
            else//체크 됬을 때, 가독성을 위해 "월요일"을 "월"로 바꿔줌
                mon="월 ";
            if(tue==null)
                tue="";
            else
                tue="화 ";
            if(wed==null)
                wed="";
            else
                wed="수 ";
            if(thu==null)
                thu="";
            else
                thu="목 ";
            if(fri==null)
                fri="";
            else
                fri="금 ";
            if(sat==null)
                sat="";
            else
                sat="토 ";
            if(sun==null)
                sun="";
            else
                sun="일";

            if(mon=="월 "&&tue=="화 "&&wed=="수 "&&thu=="목 "&&fri=="금 "&&sat=="토 "&&sun=="일")//전부 체크시
                dayBtn.setText("매일");
            else {
                dayBtn.setText(mon + tue + wed + thu + fri + sat + sun);
                if(dayBtn.getText()=="")//전부 노체크일 때
                {
                    dayBtn.setText("반복안함");
                }
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(SubActivity.this, MainActivity.class);
        setResult(RESULT_OK, intent);
        super.onBackPressed();
        overridePendingTransition(R.anim.start_enter, R.anim.start_exit);
    }

    @Override
    public void onStop()
    {
        super.onStop();
        //AlarmManager를 호출하는 PendingIntent의 두번째 인자인 requestCode값을 저장한다
        SharedPreferences sharedPreferences = (SharedPreferences)getSharedPreferences("CodeNumber", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("number", requestCode);
        editor.commit();
    }
}
