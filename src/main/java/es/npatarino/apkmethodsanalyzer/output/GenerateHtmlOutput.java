package es.npatarino.apkmethodsanalyzer.output;

import es.npatarino.apkmethodsanalyzer.dex.DexCount;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class GenerateHtmlOutput {

    private final DexCount counts;
    private final int minPercentageToPrint;

    public GenerateHtmlOutput(DexCount counts, int minPercentageToPrint) {
        this.minPercentageToPrint = minPercentageToPrint;
        this.counts = counts;
    }

    public void generate() throws IOException {
        File exportFolder = createExportFolder();
        copyAssetsFiles(exportFolder);
        generateIndexFile(exportFolder);
    }

    private void generateIndexFile(File exportFolder) throws IOException {
        File startFile = new File("assets/start.txt");
        BufferedReader startBufferedReader = new BufferedReader(new FileReader(startFile));
        File endFile = new File("assets/end.txt");
        BufferedReader endBufferedReader = new BufferedReader(new FileReader(endFile));

        File f = new File(exportFolder, "methods.htm");
        if (!f.exists()) {
            f.createNewFile();
        }
        BufferedWriter bw = new BufferedWriter(new FileWriter(f));

        String line;
        while ((line = startBufferedReader.readLine()) != null) {
            bw.write(line);
            bw.newLine();
        }

        String content = getGeneratedContent(counts.packageTree, "", counts.getOverallCount(), "");
        bw.write(content);
        bw.newLine();

        while ((line = endBufferedReader.readLine()) != null) {
            bw.write(line);
            bw.newLine();
        }

        startBufferedReader.close();
        bw.close();
    }

    private void copyAssetsFiles(File exportFolder) throws IOException {
        File d3File = new File("assets/d3.min.js");
        File d3PieFile = new File("assets/d3pie.min.js");
        File cssFolderToCopy = new File("assets/css");
        File fontsFolderToCopy = new File("assets/fonts");
        File fontFolderToCopy = new File("assets/font");
        File jsFolderToCopy = new File("assets/js");

        File copyD3 = new File(exportFolder, d3File.getName());
        File copyD3Pie = new File(exportFolder, d3PieFile.getName());
        File cssFolderCopy = new File(exportFolder, cssFolderToCopy.getName());
        File fontsFolderCopy = new File(exportFolder, fontsFolderToCopy.getName());
        File fontFolderCopy = new File(exportFolder, fontFolderToCopy.getName());
        File jsFolderCopy = new File(exportFolder, jsFolderToCopy.getName());

        copyFile(cssFolderToCopy, cssFolderCopy);
        copyFile(fontsFolderToCopy, fontsFolderCopy);
        copyFile(fontFolderToCopy, fontFolderCopy);
        copyFile(jsFolderToCopy, jsFolderCopy);
        copyFile(d3File, copyD3);
        copyFile(d3PieFile, copyD3Pie);
    }

    private File createExportFolder() {
        File exportFolder = new File("export/");
        exportFolder.mkdirs();
        return exportFolder;
    }

    private void copyFile(File fileToCopy, File newFile) throws IOException {
        if (fileToCopy == null || newFile == null || !fileToCopy.exists()) {
            throw new IOException("There is a problem with the source or target file");
        }
        if (fileToCopy.isDirectory()) {
            copyDirectory(fileToCopy, newFile);
        } else {
            copy(fileToCopy, newFile);
        }
    }

    private void copyDirectory(File fileToCopy, File newFile) throws IOException {
        if (!newFile.exists()) {
            copy(fileToCopy, newFile);
        }
        File[] files = fileToCopy.listFiles();
        if (files != null) {
            for (File file : files) {
                File fileCopy = new File(newFile, file.getName());
                copy(file, fileCopy);
            }
        }
    }

    private Path copy(File fileToCopy, File newFile) throws IOException {
        return Files.copy(fileToCopy.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    private String getGeneratedContent(DexCount.Node node, String parent, int overallCount, String fullText) {
        if (node.children.isEmpty()) {
            if ((float) (node.count * 100 / overallCount) >= (float) minPercentageToPrint) {
                if (!fullText.isEmpty()) {
                    fullText = fullText + ", \n";
                }
                Random rand = new Random();
                int r = rand.nextInt(255);
                int g = rand.nextInt(255);
                int b = rand.nextInt(255);
                return fullText + "{'label':'" + parent + "', 'value': " + node.count + ", 'color':'" + String.format("#%02x%02x%02x", r, g, b) + "'}";
            }
        } else {
            for (String packageName : node.children.keySet()) {
                String prefix = !parent.isEmpty() ? parent + "." : "";
                fullText = getGeneratedContent(node.children.get(packageName), prefix + packageName, overallCount, fullText);
            }
        }

        return fullText;
    }

}
