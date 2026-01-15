# Complete StandaloneRecents AOSP Integration - Detailed Steps

This guide provides detailed, step-by-step instructions for each requirement to replace AOSP's default Recents with your custom StandaloneRecents app.

---

## Part 1: Complete AIDL Implementation

### Step 1.1: Locate IOverviewProxy.aidl in AOSP Source

```bash
# Navigate to your AOSP source tree
cd ~/aosp  # or wherever your AOSP source is

# Find the AIDL file
find . -name "IOverviewProxy.aidl"
# Output: ./frameworks/base/core/java/android/view/IOverviewProxy.aidl
```

### Step 1.2: Copy AIDL to Your Project

```bash
# Create AIDL directory structure in StandaloneRecents
mkdir -p packages/apps/StandaloneRecents/src/main/aidl/android/view

# Copy the AIDL file
cp frameworks/base/core/java/android/view/IOverviewProxy.aidl \
   packages/apps/StandaloneRecents/src/main/aidl/android/view/

# Verify the copy
ls -l packages/apps/StandaloneRecents/src/main/aidl/android/view/IOverviewProxy.aidl
```

### Step 1.3: Examine the AIDL Interface

```bash
# View the interface to understand what methods you need to implement
cat packages/apps/StandaloneRecents/src/main/aidl/android/view/IOverviewProxy.aidl
```

**Typical IOverviewProxy.aidl content:**

```java
package android.view;

import android.graphics.Region;
import android.os.Bundle;
import android.view.MotionEvent;

oneway interface IOverviewProxy {
    void onInitialize(in Bundle params);
    void onOverviewShown(boolean fromHome);
    void onOverviewHidden(boolean fromHome, boolean triggeredByAltTab);
    void onActiveNavBarRegionChanges(in Region activeRegion);
    void onAssistantAvailable(boolean available);
    void onAssistantVisibilityChanged(float visibility);
    void onBackAction(boolean completed, int downX, int downY, boolean isButton, boolean gestureSwipeLeft);
    void onSystemUiStateChanged(int sysuiStateFlags);
    void onRotationProposal(int rotation, boolean isValid);
    void disable(int displayId, int state1, int state2, boolean animate);
    void onScreenTurnedOn();
    void onNavButtonsDarkIntensityChanged(float darkIntensity);
}
```

### Step 1.4: Implement the AIDL Interface

**Create**: `packages/apps/StandaloneRecents/src/main/java/com/custom/recents/service/OverviewProxyService.java`

```java
package com.custom.recents.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Region;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.IOverviewProxy;
import android.view.MotionEvent;
import com.custom.recents.RecentsActivity;

public class OverviewProxyService extends Service {
    private static final String TAG = "OverviewProxyService";
    
    private final IOverviewProxy.Stub mOverviewProxy = new IOverviewProxy.Stub() {
        
        @Override
        public void onInitialize(Bundle params) throws RemoteException {
            Log.d(TAG, "onInitialize called with params: " + params);
            // Initialize your recents system here
        }
        
        @Override
        public void onOverviewShown(boolean fromHome) throws RemoteException {
            Log.d(TAG, "onOverviewShown called, fromHome: " + fromHome);
            
            // Launch RecentsActivity
            Intent intent = new Intent(OverviewProxyService.this, RecentsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | 
                           Intent.FLAG_ACTIVITY_CLEAR_TOP |
                           Intent.FLAG_ACTIVITY_NO_ANIMATION);
            intent.putExtra("from_home", fromHome);
            startActivity(intent);
        }
        
        @Override
        public void onOverviewHidden(boolean fromHome, boolean triggeredByAltTab) 
                throws RemoteException {
            Log.d(TAG, "onOverviewHidden: fromHome=" + fromHome + 
                      ", triggeredByAltTab=" + triggeredByAltTab);
            
            // Send broadcast to hide recents
            Intent hideIntent = new Intent("com.custom.recents.HIDE_RECENTS");
            hideIntent.putExtra("from_home", fromHome);
            hideIntent.putExtra("alt_tab", triggeredByAltTab);
            sendBroadcast(hideIntent);
        }
        
        @Override
        public void onActiveNavBarRegionChanges(Region activeRegion) 
                throws RemoteException {
            Log.d(TAG, "onActiveNavBarRegionChanges: " + activeRegion);
            // Handle navigation bar region changes
        }
        
        @Override
        public void onAssistantAvailable(boolean available) throws RemoteException {
            Log.d(TAG, "onAssistantAvailable: " + available);
            // Handle assistant availability
        }
        
        @Override
        public void onAssistantVisibilityChanged(float visibility) 
                throws RemoteException {
            Log.d(TAG, "onAssistantVisibilityChanged: " + visibility);
            // Handle assistant visibility changes
        }
        
        @Override
        public void onBackAction(boolean completed, int downX, int downY, 
                                boolean isButton, boolean gestureSwipeLeft) 
                throws RemoteException {
            Log.d(TAG, "onBackAction: completed=" + completed);
            // Handle back gesture
        }
        
        @Override
        public void onSystemUiStateChanged(int sysuiStateFlags) 
                throws RemoteException {
            Log.d(TAG, "onSystemUiStateChanged: flags=" + sysuiStateFlags);
            // Handle SystemUI state changes
        }
        
        @Override
        public void onRotationProposal(int rotation, boolean isValid) 
                throws RemoteException {
            Log.d(TAG, "onRotationProposal: rotation=" + rotation + 
                      ", valid=" + isValid);
            // Handle rotation proposals
        }
        
        @Override
        public void disable(int displayId, int state1, int state2, boolean animate) 
                throws RemoteException {
            Log.d(TAG, "disable: displayId=" + displayId + 
                      ", state1=" + state1 + ", state2=" + state2);
            // Handle disable state
        }
        
        @Override
        public void onScreenTurnedOn() throws RemoteException {
            Log.d(TAG, "onScreenTurnedOn");
            // Handle screen on event
        }
        
        @Override
        public void onNavButtonsDarkIntensityChanged(float darkIntensity) 
                throws RemoteException {
            Log.d(TAG, "onNavButtonsDarkIntensityChanged: " + darkIntensity);
            // Handle nav button dark intensity
        }
    };
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "OverviewProxyService created");
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "OverviewProxyService bound by: " + intent);
        return mOverviewProxy.asBinder();
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, "OverviewProxyService unbound");
        return super.onUnbind(intent);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "OverviewProxyService destroyed");
    }
}
```

### Step 1.5: Update RecentsActivity to Handle Broadcasts

Add broadcast receiver to close RecentsActivity when hidden:

```java
// In RecentsActivity.java
private BroadcastReceiver mHideReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received hide broadcast");
        finish();
    }
};

@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // ... existing code ...
    
    // Register hide receiver
    IntentFilter filter = new IntentFilter("com.custom.recents.HIDE_RECENTS");
    registerReceiver(mHideReceiver, filter);
}

@Override
protected void onDestroy() {
    super.onDestroy();
    unregisterReceiver(mHideReceiver);
}
```

---

## Part 2: AOSP Build Integration

### Step 2.1: Create Android.bp File

**Create**: `packages/apps/StandaloneRecents/Android.bp`

```blueprint
// StandaloneRecents - Custom Recents Implementation
android_app {
    name: "StandaloneRecents",
    
    // Source files
    srcs: [
        "src/main/java/**/*.java",
    ],
    
    // AIDL files
    aidl: {
        local_include_dirs: ["src/main/aidl"],
        include_dirs: [
            "frameworks/base/core/java",
        ],
    },
    
    // Resource directories
    resource_dirs: [
        "src/main/res",
    ],
    
    // Manifest
    manifest: "src/main/AndroidManifest.xml",
    
    // Use platform APIs (hidden APIs)
    platform_apis: true,
    
    // Make it a privileged system app
    privileged: true,
    
    // Sign with platform certificate
    certificate: "platform",
    
    // Optional: Define as system_ext app
    system_ext_specific: true,
    
    // Static libraries
    static_libs: [
        "androidx.appcompat_appcompat",
        "com.google.android.material_material",
        "androidx.constraintlayout_constraintlayout",
    ],
    
    // Optimization settings
    optimize: {
        enabled: false,
    },
    
    // Disable dex preopt for development
    dex_preopt: {
        enabled: false,
    },
    
    // Required features
    required: [
        "privapp_whitelist_com.custom.recents.xml",
    ],
}

// Privileged permissions whitelist
prebuilt_etc {
    name: "privapp_whitelist_com.custom.recents.xml",
    sub_dir: "permissions",
    src: "privapp-permissions-com.custom.recents.xml",
    filename_from_src: true,
}
```

### Step 2.2: Create Privileged Permissions File

**Create**: `packages/apps/StandaloneRecents/privapp-permissions-com.custom.recents.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<permissions>
    <privapp-permissions package="com.custom.recents">
        <permission name="android.permission.GET_TASKS"/>
        <permission name="android.permission.REAL_GET_TASKS"/>
        <permission name="android.permission.REMOVE_TASKS"/>
        <permission name="android.permission.MANAGE_ACTIVITY_TASKS"/>
        <permission name="android.permission.STATUS_BAR_SERVICE"/>
        <permission name="android.permission.STOP_APP_SWITCHES"/>
        <permission name="android.permission.READ_FRAME_BUFFER"/>
        <permission name="android.permission.CONTROL_REMOTE_APP_TRANSITION_ANIMATIONS"/>
        <permission name="android.permission.FORCE_STOP_PACKAGES"/>
        <permission name="android.permission.INTERACT_ACROSS_USERS"/>
    </privapp-permissions>
</permissions>
```

### Step 2.3: Add to Device Build

**Edit**: `device/YOUR_VENDOR/YOUR_DEVICE/device.mk`

```makefile
# Add StandaloneRecents to product packages
PRODUCT_PACKAGES += \
    StandaloneRecents
```

### Step 2.4: Verify Build File

```bash
# Test build configuration
cd ~/aosp
source build/envsetup.sh
lunch YOUR_DEVICE-userdebug

# Build just your app to verify Android.bp
mmm packages/apps/StandaloneRecents

# Check for errors
# If successful, you'll see: "build completed successfully"
```

---

## Part 3: SystemUI Configuration

### Step 3.1: Update SystemUI Config

**Edit**: `frameworks/base/packages/SystemUI/res/values/config.xml`

```xml
<!-- Find and update these values -->

<!-- Recents component name -->
<string name="config_recentsComponentName" translatable="false">
    com.custom.recents/.RecentsActivity
</string>

<!-- Overview proxy service -->
<string name="config_overviewProxyService" translatable="false">
    com.custom.recents/.service.OverviewProxyService
</string>
```

### Step 3.2: Update Framework Config

**Edit**: `frameworks/base/core/res/res/values/config.xml`

```xml
<!-- QuickStep package name -->
<string name="config_recentsPackageName" translatable="false">
    com.custom.recents
</string>

<!-- Ensure gesture navigation is enabled -->
<integer name="config_navBarInteractionMode">2</integer>
```

### Step 3.3: Update Device Overlay (Optional but Recommended)

**Create**: `device/YOUR_VENDOR/YOUR_DEVICE/overlay/frameworks/base/core/res/res/values/config.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- Override recents package -->
    <string name="config_recentsPackageName" translatable="false">
        com.custom.recents
    </string>
</resources>
```

### Step 3.4: Verify Config Changes

```bash
# Search for old Launcher3 references
grep -r "Launcher3QuickStep" frameworks/base/packages/SystemUI/

# Should find no results after your changes

# Verify your config is set
grep -r "com.custom.recents" frameworks/base/
# Should show your config files
```

---

## Part 4: Platform Signing

### Step 4.1: Understand Platform Signing

Platform signing happens automatically in AOSP builds when you specify `certificate: "platform"` in Android.bp. No manual steps needed!

### Step 4.2: Verify Signing Configuration

**Check your Android.bp has:**

```blueprint
android_app {
    name: "StandaloneRecents",
    certificate: "platform",  // ← This line ensures platform signing
    privileged: true,
    platform_apis: true,
    // ...
}
```

### Step 4.3: Verify After Build

```bash
# After building, verify the signature
unzip -p out/target/product/YOUR_DEVICE/system/priv-app/StandaloneRecents/StandaloneRecents.apk META-INF/CERT.RSA | keytool -printcert

# Should show certificate owner matching your platform keys
```

### Step 4.4: Manual Signing (Only for Development/Testing)

**If building outside AOSP:**

```bash
# Get platform keys from your device ROM
# Usually: build/target/product/security/platform.{pk8,x509.pem}

# Sign manually
java -jar signapk.jar \
    build/target/product/security/platform.x509.pem \
    build/target/product/security/platform.pk8 \
    StandaloneRecents.apk \
    StandaloneRecents-signed.apk

# Install
adb root && adb remount
adb push StandaloneRecents-signed.apk /system/priv-app/StandaloneRecents/
adb reboot
```

---

## Part 5: Disable Default Launcher3QuickStep

### Step 5.1: Remove from Product Packages

**Edit**: `build/make/target/product/core.mk`

```makefile
# Find this line:
PRODUCT_PACKAGES += \
    Launcher3QuickStep

# Comment it out or remove:
# PRODUCT_PACKAGES += \
#     Launcher3QuickStep
```

**Or edit**: `device/YOUR_VENDOR/YOUR_DEVICE/device.mk`

```makefile
# Explicitly remove Launcher3QuickStep
PRODUCT_PACKAGES := $(filter-out Launcher3QuickStep,$(PRODUCT_PACKAGES))

# Add your app
PRODUCT_PACKAGES += \
    StandaloneRecents
```

### Step 5.2: Disable in Build System

**Create**: `device/YOUR_VENDOR/YOUR_DEVICE/remove-packages.mk`

```makefile
# Remove unwanted packages
PRODUCT_PACKAGES := $(filter-out \
    Launcher3 \
    Launcher3QuickStep \
    Launcher3Go \
    ,$(PRODUCT_PACKAGES))
```

**Then include it in device.mk:**

```makefile
# Include removal file
$(call inherit-product, device/YOUR_VENDOR/YOUR_DEVICE/remove-packages.mk)
```

### Step 5.3: Verify Removal

```bash
# After build, check if Launcher3QuickStep is in system
ls -la out/target/product/YOUR_DEVICE/system/priv-app/ | grep Launcher3

# Should show NO Launcher3QuickStep directory

# Check your app is there
ls -la out/target/product/YOUR_DEVICE/system/priv-app/ | grep StandaloneRecents
# Should show StandaloneRecents directory
```

---

## Part 6: Build & Flash ROM

### Step 6.1: Clean Build (First Time)

```bash
# Navigate to AOSP root
cd ~/aosp

# Clean everything (takes time but ensures clean build)
make clean
make clobber

# Setup build environment
source build/envsetup.sh

# Select your device
lunch YOUR_DEVICE-userdebug
# Example: lunch lineage_davinci-userdebug

# Build full ROM (takes 1-4 hours depending on hardware)
make -j$(nproc)
```

### Step 6.2: Incremental Build (After Changes)

```bash
# Setup environment
cd ~/aosp
source build/envsetup.sh
lunch YOUR_DEVICE-userdebug

# Build just your app
mmm packages/apps/StandaloneRecents

# Build system image
make systemimage -j$(nproc)

# Or build everything quickly
make -j$(nproc)
```

### Step 6.3: Flash Complete ROM

```bash
# Reboot to bootloader
adb reboot bootloader

# Wait for device to enter fastboot mode

# Flash all partitions (using flashall)
cd out/target/product/YOUR_DEVICE/
fastboot flashall

# Alternative: Flash specific partitions
fastboot flash boot boot.img
fastboot flash system system.img
fastboot flash vendor vendor.img
fastboot flash userdata userdata.img

# Reboot
fastboot reboot
```

### Step 6.4: Flash System Only (Faster)

```bash
# If you only changed system apps/framework
adb reboot bootloader

# Flash system partition
fastboot flash system out/target/product/YOUR_DEVICE/system.img

# Reboot
fastboot reboot
```

### Step 6.5: Development Push (Fastest for Testing)

```bash
# For quick testing without flashing
adb root
adb remount

# Push your app
adb push out/target/product/YOUR_DEVICE/system/priv-app/StandaloneRecents/ \
     /system/priv-app/

# Push SystemUI changes
adb push out/target/product/YOUR_DEVICE/system/priv-app/SystemUI/ \
     /system/priv-app/

# Restart SystemUI
adb shell killall com.android.systemui

# Or full reboot
adb reboot
```

### Step 6.6: Verify Installation

```bash
# After boot, check if app is installed
adb shell pm list packages | grep recents
# Should show: package:com.custom.recents

# Check installation path
adb shell pm path com.custom.recents
# Should show: package:/system/priv-app/StandaloneRecents/StandaloneRecents.apk

# Check if it's running as system app
adb shell dumpsys package com.custom.recents | grep codePath
# Should show: /system/priv-app/StandaloneRecents

# Test recents trigger
adb shell input keyevent KEYCODE_APP_SWITCH
# Your app should launch!
```

### Step 6.7: Check Logs for Issues

```bash
# Watch logs in real-time
adb logcat | grep -E "(OverviewProxy|StandaloneRecents|SystemUI)"

# Check for binding
adb logcat | grep "OverviewProxyService"
# Should show: "OverviewProxyService bound"

# Check SystemUI connection
adb shell dumpsys activity services OverviewProxyService
# Should show your service connected
```

---

## Complete Build Script Example

**Create**: `build-and-flash.sh`

```bash
#!/bin/bash

# Configuration
DEVICE="YOUR_DEVICE"
AOSP_ROOT="$HOME/aosp"

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
NC='\033[0m' # No Color

echo -e "${GREEN}Building StandaloneRecents for $DEVICE${NC}"

# Navigate to AOSP
cd $AOSP_ROOT || exit 1

# Setup environment
source build/envsetup.sh
lunch ${DEVICE}-userdebug

# Build app
echo -e "${GREEN}Building StandaloneRecents...${NC}"
mmm packages/apps/StandaloneRecents

if [ $? -ne 0 ]; then
    echo -e "${RED}Build failed!${NC}"
    exit 1
fi

# Build system image
echo -e "${GREEN}Building system image...${NC}"
make systemimage -j$(nproc)

if [ $? -ne 0 ]; then
    echo -e "${RED}System image build failed!${NC}"
    exit 1
fi

echo -e "${GREEN}Build successful!${NC}"

# Ask to flash
read -p "Flash to device? (y/n) " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo -e "${GREEN}Rebooting to bootloader...${NC}"
    adb reboot bootloader
    sleep 5
    
    echo -e "${GREEN}Flashing system...${NC}"
    fastboot flash system out/target/product/$DEVICE/system.img
    
    echo -e "${GREEN}Rebooting...${NC}"
    fastboot reboot
    
    echo -e "${GREEN}Done! Waiting for device...${NC}"
    adb wait-for-device
    
    echo -e "${GREEN}Device ready!${NC}"
fi
```

**Make executable and run:**

```bash
chmod +x build-and-flash.sh
./build-and-flash.sh
```

---

## Troubleshooting Common Issues

### Issue: Build Fails with "Android.bp not found"

**Solution:**
```bash
# Ensure Android.bp is in correct location
ls packages/apps/StandaloneRecents/Android.bp

# Verify syntax
cat packages/apps/StandaloneRecents/Android.bp
```

### Issue: SystemUI Not Binding to Service

**Solution:**
```bash
# Check logs
adb logcat | grep OverviewProxy

# Verify service is exported
adb shell dumpsys package com.custom.recents | grep -A 20 "Service"

# Restart SystemUI
adb shell killall com.android.systemui
```

### Issue: Permission Denied

**Solution:**
```bash
# Verify app is in /system/priv-app, not /data/app
adb shell pm path com.custom.recents

# Check permissions whitelist
adb shell ls /system/etc/permissions/*recents*
```

---

## Complete Checklist

- [ ] IOverviewProxy.aidl copied and in correct location
- [ ] OverviewProxyService implements all AIDL methods
- [ ] Android.bp created with correct paths
- [ ] privapp-permissions XML created
- [ ] Added to device.mk PRODUCT_PACKAGES
- [ ] SystemUI config.xml updated
- [ ] Framework config.xml updated
- [ ] Launcher3QuickStep removed from build
- [ ] Clean build completed successfully
- [ ] ROM flashed to device
- [ ] App shows in /system/priv-app/
- [ ] SystemUI binds to service
- [ ] Recents launches on gesture/button

---

This completes the detailed step-by-step guide for integrating StandaloneRecents into AOSP!
