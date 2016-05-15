package es.npatarino.apkmethodsanalyzer.config;


import java.util.ArrayList;
import java.util.List;

public class Config {

    private List<Depth> depths;

    public Config() {
        depths = new ArrayList<>();
    }

    public int findMaxDepth(String packageName) {
        for (Depth depth : depths) {
            for (String currentPrefix : depth.getPackagePrefix()) {
                if (packageName.startsWith(currentPrefix)) {
                    return depth.getDepth();
                }
            }
        }
        return Integer.MAX_VALUE;
    }

}
