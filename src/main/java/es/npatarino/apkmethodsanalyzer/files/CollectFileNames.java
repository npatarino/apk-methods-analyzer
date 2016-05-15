package es.npatarino.apkmethodsanalyzer.files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CollectFileNames {

    private String[] inputFileNames;

    public CollectFileNames(String[] inputFileNames) {
        this.inputFileNames = inputFileNames;
    }

    public List<String> invoke() {
        return collectFileNames(inputFileNames);
    }

    /**
     * Checks if input files array contain directories and
     * adds it's contents to the file list if so.
     * Otherwise just adds a file to the list.
     *
     * @return a List of file names to process
     */
    public List<String> collectFileNames(String[] inputFileNames) {
        List<String> fileNames = new ArrayList<String>();
        for (String inputFileName : inputFileNames) {
            File file = new File(inputFileName);
            if (file.isDirectory()) {
                String dirPath = file.getAbsolutePath();
                for (String fileInDir : file.list()) {
                    fileNames.add(dirPath + File.separator + fileInDir);
                }
            } else {
                fileNames.add(inputFileName);
            }
        }
        return fileNames;
    }

}
