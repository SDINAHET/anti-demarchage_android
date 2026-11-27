package fr.antidemarchage;
import android.app.*;
import android.app.role.RoleManager;
import android.content.*;
import android.content.pm.PackageManager;
import android.os.*;
import android.widget.*;
import android.view.*;
public class MainActivity extends Activity {
 private SharedPreferences prefs;
 private TextView status;
 private RoleManager roles;
 @Override public void onCreate(Bundle b) {
  super.onCreate(b);
  prefs=getSharedPreferences("filter",MODE_PRIVATE); roles=getSystemService(RoleManager.class);
  ScrollView scroll=new ScrollView(this); LinearLayout root=new LinearLayout(this);
  root.setOrientation(LinearLayout.VERTICAL); int pad=(int)(24*getResources().getDisplayMetrics().density); root.setPadding(pad,pad*2,pad,pad);
  scroll.setFitsSystemWindows(true); scroll.addView(root); setContentView(scroll);
  TextView title=new TextView(this); title.setText("Dinahet AntiDémarchage"); title.setTextSize(28); root.addView(title);
  status=new TextView(this); status.setTextSize(18); root.addView(status);
  button(root,"Activer le rôle de filtrage",()->{if(roles.isRoleAvailable(RoleManager.ROLE_CALL_SCREENING)) startActivityForResult(roles.createRequestRoleIntent(RoleManager.ROLE_CALL_SCREENING),1);});
  toggle(root,"Protection automatique","enabled");
  toggle(root,"Notifications silencieuses","notifications");
  button(root,"Tester un appel fictif (sans appel réel)",()->startActivity(new Intent(this,SimulatorActivity.class)));
  if(Build.VERSION.SDK_INT>=33) button(root,"Autoriser les notifications",()->requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},2));
  TextView info=new TextView(this); info.setText("Préfixes filtrés — certains appels légitimes peuvent être concernés."); root.addView(info);
  for(String p:PrefixFilter.PREFIXES) toggle(root,p.substring(0,2)+" "+p.substring(2),p);
  button(root,"Masquer le raccourci (avancé)",()->new AlertDialog.Builder(this).setTitle("Attention : accès à l’application").setMessage("Si aucune notification de blocage n’arrive, tu pourrais ne plus pouvoir rouvrir facilement la configuration. Teste d’abord le filtrage sur ton téléphone. Tu pourras réactiver le raccourci via ADB ou réinstaller l’application.").setNegativeButton("Annuler",null).setPositiveButton("Masquer quand même",(d,w)->alias(false)).show());
  button(root,"Afficher le raccourci",()->alias(true));
  button(root,"Paramètres Android",()->startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,android.net.Uri.parse("package:"+getPackageName()))));
 }
 private void alias(boolean visible){getPackageManager().setComponentEnabledSetting(new ComponentName(this,getPackageName()+".Launcher"),visible?PackageManager.COMPONENT_ENABLED_STATE_ENABLED:PackageManager.COMPONENT_ENABLED_STATE_DISABLED,PackageManager.DONT_KILL_APP);}
 private void toggle(LinearLayout root,String label,String key){Switch s=new Switch(this);s.setText(label);s.setChecked(prefs.getBoolean(key,true));s.setOnCheckedChangeListener((v,c)->{prefs.edit().putBoolean(key,c).apply();refresh();});root.addView(s);}
 private void button(LinearLayout root,String label,Runnable action){Button b=new Button(this);b.setText(label);b.setOnClickListener(v->action.run());root.addView(b);}
 private void refresh(){if(status!=null)status.setText((roles.isRoleHeld(RoleManager.ROLE_CALL_SCREENING)&&prefs.getBoolean("enabled",true)?"Protection active":"Protection inactive")+"\nAppels bloqués : "+prefs.getInt("count",0));}
 @Override protected void onResume(){super.onResume();refresh();}
}