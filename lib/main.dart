import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';

Future main() async {
  await dotenv.load(fileName: ".env");
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Flutter Demo',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: MyHomePage(title: 'Fasstap'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  MyHomePage({Key? key, this.title}) : super(key: key);

  final String? title;

  @override
  _MyHomePageState createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {
  final amountCtrl = TextEditingController(text: '5');
  final invoiceNoCtrl = TextEditingController(text: '');
  final scrollCtrl = ScrollController();
  final uniqueId = dotenv.env['UNIQUE_ID'];
  final developerId = dotenv.env['DEVELOPER_ID'];

  static const _platform = const MethodChannel('flutter.native/fasstap');
  String logs = '';

  Future<String> _onGetLocationPermission() async {
    try {
      final String result = await _platform.invokeMethod('getPermission');
      debugPrint('Platform channel result : $result');
      setState(() {
        logs = JsonEncoder.withIndent('    ').convert(json.decode(result));
        scrollCtrl.animateTo(scrollCtrl.position.maxScrollExtent,
            duration: Duration(milliseconds: 500), curve: Curves.ease);
      });
      return result;
    } on PlatformException catch (e) {
      debugPrint("Failed to Invoke: '${e.message}'.");
      return '${e.message}';
    } on MissingPluginException catch (e) {
      debugPrint("Failed to Invoke: '${e.message}'.");
      return '${e.message}';
    }
  }

  Future<String> _onInitialise() async {
    try {
      Map<String, dynamic> args = <String, dynamic>{};
      args.putIfAbsent("uniqueId", () => "'$uniqueId'");
      args.putIfAbsent("developerId", () => "'$developerId'");
      args.putIfAbsent(
          "hostSoftSpace", () => "'${dotenv.env['HOST_SOFT_SPACE']}'");
      args.putIfAbsent(
          "hostCertPinning", () => "'${dotenv.env['HOST_CERT_PINNING']}'");
      args.putIfAbsent(
          "googleApiKey", () => "'${dotenv.env['GOOGLE_API_KEY']}'");
      args.putIfAbsent("accessKey", () => "'${dotenv.env['ACCESS_KEY']}'");
      args.putIfAbsent("secretKey", () => "'${dotenv.env['SECRET_KEY']}'");
      args.putIfAbsent("isProduction", () => true);
      print(args);
      final String result = await _platform.invokeMethod('initialise', args);
      debugPrint('Platform channel result : $result');
      setState(() {
        logs += JsonEncoder.withIndent('    ').convert(json.decode(result));
        scrollCtrl.animateTo(scrollCtrl.position.maxScrollExtent,
            duration: Duration(milliseconds: 500), curve: Curves.ease);
      });
      return result;
    } on PlatformException catch (e) {
      debugPrint("Failed to Invoke: '${e.message}'.");
      return '${e.message}';
    } on MissingPluginException catch (e) {
      debugPrint("Failed to Invoke: '${e.message}'.");
      return '${e.message}';
    }
  }

  Future<String> _onRefreshToken() async {
    try {
      Map<String, dynamic> args = <String, dynamic>{};
      args.putIfAbsent("uniqueId", () => "$uniqueId");
      args.putIfAbsent("developerId", () => "$developerId");
      final String result = await _platform.invokeMethod('refreshToken', args);
      debugPrint('Platform channel result : $result');
      setState(() {
        logs += JsonEncoder.withIndent('    ').convert(json.decode(result));
        scrollCtrl.animateTo(scrollCtrl.position.maxScrollExtent,
            duration: Duration(milliseconds: 500), curve: Curves.ease);
      });
      return result;
    } on PlatformException catch (e) {
      debugPrint("Failed to Invoke: '${e.message}'.");
      return '${e.message}';
    } on MissingPluginException catch (e) {
      debugPrint("Failed to Invoke: '${e.message}'.");
      return '${e.message}';
    }
  }

  Future<String> _onStartTransaction() async {
    try {
      var instructions = "'Please tap the card at the back of the devices|"
          "Tap your card now|"
          "Card detected|"
          "Authorising|"
          "Time out|"
          "Read card success|"
          "Read card error|"
          "Approved'";

      Map<String, dynamic> args = <String, dynamic>{};
      args.putIfAbsent("orderId", () => '0123456789');
      args.putIfAbsent(
          "amount", () => amountCtrl.text.replaceAll(new RegExp(r'[,]'), ''));
      args.putIfAbsent("currency", () => 'RM');
      args.putIfAbsent("cancel", () => 'Cancel');
      args.putIfAbsent("instructions", () => instructions);
      debugPrint(instructions);

      final String result =
          await _platform.invokeMethod('startTapCardActivity', args);
      debugPrint('Platform channel result : $result');
      setState(() {
        logs += JsonEncoder.withIndent('    ').convert(json.decode(result));
        scrollCtrl.animateTo(scrollCtrl.position.maxScrollExtent,
            duration: Duration(milliseconds: 500), curve: Curves.ease);
      });
      return result;
    } on PlatformException catch (e) {
      debugPrint("Failed to Invoke: '${e.message}'.");
      return '${e.message}';
    } on MissingPluginException catch (e) {
      debugPrint("Failed to Invoke: '${e.message}'.");
      return '${e.message}';
    }
  }

  Future<String> _onVoidTransaction() async {
    try {
      var instructions = "'Please tap the card at the back of the devices|"
          "Tap your card now|"
          "Card detected|"
          "Authorising|"
          "Time out|"
          "Read card success|"
          "Read card error|"
          "Approved'";

      Map<String, dynamic> args = <String, dynamic>{};
      args.putIfAbsent("transactionId", () => '79406');
      debugPrint(instructions);

      final String result =
          await _platform.invokeMethod('voidTransaction', args);
      debugPrint('Platform channel result : $result');
      setState(() {
        logs += JsonEncoder.withIndent('    ').convert(json.decode(result));
        scrollCtrl.animateTo(scrollCtrl.position.maxScrollExtent,
            duration: Duration(milliseconds: 500), curve: Curves.ease);
      });
      return result;
    } on PlatformException catch (e) {
      debugPrint("Failed to Invoke: '${e.message}'.");
      return '${e.message}';
    } on MissingPluginException catch (e) {
      debugPrint("Failed to Invoke: '${e.message}'.");
      return '${e.message}';
    }
  }

  @override
  void initState() {
    _onGetLocationPermission().then((permissionResult) {
      var data = json.decode(permissionResult);
      if (data['operationCode'].toString().contains('202')) {
        _onInitialise().then((permissionResult) {
          var data = json.decode(permissionResult);
          if (data['operationCode'].toString().contains('201')) {
            _onRefreshToken().then((permissionResult) {
              var data = json.decode(permissionResult);
              if (data['operationCode'].toString().contains('200')) {}
            });
          }
        });
      }
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
          title: Text(widget.title!),
          backgroundColor: Colors.blue,
          actions: [
            IconButton(
              icon: Icon(Icons.play_arrow),
              onPressed: _onInitialise,
              tooltip: 'Initialize',
            ),
            IconButton(
                icon: Icon(Icons.refresh),
                onPressed: _onRefreshToken,
                tooltip: 'Refresh Token'),
          ]),
      body: Container(
        padding: EdgeInsets.all(8.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Expanded(
              child: Row(
                children: [
                  Expanded(
                      flex: 2,
                      child: Column(
                        mainAxisAlignment: MainAxisAlignment.start,
                        children: [
                          Flexible(
                            child: TextFormField(
                              controller: invoiceNoCtrl,
                              cursorColor: Colors.white,
                              style: TextStyle(color: Colors.black),
                              keyboardType: TextInputType.number,
                              textInputAction: TextInputAction.next,
                              decoration: InputDecoration(
                                labelText: 'Invoice No',
                                labelStyle: TextStyle(color: Colors.grey),
                                isDense: true,
                                filled: true,
                                fillColor: Colors.white.withOpacity(0.5),
                                focusedBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(4.0)),
                                enabledBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(4.0)),
                              ),
                              validator: (value) {
                                if (value!.isEmpty) {
                                  return 'This field is required';
                                }
                                return null;
                              },
                            ),
                          ),
                          SizedBox(
                            height: 15,
                          ),
                          Flexible(
                            child: TextFormField(
                              controller: amountCtrl,
                              cursorColor: Colors.white,
                              style: TextStyle(color: Colors.black),
                              keyboardType: TextInputType.number,
                              textInputAction: TextInputAction.next,
                              decoration: InputDecoration(
                                labelText: 'Amount',
                                labelStyle: TextStyle(color: Colors.grey),
                                isDense: true,
                                filled: true,
                                fillColor: Colors.white.withOpacity(0.5),
                                focusedBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(4.0)),
                                enabledBorder: OutlineInputBorder(
                                    borderSide: BorderSide(color: Colors.grey),
                                    borderRadius: BorderRadius.circular(4.0)),
                              ),
                              validator: (value) {
                                if (value!.isEmpty) {
                                  return 'This field is required';
                                }
                                return null;
                              },
                            ),
                          )
                        ],
                      )),
                  Expanded(
                      flex: 1,
                      child: Column(
                        children: [
                          ElevatedButton(
                            child: const Text('Payment',
                                style: TextStyle(fontSize: 14.0)),
                            style: TextButton.styleFrom(
                              fixedSize: Size(100, 25),
                              primary: Colors.white,
                              backgroundColor: Colors.blue,
                              shape: RoundedRectangleBorder(
                                  borderRadius: new BorderRadius.circular(6.0)),
                            ),
                            onPressed: _onStartTransaction,
                          ),
                          SizedBox(
                            height: 15,
                          ),
                          ElevatedButton(
                            child: const Text('Void',
                                style: TextStyle(fontSize: 14.0)),
                            style: TextButton.styleFrom(
                              fixedSize: Size(100, 25),
                              primary: Colors.white,
                              backgroundColor: Colors.blue,
                              shape: RoundedRectangleBorder(
                                  borderRadius: new BorderRadius.circular(6.0)),
                            ),
                            onPressed: _onVoidTransaction,
                          )
                        ],
                      ))
                ],
              ),
            ),
            Expanded(
              child: Column(
                children: [
                  Container(
                    padding: EdgeInsets.symmetric(vertical: 8.0),
                    alignment: Alignment.centerLeft,
                    child: Text('Log :'),
                  ),
                  Expanded(
                    child: Container(
                      width: double.infinity,
                      decoration: BoxDecoration(
                        color: Colors.white,
                        border: Border.all(color: Colors.grey, width: 1.0),
                        borderRadius: BorderRadius.all(Radius.circular(6.0)),
                      ),
                      child: SingleChildScrollView(
                        controller: scrollCtrl,
                        child: Column(
                          mainAxisAlignment: MainAxisAlignment.start,
                          children: [
                            Container(
                              width: double.infinity,
                              padding: EdgeInsets.all(4.0),
                              child: Text(logs),
                            )
                          ],
                        ),
                      ),
                    ),
                  )
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}
