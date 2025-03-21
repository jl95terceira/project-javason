package jl95.json;

import java.util.LinkedList;

public class NodeBuilder {

    public static Node build(String repr) {
        return new NodeBuilder(repr).exec();
    }

    private final String           repr;
    private final LinkedList<Node> stack    = new LinkedList<>();
    private       String           entryKey = null;

    private NodeBuilder(String repr) {this.repr = repr;}

    private void handleValue(Node x) {
        if (stack.isEmpty()) {
            stack.add(x);
            return;
        }
        Node parent = stack.getLast();
        if (entryKey == null) {
            parent.add(x);
        }
        else {
            parent.setItem(entryKey, x);
        }
        switch (x.type()) {
            case LIST:
            case MAP:
                stack.add(x);
                break;
            default:
                break;
        }
    }

    private Node exec() {

        new StreamParser().parse(repr, new StreamParser.Handlers() {

            @Override public void handleNull() {
                handleValue(Node.Null());
            }
            @Override public void handleNumber(String nRepr) {
                handleValue(Node.Int(Long.valueOf(nRepr)));
            }
            @Override public void handleString(String s) {
                handleValue(Node.Str(s));
            }
            @Override public void handleTrue() {
                handleValue(Node.Bool(true));
            }
            @Override public void handleFalse() {
                handleValue(Node.Bool(false));
            }
            @Override public void handleArrayStart() {
                handleValue(Node.List());
            }
            @Override public void handleArrayEnd() {
                stack.removeLast();
            }
            @Override public void handleObjectStart() {
                handleValue(Node.Map());
            }
            @Override public void handleObjectEnd() {
                stack.removeLast();
            }
            @Override public void handleObjectKey(String k) {
                entryKey = k;
            }
        });
        return stack.removeLast();
    }
}
