package jl95.json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NodeBuilderTest {

    private static <T, L extends List<T>> L toList(L list, T... elements) {
        list.addAll(Arrays.asList(elements));
        return list;
    }
    private static <T> ArrayList<T> toArrayList(T... elements) {
        return toList(new ArrayList<>(elements.length), elements);
    }
    private static class Entry<K, V> {
        public final K key;
        public final V value;
        public Entry(K key, V value) {this.key = key; this.value = value;}
    }
    private static <K, V> Entry<K, V> entry(K key, V value) {return new Entry<>(key, value);}
    private static <K, V, M extends Map<K, V>> M toMap(M map, Entry<K, V>... entries) {
        for (Entry<K, V> e: entries) {
            map.put(e.key, e.value);
        }
        return map;
    }
    private static <K, V> HashMap<K, V> toHashMap(Entry<K, V>... elements) {
        return toMap(new HashMap<>(elements.length), elements);
    }

    @org.junit.Test
    public void testNumber() {
        org.junit.Assert.assertEquals   (Node.Int(4242).asInt(), NodeBuilder.build("4242").asInt());
        org.junit.Assert.assertNotEquals(Node.Int(4242).asInt(), NodeBuilder.build("2424").asInt());
    }
    @org.junit.Test
    public void testNumberPadded() {
        org.junit.Assert.assertEquals   (Node.Int(4242).asInt(), NodeBuilder.build("     4242    ").asInt());
        try {
            NodeBuilder.build("42  42");
            org.junit.Assert.fail("should not be able to parse \"42 42\"");
        }
        catch (Exception ex) {/**/}
    }
    @org.junit.Test
    public void testBoolean() {
        org.junit.Assert.assertEquals   (Node.Bool(true) .asBool(), NodeBuilder.build("true") .asBool());
        org.junit.Assert.assertNotEquals(Node.Bool(true) .asBool(), NodeBuilder.build("false").asBool());
        org.junit.Assert.assertEquals   (Node.Bool(false).asBool(), NodeBuilder.build("false").asBool());
    }
    @org.junit.Test
    public void testBooleanPadded() {
        org.junit.Assert.assertEquals   (Node.Bool(true) .asBool(), NodeBuilder.build("     true    ").asBool());
        try {
            NodeBuilder.build("tr  ue");
            org.junit.Assert.fail("should not be able to parse \"tr ue\"");
        }
        catch (Exception ex) {/**/}
    }
    @org.junit.Test
    public void testString() {
        org.junit.Assert.assertEquals   (Node.Str("foobar") .asStr(), NodeBuilder.build("\"foobar\"").asStr());
        org.junit.Assert.assertNotEquals(Node.Str("foobar") .asStr(), NodeBuilder.build("\"barfoo\"").asStr());
        org.junit.Assert.assertEquals   (Node.Str("foo\\bar") .asStr(), NodeBuilder.build("\"foo\\\\bar\"").asStr());
        org.junit.Assert.assertEquals   (Node.Str("foo\"bar") .asStr(), NodeBuilder.build("\"foo\\\"bar\"").asStr());
        org.junit.Assert.assertEquals   (Node.Str("foo\bbar") .asStr(), NodeBuilder.build("\"foo\\bbar\"").asStr());
        for (String repr: new String[] {
            "\"foo\"bar\"",
            "\"foo\\xar\""
        }) {
            try {
                NodeBuilder.build(repr);
                org.junit.Assert.fail("parsing should have failed for: "+repr);
            } catch (Exception ex) {/* as expected */}
        }
    }
    @org.junit.Test
    public void testStringPadded() {
        org.junit.Assert.assertEquals   (Node.Str("foobar") .asStr(), NodeBuilder.build("     \"foobar\"     ").asStr());
    }
    @org.junit.Test
    public void testArrayEmpty() {
        org.junit.Assert.assertEquals(Node.List().asList(), NodeBuilder.build("[]").asList());
    }
    @org.junit.Test
    public void testArray() {
        Node array = Node.List(toArrayList(
            Node.Str("abc"),
            Node.Int(123),
            Node.Bool(true)
        ));
        org.junit.Assert.assertEquals   (array.asList(), NodeBuilder.build(" [\"abc\"        ,          123,true ]  ").asList());
        org.junit.Assert.assertNotEquals(array.asList(), NodeBuilder.build("[\"abc\",321,true]").asList());
    }
    @org.junit.Test
    public void testArrayNested() {
        Node array = Node.List(toArrayList(
            Node.Str("abc"),
            Node.Int(123),
            Node.List(toArrayList(
                Node.List(toArrayList(
                    Node.Null(),
                    Node.Int(42)
                )),
                Node.Str("hello\\there")
            )),
            Node.Bool(true)
        ));
        org.junit.Assert.assertEquals   (array.asList(), NodeBuilder.build("[\"abc\", 123, [[null, 42],\"hello\\\\there\"] ,true]").asList());
        org.junit.Assert.assertNotEquals(array.asList(), NodeBuilder.build("[\"abc\", 123, [[null, 42],\"hello\\\\world\"] ,true]").asList());
    }
    @org.junit.Test
    public void testObjectEmpty() {
        org.junit.Assert.assertEquals(Node.Map().asMap(), NodeBuilder.build("{}").asMap());
    }
    @org.junit.Test
    public void testObject() {
        Node array = Node.Map(toHashMap(
            entry("foo", Node.Str("bar")),
            entry("answer", Node.Int(42)),
            entry("I'm awesome", Node.Bool(true))
        ));
        org.junit.Assert.assertEquals   (array.asList(), NodeBuilder.build("  {  \"foo\":\"bar\"        ,          \"answer\":42,\"I'm awesome\":true } ").asList());
        org.junit.Assert.assertEquals   (array.asList(), NodeBuilder.build("  {  \"foo\":\"bar\"        ,          \"answer\":42,\"I'm awesome\":false } ").asList());
    }
    @org.junit.Test
    public void testObjectNested() {
        Node array = Node.Map(toHashMap(
            entry("aaa", Node.Str("zzz")),
            entry("000", Node.Int(123)),
            entry("something", Node.Map(toHashMap(
                entry("in the way", Node.List(toArrayList(
                    Node.Null(),
                    Node.Int(42)
                ))),
                entry("she", Node.Str("knows"))
            ))),
            entry("true", Node.Bool(false))
        ));
        org.junit.Assert.assertEquals   (array.asList(), NodeBuilder.build("{\"aaa\":\"zzz\", \"000\":123, \"something\":{\"in the way\":[null, 42],\"she\":\"knows\"} ,\"true\":false}").asList());
    }
}
