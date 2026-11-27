package fr.antidemarchage;
import android.app.*;
import android.content.*;
import android.telecom.*;
import android.os.Build;
public class AntiSpamService extends CallScreeningService {
 public static final String CHANNEL="blocked_calls";
 @Override public void onScreenCall(Call.Details d) {
  if(d.getCallDirection()!=Call.Details.DIRECTION_INCOMING) return;
  String raw="", p="";
  boolean block=false;
  android.content.SharedPreferences prefs=null;
  try {
   raw=d.getHandle()==null?"":d.getHandle().getSchemeSpecificPart();
   p=PrefixFilter.match(raw);
   prefs=getSharedPreferences("filter",MODE_PRIVATE);
   block=prefs.getBoolean("enabled",true)&&!p.isEmpty()&&prefs.getBoolean(p,true);
  } catch(RuntimeException ignored) { block=false; }
  respondToCall(d,new CallResponse.Builder().setDisallowCall(block).setRejectCall(block).setSkipNotification(block).setSkipCallLog(false).build());
  if(block) {
   prefs.edit().putInt("count",prefs.getInt("count",0)+1).apply();
   if(prefs.getBoolean("notifications",true)) notifyBlocked(raw,p);
  }
 }
 private void notifyBlocked(String raw,String p) {
  try {
   NotificationManager nm=getSystemService(NotificationManager.class);
   NotificationChannel c=new NotificationChannel(CHANNEL,"Appels bloqués",NotificationManager.IMPORTANCE_LOW);
   c.setSound(null,null); c.enableVibration(false); nm.createNotificationChannel(c);
   Intent i=new Intent(this,MainActivity.class);
   PendingIntent pi=PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_IMMUTABLE|PendingIntent.FLAG_UPDATE_CURRENT);
   Notification n=new Notification.Builder(this,CHANNEL).setSmallIcon(R.drawable.shield)
    .setContentTitle("Appel de démarchage bloqué").setContentText(PrefixFilter.normalize(raw)+" • Préfixe "+p)
    .setVisibility(Notification.VISIBILITY_PRIVATE).setContentIntent(pi).setAutoCancel(true).build();
   nm.notify(1001,n);
  } catch(SecurityException ignored) {}
 }
}