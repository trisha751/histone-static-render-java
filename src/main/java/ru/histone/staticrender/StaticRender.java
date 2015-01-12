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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.yaml.snakeyaml.Yaml;
import ru.histone.Histone;
import ru.histone.HistoneBuilder;
import ru.histone.HistoneException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class StaticRender {
    private static Histone histone;
    private static Yaml yaml;
    private static Map<String, ArrayNode> layouts = new HashMap();
    private static ObjectMapper jackson;

    public static void main(String... args) {
        Path workingDir = Paths.get(".");
    }

    public static void renderSite(String srcDir, String dstDir) throws IOException, HistoneException {
        StaticRender app = new StaticRender();
        app.renderSite(Paths.get(srcDir), Paths.get(dstDir));
    }

    public StaticRender() {
        HistoneBuilder builder = new HistoneBuilder();
        try {
            histone = builder.build();
        } catch (HistoneException e) {
            throw new RuntimeException("Error initializing Histone", e);
        }
        yaml = new Yaml();
        jackson = new ObjectMapper();
    }

    public void renderSite(final Path srcDir, final Path dstDir) {
        Path contentDir = srcDir.resolve("content/");
        final Path layoutDir = srcDir.resolve("layouts/");

        FileVisitor<Path> layoutVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".histone")) {
                    ArrayNode ast = null;
                    try {
                        ast = histone.parseTemplateToAST(new FileReader(file.toFile()));
                    } catch (HistoneException e) {
                        throw new RuntimeException("Error parsing histone template:" + e.getMessage(), e);
                    }

                    final String fileName = file.getFileName().toString();
                    String layoutId = fileName.substring(0, fileName.length() - 8);
                    layouts.put(layoutId, ast);
                } else {
                    final Path relativeFileName = srcDir.resolve("layouts").relativize(Paths.get(file.toUri()));
                    final Path resolvedFile = dstDir.resolve(relativeFileName);
                    if (!resolvedFile.getParent().toFile().exists()) {
                        Files.createDirectories(resolvedFile.getParent());
                    }
                    Files.copy(Paths.get(file.toUri()), resolvedFile, StandardCopyOption.REPLACE_EXISTING, LinkOption.NOFOLLOW_LINKS);
                }
                return FileVisitResult.CONTINUE;
            }
        };


        FileVisitor<Path> contentVisitor = new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                Scanner scanner = new Scanner(file, "UTF-8");
                scanner.useDelimiter("-----");

                String meta = null;
                StringBuilder content = new StringBuilder();

                if (!scanner.hasNext()) {
                    throw new RuntimeException("Wrong format #1:" + file.toString());
                }

                if (scanner.hasNext()) {
                    meta = scanner.next();
                }

                if (scanner.hasNext()) {
                    content.append(scanner.next());
                    scanner.useDelimiter("\n");
                }

                while (scanner.hasNext()) {
                    final String next = scanner.next();
                    content.append(next);

                    if (scanner.hasNext()) {
                        content.append("\n");
                    }
                }

                Map<String, String> metaYaml = (Map<String, String>) yaml.load(meta);

                String layoutId = metaYaml.get("layout");

                if (!layouts.containsKey(layoutId)) {
                    throw new RuntimeException(MessageFormat.format("No layout with id={0} found", layoutId));
                }

                final Path relativeFileName = srcDir.resolve("content").relativize(Paths.get(file.toUri()));
                final Path resolvedFile = dstDir.resolve(relativeFileName);
                if (!resolvedFile.getParent().toFile().exists()) {
                    Files.createDirectories(resolvedFile.getParent());
                }
                Writer output = new FileWriter(resolvedFile.toFile());
                ObjectNode context = jackson.createObjectNode();
                ObjectNode metaNode = jackson.createObjectNode();
                context.put("content", content.toString());
                context.put("meta", metaNode);
                for (String key : metaYaml.keySet()) {
                    if (!key.equalsIgnoreCase("content")) {
                        metaNode.put(key, metaYaml.get(key));
                    }
                }

                try {
                    histone.evaluateAST(layoutDir.toUri().toString(), layouts.get(layoutId), context, output);
                    output.flush();
                } catch (HistoneException e) {
                    throw new RuntimeException("Error evaluating content: " + e.getMessage(), e);
                } finally {
                    output.close();
                }

                return FileVisitResult.CONTINUE;
            }
        };

        try {
            Files.walkFileTree(layoutDir, layoutVisitor);
            Files.walkFileTree(contentDir, contentVisitor);
        } catch (Exception e) {
            throw new RuntimeException("Error during site render", e);
        }
    }
}
