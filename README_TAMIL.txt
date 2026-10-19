GAME TURBO FLOATING PRO - GitHub Build Project

PROJECT STRUCTURE
- build.gradle
- settings.gradle
- gradle.properties
- app/build.gradle
- app/src/main/AndroidManifest.xml
- app/src/main/java/com/gametools/turbo/MainActivity.java
- app/src/main/java/com/gametools/turbo/FloatingService.java
- .github/workflows/android.yml

ANDROID APK BUILD (PHONE ONLY)
1. Extract this ZIP on your phone.
2. Open your GitHub repository in a browser.
3. Upload the CONTENTS of this extracted folder to the repository root.
   Important: upload .github too. It contains workflows/android.yml.
   Do not upload this ZIP as a single file inside the repository.
4. Open the repository's Actions tab.
5. Select 'Build Android APK' and tap 'Run workflow' (or wait for push build).
6. Open the completed workflow run and download the artifact named
   'GameTurboFloating-debug-apk'. Extract the downloaded artifact ZIP to get app-debug.apk.

EXPECTED ROOT STRUCTURE
build.gradle
settings.gradle
gradle.properties
app/
.github/workflows/android.yml

NOTE
This project provides a floating tools panel and device stats. Android itself controls
CPU/GPU performance; this app cannot guarantee an FPS boost or modify game performance.
