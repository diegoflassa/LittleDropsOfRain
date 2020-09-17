[Leia em Português-Brasileiro](README.ptBR.md)
# LittleDropsOfRain

LittleDropsOfRain is a simple store intented to facilitate the discovery of new products made by its owner


## Getting the code

Get the latest code from GitHub using Git or download the repository as a ZIP file.
([Download](https://github.com/diegoflassa/LittleDropsOfRain/archive/master.zip))

Or, clone it, using:

    git clone https://github.com/diegoflassa/LittleDropsOfRain.git


## Building

This project uses [Gradle](https://gradle.org/). Please refer to the following for additional information on how to [install it](https://gradle.org/install/). It is not needed, tought.
You may build the library from the source by simply running the command bellow, in the console, from the project root folder.

Note that an Keystore must be configured to [sign](https://developer.android.com/studio/publish/app-signing) the relase apk!
Its properties may be found at [gradle.properties](https://github.com/diegoflassa/LittleDropsOfRain/blob/master/gradle.properties)

Dont forget to add your [google-services.json](https://support.google.com/firebase/answer/7015592?hl=en) file to the projetc!
And, optionally, you [\*-firebase-adminsdk-\*.json](https://firebase.google.com/docs/admin/setup) file. Add it in to the raw folder

```gradle
gradlew build
```

The following files will be generated in the "app\build\outputs\apk" folder

```
* app\build\outputs\apk\debug\app-debug.apk
* or
* app\build\outputs\apk\release\app-release-signed.apk
* or
* app\build\outputs\apk\release\app-release-unsigned.apk
```

To install, use the command

```gradle
gradlew installDebug
or
gradlew installRelease
```

## QuickStart

Nothing. Just Install and navigate!


## History changes

* See [HISTORY.md](HISTORY.md)


## Contributing changes

* See [CONTRIBUTING.md](CONTRIBUTING.md)


## Licensing

* See [LICENSE](LICENSE)