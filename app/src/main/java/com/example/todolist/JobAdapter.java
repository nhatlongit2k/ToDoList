package com.example.todolist;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class JobAdapter extends BaseAdapter {

    private MainActivity context;
    private int layout;
    private List<Job> jobList;

    public JobAdapter(MainActivity context, int layout, List<Job> jobList) {
        this.context = context;
        this.layout = layout;
        this.jobList = jobList;
    }

    @Override
    public int getCount() {
        return jobList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
    private  class  ViewHolder{
        TextView txtJobName, txtJobDetail, txtDateTime;
        ImageView imgDelete, imgEdit;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout, null);
            holder.txtJobName = (TextView) convertView.findViewById(R.id.TexviewJobName);
            holder.txtJobDetail = (TextView) convertView.findViewById(R.id.TexviewJobDetail);
            holder.txtDateTime = (TextView) convertView.findViewById(R.id.TextViewTime);
            holder.imgDelete = (ImageView) convertView.findViewById(R.id.ImageDeleteJob);
            holder.imgEdit = (ImageView) convertView.findViewById(R.id.ImageEditJob);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Job job = jobList.get(position);
        holder.txtJobName.setText(job.getJobName());
        holder.txtJobDetail.setText(job.getJobDetail());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = dateFormat.format(job.getDateTime());
        holder.txtDateTime.setText(strDate);
        holder.imgEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String DateTime = strDate;
                String[] parts = DateTime.split(" ");
                String Date = parts[0]; // 004
                String Time = parts[1]; // 034556
                context.ShowEditDialog(job.getId(),job.getJobName(), job.getJobDetail(), Date, Time);
            }
        });

        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Bạn có chắc muốn xóa?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                context.cancelAlarm(jobList.indexOf(job));
                                context.DeleteJob(job.getId());
                                context.setAlarm();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
            }
        });

        return convertView;
    }
}
