package es.npatarino.apkmethodsanalyzer.config;


import java.util.ArrayList;
import java.util.List;

public class Config {

    private List<Depth> depths;

    public Config() {
        depths = new ArrayList<>();
    }

    public List<Depth> getDepths() {
        return depths;
    }
}
