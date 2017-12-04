package com.schambeck.wherefilter.parser;

import org.parboiled.BaseParser;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;

/**
 * Expression ← Term ((‘and’ / ‘or’) Term)*
 * Term ← Identifier Operator Value / ‘(’ Expression ‘)’
 * Identifier ← [a-z]+
 * Operator ← '=' '!='
 * Value ← Literal
 * Literal ← ''' [a-z] ''' / [0-9]
 */
@BuildParseTree
public class WhereParser extends BaseParser<Object> implements Visitable { // TODO: 27/11/17 Generic em vez de Object

    public Rule Expression() {
        return Term();
    }

    Rule Term() {
        return OneOrMore(
                SubExpression(),
                ZeroOrMore(Sequence(LogicalOperator(), SubExpression()))
        );
    }

    Rule SubExpression() {
        return FirstOf(
                SimpleOperator(),
                In(),
                Between(),
                Like()
        );
    }

    Rule Like() {
        return Sequence(
                Identifier(),
                IgnoreCase("like "),
                String()
        );
    }

    Rule Between() {
        return Sequence(
                Identifier(),
                IgnoreCase("between "),
                Range()
        );
    }

    Rule In() {
        return Sequence(
                Identifier(),
                IgnoreCase("in "),
                Values()
        );
    }

    Rule SimpleOperator() {
        return Sequence(
                Identifier(),
                Operator(),
                Value()
        );
    }

    // TODO: 26/11/17 implementar precedência de parênteses ()
    // TODO: 26/11/17 implementar operador not !
    Rule LogicalOperator() {
        return FirstOf(
                IgnoreCase("and "),
                IgnoreCase("or ")
        );
    }

    Rule Values() {
        return Sequence(
                "(",
                OneOrMore(
                        Value(),
                        ZeroOrMore(Sequence(",", Value()))
                ),
                ") "
        );
    }

    Rule Identifier() {
        return Sequence(
                OneOrMore(
                        FirstOf(
                                Letter(),
                                Digit(),
                                Ch('.'),
                                Ch('_')
                        )
                ),
                WhiteSpace()
        );
    }

    Rule Operator() {
        // colocar primeiro os operadores com mais caracteres
        return FirstOf(">= ", "<= ", "!= ", "= ", "> ", "< ");
    }

    Rule Value() {
        return Sequence(
                FirstOf(
                        String(),
                        Identifier(),
                        Number()
                ),
                WhiteSpace()
        );
    }

    Rule Range() {
        return Sequence(
                FirstOf(
                        String(),
                        Number()
                ),
                IgnoreCase(" and "),
                FirstOf(
                        String(),
                        Number()
                ),
                WhiteSpace()
        );
    }

//    Rule String() {
//        return Sequence("'", ZeroOrMore(ANY), "'");
//    }

    Rule String() {
        return FirstOf(STRING_LITERAL_LONG1(), STRING_LITERAL1(),
                STRING_LITERAL_LONG2(), STRING_LITERAL2());
    }

    Rule STRING_LITERAL_LONG1() {
        return Sequence("'''", ZeroOrMore(Sequence(
                Optional(FirstOf("''", "'")), FirstOf(Sequence(TestNot(FirstOf(
                        "'", "\\")), ANY), ECHAR()))), "'''", WS());
    }

    Rule STRING_LITERAL_LONG2() {
        return Sequence("\"\"\"", ZeroOrMore(Sequence(Optional(FirstOf("\"\"", "\"")),
                FirstOf(Sequence(TestNot(FirstOf("\"", "\\")), ANY), ECHAR()))), "\"\"\"", WS());
    }

    Rule STRING_LITERAL1() {
        return Sequence("'", ZeroOrMore(FirstOf(Sequence(TestNot(FirstOf("'",
                '\\', '\n', '\r')), ANY), ECHAR())), "'", WS());
    }

    Rule STRING_LITERAL2() {
        return Sequence('"', ZeroOrMore(FirstOf(Sequence(TestNot(AnyOf("\"\\\n\r")), ANY), ECHAR())), '"', WS());
    }

    Rule ECHAR() {
        return Sequence('\\', AnyOf("tbnrf\\\"\'"));
    }

    Rule WS() {
        return ZeroOrMore(FirstOf(COMMENT(), WS_NO_COMMENT()));
    }

    Rule COMMENT() {
        return Sequence('#', ZeroOrMore(Sequence(TestNot(EOL()), ANY)), EOL());
    }

    Rule WS_NO_COMMENT() {
        return FirstOf(Ch(' '), Ch('\t'), Ch('\f'), EOL());
    }

    Rule EOL() {
        return AnyOf("\n\r");
    }

    Rule Letter() {
        return FirstOf(
                CharRange('a', 'z'),
                CharRange('A', 'Z')
        );
    }

    Rule Number() {
        return Sequence(
                Optional('-'),
                OneOrMore(Digit()),
                Optional('.', OneOrMore(Digit()))
        );
    }

    Rule Digit() {
        return CharRange('0', '9');
    }

    Rule WhiteSpace() {
        return ZeroOrMore(AnyOf(" \t\f"));
    }

    // we redefine the rule creation for string literals to automatically match trailing whitespace if the string
    // literal ends with a space character, this way we don't have to insert extra whitespace() rules after each
    // character or string literal
    @Override
    protected Rule fromStringLiteral(String string) {
        return string.endsWith(" ") ?
                Sequence(String(string.substring(0, string.length() - 1)), WhiteSpace()) :
                String(string);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

}
