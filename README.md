# ASF Tariffe (Android - Kotlin)

Progetto Android nativo (Kotlin) che legge un database SQLite **offline** (`app/src/main/assets/asf_tariffe.db`) e restituisce le soluzioni tariffarie per **Origine** + **Destinazione**.

## Build APK con GitHub Actions (senza installare nulla sul PC)
1. Carica questo progetto su GitHub.
2. Vai su **Actions** → workflow **Build APK (Debug)** → **Run workflow**.
3. A build finita, scarica l'artifact **app-debug.apk**.

## Sviluppo in locale
Apri la cartella con **Android Studio** e premi **Run**.

## Struttura
- `app/src/main/assets/asf_tariffe.db` → database SQLite.
- `MainActivity` → UI + query al DB.
- `Db` → copia del DB da `assets/` a `filesDir` e apertura in sola lettura.
