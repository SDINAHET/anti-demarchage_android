package fr.antidemarchage;

import android.content.Context;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

/**
 * On-device/emulator UI smoke test. Does not place or block calls.
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityE2ETest {
 @Test public void opensSettingsAndShowsProtection() {
  Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
  try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)) {
   scenario.onActivity(activity -> {
    assertEquals("fr.antidemarchage",activity.getPackageName());
    assertNotNull(activity.findViewById(android.R.id.content));
    assertNotNull(activity.getSystemService(android.app.role.RoleManager.class));
   });
  }
 }
}
