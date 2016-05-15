/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.npatarino.apkmethodsanalyzer;

import com.google.gson.Gson;
import es.npatarino.apkmethodsanalyzer.config.Config;
import es.npatarino.apkmethodsanalyzer.dex.DexCount;
import es.npatarino.apkmethodsanalyzer.dex.DexDataException;
import es.npatarino.apkmethodsanalyzer.dex.DexMethodCounts;
import es.npatarino.apkmethodsanalyzer.files.CollectFileNames;
import es.npatarino.apkmethodsanalyzer.output.GenerateHtmlOutput;

import java.io.*;

public class Main {

    private String packageFilter;
    private int maxDepth = Integer.MAX_VALUE;
    private DexMethodCounts.Filter filter = DexMethodCounts.Filter.ALL;
    private int minPercentageToPrint = 1;

    public static void main(String[] args) {
        Main main = new Main();
        main.run(new String[]{"idealista-production-release-7.0.3.apk"});
    }

    void run(String[] args) {
        try {
            Config config = new Config();
            File configFile = new File("config/config.json");
            if (configFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(configFile));
                config = new Gson().fromJson(reader, Config.class);
            }
            String[] inputFileNames = new String[]{"idealista-production-release-7.0.3.apk"};
            int overallCount = 0;
            CollectFileNames collectFileNames = new CollectFileNames(inputFileNames);
            for (String fileName : collectFileNames.invoke()) {
                DexCount dexCount = new ProcessFile(fileName, packageFilter, maxDepth, filter, config).invoke();
                overallCount += dexCount.getOverallCount();
                new GenerateHtmlOutput(dexCount, minPercentageToPrint).generate();
            }
            System.out.println(String.format("Overall %s count: %d", "method", overallCount));
        } catch (UsageException ue) {
            usage();
            System.exit(2);
        } catch (IOException ioe) {
            if (ioe.getMessage() != null) {
                System.err.println("Failed: " + ioe);
            }
            System.exit(1);
        } catch (DexDataException dde) {
            /* a message was already reported, just bail quietly */
            System.exit(1);
        }
    }


    private void usage() {
        System.err.print(
                "DEX per-package/class method counts v1.5\n" +
                        "Usage: apk-methods-analyzer [options] <file.{dex,apk,jar,directory}> ...\n" +
                        "Options:\n" +
                        "  --include-classes\n" +
                        "  --package-filter=com.foo.bar\n" +
                        "  --filter=ALL|DEFINED_ONLY|REFERENCED_ONLY\n"
        );
    }

    private static class UsageException extends RuntimeException {
    }

}
