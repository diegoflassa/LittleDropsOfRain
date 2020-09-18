[Leia em Português-Brasileiro](README.ptBR.md)
# LittleDropsOfRain

LittleDropsOfRain is a simple store intented to facilitate the discovery of new products made by its owner


## Getting the code

Get the latest code from GitHub using Git or download the repository as a ZIP file.
([Download](https://github.com/diegoflassa/LittleDropsOfRain/archive/master.zip))

Or clone it, using:

    git clone https://github.com/diegoflassa/LittleDropsOfRain.git


## Building

This project uses [Gradle](https://gradle.org/). Please refer to the following for additional information on how to [install it](https://gradle.org/install/). It is not needed, tought.
You may build the library from the source by simply running the command bellow, in the console, from the project root folder.

Note that an Keystore must be configured to [sign](https://developer.android.com/studio/publish/app-signing) the relase apk!
Its properties may be found at [gradle.properties](https://github.com/diegoflassa/LittleDropsOfRain/blob/master/gradle.properties)

Dont forget to add your [google-services.json](https://support.google.com/firebase/answer/7015592?hl=en) file to the projetc!
And, optionally, your [\*-firebase-adminsdk-\*.json](https://firebase.google.com/docs/admin/setup) file. Add it in to the raw folder,
and change its usage in [this file](https://github.com/diegoflassa/LittleDropsOfRain/blob/master/app/src/main/java/app/web/diegoflassa_site/littledropsofrain/ui/topic/SendTopicMessageFragment.kt)
[in this line](https://github.com/diegoflassa/LittleDropsOfRain/blob/9f5775af7e0b896f4d22142bc94fb0c7b6ea169d/app/src/main/java/app/web/diegoflassa_site/littledropsofrain/ui/topic/SendTopicMessageFragment.kt#L349)

```gradle
gradlew build
```

The following files will be generated in the "app\build\outputs\apk" folder

```
* app\build\outputs\apk\debug\littledropsofrain-debug*.apk
* or
* app\build\outputs\apk\release\littledropsofrain-release*.apk
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