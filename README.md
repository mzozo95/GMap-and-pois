# TierTest, simple map application

Build instructions:

Please use the Latest Android Studio with Kotlin plugin to build the application.

You can open the project for the first time as follows:
File -> New -> Import Project -> Select the source folder

Add your api key to the machine's `gradle.properties` file:

On Windows: `C:/Users/{username}/.gradle/gradle.properties` file:

```
mapApiSecretKey = "your secret key"
mapApiKey = "your maps api key"
```

Now you can build and run the project on a device or on an emulator.

Advice for testing:
- Set your fake location to Berlin centrum!

Used libraries: 
- AndroidX
- Retrofit
- OkHttp3
- Okhttp3:logging-interceptor
- Gson
- RxJava
- Dagger2
- EasyPermissions
- GMS Location
- Android Maps utils
- Mockito
- MockitoKotlin2
- Junit

Sample image of the application: 

![sample image](images/sample.PNG)
