package com.gametools.turbo;

import android.app.*;
import android.os.*;
import android.content.*;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;

public class MainActivity extends Activity {
    LinearLayout root, content;
    TextView netText, batteryText, perfText;
    Handler handler = new Handler();
    long lastRx = -1, lastTx = -1;
    Runnable refresher = new Runnable() { public void run() { updateStats(); handler.postDelayed(this, 1000); }};

    int bg = Color.rgb(10, 14, 25), panel = Color.rgb(22, 29, 48), cyan = Color.rgb(0, 229, 255);
    public void onCreate(Bundle b) {
        super.onCreate(b);
        getWindow().setStatusBarColor(bg); getWindow().setNavigationBarColor(bg);
        root = new LinearLayout(this); root.setOrientation(1); root.setPadding(18,24,18,18); root.setBackgroundColor(bg);
        ScrollView scroll = new ScrollView(this); scroll.addView(root); setContentView(scroll);
        addText(root, "⚡ GAME TURBO", 28, cyan, true);
        addText(root, "FLOATING GAMING TOOLKIT • v2.0", 11, Color.LTGRAY, false);
        addSpace(root,16);
        card("🚀 GAME TURBO FLOATING", "Show a movable mini panel over your game.", "Enable Floating Panel", v -> enableFloating());
        card("🪟 FLOATING WINDOW", "Movable mini window with timer, notes and network speed.", "Open Floating Window", v -> enableFloating());
        card("🎮 GAME MODE", "Open a game from your installed apps. This app does not modify game files.", "Choose Game", v -> chooseGame());
        card("📊 GAME PERFORMANCE", "Device memory and network activity estimates. FPS/GPU boost is not guaranteed.", "Refresh Performance", v -> showPerformance());
        card("🔋 BATTERY MODE", "View battery level and open Android battery saver settings.", "Battery Settings", v -> batterySettings());
        card("🌐 NET SPEED", "Live device-wide network traffic estimate (not a speed-test result).", "Refresh Network", v -> updateStats());
        batteryText = addText(root, "Battery: reading…", 14, Color.WHITE, false);
        perfText = addText(root, "Performance: reading…", 14, Color.WHITE, false);
        netText = addText(root, "Network: reading…", 14, Color.WHITE, false);
        addSpace(root,12);
        addText(root, "NOTE: Android controls actual CPU/GPU performance. No fake FPS boost or free-data claims.", 12, Color.LTGRAY, false);
        handler.post(refresher);
    }
    TextView addText(LinearLayout parent, String s, int size, int color, boolean bold) {
        TextView t = new TextView(this); t.setText(s); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        t.setPadding(4,5,4,5); parent.addView(t); return t;
    }
    void addSpace(LinearLayout p, int h) { View v = new View(this); p.addView(v,new LinearLayout.LayoutParams(1,h)); }
    void card(String title, String desc, String action, View.OnClickListener listener) {
        LinearLayout box = new LinearLayout(this); box.setOrientation(1); box.setPadding(14,12,14,12);
        GradientDrawable gd = new GradientDrawable(); gd.setColor(panel); gd.setCornerRadius(22); gd.setStroke(1, Color.rgb(42,64,91)); box.setBackground(gd);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,8,0,8); root.addView(box,lp);
        addText(box,title,17,Color.WHITE,true); addText(box,desc,12,Color.LTGRAY,false);
        Button btn = new Button(this); btn.setText(action); btn.setTextColor(Color.BLACK); btn.setBackgroundTintList(android.content.res.ColorStateList.valueOf(cyan));
        box.addView(btn); btn.setOnClickListener(listener);
    }
    void enableFloating() {
        if (!Settings.canDrawOverlays(this)) {
            new AlertDialog.Builder(this).setTitle("Allow floating window")
                .setMessage("Android settings-la 'Display over other apps' permission allow pannunga. App-ku thirumbi vandhu button-ai marubadi press pannunga.")
                .setPositiveButton("Open Settings",(d,w)->startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:"+getPackageName()))))
                .setNegativeButton("Cancel",null).show();
        } else {
            startService(new Intent(this,FloatingService.class));
            Toast.makeText(this,"Floating panel started. Drag the title bar to move it.",Toast.LENGTH_LONG).show();
        }
    }
    void chooseGame() {
        String[] games = {"Free Fire (if installed)","BGMI (if installed)","Call of Duty Mobile (if installed)","Open app picker"};
        new AlertDialog.Builder(this).setTitle("Game Mode").setItems(games,(d,which)->{
            String pkg = which==0 ? "com.dts.freefireth" : which==1 ? "com.pubg.imobile" : which==2 ? "com.activision.callofduty.shooter" : "";
            if (which==3) { Intent i = new Intent(Intent.ACTION_MAIN); i.addCategory(Intent.CATEGORY_LAUNCHER); startActivity(Intent.createChooser(i,"Choose an app")); return; }
            Intent launch = getPackageManager().getLaunchIntentForPackage(pkg);
            if (launch != null) startActivity(launch); else Toast.makeText(this,"Game not found. Install it or use your launcher.",Toast.LENGTH_LONG).show();
        }).show();
    }
    void showPerformance() {
        ActivityManager am = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo(); am.getMemoryInfo(mi);
        long total = mi.totalMem/1024/1024, avail = mi.availMem/1024/1024;
        String s = "RAM: "+(total-avail)+" MB used / "+total+" MB total\nLow memory state: "+mi.lowMemory;
        perfText.setText(s);
        new AlertDialog.Builder(this).setTitle("Performance Info").setMessage(s+"\n\nFPS and CPU/GPU boost depend on Android and device manufacturer.").setPositiveButton("OK",null).show();
    }
    void batterySettings() {
        Intent i = new Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS);
        try { startActivity(i); } catch(Exception e) { startActivity(new Intent(Settings.ACTION_SETTINGS)); }
    }
    void updateStats() {
        try {
            IntentFilter f = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent b = registerReceiver(null,f);
            if (b != null) {
                int level=b.getIntExtra("level",-1), scale=b.getIntExtra("scale",100);
                batteryText.setText("🔋 Battery: "+(level*100/Math.max(1,scale))+"%");
            }
        } catch(Exception ignored) {}
        ActivityManager am=(ActivityManager)getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo mi=new ActivityManager.MemoryInfo(); am.getMemoryInfo(mi);
        perfText.setText("📊 RAM available: "+(mi.availMem/1024/1024)+" MB");
        long rx=android.net.TrafficStats.getTotalRxBytes(), tx=android.net.TrafficStats.getTotalTxBytes();
        if(lastRx>=0 && rx>=lastRx && tx>=lastTx) netText.setText("🌐 Net speed: ↓ "+((rx-lastRx)/1024)+" KB/s  ↑ "+((tx-lastTx)/1024)+" KB/s");
        else netText.setText("🌐 Net speed: measuring…");
        lastRx=rx; lastTx=tx;
    }
    @Override protected void onDestroy() { handler.removeCallbacks(refresher); super.onDestroy(); }
}
