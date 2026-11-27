package fr.antidemarchage;
import android.content.Context;
import android.content.SharedPreferences;
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
public class ScreeningUiIntegrationTest {
 private TextView findText(View view,String expected){
  if(view instanceof TextView && expected.equals(((TextView)view).getText().toString()))return (TextView)view;
  if(view instanceof ViewGroup){
   ViewGroup group=(ViewGroup)view;
   for(int i=0;i<group.getChildCount();i++){
    TextView found=findText(group.getChildAt(i),expected);if(found!=null)return found;
   }
  }
  return null;
 }
 private Switch findSwitch(View view,String description){
  if(view instanceof Switch && description.equals(view.getContentDescription()))return (Switch)view;
  if(view instanceof ViewGroup){
   ViewGroup group=(ViewGroup)view;
   for(int i=0;i<group.getChildCount();i++){
    Switch found=findSwitch(group.getChildAt(i),description);if(found!=null)return found;
   }
  }
  return null;
 }
 @Test public void dashboardAndGroupsRender(){
  try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)){
   scenario.onActivity(a->{
    View root=a.findViewById(android.R.id.content);
    assertNotNull(findText(root,"AntiDémarchage"));
    assertNotNull(findText(root,"France métropolitaine"));
    assertNotNull(findText(root,"Outre-mer"));
    assertNotNull(findSwitch(root,"Protection automatique"));
    assertNotNull(findSwitch(root,"Notifications silencieuses"));
   });
  }
 }
 @Test public void togglesPersistAfterActivityRecreation(){
  Context c=InstrumentationRegistry.getInstrumentation().getTargetContext();
  SharedPreferences p=c.getSharedPreferences("filter",Context.MODE_PRIVATE);
  boolean old=p.getBoolean("enabled",true);
  try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)){
   scenario.onActivity(a->{
    Switch s=findSwitch(a.findViewById(android.R.id.content),"Protection automatique");
    assertNotNull(s);s.setChecked(!old);
    assertEquals(!old,p.getBoolean("enabled",old));
   });
   scenario.recreate();
   scenario.onActivity(a->{
    Switch s=findSwitch(a.findViewById(android.R.id.content),"Protection automatique");
    assertNotNull(s);assertEquals(!old,s.isChecked());
   });
  }finally{p.edit().putBoolean("enabled",old).commit();}
 }
 @Test public void prefixGroupsExpandAndShowSwitches(){
  try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)){
   scenario.onActivity(a->{
    View root=a.findViewById(android.R.id.content);
    TextView metro=findText(root,"France métropolitaine");
    TextView overseas=findText(root,"Outre-mer");
    assertNotNull(metro);assertNotNull(overseas);
    ((View)metro.getParent().getParent()).performClick();
    ((View)overseas.getParent().getParent()).performClick();
    for(String prefix:PrefixFilter.PREFIXES)
     assertNotNull(prefix,findSwitch(root,prefix.substring(0,2)+" "+prefix.substring(2)));
   });
  }
 }
}
