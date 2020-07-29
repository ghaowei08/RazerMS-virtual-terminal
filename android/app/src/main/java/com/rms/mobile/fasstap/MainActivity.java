package com.rms.mobile.fasstap;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;


public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "flutter.native/fasstap";
    private MethodChannel.Result _result;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
        .setMethodCallHandler(
            (call, result) -> {
                // Note: this method is invoked on the main thread.
                // TODO
                if (call.method.equals("startActivity")) {
                    _result = result;
                    startActivity();
                } else {
                    result.notImplemented();
                }
            }
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case Constant.REQUEST_CODE_START_SCAN:

                if (data.hasExtra(Constant.ERROR_CODE)) {
                    try {
                        JSONObject json = new JSONObject();
                        json.put("errorCode", data.getStringExtra(Constant.ERROR_CODE));
                        json.put("errorMsg", data.getStringExtra(Constant.ERROR_MSG));
                        _result.success(json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject json = new JSONObject();
                        json.put(Constant.STATUS_CODE, data.getStringExtra(Constant.STATUS_CODE));
                        json.put(Constant.STATUS_MESSAGE, data.getStringExtra(Constant.STATUS_MESSAGE));
                        json.put(Constant.APPROVAL_CODE, data.getStringExtra(Constant.APPROVAL_CODE));
                        json.put(Constant.TRANSACTION_ID, data.getStringExtra(Constant.TRANSACTION_ID));
                        json.put(Constant.CARD_NO, data.getStringExtra(Constant.CARD_NO));
                        json.put(Constant.CARD_TYPE, data.getStringExtra(Constant.CARD_TYPE));
                        json.put(Constant.CARDHOLDER_NAME, data.getStringExtra(Constant.CARDHOLDER_NAME));
                        json.put(Constant.REFERENCE_NO, data.getStringExtra(Constant.REFERENCE_NO));
                        _result.success(json.toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void startActivity() {
        Intent intent = new Intent(this, TapCardActivity.class);
        startActivityForResult(intent, Constant.REQUEST_CODE_START_SCAN);
    }

}