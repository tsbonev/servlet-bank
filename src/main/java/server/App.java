package server;

public class App {
    public static void main(String[] args) {
        Jetty jetty = new Jetty(8080);
        jetty.start();
    }
}
