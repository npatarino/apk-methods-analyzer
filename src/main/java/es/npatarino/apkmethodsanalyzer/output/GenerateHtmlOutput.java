package es.npatarino.apkmethodsanalyzer.output;

import es.npatarino.apkmethodsanalyzer.dex.DexCount;

import java.io.*;
import java.nio.file.Files;
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
        File startFile = new File("assets/start.txt");
        BufferedReader startBufferedReader = new BufferedReader(new FileReader(startFile));

        File endFile = new File("assets/end.txt");
        BufferedReader endBufferedReader = new BufferedReader(new FileReader(endFile));

        File folder = new File("export/");
        folder.mkdirs();

        File d3File = new File("assets/d3.min.js");
        if (!d3File.exists()) {
            // TODO: Return error
        }

        File d3PieFile = new File("assets/d3pie.min.js");
        if (!d3PieFile.exists()) {
            // TODO: Return error
        }

        File copyD3 = new File(folder, d3File.getName());
        File copyD3Pie = new File(folder, d3PieFile.getName());
        Files.copy(d3File.toPath(), copyD3.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(d3PieFile.toPath(), copyD3Pie.toPath(), StandardCopyOption.REPLACE_EXISTING);

        File f = new File(folder, "methods.htm");
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
