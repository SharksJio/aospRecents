# Standalone Recents App

A standalone Recents/Overview application for Android 14+ that can integrate with custom Home applications.

## Features

✅ **Custom UI Components**
- Screenshot, Select, and Split action buttons
- HotSeat-style app dock below action buttons
- Modern Pixel tablet-inspired design

✅ **SystemUI Integration**
- Communicates via IOverviewProxy AIDL interface
- Can be triggered from custom Home apps
- Works as QuickStep provider

✅ **System-Level Integration**
- Requires system-level permissions
- Must be installed as system app (`/system/priv-app/`)
- OR signed with platform certificate

## Project Structure

```
StandaloneRecents/
├── build.gradle
├── src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/custom/recents/
│   │   ├── RecentsActivity.java        # Main entry point
│   │   └── service/
│   │       └── OverviewProxyService.java # SystemUI integration
│   └── res/
│       ├── layout/
│       │   ├── recents_activity.xml
│       │   ├── overview_actions_container.xml
│       │   └── recents_hotseat_dock.xml
│       ├── drawable/
│       │   ├── ic_select.xml
│       │   └── recents_dock_background.xml
│       └── values/
│           ├── strings.xml
│           └── dimens.xml
```

## Build Instructions

### Option 1: Android Studio

1. Open Android Studio
2. File > Open > Select `StandaloneRecents` directory
3. Build > Make Project (Ctrl+F9)
4. Build > Build Bundle(s) / APK(s) > Build APK(s)

### Option 2: Command Line (Gradle)

```bash
cd StandaloneRecents
./gradlew assembleDebug
# Output: build/outputs/apk/debug/StandaloneRecents-debug.apk
```

## Installation

### Development Testing (WITHOUT System Integration)

```bash
adb install StandaloneRecents-debug.apk
adb shell pm grant com.custom.recents android.permission.GET_TASKS
adb shell pm grant com.custom.recents android.permission.REAL_GET_TASKS

# Launch manually for testing
adb shell am start -n com.custom.recents/.RecentsActivity
```

### Production (AS System App)

#### Step 1: Sign with Platform Key

```bash
# Obtain platform.x509.pem and platform.pk8 from your ROM build
java -jar signapk.jar platform.x509.pem platform.pk8 \\
  StandaloneRecents.apk StandaloneRecents-signed.apk
```

#### Step 2: Install to System Partition

```bash
adb root
adb remount
adb push StandaloneRecents-signed.apk /system/priv-app/StandaloneRecents/
adb shell chmod 644 /system/priv-app/StandaloneRecents/StandaloneRecents-signed.apk
adb reboot
```

## Integration with Custom Home App

### Triggering Recents from Your Home App

```java
// In your custom Home app
public void showRecents() {
    // Option 1: Via SystemUI (requires system integration)
    Intent intent = new Intent();
    intent.setAction("android.intent.action.SHOW_RECENTS");
    sendBroadcast(intent);
    
    // Option 2: Direct launch (for development/testing)
    Intent recents = new Intent();
    recents.setClassName("com.custom.recents", 
                        "com.custom.recents.RecentsActivity");
    recents.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(recents);
}
```

### Example Recents Button in Home App

```xml
<ImageButton
    android:id="@+id/recents_button"
    android:layout_width="48dp"
    android:layout_height="48dp"
    android:src="@drawable/ic_recents"
    android:onClick="showRecents" />
```

## Required Permissions

The app requires these system-level permissions (granted automatically when installed as system app):

- `android.permission.GET_TASKS` - Read task list
- `android.permission.REAL_GET_TASKS` - Get actual recent tasks
- `android.permission.REMOVE_TASKS` - Dismiss tasks
- `android.permission.STATUS_BAR_SERVICE` - SystemUI integration
- `android.permission.STOP_APP_SWITCHES` - Prevent app switches during transitions
- `android.permission.READ_FRAME_BUFFER` - Capture task thumbnails

## Current Limitations

⚠️ **Note**: This is a basic implementation. The following features need to be completed:

- [ ] Full RecentsView implementation (currently placeholder)
- [ ] Task thumbnail display
- [ ] Swipe-to-dismiss gestures
- [ ] Complete AIDL interface implementation
- [ ] App icon population in HotSeat dock
- [ ] Animations and transitions

## Next Steps

1. **Complete RecentsView**: Implement full task view with thumbnails
2. **Gesture Handling**: Add swipe, fling gestures for task management
3. **AIDL Interface**: Implement complete IOverviewProxy.aidl
4. **HotSeat Population**: Add logic to populate dock with app icons
5. **Testing**: Comprehensive testing with custom Home app

## Architecture

```
┌─────────────────┐
│  Custom Home    │
│      App        │
└────────┬────────┘
         │ (Trigger)
         ▼
┌───────────────────┐
│    SystemUI       │
│ OverviewProxy     │
└────────┬──────────┘
         │ (AIDL)
         ▼
┌────────────────────┐
│ StandaloneRecents  │
│  OverviewProxy     │
│     Service        │
└────────┬───────────┘
         │
         ▼
┌────────────────────┐
│  RecentsActivity   │
│  - Action Buttons  │
│  - HotSeat Dock    │
│  - Task Views      │
└────────────────────┘
```

## License

Apache License 2.0

## Support

For issues or questions, refer to the implementation plan and walkthrough documents in the brain artifacts directory.
