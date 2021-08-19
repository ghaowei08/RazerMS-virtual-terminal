package com.rms.mobile.fasstap;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.flutter.Log;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugins.GeneratedPluginRegistrant;
import my.com.softspace.ssmpossdk.Environment;
import my.com.softspace.ssmpossdk.SSMPOSSDK;
import my.com.softspace.ssmpossdk.SSMPOSSDKConfiguration;
import my.com.softspace.ssmpossdk.transaction.MPOSTransaction;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionOutcome;
import my.com.softspace.ssmpossdk.transaction.MPOSTransactionParams;

import static my.com.softspace.ssmpossdk.transaction.MPOSTransaction.TransactionEvents.TransactionResult.TransactionSuccessful;


public class MainActivity extends FlutterActivity {

    private static final String CHANNEL = "flutter.native/fasstap";
    public static MethodChannel.Result _result;

    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        GeneratedPluginRegistrant.registerWith(flutterEngine);
        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
        .setMethodCallHandler(
            (call, result) -> {
                // Note: this method is invoked on the main thread.
                // TODO
                switch (call.method) {
                    case "getPermission":
                        _result = result;
                        getPermissionFasstap();
                        break;
                    case "initialise":
                        _result = result;
                        initialiseFasstap(call.arguments);
                        break;
                    case "refreshToken":
                        _result = result;
                        refreshTokenFasstap(call.arguments);
                        break;
                    case "startTransaction":
                        _result = result;
                        startTransactionFasstap(call.arguments);
                        break;
                    case "abortTransaction":
                        _result = result;
                        abortTransactionFasstap();
                        break;
                    case "voidTransaction":
                        _result = result;
                        voidTransactionFasstap(call.arguments);
                        break;
                    case "performSettlement":
                        _result = result;
                        performSettlementFasstap();
                        break;
                    case "startTapCardActivity":
                        _result = result;
                        startTransactionActivity(call.arguments);
                        break;
                    case "getTransactionStatus":
                        _result = result;
                        getTransactionStatusFasstap(call.arguments);
                        break;
                    default:
                        result.notImplemented();
                        break;
                }
            }
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        for (String permission : permissions) {
            Log.d(Constant.TAG, permission);
        }
        switch (requestCode) {
            case Constant.PERMISSIONS_REQUEST_LOCATION:
                try {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(Constant.TAG, Constant.PERMISSIONS_REQUEST_LOCATION + ": Permission has granted");
                        try {
                            JSONObject json = new JSONObject();
                            json.put(Constant.OPERATION_CODE, Constant.PERMISSION_GRANTED_CODE);
                            json.put(Constant.OPERATION_MSG, Constant.PERMISSION_GRANTED_DESC);
                            _result.success(json.toString());
                        } catch (JSONException | RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                } catch(ArrayIndexOutOfBoundsException e) {
                    Log.d(Constant.TAG, e.getMessage(), e);
                }
                break;
            case Constant.PERMISSION_REQUEST_PHONE:
                try {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(Constant.TAG, Constant.PERMISSION_REQUEST_PHONE + ":Permission has granted");

                        try {
                            JSONObject json = new JSONObject();
                            json.put(Constant.OPERATION_CODE, Constant.PERMISSION_GRANTED_CODE);
                            json.put(Constant.OPERATION_MSG, Constant.PERMISSION_GRANTED_DESC);
                            _result.success(json.toString());
                        } catch (JSONException | RuntimeException e) {
                            e.printStackTrace();
                        }
                    }
                } catch(ArrayIndexOutOfBoundsException e) {
                    Log.d(Constant.TAG, e.getMessage(), e);
                }
                break;
        }
    }

    private void startTransactionActivity(Object args) {
        try {
            JSONObject obj = new JSONObject(String.valueOf(args));
            Intent intent = new Intent(getApplicationContext(), TapCardActivity.class);
            intent.putExtra("requestCode", Constant.REQUEST_CODE_START_SCAN);
            intent.putExtra("amount", obj.getString("amount"));
            intent.putExtra("orderId", obj.getString("orderId"));
            intent.putExtra("currency", obj.getString("currency"));
            intent.putExtra("cancel", obj.getString("cancel"));
            intent.putExtra("instructions", obj.getString("instructions"));
            Log.d(Constant.TAG, obj.getString("instructions"));
            startActivity(intent);
        } catch (JSONException | RuntimeException e) {
            e.printStackTrace();
        }
    }

    private void getPermissionFasstap() {
        try {

            Log.d(Constant.TAG, "Get location permission");
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION }, Constant.PERMISSIONS_REQUEST_LOCATION);
            }else {
                try {
                    JSONObject json = new JSONObject();
                    json.put(Constant.OPERATION_CODE, Constant.PERMISSION_GRANTED_CODE);
                    json.put(Constant.OPERATION_MSG, Constant.PERMISSION_GRANTED_DESC);
                    _result.success(json.toString());
                } catch (JSONException | RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.INTERNAL_ERROR_CODE);
                json.put(Constant.OPERATION_MSG, e.getMessage());
                _result.success(json.toString());
            } catch (JSONException | RuntimeException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void initialiseFasstap(Object args) {
        try {

            Log.d(Constant.TAG, "init()");

            JSONObject obj = new JSONObject(String.valueOf(args));

            SSMPOSSDKConfiguration config = SSMPOSSDKConfiguration.Builder
                    .create()
                    .setAttestationHost(obj.getString("hostSoftSpace"))
                    .setAttestationHostCertPinning(obj.getString("hostCertPinning"))
                    .setAttestationHostReadTimeout(10000L)
                    .setAttestationRefreshInterval(300000L)
                    .setAttestationStrictHttp(true)
                    .setAttestationConnectionTimeout(30000L)
                    .setLibGoogleApiKey(obj.getString("googleApiKey"))
                    .setLibAccessKey(obj.getString("accessKey")) // please replace the access key provided by Soft Space
                    .setLibSecretKey(obj.getString("secretKey")) // please replace the secret key provided by Soft Space
                    .setUniqueID(obj.getString("uniqueId"))
                    .setDeveloperID(obj.getString("developerId"))// please set the userID shared by Soft Space
                    .setEnvironment(obj.getBoolean("isProduction") ? Environment.PROD: Environment.UAT)
                    .build();

            SSMPOSSDK.init(getApplicationContext(), config);

            Log.d(Constant.TAG,"SDK Version: " + SSMPOSSDK.getInstance().getSdkVersion());
            Log.d(Constant.TAG,"COTS ID: " + SSMPOSSDK.getInstance().getCotsId());

            if (!SSMPOSSDK.hasRequiredPermission(getApplicationContext())) {
                SSMPOSSDK.requestPermissionIfRequired(this, Constant.PERMISSION_REQUEST_PHONE);
            } else{
                try {
                    JSONObject json = new JSONObject();
                    json.put(Constant.OPERATION_CODE, Constant.NO_PERMISSION_REQUIRE_CODE);
                    json.put(Constant.OPERATION_MSG, Constant.NO_PERMISSION_REQUIRE_DESC);
                    _result.success(json.toString());
                } catch (JSONException | RuntimeException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception | Error e) {
            e.printStackTrace();
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.INTERNAL_ERROR_CODE);
                json.put(Constant.OPERATION_MSG, e.getMessage());
                _result.success(json.toString());
            } catch (JSONException | RuntimeException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void refreshTokenFasstap(Object args) {
        try {

            Log.d(Constant.TAG, "refreshToken()");

            JSONObject obj = new JSONObject(String.valueOf(args));

            SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().uniqueID = obj.getString("uniqueId");
            SSMPOSSDK.getInstance().getSSMPOSSDKConfiguration().developerID = obj.getString("developerId");
            SSMPOSSDK.getInstance().getTransaction().refreshToken(this, new MPOSTransaction.TransactionEvents() {

                @Override
                public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {
                    Log.d(Constant.TAG, "onTransactionResult :: " + result);

                    if(result == TransactionSuccessful) {
                        try {
                            JSONObject json = new JSONObject();
                            json.put(Constant.OPERATION_CODE, Constant.INITIALISED_SUCCESS_CODE);
                            json.put(Constant.OPERATION_MSG, Constant.INITIALISED_SUCCESS_DESC);
                            _result.success(json.toString());
                        } catch (JSONException | RuntimeException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if(transactionOutcome != null) {
                            Log.d(Constant.TAG, transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                            if (transactionOutcome.getStatusCode().equals(Constant.ERROR_CODE_LOGIN_REQUIRED)) {
                                Log.d(Constant.TAG, "Login required");

                                try {
                                    JSONObject json = new JSONObject();
                                    json.put(Constant.OPERATION_CODE, Constant.REQUIRE_LOGIN_CODE);
                                    json.put(Constant.OPERATION_MSG, Constant.REQUIRE_LOGIN_DESC);
                                    _result.success(json.toString());
                                } catch (JSONException | RuntimeException e) {
                                    e.printStackTrace();
                                }
                            } else if (transactionOutcome.getStatusCode().equals(Constant.ERROR_CODE_ACTIVATION_REQUIRED)) {
                                Log.d(Constant.TAG, "Activation required");

                                try {
                                    JSONObject json = new JSONObject();
                                    json.put(Constant.OPERATION_CODE, Constant.REQUIRE_ACTIVATION_CODE);
                                    json.put(Constant.OPERATION_MSG, Constant.REQUIRE_ACTIVATION_DESC);
                                    _result.success(json.toString());
                                } catch (JSONException | RuntimeException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d(Constant.TAG, "Unknown Status: " + transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                                try {
                                    JSONObject json = new JSONObject();
                                    json.put(Constant.OPERATION_CODE, transactionOutcome.getStatusCode());
                                    json.put(Constant.OPERATION_MSG, transactionOutcome.getStatusMessage());
                                    _result.success(json.toString());
                                } catch (JSONException | RuntimeException e) {
                                    e.printStackTrace();
                                }
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
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.INTERNAL_ERROR_CODE);
                json.put(Constant.OPERATION_MSG, e.getMessage());
                _result.success(json.toString());
            } catch (JSONException | RuntimeException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void startTransactionFasstap(Object args){
        try {
            Log.d(Constant.TAG, "startTransaction()");

            JSONObject obj = new JSONObject(String.valueOf(args));
            String amount = obj.getString("amount");
            amount = amount.replace(".", "");

            MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
                    .setAmount(amount)
                    .build();

            SSMPOSSDK.getInstance().getTransaction().startTransaction(this, transactionalParams, new MPOSTransaction.TransactionEvents() {

                @Override
                public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {

                    Log.d(Constant.TAG, "onTransactionResult :: " + result);

                    if(result == TransactionSuccessful) {
                        String outcome = "Status Code :: " + transactionOutcome.getStatusCode() + "\n";
                        outcome += "Status Message :: " + transactionOutcome.getStatusMessage() + "\n";
                        outcome += "Approval Code :: " + transactionOutcome.getApprovalCode() + "\n";
                        outcome += "Transaction ID :: " + transactionOutcome.getTransactionID() + "\n";
                        outcome += "Card No :: " + transactionOutcome.getCardNo() + "\n";
                        outcome += "Card Type :: " + transactionOutcome.getCardType() + "\n";
                        outcome += "Cardholder Name :: " + transactionOutcome.getCardHolderName() + "\n";
                        outcome += "Reference No :: " + transactionOutcome.getReferenceNo();
                        Log.d(Constant.TAG, outcome);

                        try {
                            JSONObject json = new JSONObject();
                            json.put(Constant.OPERATION_CODE, transactionOutcome.getStatusCode());
                            json.put(Constant.OPERATION_MSG, transactionOutcome.getStatusMessage());
                            _result.success(json.toString());
                        } catch (JSONException | RuntimeException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if(transactionOutcome != null) {
                            Log.d(Constant.TAG, transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                            try {
                                JSONObject json = new JSONObject();
                                json.put(Constant.OPERATION_CODE, transactionOutcome.getStatusCode());
                                json.put(Constant.OPERATION_MSG, transactionOutcome.getStatusMessage());
                                _result.success(json.toString());
                            } catch (JSONException | RuntimeException e) {
                                e.printStackTrace();
                            }
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
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.INTERNAL_ERROR_CODE);
                json.put(Constant.OPERATION_MSG, e.getMessage());
                _result.success(json.toString());
            } catch (JSONException | RuntimeException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void abortTransactionFasstap() {
        try {

            Log.d(Constant.TAG, "abortTransaction()");

            SSMPOSSDK.getInstance().getTransaction().abortTransaction();

            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.SCAN_CANCELLED_CODE);
                json.put(Constant.OPERATION_MSG, Constant.SCAN_CANCELLED_DESC);
                _result.success(json.toString());
            } catch (JSONException | RuntimeException e) {
                e.printStackTrace();
            }
        } catch (Exception | Error e){
            e.printStackTrace();
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.INTERNAL_ERROR_CODE);
                json.put(Constant.OPERATION_MSG, Constant.INTERNAL_ERROR_DESC);
                _result.success(json.toString());
            } catch (JSONException | RuntimeException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void voidTransactionFasstap(Object args) {
        try {

            Log.d(Constant.TAG, "voidTransaction()");

            JSONObject obj = new JSONObject(String.valueOf(args));

            MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
                    .setMPOSTransactionID(obj.getString("transactionId"))
                    .build();

            SSMPOSSDK.getInstance().getTransaction().voidTransaction(this, transactionalParams, new MPOSTransaction.TransactionEvents() {

                @Override
                public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {

                    Log.d(Constant.TAG, "onTransactionResult :: " + result);
                    
                    if(result == TransactionSuccessful) {
                        try {
                            JSONObject json = new JSONObject();
                            json.put(Constant.OPERATION_CODE, Constant.VOID_APPROVED_CODE);
                            json.put(Constant.OPERATION_MSG, Constant.VOID_APPROVED_DESC);
                            _result.success(json.toString());
                        } catch (JSONException | RuntimeException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if(transactionOutcome != null) {
                            Log.d(Constant.TAG, transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                            try {
                                JSONObject json = new JSONObject();
                                json.put(Constant.OPERATION_CODE, transactionOutcome.getStatusCode());
                                json.put(Constant.OPERATION_MSG, transactionOutcome.getStatusMessage());
                                _result.success(json.toString());
                            } catch (JSONException | RuntimeException e) {
                                e.printStackTrace();
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
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.INTERNAL_ERROR_CODE);
                json.put(Constant.OPERATION_MSG, Constant.INTERNAL_ERROR_DESC);
                _result.success(json.toString());
            } catch (JSONException | RuntimeException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void performSettlementFasstap() {
        try {

            Log.d(Constant.TAG, "performSettlement()");

            MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
                    .build();

            SSMPOSSDK.getInstance().getTransaction().performSettlement(this, transactionalParams, new MPOSTransaction.TransactionEvents() {

                @Override
                public void onTransactionResult(int result, MPOSTransactionOutcome transactionOutcome) {

                    Log.d(Constant.TAG, "onTransactionResult :: " + result);

                    if(result != TransactionSuccessful && transactionOutcome != null) {
                        Log.d(Constant.TAG, transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                        try {
                            JSONObject json = new JSONObject();
                            json.put(Constant.OPERATION_CODE, transactionOutcome.getStatusCode());
                            json.put(Constant.OPERATION_MSG, transactionOutcome.getStatusMessage());
                            _result.success(json.toString());
                        } catch (JSONException | RuntimeException e) {
                            e.printStackTrace();
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
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.INTERNAL_ERROR_CODE);
                json.put(Constant.OPERATION_MSG, Constant.INTERNAL_ERROR_DESC);
                _result.success(json.toString());
            } catch (JSONException | RuntimeException exception) {
                exception.printStackTrace();
            }
        }
    }

    private void getTransactionStatusFasstap(Object args) {
        try {

            Log.d(Constant.TAG, "getTransactionStatus()");

            JSONObject obj = new JSONObject(String.valueOf(args));

            MPOSTransactionParams transactionalParams = MPOSTransactionParams.Builder.create()
                    .setMPOSTransactionID(obj.getString("transactionId"))
                    .build();

            SSMPOSSDK.getInstance().getTransaction().getTransactionStatus(this, transactionalParams, new MPOSTransaction.TransactionEvents() {

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
                                outcome += "Contactless CVM Type :: " + transactionOutcome.getContactlessCVMType() + "\n";
                                Log.d(Constant.TAG, outcome);

                                try {
                                    JSONObject json = new JSONObject();
                                    json.put(Constant.OPERATION_CODE, Constant.QUERY_STATUS_CODE);
                                    json.put(Constant.OPERATION_MSG, Constant.QUERY_STATUS_DESC);
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
                                    _result.success(json.toString());
                                } catch (JSONException | RuntimeException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                if(transactionOutcome != null) {
                                    Log.d(Constant.TAG, transactionOutcome.getStatusCode() + " - " + transactionOutcome.getStatusMessage());

                                    try {
                                        JSONObject json = new JSONObject();
                                        json.put(Constant.STATUS_CODE, transactionOutcome.getStatusCode());
                                        json.put(Constant.STATUS_MESSAGE, transactionOutcome.getStatusMessage());
                                        _result.success(json.toString());
                                    } catch (JSONException | RuntimeException e) {
                                        e.printStackTrace();
                                    }
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
        } catch (Exception | Error e){
            e.printStackTrace();
            try {
                JSONObject json = new JSONObject();
                json.put(Constant.OPERATION_CODE, Constant.INTERNAL_ERROR_CODE);
                json.put(Constant.OPERATION_MSG, Constant.INTERNAL_ERROR_DESC);
                _result.success(json.toString());
            } catch (JSONException | RuntimeException exception) {
                exception.printStackTrace();
            }
        }
    }
}