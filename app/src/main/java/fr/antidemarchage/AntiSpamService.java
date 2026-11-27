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
   prefs=getSharedPreferences("filter",MODE_PRIVATE);
   final java.util.Map<String,?> settings=prefs.getAll();
   ScreeningPolicy.Result decision=ScreeningPolicy.evaluate(raw,!Boolean.FALSE.equals(settings.get("enabled")),prefix->!Boolean.FALSE.equals(settings.get(prefix)),
    n->!Boolean.FALSE.equals(settings.get("blacklist_enabled"))&&Boolean.TRUE.equals(settings.get("black:"+n)),
    n->Boolean.TRUE.equals(settings.get("white:"+n)));
   p=decision.prefix;
   block=decision.blocked;
  } catch(RuntimeException ignored) { block=false; android.util.Log.w("AntiSpamScreening","Decision error; allowing call"); }
  android.util.Log.i("AntiSpamScreening",block?"BLOCK: "+p:"ALLOW");
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
    .setContentTitle("Appel bloqué").setContentText(PrefixFilter.normalize(raw)+" • "+("Liste noire".equals(p)?p:"Préfixe "+p))
    .setVisibility(Notification.VISIBILITY_PRIVATE).setContentIntent(pi).setAutoCancel(true).build();
   nm.notify(1001,n);
  } catch(SecurityException ignored) {}
 }
}