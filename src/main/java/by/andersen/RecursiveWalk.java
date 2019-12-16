package by.andersen;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.RecursiveAction;

class RecursiveWalk extends RecursiveAction {
    private static final String EXTENSION_BAK_FILE = ".bak";
    private final Path dir;

    RecursiveWalk(Path dir) {
        this.dir = dir;
    }

    @Override
    protected void compute() {
        final List<RecursiveWalk> walks = new ArrayList<>();
        try {
            Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                    FileVisitResult fileVisitResult = FileVisitResult.CONTINUE;
                    if (!RecursiveWalk.this.dir.equals(dir)) {
                        RecursiveWalk w = new RecursiveWalk(dir);
                        w.fork();
                        walks.add(w);
                        fileVisitResult = FileVisitResult.SKIP_SUBTREE;
                    }
                    return fileVisitResult;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (file.toString().toLowerCase().endsWith(EXTENSION_BAK_FILE)) {
                        Files.delete(file);
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (Objects.requireNonNull(dir.toFile().listFiles()).length == 0) {
                        Files.delete(dir);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (RecursiveWalk w : walks) {
            w.join();
        }
    }
}