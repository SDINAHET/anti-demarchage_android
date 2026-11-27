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
    assertNotNull(findText(root,"DÉSACTIVER LA PROTECTION")!=null?findText(root,"DÉSACTIVER LA PROTECTION"):findText(root,"ACTIVER LA PROTECTION"));
    assertNotNull(findSwitch(root,"Notifications des appels bloqués"));
   });
  }
 }
 @Test public void mainButtonPersistsAfterActivityRecreation(){
  Context context=InstrumentationRegistry.getInstrumentation().getTargetContext();
  SharedPreferences prefs=context.getSharedPreferences("filter",Context.MODE_PRIVATE);
  boolean old=prefs.getBoolean("enabled",true);
  prefs.edit().putBoolean("enabled",true).commit();
  try(ActivityScenario<MainActivity> scenario=ActivityScenario.launch(MainActivity.class)){
   scenario.onActivity(activity->{
    View root=activity.findViewById(android.R.id.content);
    TextView button=findText(root,"DÉSACTIVER LA PROTECTION");
    assertNotNull(button);button.performClick();
    assertTrue(prefs.getBoolean("enabled",true));
   });
   {
    androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Annuler")).perform(new androidx.test.espresso.ViewAction(){
     public org.hamcrest.Matcher<View> getConstraints(){return androidx.test.espresso.matcher.ViewMatchers.isDisplayed();}
     public String getDescription(){return "Invoke dialog button without input injection";}
     public void perform(androidx.test.espresso.UiController controller,View view){assertTrue(view.performClick());controller.loopMainThreadUntilIdle();}
    });
    assertTrue(prefs.getBoolean("enabled",true));
    scenario.onActivity(activity->findText(activity.findViewById(android.R.id.content),"DÉSACTIVER LA PROTECTION").performClick());
    androidx.test.espresso.Espresso.onView(androidx.test.espresso.matcher.ViewMatchers.withText("Désactiver")).perform(new androidx.test.espresso.ViewAction(){
     public org.hamcrest.Matcher<View> getConstraints(){return androidx.test.espresso.matcher.ViewMatchers.isDisplayed();}
     public String getDescription(){return "Invoke dialog button without input injection";}
     public void perform(androidx.test.espresso.UiController controller,View view){assertTrue(view.performClick());controller.loopMainThreadUntilIdle();}
    });
   }
   scenario.onActivity(activity->{
    assertFalse(prefs.getBoolean("enabled",true));
   });
   scenario.recreate();
   scenario.onActivity(activity->{
    View root=activity.findViewById(android.R.id.content);
    assertNotNull(findText(root,"ACTIVER LA PROTECTION"));
    assertFalse(findSwitch(root,"Notifications des appels bloqués").isEnabled());
    findText(root,"ACTIVER LA PROTECTION").performClick();
    assertTrue(prefs.getBoolean("enabled",false));
   });
  }finally{prefs.edit().putBoolean("enabled",old).commit();}
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
