package com.rms.mobile.fasstap;

public class Constant {

    // Success return
    public final static String INITIALISED_SUCCESS_CODE = "200";
    public final static  String INITIALISED_SUCCESS_DESC = "Initialised success";

    public final static String NO_PERMISSION_REQUIRE_CODE = "201";
    public final static String NO_PERMISSION_REQUIRE_DESC = "No permission require";

    public final static String PERMISSION_GRANTED_CODE = "202";
    public final static String PERMISSION_GRANTED_DESC = "Permission granted";

    public final static String SUCCESSFULLY_SCAN_CODE = "203";
    public final static String SUCCESSFULLY_SCAN_DESC = "Successfully scanned";

    public final static String SUCCESSFULLY_LOGGED_CODE = "204";
    public final static String SUCCESSFULLY_LOGGED_DESC = "Login successful";

    public final static String SUCCESSFULLY_ACTIVATED_CODE = "205";
    public final static String SUCCESSFULLY_ACTIVATED_DESC = "Activate successful";

    public final static String VOID_APPROVED_CODE = "206";
    public final static String VOID_APPROVED_DESC = "Void approved";

    public final static String QUERY_STATUS_CODE = "207";
    public final static String QUERY_STATUS_DESC = "Get transaction status success";

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

    public final static String SCAN_FAILURE_CODE = "306";
    public final static String SCAN_FAILURE_DESC = "Card fails to be scanned";

    public final static String FATAL_EXCEPTION_CODE = "307";
    public final static String FATAL_EXCEPTION_DESC = "Fatal exception";

    public final static String MANUAL_INPUT_CODE = "308";
    public final static String MANUAL_INPUT_DESC = "Decide to proceed by manual input";

    public final static String UNHANDLED_EVENT_CODE = "309";
    public final static String UNHANDLED_EVENT_DESC = "Unhandled transaction UI event";

    public final static String TIME_OUT_CODE = "310";
    public final static String TIME_OUT_DESC = "Time out";

    public final static String NFC_NOT_SUPPORTED_CODE = "311";
    public final static String NFC_NOT_SUPPORTED_DESC = "NFC is not supported";

    public final static String NFC_NOT_ENABLED_CODE = "312";
    public final static String NFC_NOT_ENABLED_DESC = "NFC is not enabled";

    public final static String INTERNAL_ERROR_CODE = "999";
    public final static String INTERNAL_ERROR_DESC = "Internal error";

    public final static int REQUEST_CODE_START_INITIALISE = 10090;
    public final static int REQUEST_CODE_START_SCAN = 10092;
    public final static int REQUEST_CODE_STOP_SCAN = 10093;
    public final static int REQUEST_CODE_FINISH_SCAN = 10094;


    public final static String STATUS_CODE = "statusCode";
    public final static String STATUS_MESSAGE = "statusMessage";
    public final static String APPROVAL_CODE = "approvalCode";
    public final static String TRANSACTION_ID = "transactionID";
    public final static String CARD_NO = "cardNo";
    public final static String CARD_TYPE = "cardType";
    public final static String CARDHOLDER_NAME = "cardholderName";
    public final static String REFERENCE_NO = "referenceNo";
    public final static String ACQUIRER_ID = "acquirerID";
    public final static String APPLICATION_CRYPTOGRAM = "applicationCryptogram";
    public final static String TERMINAL_VERIFICATION_RESULTS = "terminalVerificationResults";
    public final static String TRANSACTION_STATUS_INFO = "transactionStatusInfo";
    public final static String MERCHANT_IDENTIFIER = "merchantIdentifier";
    public final static String TERMINAL_IDENTIFIER = "terminalIdentifier";
    public final static String AID = "aid";
    public final static String INVOICE_NO = "invoiceNo";
    public final static String CONTACTLESS_CVM_TYPE = "contactlessCVMType";

    public final static String TAG = "FasstapSDK";
    public final static int PERMISSIONS_REQUEST_LOCATION = 99;
    public final static int PERMISSION_REQUEST_PHONE = 1000;
    public final static String ERROR_CODE_ACTIVATION_REQUIRED = "14014";
    public final static String ERROR_CODE_LOGIN_REQUIRED = "14020";

    public final static String CARD_TYPE_VISA = "0";
    public final static String CARD_TYPE_MASTER = "1";
    public final static String CARD_TYPE_AMEX = "2";
    public final static String CARD_TYPE_JCB = "3";
    public final static String CARD_TYPE_UNIONPAY = "7";
    public final static String CARD_TYPE_DEBIT = "8";
    public final static String CARD_TYPE_TPN = "11";

    public final static String NO_CVM = "00";
    public final static String SIGNATURE = "01";
    public final static String ONLINE_PIN = "02";

    public final static String OPERATION_CODE = "operationCode";
    public final static String OPERATION_MSG = "operationMsg";
}