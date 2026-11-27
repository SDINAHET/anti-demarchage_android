package fr.antidemarchage;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class NotificationSettingsIntegrationTest {
 private TextView findText(View v,String value){
  if(v instanceof TextView && value.equals(((TextView)v).getText().toString()))return (TextView)v;
  if(v instanceof ViewGroup){
   ViewGroup g=(ViewGroup)v;
   for(int i=0;i<g.getChildCount();i++){TextView t=findText(g.getChildAt(i),value);if(t!=null)return t;}
  }
  return null;
 }
 private Switch findSwitch(View v,String name){
  if(v instanceof Switch && name.equals(v.getContentDescription()))return (Switch)v;
  if(v instanceof ViewGroup){
   ViewGroup g=(ViewGroup)v;
   for(int i=0;i<g.getChildCount();i++){Switch s=findSwitch(g.getChildAt(i),name);if(s!=null)return s;}
  }
  return null;
 }
 @Test public void disabledAppNotificationsAreExplainedEvenWhenAndroidAllowsThem(){
  Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
  SharedPreferences prefs=context.getSharedPreferences("filter",Context.MODE_PRIVATE);
  boolean previous=prefs.getBoolean("notifications",true);
  prefs.edit().putBoolean("notifications",false).commit();
  try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)){
   scenario.onActivity(activity->{
    View root=activity.findViewById(android.R.id.content);
    boolean active=prefs.getBoolean("enabled",true) && activity.getSystemService(android.app.role.RoleManager.class).isRoleHeld(android.app.role.RoleManager.ROLE_CALL_SCREENING);
    assertNotNull(findText(root,active?"Notifications de blocage désactivées dans l’application. Les appels restent filtrés.":"Notifications suspendues : la protection est inactive. Votre préférence est conservée."));
    Switch toggle=findSwitch(root,"Notifications des appels bloqués");
    assertNotNull(toggle);assertFalse(toggle.isChecked());assertEquals(active,toggle.isEnabled());
   });
  }finally{prefs.edit().putBoolean("notifications",previous).commit();}
 }
 @Test public void enablingAppNotificationsPersistsAfterRecreation(){
  Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
  SharedPreferences prefs=context.getSharedPreferences("filter",Context.MODE_PRIVATE);
  boolean previous=prefs.getBoolean("notifications",true);
  prefs.edit().putBoolean("notifications",false).commit();
  try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)){
   scenario.onActivity(activity->{
    Switch toggle=findSwitch(activity.findViewById(android.R.id.content),"Notifications des appels bloqués");
    assertNotNull(toggle);toggle.setChecked(true);
    assertTrue(prefs.getBoolean("notifications",false));
   });
   scenario.recreate();
   scenario.onActivity(activity->{
    Switch toggle=findSwitch(activity.findViewById(android.R.id.content),"Notifications des appels bloqués");
    assertNotNull(toggle);assertTrue(toggle.isChecked());
   });
  }finally{prefs.edit().putBoolean("notifications",previous).commit();}
 }
 @Test public void androidNotificationPermissionButtonIsVisible(){
  try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)){
   scenario.onActivity(activity->{
    View root=activity.findViewById(android.R.id.content);
    NotificationManager manager=activity.getSystemService(NotificationManager.class);
    assertNotNull(manager);
    boolean allowed=manager.areNotificationsEnabled();
    if(Build.VERSION.SDK_INT>=33)
     allowed=allowed && activity.checkSelfPermission("android.permission.POST_NOTIFICATIONS")==android.content.pm.PackageManager.PERMISSION_GRANTED;
    assertNotNull(findText(root,allowed?"✓ Autorisation Android accordée — réglages":"Autoriser les notifications dans Android"));
   });
  }
 }
}
