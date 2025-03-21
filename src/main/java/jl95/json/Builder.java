package jl95.json;

import java.util.LinkedList;

public abstract class Builder<N> {

    private final String        repr;
    private final LinkedList<N> stack    = new LinkedList<>();
    private       N             root     = null;
    private       String        entryKey = null;

    protected Builder(String repr) {this.repr = repr;}

    private void handleValue(N x) {
        if (root == null) {
            root = x;
        }
        else /* if we have a root already, the stack is guaranteed not to be empty */ {
            N parent = stack.getLast();
            if (entryKey == null) {
                addToArray(parent, x);
            } else {
                addToObject(parent, entryKey, x);
                entryKey = null;
            }
        }
        if (isArrayOrObject(x)) {
            stack.add(x);
        }
    }

    protected N build() {

        new StreamParser().parse(repr, new StreamParser.Handlers() {

            @Override public void handleNull() {
                handleValue(getNull());
            }
            @Override public void handleNumber(String nRepr) {
                handleValue(getNumber(nRepr));
            }
            @Override public void handleString(String s) {
                handleValue(getString(s));
            }
            @Override public void handleTrue() {
                handleValue(getTrue());
            }
            @Override public void handleFalse() {
                handleValue(getFalse());
            }
            @Override public void handleArrayStart() {
                handleValue(getArray());
            }
            @Override public void handleArrayEnd() {
                stack.removeLast();
            }
            @Override public void handleObjectStart() {
                handleValue(getObject());
            }
            @Override public void handleObjectEnd() {
                stack.removeLast();
            }
            @Override public void handleObjectKey(String k) {
                entryKey = k;
            }
        });
        return root;
    }

    protected abstract N getNull  ();
    protected abstract N getTrue  ();
    protected abstract N getFalse ();
    protected abstract N getNumber(String numberRepr);
    protected abstract N getString(String s);
    protected abstract N getArray ();
    protected abstract N getObject();
    protected abstract Boolean isArrayOrObject(N x);
    protected abstract void addToArray (N array,            N v);
    protected abstract void addToObject(N object, String k, N v);
}
