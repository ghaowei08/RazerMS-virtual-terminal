This is a virtual terminal SDK that can be installed on Android phones with NFC, developed by [RazerMS](https://github.com/hisyamadzha/Fasstap-Flutter).

### Getting Started
- Required RazerMS keystore.jks file. Place this file inside the `android -> app` directory.
- Generate a file named key.properties and place it inside the `android` directory. The content should be as follows:
```
applicationId=''
storePassword=''
keyPassword=''
keyAlias=''
storeFile=''
```
- Copy .env.example and rename it to .env, then fill in the necessary content.

All installations have been completed.

### Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

### License

[MIT](https://choosealicense.com/licenses/mit/)
