package ru.mirea.samoilenko.looper;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MyLooper extends Thread {

    public Handler mHandler;
    private final Handler mainHandler;

    public MyLooper(Handler mainThreadHandler) {
        this.mainHandler = mainThreadHandler;
    }

    @Override
    public void run() {
        Log.d("MyLooper", "run");

        Looper.prepare();

        mHandler = new Handler(Looper.myLooper()) {
            @Override
            public void handleMessage(Message msg) {
                Bundle bundle = msg.getData();

                int age = bundle.getInt("AGE");
                String job = bundle.getString("JOB");

                Log.d("MyLooper", "Received data: age = " + age + ", job = " + job);

                try {
                    Thread.sleep(age * 1000L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                String result = "Age: " + age + ", job: " + job +
                        ". Delay time was " + age + " seconds.";

                Log.d("MyLooper", result);

                Message message = Message.obtain();
                Bundle resultBundle = new Bundle();
                resultBundle.putString("RESULT", result);
                message.setData(resultBundle);

                mainHandler.sendMessage(message);
            }
        };

        Looper.loop();
    }
}