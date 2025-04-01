package jl95.json;

public class NodeBuilder extends Builder<Node> {

    public static Node build(String repr) {
        return new NodeBuilder()._build(repr);
    }

    private NodeBuilder() {super();}

    @Override
    protected BuildMethods<Node> getBuildMethods() {
        return new BuildMethods<Node>() {
            @Override
            public Node getNull() {
                return Node.Null();
            }
            @Override
            public Node getTrue() {
                return Node.Bool(true);
            }
            @Override
            public Node getFalse() {
                return Node.Bool(false);
            }
            @Override
            public Node getNumber(String numberRepr) {
                return Node.Int(Long.valueOf(numberRepr));
            }
            @Override
            public Node getString(String s) {
                return Node.Str(s);
            }
            @Override
            public Node getArray() {
                return Node.List();
            }
            @Override
            public Node getObject() {
                return Node.Map();
            }
            @Override
            public void addToArray(Node array, Node v) {
                array.add(v);
            }
            @Override
            public void addToObject(Node object, String k, Node v) {
        object.setItem(k, v);
    }
        };
    }
}
