package com.advent23;

public record AdventResult(Object val) {

    static AdventResult ofStr(Object nString) {
        return new AdventResult(nString);
    }

    static AdventResult ofLong(Long val) {
        return new AdventResult(val);
    }

    @Override
    public String toString() {
        if (val != null) {
            return val.toString();
        }
        throw new NullPointerException("no values stored");
    }
}
