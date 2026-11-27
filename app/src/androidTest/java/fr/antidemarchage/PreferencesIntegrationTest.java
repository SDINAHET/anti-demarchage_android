package fr.antidemarchage;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
@RunWith(AndroidJUnit4.class)
public class PreferencesIntegrationTest {
 private SharedPreferences prefs(){
  Context c=InstrumentationRegistry.getInstrumentation().getTargetContext();
  return c.getSharedPreferences("filter",Context.MODE_PRIVATE);
 }
 @Test public void settingsRoundTripAndRestore(){
  SharedPreferences p=prefs();
  boolean oldEnabled=p.getBoolean("enabled",true),oldNotifications=p.getBoolean("notifications",true);
  try{
   assertTrue(p.edit().putBoolean("enabled",false).putBoolean("notifications",false).commit());
   assertFalse(prefs().getBoolean("enabled",true));
   assertFalse(prefs().getBoolean("notifications",true));
   assertTrue(p.edit().putBoolean("enabled",true).commit());
   assertTrue(prefs().getBoolean("enabled",false));
  }finally{p.edit().putBoolean("enabled",oldEnabled).putBoolean("notifications",oldNotifications).commit();}
 }
 @Test public void everyPrefixPersistsIndependently(){
  SharedPreferences p=prefs();
  java.util.Map<String,Boolean> previous=new java.util.HashMap<>();
  for(String prefix:PrefixFilter.PREFIXES)previous.put(prefix,p.getBoolean(prefix,true));
  try{
   SharedPreferences.Editor e=p.edit();
   for(String prefix:PrefixFilter.PREFIXES)e.putBoolean(prefix,false);
   assertTrue(e.commit());
   for(String prefix:PrefixFilter.PREFIXES)assertFalse(prefix,prefs().getBoolean(prefix,true));
   assertTrue(p.edit().putBoolean("0162",true).commit());
   assertTrue(prefs().getBoolean("0162",false));
   for(String prefix:PrefixFilter.PREFIXES)
    if(!prefix.equals("0162"))assertFalse(prefix,prefs().getBoolean(prefix,true));
  }finally{
   SharedPreferences.Editor e=p.edit();
   for(String prefix:PrefixFilter.PREFIXES)e.putBoolean(prefix,previous.get(prefix));
   e.commit();
  }
 }
 @Test public void counterPersists(){
  SharedPreferences p=prefs();int old=p.getInt("count",0);
  try{
   assertTrue(p.edit().putInt("count",old+2).commit());
   assertEquals(old+2,prefs().getInt("count",0));
  }finally{p.edit().putInt("count",old).commit();}
 }
}
