package io.github.vinccool96.junit4.generator.generator;

import java.util.Comparator;

public class ImportStringComparator implements Comparator<String> {

    @Override
    public int compare(String o1, String o2) {
        String withoutSemicolon1 = o1.replace(";", "");
        String withoutSemicolon2 = o2.replace(";", "");
        return withoutSemicolon1.compareTo(withoutSemicolon2);
    }

}
