import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() {
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
      home: MyHomePage(title: 'Flutter Fasstap Tester'),
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
  final amountCtrl = TextEditingController(text: '250');
  final scrollCtrl = ScrollController();
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
      args.putIfAbsent("uniqueId", () => "");
      args.putIfAbsent("developerId", () => "");
      args.putIfAbsent("hostSoftSpace", () => "''");
      args.putIfAbsent("hostCertPinning", () => "''");
      args.putIfAbsent("googleApiKey", () => "''");
      args.putIfAbsent("accessKey", () => "''");
      args.putIfAbsent("secretKey", () => "''");
      args.putIfAbsent("isProduction", () => false);
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
      args.putIfAbsent("uniqueId", () => "");
      args.putIfAbsent("developerId", () => "");
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
        backgroundColor: Color(0xFF44D62C),
      ),
      body: Container(
        padding: EdgeInsets.all(8.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: <Widget>[
            Container(
              width: double.infinity,
              height: 53.0,
              child: ElevatedButton(
                child:
                    const Text('Initialise', style: TextStyle(fontSize: 14.0)),
                style: TextButton.styleFrom(
                  primary: Colors.white,
                  backgroundColor: Colors.blue,
                  shape: RoundedRectangleBorder(
                      borderRadius: new BorderRadius.circular(6.0)),
                ),
                onPressed: _onInitialise,
              ),
            ),
            SizedBox(
              height: 8.0,
            ),
            Container(
              width: double.infinity,
              height: 53.0,
              child: ElevatedButton(
                child: const Text('Refresh Token',
                    style: TextStyle(fontSize: 14.0)),
                style: TextButton.styleFrom(
                  primary: Colors.white,
                  backgroundColor: Colors.blue,
                  shape: RoundedRectangleBorder(
                      borderRadius: new BorderRadius.circular(6.0)),
                ),
                onPressed: _onRefreshToken,
              ),
            ),
            SizedBox(
              height: 8.0,
            ),
            Row(
              children: [
                Expanded(
                  child: Container(
                    height: 53.0,
                    child: ElevatedButton(
                      child: const Text('Start Transaction',
                          style: TextStyle(fontSize: 14.0)),
                      style: TextButton.styleFrom(
                        primary: Colors.white,
                        backgroundColor: Colors.blue,
                        shape: RoundedRectangleBorder(
                            borderRadius: new BorderRadius.circular(6.0)),
                      ),
                      onPressed: _onStartTransaction,
                    ),
                  ),
                ),
                SizedBox(
                  width: 8.0,
                ),
                Expanded(
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
                ),
              ],
            ),
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
    );
  }
}
