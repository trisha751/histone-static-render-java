/**
 *    Copyright 2013-2014 MegaFon
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package ru.histone.staticrender;

import com.google.common.base.Joiner;
import org.junit.Before;
import org.junit.Test;
import ru.histone.HistoneException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.Assert.fail;

public class StaticRenderTest {
    private StaticRender staticRender;

    @Before
    public void beforeTest() throws HistoneException, IOException, URISyntaxException {
        staticRender = new StaticRender();
    }

    @Test
    public void test() throws IOException, URISyntaxException {
        Path baseDir = Paths.get(this.getClass().getClassLoader().getResource("testsite/testsite.marker").toURI()).getParent().getParent().getParent().getParent();
        Path srcDir = Paths.get(baseDir.toString(), "src/test/resources/testsite");
        Path expectedDir = Paths.get(baseDir.toString(), "src/test/resources/testsite_expected");
        Path dstDir = Paths.get(baseDir.toString(), "target", "site");
        if (!dstDir.toFile().exists()) {
            Files.createDirectory(dstDir);
        }

        staticRender.renderSite(srcDir, dstDir);

        assertFilesEqualsInDirs(expectedDir, dstDir);
    }

    private void assertFilesEqualsInDirs(final Path expectedDir, final Path actualDir) throws IOException {
        final Set<String> notFoundInResult = new TreeSet();
        final Set<String> unexpectedFilesInResult = new TreeSet();
        final Set<String> notIdenticalFiles = new TreeSet();

        FileVisitor<Path> filesVisitor1 = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = expectedDir.relativize(file);

                String expected = new String(Files.readAllBytes(expectedDir.resolve(relativePath)), "UTF-8");
                String actual = null;
                final Path actualFile = actualDir.resolve(relativePath);
                if (actualFile.toFile().exists()) {
                    actual = new String(Files.readAllBytes(actualFile), "UTF-8");
                    if (!expected.equals(actual)) {
                        notIdenticalFiles.add(relativePath.toString());
                    }
                } else {
                    notFoundInResult.add(relativePath.toString());
                }


                return FileVisitResult.CONTINUE;
            }
        };


        FileVisitor<Path> filesVisitor2 = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Path relativePath = actualDir.relativize(file);

                String expected = new String(Files.readAllBytes(actualDir.resolve(relativePath)), "UTF-8");
                String actual = null;
                final Path actualFile = expectedDir.resolve(relativePath);
                if (actualFile.toFile().exists()) {
                    actual = new String(Files.readAllBytes(actualFile), "UTF-8");
                    if (!expected.equals(actual)) {
                        notIdenticalFiles.add(relativePath.toString());
                    }
                } else {
                    unexpectedFilesInResult.add(relativePath.toString());
                }


                return FileVisitResult.CONTINUE;
            }
        };

        Files.walkFileTree(expectedDir, filesVisitor1);
        Files.walkFileTree(actualDir, filesVisitor2);

        StringBuilder err = new StringBuilder();
        if (notFoundInResult.size() > 0) {
            err.append("Files not found in result: " + Joiner.on(", ").join(notFoundInResult)).append("\n");
        }
        if (unexpectedFilesInResult.size() > 0) {
            err.append("Unexpected files found in result: " + Joiner.on(", ").join(unexpectedFilesInResult)).append("\n");
        }
        if (notIdenticalFiles.size() > 0) {
            err.append("Files differ in expected and in result: " + Joiner.on(", ").join(notIdenticalFiles)).append("\n");
        }

        if (err.length() > 0) {
            fail("Folders diff fail:\n" + err.toString());
        }
    }
}
