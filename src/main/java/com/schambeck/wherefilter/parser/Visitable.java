package com.schambeck.wherefilter.parser;

public interface Visitable {

    void accept(Visitor visitor);
}
