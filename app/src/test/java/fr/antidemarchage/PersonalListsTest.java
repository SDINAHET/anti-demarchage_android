package fr.antidemarchage;
import org.junit.Test;
import static org.junit.Assert.*;
public class PersonalListsTest {
 @Test public void manualBlock(){assertTrue(ScreeningPolicy.evaluate("+33212345678",true,p->false,n->n.equals("0212345678"),n->false).blocked);}
 @Test public void whitelistWins(){assertFalse(ScreeningPolicy.evaluate("0162123456",true,p->true,n->true,n->true).blocked);}
 @Test public void disabledWins(){assertFalse(ScreeningPolicy.evaluate("0212345678",false,p->true,n->true,n->false).blocked);}
 @Test public void protectedAlwaysAllowed(){for(String n:new String[]{"15","112","114","3949","3230","3404","3646","0800112112","0972675056"}){assertTrue(ProtectedNumbers.contains(n));assertFalse(ScreeningPolicy.evaluate(n,true,p->true,k->true,k->false).blocked);}}
 @Test public void prefixStillWorks(){assertTrue(ScreeningPolicy.evaluate("0162123456",true,p->true).blocked);}
}
