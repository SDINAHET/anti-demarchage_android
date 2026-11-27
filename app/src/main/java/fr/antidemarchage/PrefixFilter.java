package fr.antidemarchage;

import java.util.List;

/** Official ARCEP verified multipurpose number prefixes (metropolitan + overseas). */
public final class PrefixFilter {
 private PrefixFilter() {}
 public static final List<String> PREFIXES = List.of(
  "0162","0163","0270","0271","0377","0378","0424","0425",
  "0568","0569","0948","0949",
  "02688","02689","05987","05988","05989",
  "09475","09476","09477","09478","09479"
 );

 public static String normalize(String raw) {
  if(raw==null || raw.indexOf('\n')>=0 || raw.indexOf('\r')>=0) return "";
  String n=raw.replaceAll("[\\s.()\\-]", "");
  if(n.startsWith("+33")) n="0"+n.substring(3);
  else if(n.startsWith("0033")) n="0"+n.substring(4);
  return n.matches("0[1-9][0-9]{8}") ? n : "";
 }

 /** Returns the matching full prefix, or empty string (fail open). */
 public static String match(String raw) {
  String number=normalize(raw);
  if(number.isEmpty()) return "";
  for(String prefix:PREFIXES) {
   if(number.startsWith(prefix)) return prefix;
  }
  return "";
 }
}
