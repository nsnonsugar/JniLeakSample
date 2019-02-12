package com.example.nonsugar.jnileaksample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Javaのワーカースレッドを起動
        Receiver.startReceiverThread();

        // JavaのBigObject送信スレッドを起動
        Receiver.starSenderThread();
    }

    public native void startNativeThread();
}
