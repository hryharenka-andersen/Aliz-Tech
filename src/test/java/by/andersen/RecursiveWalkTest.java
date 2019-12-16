package by.andersen;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RecursiveWalkTest {

    private static final String PREFIX = "sub_";
    private static final String EXTENSION_BAK = ".bak";
    private static final String EXTENSION_TXT = ".txt";
    private static final String FILE_NAME = "test_file";
    private static final String DIR_NAME = "test_dir";
    private static final String ROOT_DIR = System.getProperty("user.home") + File.separator + DIR_NAME;
    private static final int SUB_DIR_QUANTITY = 25;
    private static final int FILE_QUANTITY = 15;


    @BeforeAll
    public static void createFilesAndPackages() throws IOException {
        createDirectory(ROOT_DIR);
        for (int i = 0; i < SUB_DIR_QUANTITY; i++) {
            String subPath = ROOT_DIR + File.separator + PREFIX + i + DIR_NAME;
            createDirectory(subPath);
            String extension;
            extension = i != 0 ? EXTENSION_BAK : EXTENSION_TXT;
            for (int j = 0; j < FILE_QUANTITY; j++) {
                createFile(subPath + File.separator + j + FILE_NAME + extension);
            }
        }
    }

    private static void createDirectory(String path) {
        File file = new File(path);
        if (file.mkdir()) {
            System.out.println(path + " - Directory created");
        } else {
            System.out.println("Directory " + path + " already exists");
        }
    }

    private static void createFile(String path) throws IOException {
        File file = new File(path);
        if (file.createNewFile()) {
            System.out.println(path + " - file created");
        } else {
            System.out.println("file " + path + " already exists");
        }
    }

    @Test
    public void shouldBeOneDirWithTxtFiles() throws IOException {
        RecursiveWalk w = new RecursiveWalk(Paths.get(ROOT_DIR).toRealPath());
        ForkJoinPool p = new ForkJoinPool();
        p.invoke(w);
        assertEquals(1, Objects.requireNonNull(new File(ROOT_DIR).listFiles()).length);
    }

    @AfterAll
    public static void delete() throws IOException {
        File file = new File(ROOT_DIR);
        delete(file);
    }

    private static void delete(File f) throws IOException {
        if (f.isDirectory()) {
            for (File file : Objects.requireNonNull(f.listFiles()))
                delete(file);
        }
        if (!f.delete()) {
            throw new FileNotFoundException("Failed to delete file: " + f);
        } else {
            System.out.println(f.getAbsolutePath() + " was deleted!");
        }
    }
}