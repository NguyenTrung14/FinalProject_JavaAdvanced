package util;

// in thong bao

public class MessageUtil {

    public static void showTitle(String title) {
        System.out.println("\n==============================");
        System.out.println(" " + title.toUpperCase());
        System.out.println("==============================");
    }

    public static void success(String message) {
        System.out.println("[SUCCESS] " + message);
    }

    public static void error(String message) {
        System.out.println("[ERROR] " + message);
    }

    public static void warning(String message) {
        System.out.println("[WARNING] " + message);
    }

    public static void option(int number, String text) {
        System.out.println(number + ". " + text);
    }

    public static void line() {
        System.out.println("--------------------------------");
    }
}