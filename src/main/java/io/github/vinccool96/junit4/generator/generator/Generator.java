package io.github.vinccool96.junit4.generator.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Used to generate a JUnit 4 test suite class
 */
public class Generator {

    private static final String JAVA = ".java";

    private static final String KOTLIN = ".kt";

    private static final String ALL_TESTS = "AllTests";

    private String currentFolder;

    private String currentPackage;

    private String pathToFirstPackage;

    private static final String ALL_TESTS_JAVA = ALL_TESTS + JAVA;

    private static final String BLANK = "\n";

    public Generator(String currentFolder, String currentPackage, String pathToFirstPackage) {
        this.currentFolder = currentFolder;
        this.currentPackage = currentPackage;
        this.pathToFirstPackage = pathToFirstPackage;
    }

    public void generate() {
        File thisFolder = new File(currentFolder);
        String allTestsFile = currentFolder + "\\" + ALL_TESTS_JAVA;
        File allTests = new File(allTestsFile);
        generateFile(thisFolder, allTests);
    }

    private void generateFile(File thisFolder, File allTests) {
        ArrayList<File> testFiles = getTestFiles(thisFolder);
        LinkedList<String> stringsToWrite = new LinkedList<>();
        addStart(stringsToWrite, testFiles);
        addEnd(stringsToWrite, testFiles);
        writeAllTests(stringsToWrite, allTests);
    }

    @SuppressWarnings("ConstantConditions")
    private ArrayList<File> getTestFiles(File folder) {
        ArrayList<File> classes = new ArrayList<>();
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                addTestFilesOfFolder(classes, file);
            }
        }
        return classes;
    }

    @SuppressWarnings("ConstantConditions")
    private void addTestFilesOfFolder(ArrayList<File> classes, File folder) {
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                addTestFilesOfFolder(classes, file);
            } else if (FileUtil.getInstance().isTestFile(file)) {
                classes.add(file);
            }
        }
    }

    private void addBlank(LinkedList<String> stringsToWrite) {
        stringsToWrite.add(BLANK);
    }

    private String getImportString(File testFile) {
        String target = this.pathToFirstPackage.contains("/") ? this.pathToFirstPackage.replace("/", "\\") :
                this.pathToFirstPackage;
        String name = testFile.toString().replace(target, "");
        return "import " + name.replace(JAVA, ";").replace(KOTLIN, ";").replace("\\", ".");
    }

    private String getClassString(File testFile) {
        String name = testFile.getName();
        String CLASS = ".class";
        return name.replace(JAVA, CLASS).replace(KOTLIN, CLASS);
    }

    private LinkedList<String> getImportStrings(ArrayList<File> testFiles) {
        String IMPORTS = "import org.junit.runner.RunWith;\n" +
                "import org.junit.runners.Suite;\n" +
                "import org.junit.runners.Suite.SuiteClasses;";
        LinkedList<String> importStrings = new LinkedList<>(Arrays.asList(IMPORTS.split("\n")));
        for (File file : testFiles) {
            importStrings.add(getImportString(file));
        }
        importStrings.sort(new ImportStringComparator());
        return importStrings;
    }

    private void addStart(LinkedList<String> stringsToWrite, ArrayList<File> testFiles) {
        String packageString = "package " + this.currentPackage + ";";
        stringsToWrite.add(packageString);
        addBlank(stringsToWrite);
        LinkedList<String> importStrings = getImportStrings(testFiles);
        stringsToWrite.addAll(importStrings);
        addBlank(stringsToWrite);
    }

    private void addClass(LinkedList<String> stringsToWrite) {
        stringsToWrite.add("public class " + ALL_TESTS + " {");
        stringsToWrite.add("}");
    }

    private void addEnd(LinkedList<String> stringsToWrite, ArrayList<File> testFiles) {
        String RUN_WITH = "@RunWith(Suite.class)";
        stringsToWrite.add(RUN_WITH);
        addSuiteClassesString(stringsToWrite, testFiles);
        addClass(stringsToWrite);
    }

    private void addSuiteClassesString(LinkedList<String> stringsToWrite, ArrayList<File> testFiles) {
        StringBuilder suiteString = new StringBuilder("@SuiteClasses({ ");
        for (int i = 0; i < testFiles.size(); i++) {
            File testFile = testFiles.get(i);
            String classString = getClassString(testFile);
            suiteString.append(classString);
            if (i != testFiles.size() - 1) {
                suiteString.append(", ");
            }
        }
        suiteString.append(" })");
        stringsToWrite.add(suiteString.toString());
    }

    private void writeAllTests(LinkedList<String> stringsToWrite, File allTests) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(allTests, false));
            for (String stringToWrite : stringsToWrite) {
                if (!stringToWrite.equals(BLANK)) {
                    bufferedWriter.write(stringToWrite);
                }
                bufferedWriter.newLine();
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
