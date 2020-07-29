package com.rms.mobile.fasstap;

public class Constant {

    // Success return
    public final static String INITIALISED_SUCCESS_CODE = "200";
    public final static  String INITIALISED_SUCCESS_DESC = "Initialised success";

    public final static String NO_PERMISSION_REQUIRE_CODE = "201";
    public final static String NO_PERMISSION_REQUIRE_DESC = "No permission require";

    public final static String PERMISSION_GRANTED_CODE = "202";
    public final static String PERMISSION_GRANTED_DESC = "Permission granted";

    // Failure return
    public final static String INITIALISED_FAILED_CODE = "300";
    public final static String INITIALISED_FAILED_DESC = "Initialised failed";

    public final static String REQUIRE_PERMISSION_CODE = "301";
    public final static  String REQUIRE_PERMISSION_DESC = "Require permission before proceed";

    public final static String PERMISSION_NOT_GRANTED_CODE = "302";
    public final static String PERMISSION_NOT_GRANTED_DESC = "Permission not granted";

    public final static String REQUIRE_ACTIVATION_CODE = "303";
    public final static String REQUIRE_ACTIVATION_DESC = "Require activation before proceed";

    public final static String REQUIRE_LOGIN_CODE = "304";
    public final static String REQUIRE_LOGIN_DESC = "Require login before proceed";

    public final static String SCAN_CANCELLED_CODE = "305";
    public final static String SCAN_CANCELLED_DESC = "Scan card has been cancelled";



    public final static int REQUEST_CODE_START_SCAN = 1;


    public final static String STATUS_CODE = "STATUS_CODE";
    public final static String STATUS_MESSAGE = "STATUS_MESSAGE";
    public final static String APPROVAL_CODE = "APPROVAL_CODE";
    public final static String TRANSACTION_ID = "TRANSACTION_ID";
    public final static String CARD_NO = "CARD_NO";
    public final static String CARD_TYPE = "CARD_TYPE";
    public final static String CARDHOLDER_NAME = "CARDHOLDER_NAME";
    public final static String REFERENCE_NO = "REFERENCE_NO";

    public final static String TAG = "FasstapSDK";
    public final static int PERMISSIONS_REQUEST_LOCATION = 99;
    public final static int PERMISSION_REQUEST_PHONE = 1000;
    public final static String ERROR_CODE_ACTIVATION_REQUIRED = "14014";
    public final static String ERROR_CODE_LOGIN_REQUIRED = "14020";

    public final static String CARD_TYPE_VISA = "0";
    public final static String CARD_TYPE_MASTER = "1";
    public final static String CARD_TYPE_DEBIT = "8";

    public final static String ERROR_CODE = "ERROR_CODE";
    public final static String ERROR_MSG = "ERROR_MSG";
}