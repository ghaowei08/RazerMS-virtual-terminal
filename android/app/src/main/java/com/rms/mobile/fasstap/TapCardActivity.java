package com.rms.mobile.fasstap;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.visa.SensoryBrandingCompletionHandler;
import com.visa.SensoryBrandingView;

import java.io.IOException;

import my.com.softspace.ssmpossdk.SSMPOSSDK;
import my.com.softspace.ssmpossdk.SSMPOSSDKConfiguration;
import my.com.softspace.ssmpossdk.transaction.MPOSTransaction;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionOutcome;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionParams;

import static my.com.softspace.ssmpossdk.transaction.MPOSTransaction.TransactionEvents.TransactionResult.TransactionSuccessful;

public class TapCardActivity extends Activity {

    private LinearLayout layoutCancelScan;
    private LinearLayout layoutSensoryBranding;
    private SensoryBrandingView visaSensoryBranding;
    private VideoView masterSensoryBranding;
    private LinearLayout lyScanningEvent;
    private TextView textViewScanningEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapcard);
        Log.d(Constant.TAG,"onCreate invoked");

        layoutSensoryBranding = findViewById(R.id.lySensoryBranding);
        visaSensoryBranding = findViewById(R.id.sbVisaSensoryBranding);
        masterSensoryBranding = findViewById(R.id.vvMasterSensoryBranding);
        layoutCancelScan = findViewById(R.id.lyCancelScan);
        textViewScanningEvent = findViewById(R.id.tvScanningEvent);
        lyScanningEvent = findViewById(R.id.lyScanningEvent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Constant.TAG,"onStart invoked");

        checkLocationPermission();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(Constant.TAG,"onPause invoked");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(Constant.TAG,"onStop invoked");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(Constant.TAG,"onRestart invoked");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("lifecycle","onDestroy invoked");
    }

    @Override
    public void onRequestPermissionsResult(int resultCode, String[] permissions, int[] grantResult) {
        super.onRequestPermissionsResult(resultCode, permissions, grantResult);

        switch (resultCode) {
            case Constant.PERMISSIONS_REQUEST_LOCATION:
            case Constant.PERMISSION_REQUEST_PHONE:
                try {
                    if (grantResult[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.e(Constant.TAG, "Permission has granted");
                        initialise();
                    } else {
                        Log.e(Constant.TAG, "Permission not granted, can't proceed");
                        Intent intent = new Intent();
                        intent.putExtra(Constant.ERROR_CODE, Constant.REQUIRE_PERMISSION_CODE);
                        intent.putExtra(Constant.ERROR_MSG, Constant.REQUIRE_PERMISSION_DESC);
                        setResult(Constant.REQUEST_CODE_START_SCAN, intent);
                        finish();
                    }
                } catch(ArrayIndexOutOfBoundsException e) {
                    Log.e(Constant.TAG, e.getMessage(), e);
                }

                break;
        }
    }

    public void cancelScan(View view) {
        SSMPOSSDK.getInstance().getTransaction().abortTransaction();

        Intent intent = new Intent();
        intent.putExtra(Constant.ERROR_CODE, Constant.SCAN_CANCELLED_CODE);
        intent.putExtra(Constant.ERROR_MSG, Constant.SCAN_CANCELLED_DESC);
        setResult(Constant.REQUEST_CODE_START_SCAN, intent);
        finish();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(TapCardActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        Constant.PERMISSIONS_REQUEST_LOCATION);
        }else {
            initialise();
        }
    }

    private void initialise() {

        try {

            Log.d(Constant.TAG, "Initialising...");

            SSMPOSSDKConfiguration config = SSMPOSSDKConfiguration.Builder
                    .create()
                    .setAttestationHost("https://mpos-uat.fasspay.com:9001")
                    .setAttestationHostCertPinning("sha256/BJlJjxY7OHxhAz6yqy2gm58+qlP0AGwnBHDIG6zkhfU=")
                    .setAttestationHostReadTimeout(10000L)
                    .setAttestationRefreshInterval(300000L)
                    .setAttestationStrictHttp(true)
                    .setAttestationConnectionTimeout(30000L)
                    .setLibGoogleApiKey("AIzaSyD9l4ImfUXhDAMz4Df5rdt7gItDy91fXTE")
                    .setLibAccessKey("") // please replace the access key provided by Soft Space
                    .setLibSecretKey("") // please replace the secret key provided by Soft Space
                    .setEnableAttestation(true)
                    .setUniqueID("") // please set the userID shared by Soft Space
                    .build();

            SSMPOSSDK.init(getApplicationContext(), config);

            Log.d(Constant.TAG,"SDK Version: " + SSMPOSSDK.getInstance().getSdkVersion());
            Log.d(Constant.TAG,"COTS ID: " + SSMPOSSDK.getInstance().getCotsId());

            if (!SSMPOSSDK.hasRequiredPermission(getApplicationContext())) {
                SSMPOSSDK.requestPermissionIfRequired(TapCardActivity.this, Constant.PERMISSION_REQUEST_PHONE);
            } else {
                refreshToken();
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void refreshToken() {

        try {

            Log.d(Constant.TAG, "refreshToken()");

            SSMPOSSDK.getInstance().getTransaction().refreshToken(this, new MPOSTransaction.TransactionEvents() {

                @Override
                public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
                    Log.d(Constant.TAG, "onTransactionResult :: " + result);

                    if(result == TransactionSuccessful) {
                        Log.d(Constant.TAG, "Run start transaction");
                        new Handler().postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                Log.d(Constant.TAG, "Running..");
                                startTransaction();
                            }
                        }, 3000);
                    } else {
                        if(transactionOutcome != null) {
                            Log.d(Constant.TAG, transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                            if (transactionOutcome.getStatusCode().equals(Constant.ERROR_CODE_LOGIN_REQUIRED)) {
                                Log.d(Constant.TAG, "Login required");
                                performLogin();
                            } else if (transactionOutcome.getStatusCode().equals(Constant.ERROR_CODE_ACTIVATION_REQUIRED)) {
                                Log.d(Constant.TAG, "Activation required");
                                performActivation();
                            } else {
                                Log.d(Constant.TAG, "Unknown Status: " + transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                                postFailedResult(transactionOutcome);
                            }
                        }
                    }
                }

                @Override
                public void onTransactionUIEvent(int event) {
                    Log.d(Constant.TAG, "onTransactionUIEvent :: " + event);
                }
            });
        } catch (Exception | Error e){
            e.printStackTrace();
        }
    }

    private void performActivation() {

        try {

            Log.d(Constant.TAG, "performActivation()");

            MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
                .setTempPin("") // temporary pin for activation
                .setPin("") // new pin to change
                .setActivationCode("") // activation code for activation
                .build();

            SSMPOSSDK.getInstance().getTransaction().performActivation(this, transactionalParams, new MPOSTransaction.TransactionEvents() {
                
                @Override
                public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
                    Log.d(Constant.TAG, "onTransactionResult :: " + result);

                    if(result == TransactionSuccessful) {
                        Log.d(Constant.TAG, "Login required");
                        performLogin();
                    } else {
                        if(transactionOutcome != null) {
                            Log.d(Constant.TAG, transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                            postFailedResult(transactionOutcome);
                        }
                    }
                }

                @Override
                public void onTransactionUIEvent(int event) {
                    Log.d(Constant.TAG, "onTransactionUIEvent :: " + event);
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void performLogin() {

        try {
            Log.d(Constant.TAG, "performLogin()");

            MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
                .setPin("111111")
                .build();

            SSMPOSSDK.getInstance().getTransaction().performLogin(this, transactionalParams, new MPOSTransaction.TransactionEvents() {
                
                @Override
                public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(Constant.TAG, "onTransactionResult :: " + result);

                            if(result == TransactionSuccessful) {
                                startTransaction();
                            } else {
                                if(transactionOutcome != null) {
                                    Log.d(Constant.TAG, transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                                    postFailedResult(transactionOutcome);
                                }
                            }
                        }
                    });
                }

                @Override
                public void onTransactionUIEvent(int event) {
                    Log.d(Constant.TAG, "onTransactionUIEvent :: " + event);
                }
            });
        } catch (Exception | Error e) {
            e.printStackTrace();
        }
    }

    private void startTransaction() {

        try {
            Log.d(Constant.TAG, "startTransaction()");

            MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
                .setAmount("100")
                .build();

            SSMPOSSDK.getInstance().getTransaction().startTransaction(this, transactionalParams, new MPOSTransaction.TransactionEvents() {
                
                @Override
                public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {

                    if(result == TransactionSuccessful) {
                        String outcome = "Transaction ID :: " + transactionOutcome.getTransactionID() + "\n";
                        outcome += "Approval code :: " + transactionOutcome.getApprovalCode() + "\n";
                        outcome += "Card number :: " + transactionOutcome.getCardNo() + "\n";
                        outcome += "Cardholder name :: " + transactionOutcome.getCardHolderName() + "\n";
                        Log.d(Constant.TAG, outcome);

                        if(Constant.CARD_TYPE_VISA.equals(transactionOutcome.getCardType())) {
                            animateVisaSensoryBranding(transactionOutcome);
                        } else if(Constant.CARD_TYPE_MASTER.equals(transactionOutcome.getCardType())) {
                            animateMasterSensoryBranding(transactionOutcome);
                        } else if(Constant.CARD_TYPE_DEBIT.equals(transactionOutcome.getCardType())) {

                        }
                    } else {
                        if(transactionOutcome != null) {
                            Log.d(Constant.TAG, transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                            postFailedResult(transactionOutcome);
                        }
                    }
                }

                @Override
                public void onTransactionUIEvent(int event) {
                    Log.d(Constant.TAG, "onTransactionUIEvent :: " + event);
                    runOnUiThread(() -> {
                        layoutCancelScan.setVisibility(View.VISIBLE);
                        lyScanningEvent.setVisibility(View.VISIBLE);

                        switch (event) {
                            case TransactionUIEvent.PresentCard:
                                textViewScanningEvent.setText("Please tap your card here");
                                break;
                            case TransactionUIEvent.CardPresented:
                                textViewScanningEvent.setText("Card detected");
                                break;
                            case TransactionUIEvent.Authorising:
                                textViewScanningEvent.setText("Authorising");
                                break;
                            case TransactionUIEvent.PresentCardTimeout:
                                textViewScanningEvent.setText("Time out");
                                break;
                            case TransactionUIEvent.CardReadOk:
                                textViewScanningEvent.setText("Read card OK");
                                break;
                            case TransactionUIEvent.CardReadError:
                                textViewScanningEvent.setText("Read card error");
                                break;
                            case TransactionUIEvent.Unknown:
                            case TransactionUIEvent.CancelPin:
                            case TransactionUIEvent.EnterPin:
                            case TransactionUIEvent.PinBypass:
                            case TransactionUIEvent.PinEnterTimeout:
                            case TransactionUIEvent.PinEntered:
                            case TransactionUIEvent.RequestSignature:
                                break;
                        }

                    });
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void animateVisaSensoryBranding(MPOSTransactionOutcome transactionOutcome) {

        layoutSensoryBranding.setVisibility(View.VISIBLE);
        visaSensoryBranding.setVisibility(View.VISIBLE);

        visaSensoryBranding.setBackdropColor(-1);
        visaSensoryBranding.setConstrainedFlags(false);
        visaSensoryBranding.setSoundEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            visaSensoryBranding.setHapticFeedbackEnabled(true);
        }
        visaSensoryBranding.setCheckMarkShown(true);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                visaSensoryBranding.animate(new SensoryBrandingCompletionHandler() {

                    @Override
                    public void onComplete(Error error) {
                        visaSensoryBranding.setVisibility(View.GONE);
                        layoutSensoryBranding.setVisibility(View.GONE);
                        postSuccessResult(transactionOutcome);
                    }
                });
            }
        }, 100);
    }

    private void animateMasterSensoryBranding(MPOSTransactionOutcome transactionOutcome) {
        layoutSensoryBranding.setVisibility(View.VISIBLE);
        masterSensoryBranding.setVisibility(View.VISIBLE);

        String path = "android.resource://" + getPackageName() + "/" + R.raw.mc_sensory_transaction;
        masterSensoryBranding.setVideoURI(Uri.parse(path));
        masterSensoryBranding.start();
        masterSensoryBranding.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                    masterSensoryBranding.suspend();
                }
                masterSensoryBranding.setVisibility(View.GONE);
                layoutSensoryBranding.setVisibility(View.GONE);
                postSuccessResult(transactionOutcome);
            }
        });

    }

    private void postSuccessResult(MPOSTransactionOutcome transactionOutcome){
        Intent intent = new Intent();
        intent.putExtra(Constant.STATUS_CODE, transactionOutcome.getStatusCode());
        intent.putExtra(Constant.STATUS_MESSAGE, transactionOutcome.getStatusMessage());
        intent.putExtra(Constant.APPROVAL_CODE, transactionOutcome.getApprovalCode());
        intent.putExtra(Constant.TRANSACTION_ID, transactionOutcome.getTransactionID());
        intent.putExtra(Constant.CARD_NO, transactionOutcome.getCardNo());
        intent.putExtra(Constant.CARD_TYPE, transactionOutcome.getCardType());
        intent.putExtra(Constant.CARDHOLDER_NAME, transactionOutcome.getCardHolderName());
        intent.putExtra(Constant.REFERENCE_NO, transactionOutcome.getReferenceNo());
        setResult(Constant.REQUEST_CODE_START_SCAN, intent);
        finish();
    }

    private void postFailedResult(MPOSTransactionOutcome transactionOutcome){
        Intent intent = new Intent();
        intent.putExtra(Constant.ERROR_CODE, transactionOutcome.getStatusCode());
        intent.putExtra(Constant.ERROR_MSG, transactionOutcome.getStatusMessage());
        setResult(Constant.REQUEST_CODE_START_SCAN, intent);
        finish();
    }
}