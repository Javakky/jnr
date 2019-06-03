package com.github.javakky.jnr;

public class JextractConfigure {
    private String sourceRoot = "src/main/resources/";
    private String outPath = "libs";
    private String packageRoot = "";
    private boolean includeRoot = false;

    public String getSourceRoot() {
        return sourceRoot;
    }

    public String getOutPath() {
        return outPath;
    }

    public String getPackageRoot() {
        return packageRoot;
    }

    public boolean isIncludeRoot() {
        return includeRoot;
    }

    void setSourceRoot(String sourceRoot) {
        this.sourceRoot = sourceRoot;
    }

    void setOutPath(String outPath) {
        this.outPath = outPath;
    }
}