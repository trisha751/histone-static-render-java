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
