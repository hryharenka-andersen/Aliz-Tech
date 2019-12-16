package by.andersen;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ForkJoinPool;

public class Main {

    private static final String SPECIFY_DIR_MESSAGE = "Please, specify root dir as a command argument!";
    private static final String NOT_VALID_DIR_MESSAGE = "Dir is not valid!";
    private static final String SHUTDOWN_MESSAGE = "The program will be forced to shutdown!";

    public static void main(String[] args) {

        checkArgs(args);

        File file = Paths.get(args[0]).toFile();

        checkFile(file);

        RecursiveWalk recursiveWalk = new RecursiveWalk(file.toPath());
        ForkJoinPool p = new ForkJoinPool();
        p.invoke(recursiveWalk);
    }

    private static void checkArgs(String[] args) {
        if (args.length < 1 || args[0] == null) {
            System.out.println(SPECIFY_DIR_MESSAGE);
            forceToShutdown();
        }

    }

    private static void checkFile(File file) {
        if (file == null || !file.isDirectory()) {
            System.out.println(NOT_VALID_DIR_MESSAGE);
            forceToShutdown();
        }
    }

    private static void forceToShutdown() {
        System.out.println(SHUTDOWN_MESSAGE);
        System.exit(0);
    }
}

