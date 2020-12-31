package analiseLexico;

/**
 *
 * @author filipe
 */
public class RegularExpression {
    private static String letter = "[A-Za-z]";
    private static String digit = "[0-9]";
    private static String id = "(_|" + letter + ")(_|" + letter + "|" + digit + ")*";
    private static String reserveWord = "main|if|else|while|do|for|int|float|char";
    private static String relationOp = "<|>|<=|>=|==|!=";
    private static String arithmeticOp = "+|-|*|/|=";
    private static String attribution = "=|+=|-=";
    private static String special = ")|(|{|}|,|;";
    private static String charact = "@|#|$|%|¨|&|*|_|-|ª|§|¬|^|~|ç|°";
    
    public static boolean isReserveWord(String x) {
        return x.matches(reserveWord);
    }
    
    public static boolean isID(String x) {
        return x.matches(id);
    }  
    
    public static boolean isRelationOp(String x) {
        return x.matches(relationOp);
    }
    
    public static boolean isArithmeticOp(String x) {
        return x.matches(arithmeticOp);
    }
    
    public static boolean isAttribution(String x) {
        return x.matches(attribution);
    }
    
    public static boolean isSpecial(String x) {
        return x.matches(special);
    }
    
    public static boolean isChar(String x) {
        return x.matches(charact);
    }
}
