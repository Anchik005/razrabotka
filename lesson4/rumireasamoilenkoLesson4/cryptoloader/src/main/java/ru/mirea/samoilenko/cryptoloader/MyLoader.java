package ru.mirea.samoilenko.cryptoloader;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;

import androidx.annotation.NonNull;
import androidx.loader.content.AsyncTaskLoader;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyLoader extends AsyncTaskLoader<String> {

    public static final String ARG_CIPHER = "cipher";
    public static final String ARG_KEY = "key";

    private final byte[] cryptText;
    private final byte[] keyBytes;

    public MyLoader(@NonNull Context context, Bundle args) {
        super(context);
        cryptText = args.getByteArray(ARG_CIPHER);
        keyBytes = args.getByteArray(ARG_KEY);
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String loadInBackground() {
        SystemClock.sleep(3000);

        SecretKey originalKey = new SecretKeySpec(keyBytes, 0, keyBytes.length, "AES");
        return MainActivity.decryptMsg(cryptText, originalKey);
    }
}