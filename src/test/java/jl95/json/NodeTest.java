package jl95.json;

import org.junit.Assert;

import java.util.*;

public class NodeTest {

    private static <T> List<T>    listOf(T...          elements) {
        List<T> list = new ArrayList<>(elements.length);
        list.addAll(Arrays.asList(elements));
        return list;
    }
    private static class Entry<K, V> {K k; V v; public Entry(K k, V v){this.k=k;this.v=v;} public static <K,V> Entry<K,V> of(K k, V v){return new Entry<>(k,v);}}
    private static <K,V> Map<K,V> mapOf (Entry<K,V>... entries) {
        Map<K,V> map = new HashMap<>(entries.length);
        for (Entry<K,V> e: entries) map.put(e.k, e.v);
        return map;
    }

    @org.junit.Test
    public void testNull() {
        Assert.assertEquals(Node.Null(), Node.Null());
    }
    @org.junit.Test
    public void testBool() {
        Assert.assertEquals   (Node.Bool(true), Node.Bool(true));
        Assert.assertNotEquals(Node.Bool(true), Node.Bool(false));
        Assert.assertEquals   (Node.Bool(false), Node.Bool(false));
    }
    @org.junit.Test
    public void testInt() {
        Assert.assertEquals   (Node.Int(123), Node.Int(123));
        Assert.assertNotEquals(Node.Int(123), Node.Int(42));
        Assert.assertNotEquals(Node.Int(123), Node.Bool(true));
    }
    @org.junit.Test
    public void testString() {
        Assert.assertEquals   (Node.Str("hello"), Node.Str("hello"));
        Assert.assertNotEquals(Node.Str("hello"), Node.Str("world"));
        Assert.assertNotEquals(Node.Str("hello"), Node.Int(1234));
        Assert.assertNotEquals(Node.Str("hello"), Node.Bool(false));
    }
    private List<Node> makeList(Integer salt) {
        return listOf(Node.Bool(true), Node.Int(123+salt), Node.Str("universe"));
    }
    @org.junit.Test
    public void testList() {
        Assert.assertEquals   (makeList(0), makeList(0));
        Assert.assertNotEquals(makeList(0), makeList(1));
        Assert.assertEquals   (Node.List(), Node.List());
        Assert.assertNotEquals(Node.List(), makeList(0));
    }
    private Map<String,Node> makeMap(String salt) {
        return mapOf(
                Entry.of("abc",    Node.Bool(false)),
                Entry.of("answer", Node.Int(42)),
                Entry.of("nested", Node.List(listOf(Node.Str("eggs"+salt), Node.Null())))
        );
    }
    @org.junit.Test
    public void testMap() {
        Assert.assertEquals   (makeMap(""), makeMap(""));
        Assert.assertNotEquals(makeMap(""), makeMap(" benedict"));
        Assert.assertEquals   (Node.Map(),  Node.Map());
        Assert.assertNotEquals(Node.Map(),  makeMap(""));
    }
}
