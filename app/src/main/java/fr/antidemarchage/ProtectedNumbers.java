package fr.antidemarchage;
public final class ProtectedNumbers {
 private ProtectedNumbers(){}
 public static boolean contains(String raw){
  if(raw==null)return false;
  String s=raw.replaceAll("[ .()\\-]", "");
  if(s.matches("[0-9]{2,6}"))return true;
  String n=PrefixFilter.normalize(raw);
  return n.startsWith("08") || n.matches("09726750[0-9]{2}") || n.equals("0972675000");
 }
}
