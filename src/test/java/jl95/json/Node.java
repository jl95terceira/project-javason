package jl95.json;

import java.util.*;

public class Node {

    public static enum  Type {
        
        NULL, INT, BOOL, STR, LIST, MAP
    }
    
    // All attributes are public because Drools. (Don't ask.)
    public Type              _type = Type.NULL;
    public Long              _int  = null;
    public Boolean           _bool = null;
    public String            _str  = null;
    public List<Node>        _list = null;
    public Map<String, Node> _map  = null;
    
    private static Node _Node  (Type _t) {
        
        Node a = new Node();
        a._type = _t;
        return a;
    }
            
    public static Node Null()             { Node a = _Node(Type.NULL);              return a; }
    public static Node Int (Long       x) { Node a = _Node(Type.INT);  a._int  = x; return a; }
    public static Node Int (Integer    x) { return Int(x.longValue()); }
    public static Node Int (Short      x) { return Int(x.longValue()); }
    public static Node Int (Byte       x) { return Int(x.longValue()); }
    public static Node Bool(Boolean    x) { Node  a = _Node(Type.BOOL); a._bool = x; return a; }
    public static Node Str (String     x) { Node  a = _Node(Type.STR);  a._str  = x; return a; }
    public static Node Str (Long       x) { return Str(Long   .toString(x)); }
    public static Node Str (Integer    x) { return Str(Integer.toString(x)); }
    public static Node Str (Short      x) { return Str(Short  .toString(x)); }
    public static Node Str (Byte       x) { return Str(Byte   .toString(x)); }
    public static Node List()             { return Node.List(new ArrayList<>()); }
    public static Node List(List<Node> x) { Node a = _Node(Type.LIST); a._list = x; return a; }
    public static Node Map ()             { return Node.Map (new HashMap<>()); }
    public static Node Map (Map<String, 
                             Node>     x) { Node a = _Node(Type.MAP);  a._map  = x; return a; }
    
    public Node() {}
    
    public Type              type    () { return _type; }
    public Long              asInt   () { return _int; }
    public Boolean           asBool  () { return _bool; }
    public String            asStr   () { return _str; }
    public List<Node>        asList  () { return _list; }
    public Map<String, Node> asMap   () { return _map; }
    public Object            asObj   () {
        
        switch (type()) {
            
            case NULL: return null;
            case INT : return asInt();
            case BOOL: return asBool();
            case STR : return asStr();
            case LIST: return asList();
            case MAP : return asMap();
            default  : throw new AssertionError("not switching through all cases of type");
        }
    }
    public Node              get     (Integer i)         { return _list.get(i); }
    public void              set     (Integer i,
                                      Node    x)         { _list.set(i, x); }
    public void              add     (Node    x)         { _list.add   (x); }
    public Node              getItem (String  i)         { return _map .get(i); }
    public Node              getItem (Integer i)         { return getItem(i.toString()); }
    public Node              getItem (Long    i)         { return getItem(i.toString()); }
    public void              setItem (String  i, Node x) { _map .put(i, x); }
    public void              setItem (Integer i, Node x) { setItem (i.toString(), x); }
    public void              setItem (Long    i, Node x) { setItem (i.toString(), x); }
    public Node              copy    () {
        
        switch (type()) {
            
            case NULL: return Null();
            case INT : return Int (asInt ());
            case BOOL: return Bool(asBool());
            case STR : return Str (asStr ());
            case LIST: return List(asList());
            case MAP : return Map (asMap ());
            default: throw new AssertionError("not switching through all cases of type");
        }
    }
    public Node              deepcopy() {
        
        switch (type()) {
            
            case LIST:
                
                List<Node> l = new java.util.ArrayList<>(asList().size());
                for (Node x: asList()) {
                    
                    l.add(x.deepcopy());
                }
                return Node.List(l);
                
            case MAP :
                
                Map<String,Node> m = new java.util.HashMap<>(asMap().size());
                for (Map.Entry<String,Node> x: asMap().entrySet()) {
                    
                    m.put(x.getKey(), x.getValue().deepcopy());
                }
                return Node.Map(m);
                
            default: return copy();
        }
    }
    
    @Override public    String      toString() {
        
        return String.format("Node(%s)", asObj());
    }
}
