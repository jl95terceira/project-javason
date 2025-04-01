package jl95.json;

import java.util.LinkedList;

/**
 * A helping abstract class to quickly develop "builders" on top of the stream-parser.
 * This is useful if you just want to load the entire JSON serial into an equivalent Java object in memory.
 * @param <N> object type to build - expected to be able to represent any JSON element / value
 */
public abstract class Builder<N> {

    private final BuildMethods<N> b;
    private       String          repr;
    private       LinkedList<N>   stack;
    private       N               root;
    private       String          entryKey;

    private void handleValue(N x, Boolean isArrayOrObject) {
        if (root == null) {
            root = x;
        }
        else /* if we have a root already, the stack is guaranteed not to be empty */ {
            N parent = stack.getLast();
            if (entryKey == null) {
                b.addToArray(parent, x);
            } else {
                b.addToObject(parent, entryKey, x);
                entryKey = null;
            }
        }
        if (isArrayOrObject) {
            stack.add(x);
        }
    }

    protected Builder() {
        this.b = getBuildMethods();
    }

    protected N _build(String repr) {

        this.repr = repr;
        stack     = new LinkedList<>();
        root      = null;
        entryKey  = null;
        new StreamParser().parse(repr, new StreamParser.Handlers() {

            @Override public void handleNull        () {
                handleValue(b.getNull(), false);
            }
            @Override public void handleNumber      (String nRepr) {
                handleValue(b.getNumber(nRepr), false);
            }
            @Override public void handleString      (String s) {
                handleValue(b.getString(s), false);
            }
            @Override public void handleTrue        () {
                handleValue(b.getTrue(), false);
            }
            @Override public void handleFalse       () {
                handleValue(b.getFalse(), false);
            }
            @Override public void handleArrayStart  () {
                handleValue(b.getArray(), true);
            }
            @Override public void handleArrayEnd    () {
                stack.removeLast();
            }
            @Override public void handleObjectStart () {
                handleValue(b.getObject(), true);
            }
            @Override public void handleObjectEnd   () {
                stack.removeLast();
            }
            @Override public void handleObjectKey   (String k) {
                entryKey = k;
            }
        });
        return root;
    }

    /**
     * @return methods with which to build
     */
    protected abstract BuildMethods<N> getBuildMethods();

    /**
     * methods
     * - to construct objects that correspond with JSON elements / values and
     * - to manipulate objects, if they correspond with container JSON elements (arrays and objects).
     * @param <N> object type
     */
    public interface BuildMethods<N> {
        /**
         * @return equivalent object of a JSON <b>null</b>
         */
        N getNull  ();
        /**
         * @return equivalent object of a JSON <b>true</b>
         */
        N getTrue  ();
        /**
         * @return equivalent object of a JSON <b>false</b>
         */
        N getFalse ();
        /**
         * @return equivalent object of a JSON <b>number</b>
         */
        N getNumber(String numberRepr);
        /**
         * @return equivalent object of a JSON <b>string</b>
         */
        N getString(String s);
        /**
         * @return equivalent object of a JSON <b>array</b> (list)
         */
        N getArray ();
        /**
         * @return equivalent object of a JSON <b>object</b> (map)
         */
        N getObject();
        /**
         * to an object representing a JSON array, add another object representing any JSON element / value
         * @param array equivalent object of a JSON array
         * @param v object to add
         */
        void addToArray (N array,            N v);
        /**
         * to an object representing a JSON object, map with a key another object representing any JSON element / value
         * @param object equivalent object of a JSON object
         * @param k key
         * @param v object to map by the key
         */
        void addToObject(N object, String k, N v);
    }
}
