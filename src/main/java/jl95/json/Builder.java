package jl95.json;

import java.util.LinkedList;

public abstract class Builder<N> {

    public interface BuildMethods<N> {
        N getNull  ();
        N getTrue  ();
        N getFalse ();
        N getNumber(String numberRepr);
        N getString(String s);
        N getArray ();
        N getObject();
        void addToArray (N array,            N v);
        void addToObject(N object, String k, N v);
    }

    private final BuildMethods<N> b;
    private final String          repr;
    private final LinkedList<N>   stack    = new LinkedList<>();
    private       N               root     = null;
    private       String          entryKey = null;

    protected Builder(String repr) {
        this.repr = repr;
        this.b    = getBuildMethods();
    }

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

    protected N build() {

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

    protected abstract BuildMethods<N> getBuildMethods();
}
