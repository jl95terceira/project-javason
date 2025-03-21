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
        BEFORE,
        IN_NUMBER,
        IN_BOOLEAN,
        IN_STRING,
        IN_STRING_ESCAPING,
        AFTER;
    }
    
    private State state;
    private int   left;
    private LinkedList<Node> nodeStack;
    private String entryKey;

    private void handleNode(Node n) {
        Node parent = nodeStack.getLast();
        switch (parent.type()) {
            case LIST:
                parent.add(n);
                break;
            case MAP:
                if (entryKey == null) {
                    entryKey = n.asStr();
                } else {
                    parent.setItem(entryKey, n);
                }
                break;
        }
        state = State.AFTER;
    }

    public Node parse(String repr) {
        state     = State.BEFORE;
        left      = -1;
        nodeStack = new LinkedList<>();
        entryKey  = null;
        int i = 0;
        while (true) {
            if (i >= repr.length()) {
                switch (state) {
                    case IN_NUMBER :
                        if (!nodeStack.isEmpty()) throw new RuntimeException("parent not closed");
                        return Node.Int (Long   .valueOf(repr.substring(left)));
                    case IN_BOOLEAN:
                        if (!nodeStack.isEmpty()) throw new RuntimeException("parent not closed");
                        return Node.Bool(Boolean.valueOf(repr.substring(left)));
                    case IN_STRING:
                        if (!nodeStack.isEmpty()) throw new RuntimeException("parent not closed");
                        return Node.Str (repr.substring(left).substring(1, repr.length()-1).replace("\\\"", "\"").replace("\\\\", "\\\\"));
                }
                if (nodeStack.isEmpty()) throw new RuntimeException("invalid");
                return nodeStack.removeLast();
            }
            char c = repr.charAt(i);
            System.out.printf("%s: %s\n", i, c);
            switch (state) {
                case BEFORE:
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
                        nodeStack.add(Node.List());
                        state = State.BEFORE;
                    }
                    else if (c == '{') {
                        nodeStack.add(Node.Map());
                        state = State.BEFORE;
                    }
                    else if (ws.contains(c)) {
                        /*pass*/
                    }
                    else {
                        throw new RuntimeException("invalid starting character "+c);
                    }
                    left = i;
                    i++;
                    break;
                case IN_NUMBER:
                    if (digits.contains(c)) {
                        i++;
                    }
                    else {
                        handleNode(Node.Int(Long.valueOf(repr.substring(left, i))));
                        state = State.AFTER;
                    }
                    break;
                case IN_BOOLEAN:
                    if (booleanChars.contains(c)) {
                        i++;
                    }
                    else {
                        nodeStack.add(Node.Bool(Boolean.valueOf(repr.substring(left, i))));
                        state = State.AFTER;
                    }
                    break;
                case IN_STRING:
                    if (c == '\\') {
                        i++;
                        state = State.IN_STRING_ESCAPING;
                    }
                    else if (c != '"') {
                        i++;
                    }
                    else {
                        nodeStack.add(Node.Bool(Boolean.valueOf(repr.substring(left, i))));
                        state = State.AFTER;
                    }
                    break;
                case IN_STRING_ESCAPING:
                    i++;
                    break;
                case AFTER:
                    if (ws.contains(c)) {
                        /*pass*/
                    }
                    else if (c == ',') {
                        state = State.BEFORE;
                    }
                    else if (c == ']') {
                        Node parent = nodeStack.removeLast();
                        if (parent.type() != Node.Type.LIST) throw new RuntimeException("bad array closing character "+c);
                        handleNode(parent);
                        /* keep state */
                    }
                    else if (c == '}') {
                        Node parent = nodeStack.removeLast();
                        if (parent.type() != Node.Type.MAP) throw new RuntimeException("bad object closing character "+c);
                        handleNode(parent);
                        /* keep state */
                    }
                    else throw new RuntimeException("invalid character "+c+" after value");
                    left = i;
                    i++;
                    break;
                default:
                    throw new RuntimeException("invalid");
            }
        }
    }
}
