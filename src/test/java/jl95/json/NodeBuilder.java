package jl95.json;

import java.util.LinkedList;

public class NodeBuilder extends GenericStreamBuilder<Node> {

    public static Node build(String repr) {
        return new NodeBuilder(repr).build();
    }

    private NodeBuilder(String repr) {super(repr);}

    @Override
    protected Node getNull() {
        return Node.Null();
    }
    @Override
    protected Node getTrue() {
        return Node.Bool(true);
    }
    @Override
    protected Node getFalse() {
        return Node.Bool(false);
    }
    @Override
    protected Node getNumber(String numberRepr) {
        return Node.Int(Long.valueOf(numberRepr));
    }
    @Override
    protected Node getString(String s) {
        return Node.Str(s);
    }
    @Override
    protected Node getArray() {
        return Node.List();
    }
    @Override
    protected Node getObject() {
        return Node.Map();
    }
    @Override
    protected Boolean isArrayOrObject(Node x) {
        switch (x.type()) {
        case LIST:
        case MAP:
            return true;
        }
        return false;
    }
    @Override
    protected void addToArray(Node array, Node v) {
        array.add(v);
    }
    @Override
    protected void addToObject(Node object, String k, Node v) {
        object.setItem(k, v);
    }
}
