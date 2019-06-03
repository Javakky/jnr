import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;
import java.util.spi.ToolProvider;

import static java.util.ServiceLoader.*;

class Test {
    public static void main(String[] args) {
        ServiceLoader<ToolProvider> providers = load(ToolProvider.class);
        ToolProvider provider = null;
        for (Iterator<ToolProvider> it = providers.iterator(); it.hasNext(); ) {
            ToolProvider tool = it.next();
            if (tool.name().equals("jextract"))provider = tool;
        }
        String sourcePath = "./src/test/resources/test";
        runJextract(new File(Objects.requireNonNull(sourcePath)), provider, true);

    }


    private static void runJextract(File sourceDir, ToolProvider provider, boolean isFirst, String... route) {
        List<String> headers = new ArrayList<String>();
        if (sourceDir.isFile()){
            headers.add(sourceDir.getAbsolutePath());
        }
        else {
            if(!isFirst || false) {
                route = ArrayUtils.add(route, sourceDir.getName());
            }
            for (File file : Objects.requireNonNull(sourceDir.listFiles())) {
                if (file.isDirectory()) {
                    runJextract(file, provider, false, route);
                } else {
                    headers.add(file.getAbsolutePath());
                }
            }
        }
        StringBuilder packagePath = new StringBuilder();
        packagePath.append("pkg");
        for (int i = 0; i < route.length; i++) {
            packagePath.append(".").append(route[i]);
        }
        List<String> commandlnArgs = new ArrayList<>();
        //if (project.jextract.OutDir != null && !StringUtils.isEmpty(project.jextract.OutDir)) {
            commandlnArgs.add("-o");
            String outPath;
            if (!StringUtils.isEmpty(packagePath.toString())) {
                outPath = packagePath.toString();
            }else{
                outPath = new File(headers.get(0)).getName();
            }
            outPath = "./libs/" + outPath + ".jar";
            File libDir = new File("./libs");
            if(!libDir.exists()){
                libDir.mkdir();
            }
            File outJar = new File(outPath);
            if(!outJar.exists()) {
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
        provider.run(System.out, System.err, (String[])commandlnArgs.toArray(new String[commandlnArgs.size()]));
    }
}
