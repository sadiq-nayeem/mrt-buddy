# MRT Buddy App Icon Color Switcher Guide

## Current Status
✅ **Blue icon is currently active** - Changed from green (#3DDC84) to Material Blue (#2196F3)

## Available Icon Colors

### 1. Material Blue (Currently Active) - `#2196F3`
- Default Google Material Design blue
- Good contrast and visibility
- Currently applied to `ic_launcher_background.xml`

### 2. Original Green (Backup) - `#3DDC84`
- Original green color
- Available in `ic_launcher_background_green.xml`

### 3. Alternative Blue Options
- **Darker Blue** - `#1976D2` (More professional)
- **Lighter Blue** - `#42A5F5` (Softer appearance)
- **Deep Blue** - `#0D47A1` (Bold and strong)
- **Cyan Blue** - `#0288D1` (Modern tech look)

## How to Switch Colors

### Option 1: Quick Switch (Recommended)
1. Open `ic_launcher_background_blue_options.xml`
2. Uncomment your preferred color (remove `<!--` and `-->`)
3. Comment out the current active color
4. Copy the active path element to replace the one in `ic_launcher_background.xml`

### Option 2: Direct Edit
Edit `composeApp/src/androidMain/res/drawable/ic_launcher_background.xml`:
- Change `android:fillColor="#2196F3"` to your desired color

### Option 3: Restore Original Green
```bash
# Replace the main file with the green backup
cp composeApp/src/androidMain/res/drawable/ic_launcher_background_green.xml \
   composeApp/src/androidMain/res/drawable/ic_launcher_background.xml
```

## Build and Test
After changing colors:
```bash
./gradlew :composeApp:assembleDebug
```

## Notes
- The adaptive icon system automatically handles different device shapes
- The white Android robot foreground remains unchanged
- Grid pattern overlay is preserved for visual consistency
- All changes are reversible using the backup file

## Build Status
✅ **Last build successful** - Blue icon is ready for testing