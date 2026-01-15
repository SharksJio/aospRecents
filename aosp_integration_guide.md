# Replacing AOSP Recents with Custom StandaloneRecents

## Overview

To replace the default AOSP Recents (Launcher3/QuickStep) with your custom StandaloneRecents app, you need to:

1. ✅ Implement complete AIDL interface
2. ✅ Integrate into AOSP build system
3. ✅ Configure SystemUI to use your app
4. ✅ Sign with platform certificate
5. ✅ Disable default Launcher3/QuickStep
6. ✅ Build and flash ROM

---

## Step 1: Complete AIDL Implementation

### 1.1 Copy AIDL Interface from AOSP

**Location in AOSP**: `frameworks/base/core/java/android/view/IOverviewProxy.aidl`

```bash
# In your AOSP source tree
cp frameworks/base/core/java/android/view/IOverviewProxy.aidl \
   path/to/StandaloneRecents/src/main/aidl/android/view/
```

### 1.2 Update OverviewProxyService

Replace the stub implementation with proper AIDL:

```java
package com.custom.recents.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Region;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.IOverviewProxy;
import android.view.MotionEvent;

public class OverviewProxyService extends Service {
    
    private final IOverviewProxy.Stub mOverviewProxy = new IOverviewProxy.Stub() {
        
        @Override
        public void onOverviewShown(boolean fromHome) throws RemoteException {
            // Launch RecentsActivity
            Intent intent = new Intent(OverviewProxyService.this, 
                                      com.custom.recents.RecentsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                           Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        
        @Override
        public void onOverviewHidden(boolean fromHome, boolean triggeredByAltTab) 
                throws RemoteException {
            // Handle overview hidden
            sendBroadcast(new Intent("com.custom.recents.HIDE_RECENTS"));
        }
        
        @Override
        public void onActiveNavBarRegionChanges(Region activeRegion) 
                throws RemoteException {
            // Handle nav bar region changes
        }
        
        @Override
        public void onInitialize(Bundle params) throws RemoteException {
            // Initialize overview proxy
        }
        
        @Override
        public void onAssistantAvailable(boolean available) throws RemoteException {
            // Handle assistant availability
        }
        
        @Override
        public void onAssistantVisibilityChanged(float visibility) 
                throws RemoteException {
            // Handle assistant visibility
        }
        
        // Implement other required AIDL methods...
    };
    
    @Override
    public IBinder onBind(Intent intent) {
        return mOverviewProxy.asBinder();
    }
}
```

---

## Step 2: Integrate into AOSP Build System

### 2.1 Create Android.bp (AOSP Build File)

**File**: `StandaloneRecents/Android.bp`

```blueprint
android_app {
    name: "StandaloneRecents",
    
    srcs: [
        "src/main/java/**/*.java",
    ],
    
    resource_dirs: [
        "src/main/res",
    ],
    
    manifest: "src/main/AndroidManifest.xml",
    
    platform_apis: true,
    privileged: true,
    certificate: "platform",
    
    static_libs: [
        "androidx.appcompat_appcompat",
        "com.google.android.material_material",
    ],
    
    optimize: {
        enabled: false,
    },
    
    dex_preopt: {
        enabled: false,
    },
}
```

### 2.2 Alternative: Android.mk (Legacy Build)

**File**: `StandaloneRecents/Android.mk`

```makefile
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE_TAGS := optional
LOCAL_PACKAGE_NAME := StandaloneRecents
LOCAL_CERTIFICATE := platform
LOCAL_PRIVILEGED_MODULE := true

LOCAL_SRC_FILES := $(call all-java-files-under, src/main/java)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/src/main/res

LOCAL_MANIFEST_FILE := src/main/AndroidManifest.xml

LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.appcompat_appcompat \
    com.google.android.material_material

LOCAL_USE_AAPT2 := true
LOCAL_PROGUARD_ENABLED := disabled

include $(BUILD_PACKAGE)
```

### 2.3 Place in AOSP Source Tree

```bash
# Copy to AOSP source
mkdir -p packages/apps/StandaloneRecents
cp -r /path/to/StandaloneRecents/* packages/apps/StandaloneRecents/

# Or add to device-specific location
mkdir -p device/your-vendor/your-device/apps/StandaloneRecents
```

---

## Step 3: Configure SystemUI Integration

### 3.1 Update SystemUI Configuration

**File**: `frameworks/base/packages/SystemUI/res/values/config.xml`

Find and update:

```xml
<!-- QuickStep package name -->
<string name="config_recentsComponentName" translatable="false">
    com.custom.recents/.RecentsActivity
</string>

<!-- Overview proxy service -->
<string name="config_overviewProxyService" translatable="false">
    com.custom.recents/.service.OverviewProxyService
</string>
```

### 3.2 Update Navigation Mode Configuration

**File**: `frameworks/base/core/res/res/values/config.xml`

```xml
<!-- Enable gesture navigation -->
<integer name="config_navBarInteractionMode">2</integer>

<!-- QuickStep package -->
<string name="config_recentsPackageName" translatable="false">
    com.custom.recents
</string>
```

---

## Step 4: Disable Default Launcher3/QuickStep

### 4.1 Remove from Build

**File**: `build/make/target/product/core.mk` or your device makefile

Remove or comment out:

```makefile
# PRODUCT_PACKAGES += \
#     Launcher3QuickStep
```

### 4.2 Add Your App to Build

```makefile
PRODUCT_PACKAGES += \
    StandaloneRecents
```

### 4.3 Set as System App

```makefile
# Make it a privileged system app
PRODUCT_PACKAGES += \
    StandaloneRecents

# Or in device.mk
$(call inherit-product-if-exists, packages/apps/StandaloneRecents/StandaloneRecents.mk)
```

---

## Step 5: Update AndroidManifest.xml

### 5.1 Add System Permissions

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.custom.recents"
    android:sharedUserId="android.uid.system">

    <!-- System-level permissions -->
    <uses-permission android:name="android.permission.GET_TASKS" />
    <uses-permission android:name="android.permission.REAL_GET_TASKS" />
    <uses-permission android:name="android.permission.REMOVE_TASKS" />
    <uses-permission android:name="android.permission.MANAGE_ACTIVITY_TASKS" />
    <uses-permission android:name="android.permission.STATUS_BAR_SERVICE" />
    <uses-permission android:name="android.permission.STOP_APP_SWITCHES" />
    <uses-permission android:name="android.permission.READ_FRAME_BUFFER" />
    <uses-permission android:name="android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS" />

    <application
        android:label="Recents"
        android:persistent="true"
        android:allowBackup="false">

        <activity
            android:name=".RecentsActivity"
            android:excludeFromRecents="true"
            android:exported="true"
            android:launchMode="singleInstance"
            android:theme="@android:style/Theme.DeviceDefault.NoActionBar.Fullscreen">
            
            <!-- Make it the recents provider -->
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.DEFAULT" />
            </intent-filter>
        </activity>

        <service
            android:name=".service.OverviewProxyService"
            android:permission="android.permission.STATUS_BAR_SERVICE"
            android:exported="true"
            android:enabled="true"
            android:directBootAware="true">
            <intent-filter>
                <action android:name="android.intent.action.QUICKSTEP_SERVICE" />
            </intent-filter>
        </service>

    </application>

</manifest>
```

---

## Step 6: SELinux Policy Configuration

### 6.1 Create SELinux Policy

**File**: `device/your-vendor/your-device/sepolicy/file_contexts`

```
/system/priv-app/StandaloneRecents(/.*)?    u:object_r:system_app_data_file:s0
```

**File**: `device/your-vendor/your-device/sepolicy/StandaloneRecents.te`

```
type standalone_recents, domain;
type standalone_recents_exec, exec_type, file_type;

app_domain(standalone_recents)
platform_app(standalone_recents)

allow standalone_recents activity_service:service_manager find;
allow standalone_recents activity_task_service:service_manager find;
allow standalone_recents statusbar_service:service_manager find;
```

---

## Step 7: Build the ROM

### 7.1 Clean Build (Recommended)

```bash
cd /path/to/aosp

# Clean previous builds
make clean

# Setup environment
source build/envsetup.sh

# Choose your device
lunch your_device-userdebug

# Build ROM
make -j$(nproc)
```

### 7.2 Incremental Build (Faster)

```bash
# Build only your app
mmm packages/apps/StandaloneRecents

# Build system image
make systemimage -j$(nproc)
```

---

## Step 8: Flash and Test

### 8.1 Flash ROM

```bash
# Flash entire ROM
adb reboot bootloader
fastboot flashall

# Or flash only system
fastboot flash system system.img
fastboot reboot
```

### 8.2 Verify Installation

```bash
# Check if app is installed
adb shell pm list packages | grep recents

# Check if running as system app
adb shell dumpsys package com.custom.recents | grep codePath

# Should show: /system/priv-app/StandaloneRecents

# Test recents
adb shell input keyevent KEYCODE_APP_SWITCH
```

---

## Step 9: Testing & Validation

### 9.1 Test Recents Trigger

```bash
# Via gesture (swipe up and hold)
# Via button (tap recents button)
# Via ADB
adb shell am start -n com.custom.recents/.RecentsActivity
```

### 9.2 Check SystemUI Binding

```bash
# Check if SystemUI bound to your service
adb shell dumpsys activity services OverviewProxyService

# Should show connection to com.custom.recents
```

### 9.3 Verify Permissions

```bash
# Check granted permissions
adb shell dumpsys package com.custom.recents | grep permission

# All system permissions should be granted
```

---

## Troubleshooting

### Issue: App Not Replacing Default Recents

**Solution**: Ensure default Launcher3QuickStep is removed:

```bash
adb shell pm list packages | grep launcher
# Should NOT show Launcher3QuickStep

# Manually disable if present
adb shell pm disable-user com.android.launcher3
```

### Issue: SystemUI Not Binding

**Check logs**:
```bash
adb logcat | grep OverviewProxy
```

**Fix**: Verify service intent-filter and permissions

### Issue: Permission Denied

**Solution**: App must be signed with platform key and in /system/priv-app/

---

## Complete Example: Device Makefile

**File**: `device/your-vendor/your-device/device.mk`

```makefile
# Custom Recents
PRODUCT_PACKAGES += \
    StandaloneRecents

# Remove default launcher
PRODUCT_PACKAGES := $(filter-out Launcher3QuickStep,$(PRODUCT_PACKAGES))

# System properties
PRODUCT_PROPERTY_OVERRIDES += \
    ro.recents.package=com.custom.recents
```

---

## Summary Checklist

- [ ] Copy IOverviewProxy.aidl from AOSP
- [ ] Implement complete AIDL interface
- [ ] Create Android.bp build file
- [ ] Place in packages/apps/StandaloneRecents
- [ ] Update SystemUI config.xml
- [ ] Update AndroidManifest with system permissions
- [ ] Add SELinux policies
- [ ] Remove Launcher3QuickStep from build
- [ ] Add StandaloneRecents to PRODUCT_PACKAGES
- [ ] Build ROM
- [ ] Flash and test

---

## Quick Reference

**Build Command**:
```bash
mmm packages/apps/StandaloneRecents && make systemimage -j$(nproc)
```

**Install to Running Device** (for development):
```bash
adb root && adb remount
adb push out/target/product/YOUR_DEVICE/system/priv-app/StandaloneRecents/ \
  /system/priv-app/
adb reboot
```

**Verify**:
```bash
adb shell pm path com.custom.recents
# Should show: package:/system/priv-app/StandaloneRecents/StandaloneRecents.apk
```

---

This completes the full integration process for replacing AOSP Recents with your custom implementation!
