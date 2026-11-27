package fr.antidemarchage;
import org.junit.Test;
import static org.junit.Assert.*;
public class PrefixFilterTest {
 @Test public void allPrefixes(){for(String p:PrefixFilter.PREFIXES){assertEquals(p,PrefixFilter.match(p+"000000"));assertEquals(p,PrefixFilter.match(p+"999999"));}}
 @Test public void formats(){assertEquals("0162",PrefixFilter.match("+33 1 62 45 78 90"));assertEquals("0162",PrefixFilter.match("0033162457890"));}
 @Test public void allowed(){for(String n:new String[]{"0297123456","0612345678","0162","+44162457890",null,"private"})assertEquals("",PrefixFilter.match(n));}
}