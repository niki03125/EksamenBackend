package app.exceptions;

//record er for at minimere boiler plade code, automatisk getter, setter, toString, equals, hashcode
public record Message(int statusCode, String msg) {
}
