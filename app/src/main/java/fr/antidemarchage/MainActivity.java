package fr.antidemarchage;

import android.app.*;
import android.app.role.RoleManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.*;
import android.view.*;
import android.widget.*;

/** V2 dashboard. Screening decisions remain in AntiSpamService. */
public class MainActivity extends Activity {
 private SharedPreferences prefs;
 private RoleManager roles;
 private TextView protectionTitle, protectionDetail, countText, prefixCount, roleButton, notificationButton, shortcutButton, notificationDetail, mainToggleButton;
 private LinearLayout root;
 private Switch notificationSwitch;
 private final int ORANGE=Color.rgb(237,111,19), INK=Color.rgb(35,40,47), MUTED=Color.rgb(101,109,119);
 private int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+0.5f);}
 private GradientDrawable shape(int color,int radius){
  GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;
 }
 private TextView text(String value,int sp,int color,boolean bold){
  TextView t=new TextView(this);t.setText(value);t.setTextSize(sp);t.setTextColor(color);
  if(bold)t.setTypeface(null,Typeface.BOLD);return t;
 }
 private LinearLayout vertical(){
  LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;
 }
 private void gap(LinearLayout l,int height){View v=new View(this);l.addView(v,new LinearLayout.LayoutParams(1,dp(height)));}
 private LinearLayout card(){
  LinearLayout l=vertical();l.setPadding(dp(18),dp(14),dp(18),dp(14));
  l.setBackground(shape(Color.WHITE,18));root.addView(l,new LinearLayout.LayoutParams(-1,-2));gap(root,12);return l;
 }
 private void button(LinearLayout parent,String label,Runnable action){
  TextView b=text(label,14,INK,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(12),dp(15),dp(12),dp(15));
  b.setBackground(shape(0xFFF3F4F6,12));b.setOnClickListener(v->action.run());
  parent.addView(b,new LinearLayout.LayoutParams(-1,-2));gap(parent,8);
 }
 private Switch switchRow(LinearLayout parent,String label,String key){
  LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(0,dp(5),0,dp(5));
  TextView name=text(label,15,INK,false);row.addView(name,new LinearLayout.LayoutParams(0,-2,1));
  Switch s=new Switch(this);
  s.setChecked(prefs.getBoolean(key,true));
  s.setContentDescription(label);s.setOnCheckedChangeListener((v,checked)->{
   prefs.edit().putBoolean(key,checked).apply();refresh();
  });
  row.addView(s);parent.addView(row,new LinearLayout.LayoutParams(-1,-2));return s;
 }
 private void prefixSection(String title,String description,int start,int end){
  LinearLayout section=card();
  LinearLayout heading=new LinearLayout(this);heading.setGravity(Gravity.CENTER_VERTICAL);
  LinearLayout names=vertical();names.addView(text(title,17,INK,true));names.addView(text(description,12,MUTED,false));
  heading.addView(names,new LinearLayout.LayoutParams(0,-2,1));
  TextView arrow=text("Afficher  ▾",13,ORANGE,true);heading.addView(arrow);
  section.addView(heading);
  LinearLayout items=vertical();items.setVisibility(View.GONE);
  gap(items,10);
  for(int i=start;i<end;i++){
   String p=PrefixFilter.PREFIXES.get(i);
   switchRow(items,p.substring(0,2)+" "+p.substring(2),p);
  }
  section.addView(items);
  heading.setContentDescription(title+", développer ou réduire");
  heading.setFocusable(true);heading.setOnClickListener(v->{
   boolean open=items.getVisibility()!=View.VISIBLE;
   items.setVisibility(open?View.VISIBLE:View.GONE);
   arrow.setText(open?"Masquer  ▴":"Afficher  ▾");
  });
 }
 @Override public void onCreate(Bundle b){
  super.onCreate(b);
  prefs=getSharedPreferences("filter",MODE_PRIVATE);roles=getSystemService(RoleManager.class);
  getWindow().setStatusBarColor(0xFFF7F8FA);getWindow().setNavigationBarColor(0xFFF7F8FA);
  getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR|View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
  ScrollView scroll=new ScrollView(this);scroll.setFitsSystemWindows(true);scroll.setClipToPadding(false);scroll.setFillViewport(true);
  root=vertical();root.setPadding(dp(18),dp(12),dp(18),dp(20));root.setBackgroundColor(0xFFF7F8FA);
  scroll.addView(root);setContentView(scroll);
  LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);
  TextView logo=text("☎",26,Color.WHITE,true);logo.setGravity(Gravity.CENTER);logo.setBackground(shape(ORANGE,14));
  header.addView(logo,new LinearLayout.LayoutParams(dp(48),dp(48)));
  LinearLayout heading=vertical();heading.setPadding(dp(12),0,0,0);
  heading.addView(text("AntiDémarchage",23,INK,true));heading.addView(text("Dinahet · Protection des appels",12,MUTED,false));
  header.addView(heading);root.addView(header);gap(root,14);
  LinearLayout overview=card();
  protectionTitle=text("",19,INK,true);overview.addView(protectionTitle);
  protectionDetail=text("",13,MUTED,false);overview.addView(protectionDetail);gap(overview,14);
  countText=text("",27,ORANGE,true);overview.addView(countText);
  overview.addView(text("appels bloqués depuis l'installation",12,MUTED,false));gap(overview,12);
  prefixCount=text("",13,MUTED,true);overview.addView(prefixCount);
  gap(overview,14);
  mainToggleButton=text("",17,Color.WHITE,true);
  mainToggleButton.setGravity(Gravity.CENTER);
  mainToggleButton.setMinHeight(dp(54));
  mainToggleButton.setPadding(dp(16),dp(15),dp(16),dp(15));
  mainToggleButton.setOnClickListener(v->{
   if(prefs.getBoolean("enabled",true)){
    new AlertDialog.Builder(this).setTitle("Désactiver la protection ?")
     .setMessage("Les appels de démarchage ne seront plus bloqués par cette application. Vos réglages seront conservés.")
     .setNegativeButton("Annuler",null).setPositiveButton("Désactiver",(dialog,which)->{prefs.edit().putBoolean("enabled",false).apply();refresh();}).show();
   }else{prefs.edit().putBoolean("enabled",true).apply();refresh();}
  });
  overview.addView(mainToggleButton,new LinearLayout.LayoutParams(-1,-2));
  LinearLayout permissions=card();permissions.addView(text("Autorisations Android",17,INK,true));gap(permissions,8);
  roleButton=text("",14,Color.WHITE,true);roleButton.setGravity(Gravity.CENTER);
  roleButton.setPadding(dp(12),dp(14),dp(12),dp(14));roleButton.setBackground(shape(ORANGE,12));
  roleButton.setOnClickListener(v->{
   if(roles!=null&&roles.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)){
    new AlertDialog.Builder(this).setTitle("Filtrage autorisé").setMessage("Android a déjà accordé le rôle de filtrage à Dinahet AntiDémarchage. Aucune action supplémentaire n’est nécessaire.").setPositiveButton("OK",null).show();
   }else if(roles!=null&&roles.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING))
    startActivityForResult(roles.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING),1);
   else new AlertDialog.Builder(this).setMessage("Le rôle de filtrage n'est pas disponible sur cet appareil.").setPositiveButton("OK",null).show();
  });
  permissions.addView(roleButton);
  LinearLayout settings=card();settings.addView(text("Protection",17,INK,true));gap(settings,6);
  notificationSwitch=switchRow(settings,"Notifications des appels bloqués","notifications");
  notificationDetail=text("",12,MUTED,false);settings.addView(notificationDetail);
  gap(settings,8);
  notificationButton=text("",14,INK,true);notificationButton.setGravity(Gravity.CENTER);
  notificationButton.setPadding(dp(12),dp(15),dp(12),dp(15));notificationButton.setBackground(shape(0xFFF3F4F6,12));
  notificationButton.setOnClickListener(v->openNotificationSettings());
  settings.addView(notificationButton);
  root.addView(text("PRÉFIXES FILTRÉS",12,MUTED,true));gap(root,8);
  root.addView(text("Chaque préfixe peut être activé séparément. Certains appels légitimes peuvent être concernés.",13,MUTED,false));gap(root,12);
  prefixSection("France métropolitaine","12 préfixes",0,12);
  prefixSection("Outre-mer","10 préfixes",12,PrefixFilter.PREFIXES.size());
  LinearLayout advanced=card();advanced.addView(text("Réglages avancés",17,INK,true));gap(advanced,12);
  button(advanced,"Paramètres Android",()->startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:"+getPackageName()))));
  button(advanced,"Masquer le raccourci (avancé)",()->new AlertDialog.Builder(this)
   .setTitle("Masquer le raccourci ?")
   .setMessage("Sans icône ni notification, les réglages pourraient devenir difficiles à retrouver. Ne masque le raccourci qu'après avoir testé le filtrage.")
   .setNegativeButton("Annuler",null).setPositiveButton("Masquer",(d,w)->alias(false)).show());
  shortcutButton=text("",14,INK,true);shortcutButton.setGravity(Gravity.CENTER);
  shortcutButton.setPadding(dp(12),dp(15),dp(12),dp(15));shortcutButton.setBackground(shape(0xFFF3F4F6,12));
  shortcutButton.setOnClickListener(v->alias(true));advanced.addView(shortcutButton);
  refresh();
 }
 private boolean shortcutVisible(){
  int state=getPackageManager().getComponentEnabledSetting(new ComponentName(this,getPackageName()+".Launcher"));
  return state!=PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
 }
 private void alias(boolean visible){
  try{
   getPackageManager().setComponentEnabledSetting(new ComponentName(this,getPackageName()+".Launcher"),
    visible?PackageManager.COMPONENT_ENABLED_STATE_ENABLED:PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
   refresh();
   new AlertDialog.Builder(this).setTitle(visible?"Raccourci activé":"Raccourci masqué")
    .setMessage(visible?"Le raccourci est activé dans Android. Le lanceur Xiaomi peut mettre quelques secondes à actualiser l'icône. Si elle reste absente, vérifie aussi le tiroir d'applications.":"Le raccourci a été désactivé. Le filtrage des appels reste indépendant de l'icône.")
    .setPositiveButton("OK",null).show();
  }catch(RuntimeException e){
   new AlertDialog.Builder(this).setTitle("Impossible de modifier le raccourci").setMessage("Le lanceur ou Android refuse cette opération.").setPositiveButton("OK",null).show();
  }
 }
 private boolean notificationsAllowed(){
  NotificationManager nm=getSystemService(NotificationManager.class);
  if(nm!=null&&!nm.areNotificationsEnabled())return false;
  return Build.VERSION.SDK_INT<33 || checkSelfPermission("android.permission.POST_NOTIFICATIONS")==PackageManager.PERMISSION_GRANTED;
 }
 private void openNotificationSettings(){
  if(Build.VERSION.SDK_INT>=33 && checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=PackageManager.PERMISSION_GRANTED){
   requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},2);return;
  }
  try{
   Intent intent=new Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
   intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE,getPackageName());
   startActivity(intent);
  }catch(android.content.ActivityNotFoundException e){
   startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:"+getPackageName())));
  }
 }
 @Override public void onRequestPermissionsResult(int requestCode,String[] permissions,int[] grantResults){
  super.onRequestPermissionsResult(requestCode,permissions,grantResults);
  if(requestCode==2 && !notificationsAllowed())
   new AlertDialog.Builder(this).setMessage("Notifications non autorisées. Vous pouvez les activer dans les paramètres Android de l'application.").setNegativeButton("Fermer",null).setPositiveButton("Paramètres",(d,w)->{
    Intent intent=new Intent(android.provider.Settings.ACTION_APP_NOTIFICATION_SETTINGS);
    intent.putExtra(android.provider.Settings.EXTRA_APP_PACKAGE,getPackageName());startActivity(intent);
   }).show();
  refresh();
 }
 private void refresh(){
  if(protectionTitle==null)return;
  boolean granted=roles!=null&&roles.isRoleHeld(RoleManager.ROLE_CALL_SCREENING);
  boolean enabled=prefs.getBoolean("enabled",true);
  protectionTitle.setText(!enabled?"Protection désactivée":granted?"Protection active":"Protection non autorisée");
  protectionTitle.setTextColor(granted&&enabled?0xFF237B49:0xFF9A4A08);
  protectionDetail.setText(!enabled?"Vos appels ne sont plus filtrés par cette application.":!granted?"Autorisez le filtrage dans Android pour activer la protection.":"Les appels correspondant aux préfixes activés sont bloqués.");
  roleButton.setText(granted?"✓ Rôle de filtrage autorisé — détails":"Activer le rôle de filtrage");
  boolean systemNotifications=notificationsAllowed();
  boolean appNotifications=prefs.getBoolean("notifications",true);
  if(notificationButton!=null)notificationButton.setText(systemNotifications?"✓ Autorisation Android accordée — réglages":"Autoriser les notifications dans Android");
  if(notificationSwitch!=null){notificationSwitch.setEnabled(enabled&&granted);notificationSwitch.setAlpha(enabled&&granted?1f:0.5f);}
  if(notificationDetail!=null)notificationDetail.setText(!enabled||!granted?"Notifications suspendues : la protection est inactive. Votre préférence est conservée.":!appNotifications?"Notifications de blocage désactivées dans l’application. Les appels restent filtrés.":!systemNotifications?"Notifications de blocage activées dans l’application, mais bloquées par Android.":"Notifications de blocage activées et autorisées par Android (sans son ni vibration).");
  if(shortcutButton!=null)shortcutButton.setText(shortcutVisible()?"✓ Raccourci activé — vérifier":"Afficher le raccourci");
  if(mainToggleButton!=null){
   mainToggleButton.setText(enabled?"DÉSACTIVER LA PROTECTION":"ACTIVER LA PROTECTION");
   mainToggleButton.setBackground(shape(enabled?0xFF414B57:0xFF237B49,14));
   mainToggleButton.setContentDescription(enabled?"Désactiver le filtrage des appels":"Activer le filtrage des appels");
   mainToggleButton.setFocusable(true);
  }
  countText.setText(String.valueOf(prefs.getInt("count",0)));
  int n=0;for(String p:PrefixFilter.PREFIXES)if(prefs.getBoolean(p,true))n++;
  prefixCount.setText(n+" / "+PrefixFilter.PREFIXES.size()+" préfixes activés");
 }
 @Override protected void onResume(){super.onResume();refresh();}
}
