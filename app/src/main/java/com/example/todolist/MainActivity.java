package com.example.todolist;



import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    static Database database;
    ListView lvJob;
    static ArrayList<Job> arrayJob;
    static JobAdapter adapter;
    ImageView imgAdd;
    SimpleDateFormat simpleDateFormat;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;
    public static ArrayList<PendingIntent> ArrayPending;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        AnhXa();

        database = new Database(this, "testSql.sqlite", null,1);

        database.QueryData("CREATE TABLE IF NOT EXISTS CongViec(Id INTEGER PRIMARY KEY AUTOINCREMENT, TenCV VARCHAR(100), NoiDung VARCHAR(400), ThoiGian DateTime)");
        //database.QueryData("DROP TABLE CongViec");
        //database.QueryData("INSERT INTO CongViec VALUES(null, 'Lập trình android', 'Làm bài tập về to do list', '2008-12-01 10:00:00')");

       getData();

        imgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowAddDialog();
            }
        });
    }

    private void AnhXa(){
        imgAdd = (ImageView) findViewById(R.id.imageViewAdd);
        lvJob = (ListView) findViewById(R.id.listViewJob);
        arrayJob = new ArrayList<>();
        adapter = new JobAdapter(this, R.layout.job_list_layout, arrayJob);
        lvJob.setAdapter(adapter);
        ArrayPending = new ArrayList<PendingIntent>();
    }

    public static void getData(){
        arrayJob.clear();
        Cursor dataCongViec = database.GetData("SELECT * FROM CongViec");
        while (dataCongViec.moveToNext()){
            int id = dataCongViec.getInt(0);
            String jobName = dataCongViec.getString(1);
            String jobDetail = dataCongViec.getString(2);
            try {
                //Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z").parse(dataCongViec.getString(3));
                Date dateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dataCongViec.getString(3));
                arrayJob.add(new Job(id, jobName,jobDetail, dateTime));
                Collections.sort(arrayJob, new Comparator<Job>() {
                    @Override
                    public int compare(Job o1, Job o2) {
                        return o1.getDateTime().compareTo(o2.getDateTime());
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void ShowAddDialog(){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_job);
        EditText edtJobName = (EditText) dialog.findViewById(R.id.editTextJobName);
        EditText edtJobDetail = (EditText) dialog.findViewById(R.id.editTextJobDeltail);
        EditText edtDatePicker = (EditText) dialog.findViewById(R.id.editTextDate);
        EditText edtTimePicker = (EditText) dialog.findViewById(R.id.editTextTime);
        Button btnThem = (Button) dialog.findViewById(R.id.buttonAdd);
        btnThem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String jobName = edtJobName.getText().toString();
                    if(jobName.equals(""))
                        throw new Exception("Vui lòng nhập tên công việc!!");
                    String jobDetail = edtJobDetail.getText().toString();
                    if(jobDetail.equals(""))
                        throw new Exception("Vui lòng nhập nội dung công việc!!");
                    if(edtDatePicker.getText().toString().equals(""))
                        throw new Exception("Vui lòng nhập ngày!!");
                    if(edtTimePicker.getText().toString().equals(""))
                        throw new Exception("Vui lòng nhập giờ công việc!!");
                    String dateTime = edtDatePicker .getText().toString()+ " " + edtTimePicker.getText().toString() + ":00";
                    database.QueryData("INSERT INTO CongViec VALUES(null, '"+jobName+"', '"+jobDetail+"', '"+dateTime+"')");
                    Toast.makeText(MainActivity.this, "Thêm thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
//                    getData();
//                    startService(v);
//                    cancelAlarm();
                    setAlarm();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        edtDatePicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar calendar = Calendar.getInstance();
                    int date = calendar.get(Calendar.DATE);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            calendar.set(year, month, dayOfMonth);
                            edtDatePicker.setText(simpleDateFormat.format(calendar.getTime()));
                        }
                    }, year, month, date);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                }
                return true;
            }
        });

        edtTimePicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            edtTimePicker.setText(hourOfDay + ":"+minute);
                        }
                    }, 0,0,true);

                    timePickerDialog.show();
                }
                return true;
            }
        });


        dialog.show();
    }



    public void ShowEditDialog(int id, String jobName, String jobDetail, String Date, String Time){
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_job);
        EditText edtJobName = (EditText) dialog.findViewById(R.id.editTextJobName);
        edtJobName.setText(jobName);
        EditText edtJobDetail = (EditText) dialog.findViewById(R.id.editTextJobDeltail);
        edtJobDetail.setText(jobDetail);
        EditText edtDatePicker = (EditText) dialog.findViewById(R.id.editTextDate);
        edtDatePicker.setText(Date);
        EditText edtTimePicker = (EditText) dialog.findViewById(R.id.editTextTime);
        edtTimePicker.setText(Time);
        Button btnSua = (Button) dialog.findViewById(R.id.buttonAdd);

        TextView tvTitle = (TextView) dialog.findViewById(R.id.textViewTitle);
        tvTitle.setText("Sửa công việc");
        btnSua.setText("Sửa");

        btnSua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String edtjobName = edtJobName.getText().toString();
                    if(edtjobName.equals(""))
                        throw new Exception("Vui lòng nhập tên công việc!!");
                    String edtjobDetail = edtJobDetail.getText().toString();
                    if(edtjobDetail.equals(""))
                        throw new Exception("Vui lòng nhập nội dung công việc!!");
                    if(edtDatePicker.getText().toString().equals(""))
                        throw new Exception("Vui lòng nhập ngày!!");
                    if(edtTimePicker.getText().toString().equals(""))
                        throw new Exception("Vui lòng nhập giờ công việc!!");
                    String dateTime = edtDatePicker .getText().toString()+ " " + edtTimePicker.getText().toString() + ":00";
                    database.QueryData("UPDATE CongViec SET TenCV = '"+edtjobName+"', NoiDung = '"+edtjobDetail+"', ThoiGian = '"+dateTime+"' WHERE Id = "+id+"");
                    Toast.makeText(MainActivity.this, "Sửa thành công", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
//                    getData();
//                    stopService(v);
//                    startService(v);
//                    cancelAlarm();
                    setAlarm();
                }catch (Exception e){
                    Toast.makeText(MainActivity.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        edtDatePicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar calendar = Calendar.getInstance();
                    int date = calendar.get(Calendar.DATE);
                    int month = calendar.get(Calendar.MONTH);
                    int year = calendar.get(Calendar.YEAR);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            calendar.set(year, month, dayOfMonth);
                            edtDatePicker.setText(simpleDateFormat.format(calendar.getTime()));
                        }
                    }, year, month, date);
                    datePickerDialog.show();
                }
                return true;
            }
        });

        edtTimePicker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TimePickerDialog timePickerDialog = new TimePickerDialog(MainActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            edtTimePicker.setText(hourOfDay + ":"+minute);
                        }
                    }, 0,0,true);

                    timePickerDialog.show();
                }
                return true;
            }
        });

        dialog.show();
    }

    public void DeleteJob(int id){
        database.QueryData("DELETE FROM CongViec WHERE Id = "+id);
//        Toast.makeText(MainActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
        getData();
    }

    public void setAlarm(){
        getData();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(int i=0;i<arrayJob.size();i++){
            String strDate = dateFormat.format(arrayJob.get(i).getDateTime());
            int year = Integer.parseInt(strDate.substring(0,4));
            int month = Integer.parseInt(strDate.substring(5,7));
            int date = Integer.parseInt(strDate.substring(8,10));
            int hour = Integer.parseInt(strDate.substring(11,13));
            int minute = Integer.parseInt(strDate.substring(14,16));
            int second = Integer.parseInt(strDate.substring(17,19));
            Log.d("TAG", "onStartCommand: "+year+"/"+month+"/"+date+":"+hour+"."+minute+"."+second);
            Calendar calendar = Calendar.getInstance();
            calendar.set(year,month-1,date,hour,minute,second);
            alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
            Intent intent1Alarm = new Intent(this, AlarmReceiver.class);

            Bundle args = new Bundle();
            args.putSerializable("chatobj",(Serializable)arrayJob.get(i));
            intent1Alarm.putExtra("DATA",args);

            //intent1Alarm.putExtra("Job", arrayJob.get(i));
            pendingIntent = PendingIntent.getBroadcast(
                    this, i,intent1Alarm,pendingIntent.FLAG_UPDATE_CURRENT
            );
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
            ArrayPending.add(pendingIntent);
        }

    }

    public void cancelAlarm(int index){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
//        Intent intent1Alarm = new Intent(this, AlarmReceiver.class);
//        sendBroadcast(intent1Alarm);
        alarmManager.cancel(ArrayPending.get(index));
        ArrayPending.remove(index);
    }
}