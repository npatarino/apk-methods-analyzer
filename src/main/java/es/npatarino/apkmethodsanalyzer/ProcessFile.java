package es.npatarino.apkmethodsanalyzer;

import es.npatarino.apkmethodsanalyzer.config.Config;
import es.npatarino.apkmethodsanalyzer.dex.DexCount;
import es.npatarino.apkmethodsanalyzer.dex.DexData;
import es.npatarino.apkmethodsanalyzer.dex.DexMethodCounts;
import es.npatarino.apkmethodsanalyzer.files.OpenInputFiles;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

public class ProcessFile {

    private final String packageFilter;
    private final int maxDepth;
    private final DexCount.Filter filter;
    private final Config config;
    private final String fileName;

    public ProcessFile(String fileName, String packageFilter, int maxDepth, DexMethodCounts.Filter filter,
                       Config config) {
        this.fileName = fileName;
        this.packageFilter = packageFilter;
        this.maxDepth = maxDepth;
        this.filter = filter;
        this.config = config;
    }

    public DexCount invoke() throws IOException {
        return processFile(fileName, config);
    }

    private DexCount processFile(String fileName, Config config) throws IOException {
        System.out.println("Processing " + fileName);
        DexCount counts = new DexMethodCounts(config);
        List<RandomAccessFile> dexFiles = new OpenInputFiles(fileName).invoke();
        loadDexFiles(counts, dexFiles);
        return counts;
    }

    private void loadDexFiles(DexCount counts, List<RandomAccessFile> dexFiles) throws IOException {
        for (RandomAccessFile dexFile : dexFiles) {
            DexData dexData = new DexData(dexFile);
            dexData.load();
            counts.generate(dexData, true, packageFilter, maxDepth, filter);
            dexFile.close();
        }
    }

}
