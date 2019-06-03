package com.github.javakky.jnr;

public class JextractConfigure {
    public String sourceRoot = "src/main/resources/";
    public String outPath = "libs";
    public String packageRoot = "";
    public boolean includeRoot = false;

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