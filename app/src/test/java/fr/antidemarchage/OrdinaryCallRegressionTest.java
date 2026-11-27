package fr.antidemarchage;
import org.junit.Test;
import static org.junit.Assert.*;
public class OrdinaryCallRegressionTest {
 @Test public void ordinaryCallsAllowedWithEveryRuleEnabled(){
  for(String n:new String[]{"0297123456","0298123456","0612345678","0712345678","0912345678","+33 6 12 34 56 78","0033297123456","0151000000","0268700000","0281000000","0341000000","0441000000","0508600000","0541000000","0598550000","0598640000","0941000000","0947020000","0947090000","0800123456","112","private","",null})
   assertFalse(String.valueOf(n),ScreeningPolicy.evaluate(n,true,p->true).blocked);
 }
 @Test public void whitelistOverridesEveryPrefixAndBlacklist(){
  for(String p:PrefixFilter.PREFIXES){
   String n=p+"0000000000".substring(p.length());
   assertFalse(n,ScreeningPolicy.evaluate(n,true,k->true,k->true,k->true).blocked);
  }
 }
 @Test public void disabledProtectionOverridesAllRules(){
  assertFalse(ScreeningPolicy.evaluate("0162123456",false,k->true,k->true,k->false).blocked);
 }
 @Test public void blacklistOnlyMatchesExplicitNumber(){
  ScreeningPolicy.EnabledPrefix black=n->n.equals("0297123456");
  assertTrue(ScreeningPolicy.evaluate("+33297123456",true,k->true,black,k->false).blocked);
  assertFalse(ScreeningPolicy.evaluate("0297123457",true,k->true,black,k->false).blocked);
 }
}
