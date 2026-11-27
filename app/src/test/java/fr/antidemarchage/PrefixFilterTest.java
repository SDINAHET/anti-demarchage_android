package fr.antidemarchage;
import org.junit.Test;
import static org.junit.Assert.*;
public class PrefixFilterTest {
 @Test public void allPrefixesAndFormats() {
  assertEquals(12, PrefixFilter.PREFIXES.size());
  for(String p:PrefixFilter.PREFIXES) {
   for(String suffix:new String[]{"000000","999999","123456"}) {
    assertEquals(p,PrefixFilter.match(p+suffix));
    assertEquals(p,PrefixFilter.match("+33"+p.substring(1)+suffix));
    assertEquals(p,PrefixFilter.match("0033"+p.substring(1)+suffix));
   }
  }
 }
 @Test public void normalNumbersAreNeverBlocked() {
  for(String number:new String[]{
   "0297123456","0298123456","0612345678","0712345678",
   "0800112112","0140123456","0177123456","0930123456",
   "112","15","17","18","+44162457890",null,"private",""
  }) assertEquals(number, "", PrefixFilter.match(number));
 }
 @Test public void separatorsAreNormalized() {
  assertEquals("0162",PrefixFilter.match("+33 (1) 62 45 78 90"));
  assertEquals("0162",PrefixFilter.match("01.62.45.78.90"));
  assertEquals("0162",PrefixFilter.match("01-62-45-78-90"));
  assertEquals("",PrefixFilter.match("0162-invalid"));
 }
 @Test public void refuseMalformedNumbers() {
  for(String number:new String[]{"0162","016200000","01620000000","+330162123456","0162abcdefgh","00331620000"})
   assertEquals("",PrefixFilter.match(number));
 }
}
