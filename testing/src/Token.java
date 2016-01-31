//For use w/Adaptive Text Field
public class Token {

    private static final String[] operations = new String[]{"+", "*", "/", "^"};
    private static final String[] functions = new String[]{"sin(", "cos(", "tan(", "log(", "("};
    private static final String[] numbers = new String[]{ "-", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "." };

    private static final String[] leftAssoc = new String[]{"+", "*", "/"};

    private static final String[][] precedences = new String[][]{
            {"+"}, {"*", "/"}, {"^"}, {"(", ")", "sin(", "cos(", "tan("}
    };

    private String operation;

    public Token(String op){
        operation = op;
    }

    public boolean isNumber(){
        return contains(numbers, operation);
    }

    public boolean isFunction(){
        return contains(functions, operation);
    }

    public boolean isOperation(){
        return contains(operations, operation);
    }

    public static boolean isNumber(String s){
        return contains(numbers, s);
    }

    public static boolean isFunction(String s){
        return contains(functions, s);
    }

    public static boolean isOperation(String s){
        return contains(operations, s);
    }

    public static boolean isNumber(Token t){
        return contains(numbers, t.getOp());
    }

    public static boolean isFunction(Token t){
        return contains(functions, t.getOp());
    }

    public static boolean isOperation(Token t){
        return contains(operations, t.getOp());
    }

    public String getOp(){
        return operation;
    }

    public boolean isLeftAssociative(){
        return contains(leftAssoc, operation);
    }

    public int getPrecedence(){
        for(int i = 0; i < precedences.length; i++){
            if(contains(precedences[i], operation)) return i;
        }
        return 0;
    }

    public static String[] getPossibleTokens(){
        String[] totalTokens = new String[operations.length + functions.length];
        for (int i = 0; i < operations.length; i++){
            totalTokens[i] = operations[i];
        }
        for (int i = 0; i < functions.length; i++){
            totalTokens[i + operations.length] = functions[i];
        }
        return totalTokens;
    }

    //basic method for if an array contains an object
    private static boolean contains(Object[] arr, Object o){
        return indexOf(arr, o) != -1;
    }

    //Basic array index finding
    private static int indexOf(Object[] arr, Object o){
        for(int i = 0; i < arr.length; i++){
            if(arr[i].equals(o)) return i;
        }
        return -1;
    }

}
