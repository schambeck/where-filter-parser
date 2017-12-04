# where-filter-parser

Maven:
```xml
<dependency>
  <groupId>com.schambeck.wherefilter</groupId>
  <artifactId>where-filter-parser</artifactId>
  <version>1.0-SNAPSHOT</version>
</dependency>
```

Example:
```java
public class Main {
    public static void main(String[] args) {
        String where = "name LIKE 'mouse%'";
        WhereParser parser = Parboiled.createParser(WhereParser.class);
        ParsingResult<?> result = new ReportingParseRunner(parser.Expression()).run(where);
        String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
        System.out.println(parseTreePrintOut);
    }
}
```
