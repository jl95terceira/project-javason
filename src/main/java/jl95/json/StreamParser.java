package jl95.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A JSON stream-parser.
 * A stream-parser works not by parsing a serial (e.g. JSON) into an equivalent object (e.g. a Java object)
 * but rather by calling back given "handler" functions according to the types of the (JSON) elements that it finds during parsing.
 * Applications may set the handlers to build an equivalent (Java) object containing the whole serial or they
 * may process the (JSON) elements on the fly - much more efficient if we want to parse into a specialized class / struct.
 */
public class StreamParser {

    private static class EscapableCharMapping {
        public final Character from;
        public final String    fromAsEscaping;
        public final Character to;
        public final String    toAsString;
        public final String    placeholder = UUID.randomUUID().toString();
        public EscapableCharMapping(Character from, Character to) {
            this.from = from;
            this.to   = to;
            this.fromAsEscaping = ""+'\\'+from;
            this.toAsString     = ""     +to;
        }
    }
    private        enum  State {
        BEFORE_VALUE,
        IN_NUMBER,
        IN_WORD,
        IN_STRING,
        IN_STRING_ESCAPING,
        AFTER_VALUE,
        BEFORE_KEY,
        AFTER_KEY;
    }
    private        enum  StackValue {
        ARRAY,
        OBJECT;
    }

    private static <T> Set <T> setOf (T... elements) {
        Set<T> set = new HashSet<>();
        for (T element: elements) {
            set.add(element);
        }
        return set;
    }
    private static <T> List<T> listOf(T... elements) {
        List<T> list = new ArrayList<>(elements.length);
        list.addAll(Arrays.asList(elements));
        return list;
    }
    private static Set<Character>  ws             = setOf(' ','\t','\r','\n');
    private static Set<Character>  digits         = setOf('0','1','2','3','4','5','6','7','8','9','.');
    private static Set<Character>  wordStarting   = setOf('n','t','f');
    private static Set<Character>  wordChars      = setOf('n','u','l','l','t','r','u','e','f','a','l','s','e');
    private static List<EscapableCharMapping> escapableCharMappings = listOf(
        new EscapableCharMapping('\\', '\\'),
        new EscapableCharMapping('"', '"'),
        new EscapableCharMapping('/', '/'),
        new EscapableCharMapping('b', '\b'),
        new EscapableCharMapping('f', '\f'),
        new EscapableCharMapping('n', '\n'),
        new EscapableCharMapping('r', '\r'),
        new EscapableCharMapping('t', '\t')
        //new EscapableCharMapping('u', ?), // TODO(?): handle unicode point
    );
    private static Set<Character>  escapableChars = escapableCharMappings.stream().map(esc -> esc.from).collect(Collectors.toSet());

    private Handlers handlers;

    private String resolveStringWithinQuotes(String reprWithinQuotes) {
        for (EscapableCharMapping esc: escapableCharMappings) {
            reprWithinQuotes = reprWithinQuotes.replace(esc.fromAsEscaping, esc.placeholder);
        }
        for (EscapableCharMapping esc: escapableCharMappings) {
            reprWithinQuotes = reprWithinQuotes.replace(esc.placeholder, esc.toAsString);
        }
        return reprWithinQuotes;
    }
    private void   handleWord(String word) {
        if      (word.equals("true"))  { handlers.handleTrue (); }
        else if (word.equals("false")) { handlers.handleFalse(); }
        else if (word.equals("null"))  { handlers.handleNull (); }
        else throw new RuntimeException("invalid word "+word);
    }

    /**
     * handlers for JSON elements of the various types
     */
    public interface Handlers {
        void handleNull       ();
        void handleNumber     (String  nRepr);
        void handleString     (String  s);
        void handleTrue       ();
        void handleFalse      ();
        void handleArrayStart ();
        void handleArrayEnd   ();
        void handleObjectStart();
        void handleObjectEnd  ();
        void handleObjectKey  (String k);
    }

    /**
     * parse a whole JSON serial
     * @param serial JSON serial
     * @param handlers handlers
     */
    public void parse(String serial, Handlers handlers) {
        State state = State.BEFORE_VALUE;
        Boolean stateInObjectKey = false;
        LinkedList<StackValue> stack = new LinkedList<>();
        int left = -1;
        this.handlers = handlers;
        int i = 0;
        while (true) {
//            System.out.printf("%s :: %s\n", state, i);
            if (i >= serial.length()) {
                if (state == State.AFTER_VALUE) {
                    break;
                }
                if (!stack.isEmpty()) throw new RuntimeException("did not close parent");
                switch (state) {
                    case IN_NUMBER:
                        handlers.handleNumber(serial.substring(left));
                        break;
                    case IN_WORD:
                        handleWord(serial.substring(left));
                        break;
                    default:
                        throw new RuntimeException("invalid");
                }
                break;
            }
            char c = serial.charAt(i);
//            System.out.printf("    %s\n", c);
            switch (state) {
                case BEFORE_VALUE:
                    if (digits.contains(c)) {
                        state = State.IN_NUMBER;
                    }
                    else if (wordStarting.contains(c)) {
                        state = State.IN_WORD;
                    }
                    else if (c == '"') {
                        state = State.IN_STRING;
                        stateInObjectKey = false;
                    }
                    else if (c == '[') {
                        stack.add(StackValue.ARRAY);
                        handlers.handleArrayStart();
                        state = State.BEFORE_VALUE;
                    }
                    else if (c == '{') {
                        stack.add(StackValue.OBJECT);
                        handlers.handleObjectStart();
                        state = State.BEFORE_KEY;
                    }
                    else if (ws.contains(c)) {
                        /*pass*/
                    }
                    else if (c == ']') {
                        state = State.AFTER_VALUE;
                        continue;
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
                        handlers.handleNumber(serial.substring(left, i));
                        state = State.AFTER_VALUE;
                    }
                    break;
                case IN_WORD:
                    if (wordChars.contains(c)) {
                        i++;
                    }
                    else {
                        handleWord(serial.substring(left, i));
                        state = State.AFTER_VALUE;
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
                        String s = resolveStringWithinQuotes(serial.substring(left +1, i));
                        if (!stateInObjectKey) {
                            handlers.handleString(s);
                            state = State.AFTER_VALUE;
                        }
                        else {
                            handlers.handleObjectKey(s);
                            state = State.AFTER_KEY;
                        }
                        i++;
                    }
                    break;
                case IN_STRING_ESCAPING:
                    if (!escapableChars.contains(c)) {
                        throw new RuntimeException("inescapable character "+c);
                    }
                    i++;
                    state = State.IN_STRING;
                    break;
                case AFTER_VALUE:
                    if (ws.contains(c)) {
                        /*pass*/
                    }
                    else if (c == ',') {
                        if (!(stack.getLast() == StackValue.ARRAY  ||
                              stack.getLast() == StackValue.OBJECT)) {
                            throw new RuntimeException("value / entry "+c+" separator not expected");
                        }
                        state = stack.getLast() == StackValue.ARRAY? State.BEFORE_VALUE: State.BEFORE_KEY;
                    }
                    else if (c == ']') {
                        if (stack.getLast() != StackValue.ARRAY) throw new RuntimeException("bad closing character "+c+" - not in array");
                        stack.removeLast();
                        handlers.handleArrayEnd();
                        state = State.AFTER_VALUE;
                    }
                    else if (c == '}') {
                        if (stack.getLast() != StackValue.OBJECT) throw new RuntimeException("bad closing character "+c+" - not in object");
                        stack.removeLast();
                        handlers.handleObjectEnd();
                        state = State.AFTER_VALUE;
                    }
                    else throw new RuntimeException("invalid character "+c+" after value");
                    left = i;
                    i++;
                    break;
                case BEFORE_KEY:
                    if (ws.contains(c)) {
                        /*pass*/
                    }
                    else if (c == '"') {
                        state = State.IN_STRING;
                        stateInObjectKey = true;
                    }
                    else if (c == '}') {
                        state = State.AFTER_VALUE;
                        continue;
                    }
                    left = i;
                    i++;
                    break;
                case AFTER_KEY:
                    if (ws.contains(c)) {
                        /*pass*/
                    }
                    else if (c == ':') {
                        state = State.BEFORE_VALUE;
                    }
                    left = i;
                    i++;
                    break;
                default:
                    throw new RuntimeException("invalid");
            }
        }
    }
}
