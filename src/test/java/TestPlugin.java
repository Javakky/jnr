import com.github.javakky.jnr.JextractConfigure;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.spi.ToolProvider;

import static java.util.ServiceLoader.*;

public class TestPlugin {

    public static void main(String[] args) {
        JextractConfigure conf = new JextractConfigure();
        jextract(conf);
    }

    private static void jextract(JextractConfigure conf) {
        System.out.println(System.getProperty("java.version"));
        ServiceLoader<ToolProvider> providers = load(ToolProvider.class);
        ToolProvider provider = null;
        for (ToolProvider tool : providers) {
            System.out.println(tool.name());
            if (tool.name().equals("jextract")) provider = tool;
        }
        if(provider == null) System.out.println("エラー: jextractが存在しません。");
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
            if(headers.size() <= 0) return;
        }
        StringBuilder packagePath = new StringBuilder();
        packagePath.append(conf.getPackageRoot());
        for (int i = 0; i < route.length; i++) {
            packagePath.append(".").append(route[i]);
        }
        List<String> commandlnArgs = new ArrayList<>();
        //if (project.jextract.OutDir != null && !StringUtils.isEmpty(project.jextract.OutDir)) {
        commandlnArgs.add("-o");
        String outPath;
        if (!StringUtils.isEmpty(packagePath.toString())) {
            outPath = packagePath.toString();
        } else {
            outPath = new File(headers.get(0)).getName();
        }
        outPath = conf.getOutPath() + (conf.getOutPath().endsWith("/") ? "" : "/") + outPath + ".jar";
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
        }
        commandlnArgs.addAll(headers);
        provider.run(System.out, System.err, (String[]) commandlnArgs.toArray(new String[commandlnArgs.size()]));
    }
}
