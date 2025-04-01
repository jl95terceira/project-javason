package jl95.json;

import java.util.*;

public class Node {

    public enum Type {
        
        NULL, LONG, DOUBLE, BOOL, STRING, LIST, MAP
    }
    
    // All attributes are public because Drools. (Don't ask.)
    public Type              _type = Type.NULL;
    public Long              _int  = null;
    public Double            _float= null;
    public Boolean           _bool = null;
    public String            _str  = null;
    public List<Node>        _list = null;
    public Map<String, Node> _map  = null;
    
    private static Node _Node  (Type _t) {
        
        Node a = new Node();
        a._type = _t;
        return a;
    }
            
    public static Node Null  ()             { return _Node(Type.NULL); }
    public static Node Long  (Long       x) { Node a = _Node(Type.LONG);  a._int  = x; return a; }
    public static Node Long  (Integer    x) { return Long(x.longValue()); }
    public static Node Long  (Short      x) { return Long(x.longValue()); }
    public static Node Long  (Byte       x) { return Long(x.longValue()); }
    public static Node Double(Double     x) { Node a = _Node(Type.DOUBLE);  a._float  = x; return a; }
    public static Node Double(Float      x) { return Double(x.doubleValue()); }
    public static Node Bool  (Boolean    x) { Node  a = _Node(Type.BOOL); a._bool = x; return a; }
    public static Node String(String     x) { Node  a = _Node(Type.STRING);  a._str  = x; return a; }
    public static Node String(Long       x) { return String(Long   .toString(x)); }
    public static Node String(Integer    x) { return String(Integer.toString(x)); }
    public static Node String(Short      x) { return String(Short  .toString(x)); }
    public static Node String(Byte       x) { return String(Byte   .toString(x)); }
    public static Node String(Double     x) { return String(Double .toString(x)); }
    public static Node String(Float      x) { return String(Float  .toString(x)); }
    public static Node List  ()             { return Node.List(new ArrayList<>()); }
    public static Node List  (List<Node> x) { Node a = _Node(Type.LIST); a._list = x; return a; }
    public static Node Map   ()             { return Node.Map (new HashMap<>()); }
    public static Node Map   (Map<String, Node> x) { Node a = _Node(Type.MAP);  a._map  = x; return a; }
    
    public Node() {}
    
    public Type              type     () { return _type; }
    public Long              asLong   () { return _int; }
    public Double            asDouble () { return _float; }
    public Boolean           asBoolean() { return _bool; }
    public String            asString () { return _str; }
    public List<Node>        asList   () { return _list; }
    public Map<String, Node> asMap    () { return _map; }
    public Object            asObject () {
        
        switch (type()) {
            
            case NULL : return null;
            case LONG: return asLong();
            case DOUBLE: return asDouble();
            case BOOL : return asBoolean();
            case STRING: return asString();
            case LIST : return asList();
            case MAP  : return asMap();
            default   : throw new AssertionError("not switching through all cases of type");
        }
    }
    public Node              get    (Integer i)         { return _list.get(i); }
    public void              set    (Integer i, Node x) { _list.set(i, x); }
    public void              add    (Node    x)         { _list.add   (x); }
    public Node              getItem(String  i)         { return _map .get(i); }
    public Node              getItem(Integer i)         { return getItem(i.toString()); }
    public Node              getItem(Long    i)         { return getItem(i.toString()); }
    public void              setItem(String  i, Node x) { _map .put(i, x); }
    public void              setItem(Integer i, Node x) { setItem (i.toString(), x); }
    public void              setItem(Long    i, Node x) { setItem (i.toString(), x); }
    public Node              copy    () {
        
        switch (type()) {
            
            case NULL  : return Null  ();
            case LONG  : return Long  (asLong  ());
            case DOUBLE: return Double(asDouble());
            case BOOL  : return Bool  (asBoolean());
            case STRING: return String(asString());
            case LIST  : return List  (asList  ());
            case MAP   : return Map   (asMap   ());
            default: throw new AssertionError("not switching through all cases of type");
        }
    }
    public Node              deepCopy() {
        
        switch (type()) {
            
            case LIST:
                
                List<Node> l = new java.util.ArrayList<>(asList().size());
                for (Node x: asList()) {
                    
                    l.add(x.deepCopy());
                }
                return Node.List(l);
                
            case MAP :
                
                Map<String,Node> m = new java.util.HashMap<>(asMap().size());
                for (Map.Entry<String,Node> x: asMap().entrySet()) {
                    
                    m.put(x.getKey(), x.getValue().deepCopy());
                }
                return Node.Map(m);
                
            default: return copy();
        }
    }
    
    @Override public String  toString() {
        
        return String.format("Node(%s)", asObject());
    }
    @Override public boolean equals  (Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Node node = (Node) o;
        return type() == node.type() &&
               Objects.equals(asObject(), node.asObject());
    }
    @Override public int     hashCode() {
        return Objects.hash(type(), asObject());
    }
}
