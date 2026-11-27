package fr.antidemarchage;
import org.junit.Test;
import static org.junit.Assert.*;
public class PrefixFilterTest {
 @Test public void allOfficialPrefixesAndInternationalFormats() {
  assertEquals(22, PrefixFilter.PREFIXES.size());
  for(String p:PrefixFilter.PREFIXES) {
   for(char digit:new char[]{'0','5','9'}) {
    String number=p+String.valueOf(digit).repeat(10-p.length());
    assertEquals(number,p,PrefixFilter.match(number));
    assertEquals(number,p,PrefixFilter.match("+33"+number.substring(1)));
    assertEquals(number,p,PrefixFilter.match("0033"+number.substring(1)));
   }
  }
 }
 @Test public void normalNumbersAreNeverBlocked() {
  for(String number:new String[]{
   "0297123456","0298123456","0612345678","0712345678",
   "0800112112","0140123456","0177123456","0930123456",
   "112","15","17","18","+44162457890",null,"private","",
   "0268700000","0598600000","0947400000"
  }) assertEquals(number, "", PrefixFilter.match(number));
 }
 @Test public void separatorsAreNormalized() {
  assertEquals("0162",PrefixFilter.match("+33 (1) 62 45 78 90"));
  assertEquals("0162",PrefixFilter.match("01.62.45.78.90"));
  assertEquals("0162",PrefixFilter.match("01-62-45-78-90"));
  assertEquals("02688",PrefixFilter.match("02 68 80 00 00"));
  assertEquals("09475",PrefixFilter.match("09 47 50 00 00"));
  assertEquals("",PrefixFilter.match("0162-invalid"));
 }
 @Test public void refuseMalformedNumbers() {
  for(String number:new String[]{"0162","016200000","01620000000","+330162123456","0162abcdefgh","00331620000"})
   assertEquals("",PrefixFilter.match(number));
 }
}
