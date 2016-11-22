package am.server;

import static spark.Spark.get;

public class Main {
    public static void main(String[] args) {
        get("/", (req, res) -> "Hello World");
    }
}
