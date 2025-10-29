package com.wileyedge.flooring.view;

import java.util.Scanner;

public class UserIOConsoleImpl implements UserIO{

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void println(String msg) {
        System.out.println(msg);
    }

    @Override
    public String readString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    @Override
    public int readInt(String prompt, int min, int max) {

        while (true) {
            System.out.print(prompt);

            try {
                int val = Integer.parseInt(scanner.nextLine());
                if (val < min || val > max) println("Value must be between " + min + " and " + max);
                else return val;
            } catch (NumberFormatException e) { println("Please enter a valid integer"); }
        }

    }
}
