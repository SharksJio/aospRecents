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

See full implementation details in the complete guide at:
`C:\Users\shara\.gemini\antigravity\brain\d9ab6086-d6f1-4414-9a9d-77c847f77018\detailed_aosp_steps.md`

---

For complete detailed steps for all 6 parts (AIDL, Build Integration, SystemUI Config, Platform Signing, Disable Default, Build & Flash), please refer to the full guide in the artifacts directory.
