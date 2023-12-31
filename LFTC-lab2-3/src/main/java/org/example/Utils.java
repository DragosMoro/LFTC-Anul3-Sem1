package org.example;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {
    public static final String FILE_NAME = "src/main/resources/input.txt";

    public static final String FILE_NAME_DFA = "src/main/resources/dfa1.txt";
    public static final String OUTPUT_FILE_NAME = "src/main/resources/output.txt";
    public static final String REGEX_STRING = "^[A-Za-z][A-Za-z0-9]{0,7}$";
    public static final String REGEX_NUMBER = "^([+-]?[1-9]\\d*|0)$";
    public static final String REGEX_FLOAT = "^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$";

    public static final String FILE_NAME_DFA_INT_CONSTANT = "src/main/resources/dfa_int_constant.txt";
    public static final String FILE_NAME_DFA_FLOAT_CONSTANT = "src/main/resources/dfa_float_constant.txt";
    public static final String FILE_NAME_DFA_IDENTIFIER = "src/main/resources/dfa_identifier.txt";

    public static List<String> symbolsList = new ArrayList<>() {
        public boolean add(String elem) {
            if (!contains(elem)) {
                int index = Collections.binarySearch(this, elem);
                if (index < 0) {
                    index = ~index;
                }
                super.add(index, elem);
                return true;
            }
            return false;
        }
    };
}
