package com.github.javakky.jnr;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Plugin;
import org.gradle.api.Project;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.spi.ToolProvider;

import static java.util.ServiceLoader.*;

public class JnrPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {

        JextractConfigure jextractConfigure = project.getExtensions()
                .create("jextract", JextractConfigure.class);
        jextractConfigure.setSourceRoot(project.getRootDir() + "/" + jextractConfigure.getSourceRoot());
        jextractConfigure.setOutPath(project.getRootDir() + "/" + jextractConfigure.getOutPath());

        project.task("jextract")
                .doLast(task -> {
                    try {
                        jextract(jextractConfigure);
                    } catch (Throwable e) {
                        e.printStackTrace();
                        throw e;
                    }
                });

    }

    private static void compileClang() {
        Runtime runtime = Runtime.getRuntime();
        Process p = null;
        try {
            p = runtime.exec("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Scanner s = new Scanner(p.getInputStream());

        while (s.hasNext()) {
            System.out.println(s.nextLine());
        }

        s = new Scanner(p.getErrorStream());
        while (s.hasNext()) {
            System.out.println(s.nextLine());
        }

    }

    private static void jextract(JextractConfigure conf) {
        if (Double.valueOf(System.getProperty("java.specification.version")) < 13) {
            System.out.println("警告: Javaのバージョンが13以下です。");
        }
        ServiceLoader<ToolProvider> providers = load(ToolProvider.class, ClassLoader.getSystemClassLoader());
        ToolProvider provider = null;
        for (ToolProvider tool : providers) {
            System.out.println(tool.name());
            if (tool.name().equals("jextract")) provider = tool;
        }
        if (provider == null) {
            System.out.println("エラー: jextractが存在しません。");
            throw new RuntimeException();
        }
        String sourcePath = conf.getSourceRoot();
        runJextract(new File(Objects.requireNonNull(sourcePath)), provider, conf, true);
    }


    private static void runJextract(File sourceDir, ToolProvider provider, JextractConfigure conf, boolean isFirst, String... route) {
        List<String> headers = new ArrayList<String>();
        if (sourceDir.isFile()) {
            System.out.println("警告: sourceRootがディレクトリではなくファイルです。");
            headers.add(sourceDir.getAbsolutePath());
        } else {
            if (!isFirst || conf.isIncludeRoot()) {
                route = ArrayUtils.add(route, sourceDir.getName());
            }
            for (File file : Objects.requireNonNull(sourceDir.listFiles())) {
                if (file.isDirectory()) {
                    runJextract(file, provider, conf, false, route);
                } else if (file.getName().endsWith(".h")) {
                    headers.add(file.getAbsolutePath());
                }
            }
            if (headers.size() <= 0) {
                System.out.println("警告: ディレクトリに.hファイルが存在しません。　dir:" + sourceDir.getName());
                return;
            }
        }
        List<String> commandlnArgs = new ArrayList<>();
        StringBuilder packagePath = new StringBuilder();
        packagePath.append(conf.getPackageRoot());
        for (int i = 0; i < route.length; i++) {
            packagePath.append(".").append(route[i]);
        }
        commandlnArgs.add("-o");
        String outPath;
        if (!StringUtils.isEmpty(packagePath.toString())) {
            outPath = packagePath.toString();
        } else {
            outPath = new File(headers.get(0)).getName();
        }
        if (StringUtils.isEmpty(conf.getOutPath())) {
            System.out.println("警告: 出力先のディレクトリが設定されていません。");
            outPath = "./" + outPath + ".jar";
        } else {
            outPath = conf.getOutPath() + (conf.getOutPath().endsWith("/") ? "" : "/") + outPath + ".jar";
        }
        File libDir = new File(conf.getOutPath());
        if (!libDir.exists()) {
            libDir.mkdir();
        }
        File outJar = new File(outPath);
        if (!outJar.exists()) {
            try {
                outJar.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        commandlnArgs.add(outPath);

        //}
        if (!StringUtils.isEmpty(packagePath.toString())) {
            commandlnArgs.add("-t");
            commandlnArgs.add(packagePath.toString());
        }else{
            System.out.println("警告: ルート・パッケージ名が設定されていません。");
        }
        commandlnArgs.addAll(headers);
        provider.run(System.out, System.err, (String[]) commandlnArgs.toArray(new String[commandlnArgs.size()]));
    }

}
