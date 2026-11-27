package fr.antidemarchage;
import org.junit.Test;
import static org.junit.Assert.*;
public class ScreeningPolicyTest {
 @Test public void allPrefixesBlockedWhenEnabled(){
  for(String p:PrefixFilter.PREFIXES){
   String n=p+"0".repeat(10-p.length());
   ScreeningPolicy.Result r=ScreeningPolicy.evaluate(n,true,key->true);
   assertTrue(p,r.blocked);assertEquals(p,r.prefix);
  }
 }
 @Test public void disabledGloballyAlwaysAllows(){
  for(String p:PrefixFilter.PREFIXES)
   assertFalse(ScreeningPolicy.evaluate(p+"0".repeat(10-p.length()),false,key->true).blocked);
 }
 @Test public void disabledIndividuallyAllowsOnlyThatPrefix(){
  assertFalse(ScreeningPolicy.evaluate("0162123456",true,p->!p.equals("0162")).blocked);
  assertTrue(ScreeningPolicy.evaluate("0163123456",true,p->!p.equals("0162")).blocked);
 }
 @Test public void emergencyAndOrdinaryNumbersAllowed(){
  for(String n:new String[]{"15","17","18","112","0612345678","0712345678","0297123456","",null,"private"})
   assertFalse(String.valueOf(n),ScreeningPolicy.evaluate(n,true,p->true).blocked);
 }
 @Test public void nullSelectorFailsOpen(){
  assertFalse(ScreeningPolicy.evaluate("0162123456",true,null).blocked);
 }
 @Test public void internationalAndFormattedNumbers(){
  assertTrue(ScreeningPolicy.evaluate("+33 1 62 12 34 56",true,p->true).blocked);
  assertTrue(ScreeningPolicy.evaluate("0033 1 62 12 34 56",true,p->true).blocked);
 }
 @Test public void allPrefixesUnique(){
  assertEquals(PrefixFilter.PREFIXES.size(),new java.util.HashSet<>(PrefixFilter.PREFIXES).size());
 }
 @Test public void malformedNumbersFailOpen(){
  for(String n:new String[]{"0162","01620000000","+330162123456","0162INVALID"})
   assertFalse(ScreeningPolicy.evaluate(n,true,p->true).blocked);
 }
}
