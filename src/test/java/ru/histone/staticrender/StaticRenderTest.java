package ru.histone.staticrender;

import org.junit.Before;
import org.junit.Test;
import ru.histone.HistoneException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StaticRenderTest {
    private StaticRender staticRender;

    @Before
    public void beforeTest() throws HistoneException, IOException, URISyntaxException {
        staticRender = new StaticRender();
    }

    @Test
    public void test() throws IOException, URISyntaxException {
        Path srcDir = Paths.get("/Users/pit/Work/Histone/histone-static-render/src/test/resources/testsite/");
        Path dstDir = Paths.get("/Users/pit/Work/Histone/histone-static-render/target/", "site");
        if (!dstDir.toFile().exists()) {
            Files.createDirectory(dstDir);
        }

        staticRender.renderSite(srcDir, dstDir);
    }
}
