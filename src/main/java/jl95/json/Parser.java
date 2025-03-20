package jl95.json;

import java.util.*;

public class Parser {

    private static <T> Set<T> setOf(T... elements) {
        Set<T> set = new HashSet<>();
        for (T element: elements) {
            set.add(element);
        }
        return set;
    }
    private static Set<Character> ws              = setOf(' ','\t','\r','\n');
    private static Set<Character> digits          = setOf('0','1','2','3','4','5','6','7','8','9','.');
    private static Set<Character> booleanStarting = setOf('t','f');
    private static Set<Character> booleanChars    = setOf('t','r','u','e','f','a','l','s','e');
        
    private enum State {
        INIT,
        IN_NUMBER,
        IN_BOOLEAN,
        IN_STRING,
        IN_ARRAY,
        IN_OBJECT;
    }
    
    private State state = State.INIT;
    private int   left  = -1;

    public Node parse(String repr) {
        state = State.INIT;
        left = -1;
        int i = 0;
        while (true) {
            if (i >= repr.length()) {
                switch (state) {
                    case IN_NUMBER : return Node.Int (Long   .valueOf(repr.substring(left)));
                    case IN_BOOLEAN: return Node.Bool(Boolean.valueOf(repr.substring(left)));
                    case IN_STRING:  return Node.Str (repr.substring(left).substring(1, repr.length()-1).replace("\\\"", "\"").replace("\\\\", "\\\\"));
                    default: throw new RuntimeException("invalid");
                }
            }
            char c = repr.charAt(i);
            System.out.printf("%s: %s\n", i, c);
            switch (state) {
                case INIT:
                    left = i;
                    i++;
                    if (digits.contains(c)) {
                        state = State.IN_NUMBER;
                    }
                    else if (booleanStarting.contains(c)) {
                        state = State.IN_BOOLEAN;
                    }
                    else if (c == '"') {
                        state = State.IN_STRING;
                    }
                    else if (c == '[') {
                        state = State.IN_ARRAY;
                    }
                    else if (c == '{') {
                        state = State.IN_OBJECT;
                    }
                    else if (ws.contains(c)) {
                        /*pass*/
                    }
                    else {
                        throw new RuntimeException("invalid starting character "+c);
                    }
                    break;
                case IN_NUMBER:
                    if (digits.contains(c)) {
                        i++;
                    }
                    else {
                        int k = i;
                        while (i < repr.length()) {
                            if (!ws.contains(repr.charAt(i))) {
                                throw new RuntimeException("looks like a number but invalid");
                            }
                            i++;
                        }
                        return Node.Int(Long.valueOf(repr.substring(left, k)));
                    }
                    break;
                case IN_BOOLEAN:
                    if (booleanChars.contains(c)) {
                        i++;
                    }
                    else {
                        int k = i;
                        while (i < repr.length()) {
                            if (!ws.contains(repr.charAt(i))) {
                                throw new RuntimeException("looks like a bool but invalid");
                            }
                            i++;
                        }
                        return Node.Bool(Boolean.valueOf(repr.substring(left, k)));
                    }
                    break;
                default:
                    throw new RuntimeException("invalid");
            }
        }
    }
}
