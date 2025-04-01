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
        org.junit.Assert.assertEquals   (Node.Long(4242).asLong(), NodeBuilder.build("4242").asLong());
        org.junit.Assert.assertNotEquals(Node.Long(4242).asLong(), NodeBuilder.build("2424").asLong());
        org.junit.Assert.assertEquals   (Node.Double(42.42).asDouble(), NodeBuilder.build("42.42").asDouble());
        org.junit.Assert.assertNotEquals(Node.Double(42.42).asDouble(), NodeBuilder.build("24.24").asDouble());
    }
    @org.junit.Test
    public void testNumberPadded() {
        org.junit.Assert.assertEquals   (Node.Long(4242).asLong(), NodeBuilder.build("     4242    ").asLong());
        try {
            NodeBuilder.build("42  42");
            org.junit.Assert.fail("should not be able to parse \"42 42\"");
        }
        catch (Exception ex) {/**/}
    }
    @org.junit.Test
    public void testBoolean() {
        org.junit.Assert.assertEquals   (Node.Bool(true) .asBoolean(), NodeBuilder.build("true") .asBoolean());
        org.junit.Assert.assertNotEquals(Node.Bool(true) .asBoolean(), NodeBuilder.build("false").asBoolean());
        org.junit.Assert.assertEquals   (Node.Bool(false).asBoolean(), NodeBuilder.build("false").asBoolean());
    }
    @org.junit.Test
    public void testBooleanPadded() {
        org.junit.Assert.assertEquals   (Node.Bool(true) .asBoolean(), NodeBuilder.build("     true    ").asBoolean());
        try {
            NodeBuilder.build("tr  ue");
            org.junit.Assert.fail("should not be able to parse \"tr ue\"");
        }
        catch (Exception ex) {/**/}
    }
    @org.junit.Test
    public void testString() {
        org.junit.Assert.assertEquals   (Node.String("foobar")  .asString(), NodeBuilder.build("\"foobar\"")    .asString());
        org.junit.Assert.assertNotEquals(Node.String("foobar")  .asString(), NodeBuilder.build("\"barfoo\"")    .asString());
        org.junit.Assert.assertEquals   (Node.String("foo\\bar").asString(), NodeBuilder.build("\"foo\\\\bar\"").asString());
        org.junit.Assert.assertEquals   (Node.String("foo\"bar").asString(), NodeBuilder.build("\"foo\\\"bar\"").asString());
        org.junit.Assert.assertEquals   (Node.String("foo\bbar").asString(), NodeBuilder.build("\"foo\\bbar\"") .asString());
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
        org.junit.Assert.assertEquals   (Node.String("foobar") .asString(), NodeBuilder.build("     \"foobar\"     ").asString());
    }
    @org.junit.Test
    public void testArrayEmpty() {
        org.junit.Assert.assertEquals(Node.List().asList(), NodeBuilder.build("[]").asList());
    }
    @org.junit.Test
    public void testArray() {
        Node array = Node.List(toArrayList(
            Node.String("abc"),
            Node.Long(123),
            Node.Bool(true)
        ));
        org.junit.Assert.assertEquals   (array.asList(), NodeBuilder.build(" [\"abc\"        ,          123,true ]  ").asList());
        org.junit.Assert.assertNotEquals(array.asList(), NodeBuilder.build("[\"abc\",321,true]").asList());
    }
    @org.junit.Test
    public void testArrayNested() {
        Node array = Node.List(toArrayList(
            Node.String("abc"),
            Node.Long(123),
            Node.List(toArrayList(
                Node.List(toArrayList(
                    Node.Null(),
                    Node.Long(42)
                )),
                Node.String("hello\\there")
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
            entry("foo", Node.String("bar")),
            entry("answer", Node.Long(42)),
            entry("I'm awesome", Node.Bool(true))
        ));
        org.junit.Assert.assertEquals   (array.asList(), NodeBuilder.build("  {  \"foo\":\"bar\"        ,          \"answer\":42,\"I'm awesome\":true } ").asList());
        org.junit.Assert.assertEquals   (array.asList(), NodeBuilder.build("  {  \"foo\":\"bar\"        ,          \"answer\":42,\"I'm awesome\":false } ").asList());
    }
    @org.junit.Test
    public void testObjectNested() {
        Node array = Node.Map(toHashMap(
            entry("aaa", Node.String("zzz")),
            entry("000", Node.Long(123)),
            entry("something", Node.Map(toHashMap(
                entry("in the way", Node.List(toArrayList(
                    Node.Null(),
                    Node.Long(42)
                ))),
                entry("she", Node.String("knows"))
            ))),
            entry("true", Node.Bool(false))
        ));
        org.junit.Assert.assertEquals   (array.asList(), NodeBuilder.build("{ \r\n\"aaa\":\"zzz\", \"000\":123, \n\"something\":{\"in the way\":\n\n\r\t[null, 42],\"she\":\"knows\"} ,\"true\":false}").asList());
    }
}
