package com.wileyedge.flooring.view;

public interface UserIO {
    void println(String msg);
    String readString(String prompt);
    int readInt(String prompt, int min, int max);
}
