package fr.antidemarchage;
public final class PrefixFilter {
 private PrefixFilter() {}
 public static final java.util.List<String> PREFIXES = java.util.List.of("0162","0163","0270","0271","0377","0378","0424","0425","0568","0569","0948","0949");
 public static String normalize(String raw) {
  if(raw==null) return "";
  String n=raw.replaceAll("[\\s.()\\-]", "");
  if(n.startsWith("+33")) n="0"+n.substring(3);
  else if(n.startsWith("0033")) n="0"+n.substring(4);
  return n.matches("0[1-9][0-9]{8}") ? n : "";
 }
 public static String match(String raw) {
  String n=normalize(raw);
  if(n.isEmpty()) return "";
  String p=n.substring(0,4);
  return PREFIXES.contains(p)?p:"";
 }
}