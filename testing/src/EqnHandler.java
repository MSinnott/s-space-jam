import java.util.*;

/*
    Parses, converts to Reverse Polish Notation and evaluates an expression
 */

public class EqnHandler {

    public static class OFMap extends HashMap<String, int[]> {
        ArrayList<String> ofList;
        public OFMap(ArrayList<String> toAdd){
            ofList = toAdd;
        }
        @Override
        public int[] put(String s, int[] ints){
            ofList.add(s);
            return super.put(s, ints);
        }
    }

    private static ArrayList<String> operations = new ArrayList<String>();
    private static Map<String, int[]> operators = new OFMap(operations);

    private static ArrayList<String> functions = new ArrayList<String>();
    private static Map<String, int[]> functors = new OFMap(functions);

    private static String[] variables = new String[]{"t"};
    private static String[] numbers =  new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "." };

    private static String[] totalTokens;

    private static int lAssoc = 0;
    private static int rAssoc = 1;

    static {
        operators.put("+", new int[] { 0, lAssoc, 2 });
        operators.put("-", new int[] { 0, lAssoc, 2 });
        operators.put("*", new int[] { 5, lAssoc, 2});
        operators.put("/", new int[] { 5, lAssoc, 2});
        operators.put("%", new int[] { 5, lAssoc, 2});
        operators.put("^", new int[] { 10, rAssoc, 2});

        functors.put("sin(", new int[] {1});
        functors.put("cos(", new int[] {1});
        functors.put("tan(", new int[] {1});

        totalTokens = new String[functions.size() + operations.size() + variables.length + 2];
        for(int i = 0 ; i < functions.size(); i++){
            totalTokens[i] = functions.get(i);
        }
        for(int i = 0 ; i < operations.size(); i++){
            totalTokens[i + functions.size()] = operations.get(i);
        }
        for(int i = 0 ; i < variables.length; i++){
            totalTokens[i+functions.size()+operations.size()] = variables[i];
        }
        totalTokens[functions.size() + operations.size() + variables.length] = "(";
        totalTokens[functions.size() + operations.size() + variables.length + 1] = ")";
    }

    //eval sample requires a RPN fixed array of tokens
    public static float evalSample(int t, ArrayList<String> rpnTokens){
        Stack<Float> numStack = new Stack<Float>();
        int loc = 0;
        while(loc < rpnTokens.size()){
            String rpnTop = rpnTokens.get(loc);
            if(isNumber(rpnTop)) {
                numStack.push(Float.valueOf(rpnTop));
            } else if(operations.contains(rpnTop)){
                float[] args = new float[operators.get(rpnTop)[2]];
                for(int i = args.length - 1; i >= 0; i--){
                    args[i] = numStack.pop();
                }
                numStack.push(evalOp(rpnTop, args));
            } else if(functions.contains(rpnTop)){
                float[] args = new float[functors.get(rpnTop)[0]];
                for(int i = args.length - 1; i >= 0; i--){
                    args[i] = numStack.pop();
                }
                numStack.push(evalFunc(rpnTop, args));
            } else if(rpnTop.equals("t")){
                numStack.push((float) t);
            }
            loc++;
        }
        return numStack.pop();
    }

    public static Float evalOp(String opName, float[] args){
        switch (opName){
            case "+":
                return args[0] + args[1];
            case "-":
                return args[0] + args[1];
            case "*":
                return args[0] * args[1];
            case "/":
                return args[0] / args[1];
            case "^":
                return (float) Math.pow(args[0], args[1]);
            default:
                return Float.NaN;
        }
    }

    public static Float evalFunc(String funcName, float[] args){
        switch (funcName){
            case "sin(":
                return (float) Math.sin(args[0]);
            case "cos(":
                return (float) Math.cos(args[0]);
            case "tan(":
                return (float) Math.tan(args[0]);
            default:
                return Float.NaN;
        }
    }

    public static boolean isNumber(String s){
        for(int i = 0; i < s.length(); i++){
            if(!contains(numbers ,charAt(s, i)) && !charAt(s, i).equals("-")) return false;
        }
        return true;
    }

    //this is actually a very dece parser --m (need to fix ...)-123...)
    public static ArrayList<String> parse(String text){
        text = text.replace(" ", "");
        ArrayList<String> parsedTokens = new ArrayList<String>();

        int loc = 0;
        while (loc < text.length()){
            String toAdd = getToken(text, loc);
            if(toAdd.equals("-")){
                String lastOp = parsedTokens.get(parsedTokens.size() - 1);
                if(operations.contains(lastOp) || functions.contains(lastOp) || lastOp.equals("(")){
                    parsedTokens.add("-1");
                    parsedTokens.add("*");
                    loc += toAdd.length();
                    toAdd = "";
                }
            }
            loc += toAdd.length();
            if(toAdd.length() > 0) parsedTokens.add(toAdd);
        }
        return parsedTokens;
    }

    private static String getToken(String text, int startLoc){
        ArrayList<String> possibleTokens = new ArrayList<>();
        if(!contains(numbers, charAt(text, startLoc))) {
            for (String str : totalTokens) {
                if (isAt(str, startLoc, text)) {
                    possibleTokens.add(str);
                }
            }
            int len = -1;
            String finToken = "";
            for (String token : possibleTokens) {
                if (token.length() > len) {
                    len = token.length();
                    finToken = token;
                }
            }
            return finToken;
        } else {
            int loc = startLoc;
            String num = "";
            while(contains(numbers, charAt(text, loc))){
                num+=charAt(text, loc);
                loc++;
            }
            return num;
        }
    }
    
    //Google "shunting-yard algorithm"
    public static ArrayList<String> convertToRPN(ArrayList<String> parsedTokens) {
        ArrayList<String> rpnTokens = new ArrayList<String>();
        ArrayList<String> infTokens = new ArrayList<String>(parsedTokens);
        Stack<String> opStack = new Stack<String>();

        String currentToken;
        while(infTokens.size() > 0){
            currentToken = infTokens.get(0);
            infTokens.remove(0);
            if(isNumber(currentToken) || contains(variables, currentToken)) {
                rpnTokens.add(currentToken);
            }else if(functions.contains(currentToken) || currentToken.equals("(")) {
                opStack.push(currentToken);
            }else if(currentToken.equals(",")){
                String TopToken = "";
                while (!functions.contains(TopToken)){
                    TopToken = opStack.pop();
                    rpnTokens.add(TopToken);
                    if(opStack.size() == 0) return new ArrayList<String>();
                }
            } else if (operations.contains(currentToken)){
                String topOp;
                do{
                    if(opStack.size() == 0){
                        break;
                    }
                    topOp = opStack.peek();
                    if(functions.contains(topOp)){
                        break;
                    }
                    if(operations.contains(topOp)){
                        int[] currentDet = operators.get(currentToken);
                        int[] topDet = operators.get(topOp);
                        if( (currentDet[0] == lAssoc && currentDet[1] <= topDet[1] ) || (currentDet[0] == rAssoc && currentDet[1] < topDet[1] )){
                            rpnTokens.add(opStack.pop());
                        } else {
                            break;
                        }
                    }
                } while(operations.contains(topOp) || functions.contains(topOp));
                opStack.push(currentToken);
            } else if(currentToken.equals(")")){
                while(!opStack.empty() && !functions.contains(opStack.peek()) && !opStack.peek().equals("(")){
                    rpnTokens.add(opStack.pop());
                }
                if(opStack.empty()){
                    continue;
                }
                if(functions.contains(opStack.peek())){
                    rpnTokens.add(opStack.pop());
                } else if (opStack.peek().equals("(")){
                    opStack.pop();
                }
            }
        }
        while(opStack.size() > 0){
            rpnTokens.add(opStack.pop());
        }

        return rpnTokens;
    }

    //basic helper methods to make the parser easier to read
    private static boolean isAt(String fnd, int loc, String str){
        if(loc > str.length() || loc + fnd.length() > str.length()) return false;
        return str.substring(loc, loc + fnd.length()).equals(fnd);
    }

    private static String charAt(String str, int loc){
        if(loc > str.length() || loc + 1 > str.length()) return "";
        return str.substring(loc, loc+1);
    }

    private static boolean contains(Object[] objects, Object obj){
        for(Object o: objects){
            if(o.equals(obj)) return true;
        }
        return false;
    }

    private static void printCol(Collection c){
        for(Object o: c) System.out.println(o);
    }

}
