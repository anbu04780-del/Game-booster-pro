package com.gametools.turbo;

import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.IBinder;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import android.net.TrafficStats;

public class FloatingService extends Service {
    WindowManager wm; View panel; WindowManager.LayoutParams params;
    long rx0=TrafficStats.getTotalRxBytes(), tx0=TrafficStats.getTotalTxBytes();
    public void onCreate() {
        super.onCreate();
        if (!Settings.canDrawOverlays(this)) { stopSelf(); return; }
        wm=(WindowManager)getSystemService(WINDOW_SERVICE);
        LinearLayout box=new LinearLayout(this); box.setOrientation(1); box.setPadding(12,8,12,8);
        GradientDrawable gd=new GradientDrawable(); gd.setColor(Color.argb(238,15,22,38)); gd.setCornerRadius(20); gd.setStroke(2,Color.rgb(0,229,255)); box.setBackground(gd);
        TextView head=new TextView(this); head.setText("⚡ TURBO  ⋮"); head.setTextColor(Color.rgb(0,229,255)); head.setTypeface(null,Typeface.BOLD); head.setTextSize(16); head.setPadding(8,8,8,8); box.addView(head);
        TextView stats=new TextView(this); stats.setTextColor(Color.WHITE); stats.setTextSize(13); stats.setText("Floating game tools\nBattery • RAM • Net speed"); stats.setPadding(8,6,8,6); box.addView(stats);
        Button timer=new Button(this); timer.setText("Start 10-min timer"); box.addView(timer);
        timer.setOnClickListener(v -> new android.app.AlertDialog.Builder(this).setTitle("Gaming break").setMessage("10 minutes is up when your timer finishes. Use your phone Clock app for a reliable background timer.").setPositiveButton("OK",null).show());
        Button close=new Button(this); close.setText("Close panel"); box.addView(close); close.setOnClickListener(v->stopSelf());
        int type=android.os.Build.VERSION.SDK_INT>=26?WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY:WindowManager.LayoutParams.TYPE_PHONE;
        params=new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT,type,WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,PixelFormat.TRANSLUCENT);
        params.gravity=Gravity.TOP|Gravity.END; params.x=18; params.y=120;
        wm.addView(box,params); panel=box;
        head.setOnTouchListener(new View.OnTouchListener(){ int sx,sy; float tx,ty;
            public boolean onTouch(View v, android.view.MotionEvent e){
                if(e.getAction()==MotionEvent.ACTION_DOWN){sx=params.x;sy=params.y;tx=e.getRawX();ty=e.getRawY();return true;}
                if(e.getAction()==MotionEvent.ACTION_MOVE){params.x=sx+(int)(tx-e.getRawX());params.y=sy+(int)(e.getRawY()-ty);wm.updateViewLayout(panel,params);return true;}
                return false;
            }});
    }
    public int onStartCommand(Intent i,int flags,int startId){return START_NOT_STICKY;}
    public void onDestroy(){if(wm!=null&&panel!=null)try{wm.removeView(panel);}catch(Exception ignored){} super.onDestroy();}
    public IBinder onBind(Intent i){return null;}
}
