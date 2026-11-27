package fr.antidemarchage;
import org.junit.Test;
import static org.junit.Assert.*;
public class PrefixFilterBoundaryTest {
 @Test public void everyPrefixMatchesOnlyExactLeadingDigits(){
  for(String p:PrefixFilter.PREFIXES){
   String n=p+"1".repeat(10-p.length());
   assertEquals(p,PrefixFilter.match(n));
   assertEquals(p,PrefixFilter.match("+33"+n.substring(1)));
   assertEquals(p,PrefixFilter.match("0033"+n.substring(1)));
  }
 }
 @Test public void prefixNotAtBeginningDoesNotMatch(){
  assertEquals("",PrefixFilter.match("0616234567"));
  assertEquals("",PrefixFilter.match("0716234567"));
  assertEquals("",PrefixFilter.match("0148016234"));
 }
 @Test public void punctuationAndNonDigitsFailOpen(){
  for(String n:new String[]{"0162#12345","0162/12345","0162;12345","0162abc456","0162\n123456"})
   assertEquals("",PrefixFilter.match(n));
 }
 @Test public void normalizationIsIdempotent(){
  for(String p:PrefixFilter.PREFIXES){
   String n=p+"3".repeat(10-p.length());
   assertEquals(PrefixFilter.normalize(n),PrefixFilter.normalize(PrefixFilter.normalize(n)));
  }
 }
 @Test public void prefixToggleIsIsolated(){
  for(String disabled:PrefixFilter.PREFIXES){
   String n=disabled+"0".repeat(10-disabled.length());
   assertFalse(ScreeningPolicy.evaluate(n,true,p->!p.equals(disabled)).blocked);
  }
 }
}
