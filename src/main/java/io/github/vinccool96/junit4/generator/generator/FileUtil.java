package io.github.vinccool96.junit4.generator.generator;

import java.io.*;

class FileUtil {

    private static FileUtil instance;

    public static FileUtil getInstance() {
        if (instance == null) {
            instance = new FileUtil();
        }
        return instance;
    }

    private FileUtil() {
    }

    public boolean isTestFile(File file) throws IOException {
        boolean result = false;
        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);
        String line;
        while ((line = br.readLine()) != null) {
            if (line.contains("@Test")) {
                result = true;
                break;
            }
            System.out.println(line);
        }
        br.close();
        fr.close();
        return result;
    }

}
