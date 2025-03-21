package jl95.json;

public class NodeBuilderTest {
 
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
}
