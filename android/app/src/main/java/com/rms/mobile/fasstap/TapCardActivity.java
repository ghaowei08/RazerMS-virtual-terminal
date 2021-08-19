package com.rms.mobile.fasstap;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;
import android.widget.ImageView;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import my.com.softspace.ssmpossdk.SSMPOSSDK;
import my.com.softspace.ssmpossdk.transaction.MPOSTransaction;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionOutcome;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionParams;

import static com.rms.mobile.fasstap.MainActivity._result;
import static my.com.softspace.ssmpossdk.transaction.MPOSTransaction.TransactionEvents.TransactionResult.TransactionSuccessful;

import java.text.NumberFormat;
import java.util.Locale;

public class TapCardActivity extends AppCompatActivity {

    private TextView ivUiEvent;
    private TextView tvAmount;
    private TextView tvOrderId;
    private TextView tvCurrency;
    private TextView tvMaskPanNo;
    private TextView tvInstruction;
    private ImageView ivWaveNow;
    private ImageView ivChecked;
    private ProgressBar pbWaiting;
    private Button btnManualInput;

    private String[] instructions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tapcard);

        Log.d(Constant.TAG,"onCreate invoked");

        ImageView ivCancelScan = findViewById(R.id.cancel_scan_btn);
        btnManualInput = findViewById(R.id.manual_input_btn);
        tvAmount = findViewById(R.id.amount_txt);
        tvOrderId = findViewById(R.id.order_id_txt);
        tvCurrency = findViewById(R.id.currency_txt);
        tvMaskPanNo = findViewById(R.id.mask_pan_no_txt);
        tvInstruction = findViewById(R.id.instruction_txt);
        ivUiEvent = findViewById(R.id.fasstap_ui_event_txt);
        ivWaveNow = findViewById(R.id.wave_now);
        ivChecked = findViewById(R.id.checked);
        pbWaiting = findViewById(R.id.progressBar);

        ivCancelScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelScan();
            }
        });

        btnManualInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancelScan();
            }
        });

        NfcManager manager = (NfcManager) getApplicationContext().getSystemService(Context.NFC_SERVICE);
        NfcAdapter adapter = manager.getDefaultAdapter();
        if (adapter == null) {
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.NFC_NOT_SUPPORTED_CODE);
                json.put(Constant.OPERATION_MSG, Constant.NFC_NOT_SUPPORTED_DESC);
                _result.success(json.toString());
                finish();
            } catch (JSONException | RuntimeException e) {
                e.printStackTrace();
                finish();
            }
        } else if (adapter != null && !adapter.isEnabled()) {
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.NFC_NOT_ENABLED_CODE);
                json.put(Constant.OPERATION_MSG, Constant.NFC_NOT_ENABLED_DESC);
                _result.success(json.toString());
                finish();
            } catch (JSONException | RuntimeException e) {
                e.printStackTrace();
                finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        Log.d(Constant.TAG,"onBackPressed invoked");
        SSMPOSSDK.getInstance().getTransaction().abortTransaction();

        try {
            JSONObject json = new JSONObject();
            json.put(Constant.OPERATION_CODE, Constant.SCAN_CANCELLED_CODE);
            json.put(Constant.OPERATION_MSG, Constant.SCAN_CANCELLED_DESC);
            _result.success(json.toString());
            finish();
        } catch (JSONException | RuntimeException e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(Constant.TAG,"onStart invoked");

        int requestCode = getIntent().getExtras().getInt("requestCode");
        Log.d(Constant.TAG,"Incoming requestCode: " + requestCode);

        switch (requestCode) {
            case Constant.REQUEST_CODE_START_SCAN:
                try {
                    if(getIntent().getExtras() != null){
                        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.US);
                        float fAmount = Float.parseFloat(getIntent().getExtras().getString("amount"));
                        String strAmount = formatter.format(fAmount);
                        instructions = getIntent().getExtras().getString("instructions").split("\\|");
                        
                        tvAmount.setText(strAmount.replaceAll("[$]", ""));
                        tvOrderId.setText(getIntent().getExtras().getString("orderId"));
                        tvCurrency.setText(getIntent().getExtras().getString("currency"));
                        btnManualInput.setText(getIntent().getExtras().getString("cancel"));
                        tvInstruction.setText(instructions[0]);
                    }
                    if (SSMPOSSDK.requestPermissionIfRequired(this, Constant.PERMISSION_REQUEST_PHONE)) {
                        new Thread() {
                            @Override
                            public void run() {
                                startTransaction();
                            }
                        }.start();
                    }
                } catch (Exception | Error e) {
                    e.printStackTrace();
                    try {
                        JSONObject json = new JSONObject();
                        json.put(Constant.OPERATION_CODE, Constant.FATAL_EXCEPTION_CODE);
                        json.put(Constant.OPERATION_MSG, Constant.FATAL_EXCEPTION_DESC);
                        _result.success(json.toString());
                        finish();
                    } catch (JSONException exp) {
                        exp.printStackTrace();
                        finish();
                    }
                }
                break;
        }
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
        Log.d(Constant.TAG,"onDestroy invoked");
    }

    private void cancelScan() {
        SSMPOSSDK.getInstance().getTransaction().abortTransaction();

        try {
            JSONObject json = new JSONObject();
            json.put(Constant.OPERATION_CODE, Constant.SCAN_CANCELLED_CODE);
            json.put(Constant.OPERATION_MSG, Constant.SCAN_CANCELLED_DESC);
            _result.success(json.toString());
            finish();
        } catch (JSONException  | RuntimeException e) {
            e.printStackTrace();
            finish();
        }
    }

    private void manualInput() {
        SSMPOSSDK.getInstance().getTransaction().abortTransaction();

        try {
            JSONObject json = new JSONObject();
            json.put(Constant.OPERATION_CODE, Constant.MANUAL_INPUT_CODE);
            json.put(Constant.OPERATION_MSG, Constant.MANUAL_INPUT_DESC);
            _result.success(json.toString());
            finish();
        } catch (JSONException  | RuntimeException e) {
            e.printStackTrace();
            finish();
        }
    }

    private void startTransaction() {

        try {
            Log.d(Constant.TAG, "startTransaction()");

            String amount = tvAmount.getText().toString();
            amount = amount.replaceAll("[$,.]", "");
            Log.d(Constant.TAG, "amount :: " + amount);

            MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
                    .setAmount(amount)
                    .build();

            SSMPOSSDK.getInstance().getTransaction().startTransaction(this, transactionalParams, new MPOSTransaction.TransactionEvents() {

                @Override
                public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {

                    Log.d(Constant.TAG, "onTransactionResult :: " + result);

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            if(result == TransactionSuccessful) {
                                String outcome = "Status Code :: " + transactionOutcome.getStatusCode() + "\n";
                                outcome += "Status Message :: " + transactionOutcome.getStatusMessage() + "\n";
                                outcome += "Approval Code :: " + transactionOutcome.getApprovalCode() + "\n";
                                outcome += "Transaction ID :: " + transactionOutcome.getTransactionID() + "\n";
                                outcome += "Card No :: " + transactionOutcome.getCardNo() + "\n";
                                outcome += "Card Type :: " + transactionOutcome.getCardType() + "\n";
                                outcome += "Cardholder Name :: " + transactionOutcome.getCardHolderName() + "\n";
                                outcome += "Reference No :: " + transactionOutcome.getReferenceNo() + "\n";
                                outcome += "Acquirer ID :: " + transactionOutcome.getAcquirerID() + "\n";
                                outcome += "Application Cryptogram :: " + transactionOutcome.getApplicationCryptogram() + "\n";
                                outcome += "Terminal Verification Results :: " + transactionOutcome.getTerminalVerificationResults() + "\n";
                                outcome += "Transaction Status Info :: " + transactionOutcome.getTransactionStatusInfo() + "\n";
                                outcome += "Merchant Identifier :: " + transactionOutcome.getMerchantIdentifier() + "\n";
                                outcome += "Terminal Identifier :: " + transactionOutcome.getTerminalIdentifier() + "\n";
                                outcome += "Application ID :: " + transactionOutcome.getAid() + "\n";
                                outcome += "Invoice No :: " + transactionOutcome.getInvoiceNo() + "\n";
                                outcome += "Contactless CVM Type :: " + transactionOutcome.getContactlessCVMType();
                                Log.d(Constant.TAG, outcome);

                                tvMaskPanNo.setText(transactionOutcome.getCardNo());
                                ivUiEvent.setText(transactionOutcome.getStatusMessage());
                                ivUiEvent.setText(instructions[7]);
                                pbWaiting.setVisibility(View.GONE);
                                ivChecked.setVisibility(View.VISIBLE);
                                ((Animatable) ivChecked.getDrawable()).start();

                                try {
                                    JSONObject json = new JSONObject();
                                    json.put(Constant.OPERATION_CODE, Constant.SUCCESSFULLY_SCAN_CODE);
                                    json.put(Constant.OPERATION_MSG, Constant.SUCCESSFULLY_SCAN_DESC);
                                    json.put(Constant.STATUS_CODE, transactionOutcome.getStatusCode());
                                    json.put(Constant.STATUS_MESSAGE, transactionOutcome.getStatusMessage());
                                    json.put(Constant.APPROVAL_CODE, transactionOutcome.getApprovalCode());
                                    json.put(Constant.TRANSACTION_ID, transactionOutcome.getTransactionID());
                                    json.put(Constant.CARD_NO, transactionOutcome.getCardNo());
                                    json.put(Constant.CARD_TYPE, transactionOutcome.getCardType());
                                    json.put(Constant.CARDHOLDER_NAME, transactionOutcome.getCardHolderName());
                                    json.put(Constant.REFERENCE_NO, transactionOutcome.getReferenceNo());
                                    json.put(Constant.ACQUIRER_ID, transactionOutcome.getAcquirerID());
                                    json.put(Constant.APPLICATION_CRYPTOGRAM, transactionOutcome.getApplicationCryptogram());
                                    json.put(Constant.TERMINAL_VERIFICATION_RESULTS, transactionOutcome.getTerminalVerificationResults());
                                    json.put(Constant.TRANSACTION_STATUS_INFO, transactionOutcome.getTransactionStatusInfo());
                                    json.put(Constant.MERCHANT_IDENTIFIER, transactionOutcome.getMerchantIdentifier());
                                    json.put(Constant.TERMINAL_IDENTIFIER, transactionOutcome.getTerminalIdentifier());
                                    json.put(Constant.AID, transactionOutcome.getAid());
                                    json.put(Constant.INVOICE_NO, transactionOutcome.getInvoiceNo());
                                    json.put(Constant.CONTACTLESS_CVM_TYPE, transactionOutcome.getContactlessCVMType());
                                    _result.success(json.toString());
                                    finish();
                                } catch (JSONException | RuntimeException e) {
                                    e.printStackTrace();
                                    finish();
                                }
                                // new Handler().postDelayed(new Runnable() {
                                //     @Override
                                //     public void run() {
                                //         finish();
                                //     }
                                // }, 1000);
                            } else {
                                if(transactionOutcome != null) {
                                    Log.d(Constant.TAG, transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                                    try {
                                        JSONObject json = new JSONObject();
                                        json.put(Constant.OPERATION_CODE, Constant.SCAN_FAILURE_CODE);
                                        json.put(Constant.OPERATION_MSG, Constant.SCAN_FAILURE_DESC);
                                        json.put(Constant.STATUS_MESSAGE, transactionOutcome.getStatusMessage());
                                        json.put(Constant.STATUS_CODE, transactionOutcome.getStatusCode());
                                        _result.success(json.toString());
                                        finish();
                                    } catch (JSONException | RuntimeException e) {
                                        e.printStackTrace();
                                        finish();
                                    }
                                }
                            }
                        }
                    });

                }

                @Override
                public void onTransactionUIEvent(int event) {
                    Log.d(Constant.TAG, "onTransactionUIEvent :: " + event);
                    runOnUiThread(() -> {

                        JSONObject json = new JSONObject();
                        switch (event) {
                            case TransactionUIEvent.PresentCard:
                                ivUiEvent.setText(instructions[1]);
                                break;
                            case TransactionUIEvent.CardPresented:
                                ivWaveNow.setVisibility(View.GONE);
                                pbWaiting.setVisibility(View.VISIBLE);
                                ivUiEvent.setText(instructions[2]);
                                break;
                            case TransactionUIEvent.Authorising:
                                ivUiEvent.setText(instructions[3]);
                                break;
                            case TransactionUIEvent.PresentCardTimeout:
                                ivWaveNow.setVisibility(View.VISIBLE);
                                pbWaiting.setVisibility(View.GONE);
                                ivUiEvent.setText(instructions[4]);
                                try {
                                    json.put(Constant.OPERATION_CODE, Constant.TIME_OUT_CODE);
                                    json.put(Constant.OPERATION_MSG, Constant.TIME_OUT_DESC);
                                    _result.success(json.toString());
                                    finish();
                                } catch (JSONException | RuntimeException e) {
                                    e.printStackTrace();
                                    finish();
                                }
                                break;
                            case TransactionUIEvent.CardReadOk:
                                ivUiEvent.setText(instructions[5]);
                                // you may customize card reads OK sound & vibration, below is some example
                                ToneGenerator toneGenerator = new ToneGenerator(AudioManager.STREAM_MUSIC, ToneGenerator.MAX_VOLUME);
                                toneGenerator.startTone(ToneGenerator.TONE_DTMF_P, 500);

                                Vibrator v = (Vibrator) TapCardActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
                                if (v.hasVibrator())
                                {
                                    v.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
                                }
                                break;
                            case TransactionUIEvent.CardReadError:
                                ivUiEvent.setText(instructions[6]);
                                try {
                                    json.put(Constant.OPERATION_CODE, Constant.SCAN_FAILURE_CODE);
                                    json.put(Constant.OPERATION_MSG, Constant.SCAN_FAILURE_DESC);
                                    _result.success(json.toString());
                                    finish();
                                } catch (JSONException | RuntimeException e) {
                                    e.printStackTrace();
                                    finish();
                                }
                                break;
                            case TransactionUIEvent.Unknown:
                            case TransactionUIEvent.CancelPin:
                            case TransactionUIEvent.EnterPin:
                            case TransactionUIEvent.PinBypass:
                            case TransactionUIEvent.PinEnterTimeout:
                            case TransactionUIEvent.PinEntered:
                            case TransactionUIEvent.RequestSignature:
                                try {
                                    json.put(Constant.OPERATION_CODE, Constant.UNHANDLED_EVENT_CODE);
                                    json.put(Constant.OPERATION_MSG, Constant.UNHANDLED_EVENT_DESC);
                                    _result.success(json.toString());
                                    finish();
                                } catch (JSONException | RuntimeException e) {
                                    e.printStackTrace();
                                    finish();
                                }
                                break;
                        }

                    });
                }
            });

        } catch (Exception | Error e) {
            e.printStackTrace();
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.FATAL_EXCEPTION_CODE);
                json.put(Constant.OPERATION_MSG, Constant.FATAL_EXCEPTION_DESC);
                _result.success(json.toString());
                finish();
            } catch (JSONException | RuntimeException exception) {
                exception.printStackTrace();
                finish();
            }
        }
    }

}
