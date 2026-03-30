package util;

import java.util.Scanner;

// nhap du lieu tu ban phim

public class InputUtil {
    private static final Scanner sc = new Scanner(System.in);

    public static String inputString(String message) {
        System.out.print(message);
        return sc.nextLine().trim();
    }

    public static int inputInt(String message) {
        while (true) {
            try {
                System.out.print(message);
                int value = Integer.parseInt(sc.nextLine());
                return value;
            } catch (Exception e) {
                System.out.println("Vui long nhap so nguyen hop le!");
            }
        }
    }

    public static double inputDouble(String message) {
        while (true) {
            try {
                System.out.print(message);
                double value = Double.parseDouble(sc.nextLine());
                return value;
            } catch (Exception e) {
                System.out.println("Vui long nhap so thuc hop le!");
            }
        }
    }

    public static int inputPositiveInt(String message) {
        while (true) {
            int value = inputInt(message);
            if (value > 0) {
                return value;
            }
            System.out.println("gia tri phai > 0!");
        }
    }

    public static boolean inputYesNo(String message) {
        while (true) {
            System.out.print(message + " (y/n): ");
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                return true;
            }
            if (input.equals("n")) {
                return false;
            }
            System.out.println("chi duoc nhap y hoac n!");
        }
    }
}
