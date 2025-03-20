package jl95.json;

public class ParserTest {
 
    @org.junit.Test
    public void testNumber() {
        org.junit.Assert.assertEquals   (Node.Int(4242).asInt(), new Parser().parse("4242").asInt());
        org.junit.Assert.assertNotEquals(Node.Int(4242).asInt(), new Parser().parse("2424").asInt());
        org.junit.Assert.assertEquals   (Node.Int(4242).asInt(), new Parser().parse("     4242    ").asInt());
        try {
            new Parser().parse("42  42");
            org.junit.Assert.fail("should not be able to parse \"42 42\"");
        }
        catch (Exception ex) {/**/}
    }
    @org.junit.Test
    public void testBoolean() {
        org.junit.Assert.assertEquals   (Node.Bool(true) .asBool(), new Parser().parse("true") .asBool());
        org.junit.Assert.assertNotEquals(Node.Bool(true) .asBool(), new Parser().parse("false").asBool());
        org.junit.Assert.assertEquals   (Node.Bool(false).asBool(), new Parser().parse("false").asBool());
        org.junit.Assert.assertEquals   (Node.Bool(true) .asBool(), new Parser().parse("     true    ").asBool());
        try {
            new Parser().parse("tr  ue");
            org.junit.Assert.fail("should not be able to parse \"tr ue\"");
        }
        catch (Exception ex) {/**/}
    }
}
