[Read in English-American](README.md)
# LittleDropsOfRain

LittleDropsOfRain é uma simples loja cujo objetivo é facilitar a descoberta de novos produtos criados por sua dona


## Obtendo o código

Obtenha a versão mais recente do código no Github utilizando o Git, ou baixe o repositório em um arquivo ZIP.
([Download](https://github.com/diegoflassa/LittleDropsOfRain/archive/master.zip))

Ou, clone, utilizando:

    git clone https://github.com/diegoflassa/LittleDropsOfRain.git


## Building

Este projeto usa [Gradle](https://gradle.org/). Por favor, veja o link a seguir para informações adicionais sobre sua [install it](https://gradle.org/install/). Contudo, não é necessário
Você pode gerar o apk a partir do fonte através do commando abaixo, no console, na pasta-raiz do projeto.

Note que uma Keystore deve ser configurada para [assinar](https://developer.android.com/studio/publish/app-signing) a versão relase do apk!
Suas propriedades podem ser encontradas no arquivo [gradle.properties](https://github.com/diegoflassa/LittleDropsOfRain/blob/master/gradle.properties)

Não esqueça de adicionar seu arquivo [google-services.json](https://support.google.com/firebase/answer/7015592?hl=en) no projeto!
E, opcionalmente, seu arquivo [\*-firebase-adminsdk-\*.json](https://firebase.google.com/docs/admin/setup). Adicione-o na pasta raw

```gradle
gradlew build
```
Os seguintes arquivos serão gerados na pasta "app\build\outputs\apk"

```
* app\build\outputs\apk\debug\app-debug.apk
* ou
* app\build\outputs\apk\release\app-release-signed.apk
* or
* app\build\outputs\apk\release\app-release-unsigned.apk
```

Para instalar, utilize o comando

```gradle
gradlew installDebug
ou
gradlew installRelease
```

## QuickStart

Nada. Apenas instale e navege!


## Histórico de Mudanças

* Veja [HISTORY](HISTORY.ptBR.md)


## Contribuições

* Veja [CONTRIBUTING](CONTRIBUTING.ptBR.md)


## Licenciamento

* Veja [LICENSE](LICENSE)