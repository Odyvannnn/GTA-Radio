# 📻 GTA Radio

**GTA Radio** is an atmospheric Android app that allows you to listen to radio stations from the **Grand Theft Auto** game series as a continuous, real-time broadcast.

## 📥 How to Use

### 1. Install the App
- Download the APK from [Releases](https://github.com/Odyvannnn/GTA-Radio/releases).

### 2. Add Your Audio Files
The app **does not include radio stations by default** (due to copyright and size).  
You must add them **manually**:

#### Where to place files:
Internal storage/GtaRadio/radio/...

#### Required folder structure:
GtaRadio/radio/gta_sa/Radio_Los_Santos.m4a , K-DST.m4a , ...

> 💡 **Important!**  
> - Folder names (`gta_sa`, `gta_v`) **must exactly match** the `id` values in the app’s internal `games_catalog.json`.  
> - Supported formats: **MP3 or M4A** (AAC 192 kbps in `.m4a` recommended as a good balance between size and quality).  
> - Filenames are **case-sensitive** and must include the correct extension.
