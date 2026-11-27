package fr.antidemarchage;

/** Shared, side-effect-free decision used by the real Android screening service. */
public final class ScreeningPolicy {
 private ScreeningPolicy() {}
 public interface EnabledPrefix { boolean isEnabled(String prefix); }
 public static Result evaluate(String raw, boolean enabled, EnabledPrefix selected) {
  return evaluate(raw,enabled,selected,n->false,n->false);
 }
 public static Result evaluate(String raw, boolean enabled, EnabledPrefix selected, EnabledPrefix blacklist, EnabledPrefix whitelist) {
  String n=PrefixFilter.normalize(raw);
  if(!enabled || n.isEmpty() || ProtectedNumbers.contains(raw) || whitelist.isEnabled(n)) return new Result(false,"");
  if(blacklist.isEnabled(n)) return new Result(true,"Liste noire");
  String prefix=PrefixFilter.match(raw);
  boolean blocked=enabled && !prefix.isEmpty() && selected!=null && selected.isEnabled(prefix);
  return new Result(blocked,prefix);
 }
 public static final class Result {
  public final boolean blocked;
  public final String prefix;
  Result(boolean blocked,String prefix){this.blocked=blocked;this.prefix=prefix;}
 }
}
