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
 private TextView protectionTitle, protectionDetail, countText, prefixCount, roleButton;
 private LinearLayout root;
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
  LinearLayout l=vertical();l.setPadding(dp(18),dp(17),dp(18),dp(17));
  l.setBackground(shape(Color.WHITE,18));root.addView(l,new LinearLayout.LayoutParams(-1,-2));gap(root,12);return l;
 }
 private void button(LinearLayout parent,String label,Runnable action){
  TextView b=text(label,14,INK,true);b.setGravity(Gravity.CENTER);b.setPadding(dp(12),dp(15),dp(12),dp(15));
  b.setBackground(shape(0xFFF3F4F6,12));b.setOnClickListener(v->action.run());
  parent.addView(b,new LinearLayout.LayoutParams(-1,-2));gap(parent,8);
 }
 private Switch switchRow(LinearLayout parent,String label,String key){
  LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);row.setPadding(0,dp(9),0,dp(9));
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
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);
  root=vertical();root.setPadding(dp(18),dp(22),dp(18),dp(24));root.setBackgroundColor(0xFFF7F8FA);
  scroll.addView(root);setContentView(scroll);
  LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);
  TextView logo=text("☎",26,Color.WHITE,true);logo.setGravity(Gravity.CENTER);logo.setBackground(shape(ORANGE,14));
  header.addView(logo,new LinearLayout.LayoutParams(dp(48),dp(48)));
  LinearLayout heading=vertical();heading.setPadding(dp(12),0,0,0);
  heading.addView(text("AntiDémarchage",23,INK,true));heading.addView(text("Dinahet · Protection des appels",12,MUTED,false));
  header.addView(heading);root.addView(header);gap(root,20);
  LinearLayout overview=card();
  protectionTitle=text("",19,INK,true);overview.addView(protectionTitle);
  protectionDetail=text("",13,MUTED,false);overview.addView(protectionDetail);gap(overview,14);
  countText=text("",27,ORANGE,true);overview.addView(countText);
  overview.addView(text("appels bloqués depuis l'installation",12,MUTED,false));gap(overview,12);
  prefixCount=text("",13,MUTED,true);overview.addView(prefixCount);
  LinearLayout permissions=card();permissions.addView(text("Autorisations Android",17,INK,true));gap(permissions,8);
  roleButton=text("",14,Color.WHITE,true);roleButton.setGravity(Gravity.CENTER);
  roleButton.setPadding(dp(12),dp(14),dp(12),dp(14));roleButton.setBackground(shape(ORANGE,12));
  roleButton.setOnClickListener(v->{
   if(roles!=null&&roles.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING))
    startActivityForResult(roles.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING),1);
   else new AlertDialog.Builder(this).setMessage("Le rôle de filtrage n'est pas disponible sur cet appareil.").setPositiveButton("OK",null).show();
  });
  permissions.addView(roleButton);
  LinearLayout settings=card();settings.addView(text("Protection",17,INK,true));gap(settings,6);
  switchRow(settings,"Protection automatique","enabled");
  switchRow(settings,"Notifications silencieuses","notifications");
  if(Build.VERSION.SDK_INT>=33){
   gap(settings,8);
   button(settings,"Autoriser les notifications",()->requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},2));
  }
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
  button(advanced,"Afficher le raccourci",()->alias(true));
  refresh();
 }
 private void alias(boolean visible){
  getPackageManager().setComponentEnabledSetting(new ComponentName(this,getPackageName()+".Launcher"),
   visible?PackageManager.COMPONENT_ENABLED_STATE_ENABLED:PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);
 }
 private void refresh(){
  if(protectionTitle==null)return;
  boolean granted=roles!=null&&roles.isRoleHeld(RoleManager.ROLE_CALL_SCREENING);
  boolean enabled=prefs.getBoolean("enabled",true);
  protectionTitle.setText(granted&&enabled?"Protection active":"Protection inactive");
  protectionDetail.setText(!granted?"Autorisation de filtrage Android nécessaire":enabled?"Filtrage des appels entrants autorisé":"Protection désactivée dans l'application");
  roleButton.setText(granted?"✓ Rôle de filtrage autorisé":"Activer le rôle de filtrage");
  countText.setText(String.valueOf(prefs.getInt("count",0)));
  int n=0;for(String p:PrefixFilter.PREFIXES)if(prefs.getBoolean(p,true))n++;
  prefixCount.setText(n+" / "+PrefixFilter.PREFIXES.size()+" préfixes activés");
 }
 @Override protected void onResume(){super.onResume();refresh();}
}
