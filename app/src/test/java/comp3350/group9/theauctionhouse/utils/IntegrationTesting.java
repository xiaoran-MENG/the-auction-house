package comp3350.group9.theauctionhouse.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import comp3350.group9.theauctionhouse.persistence.HSQLDBConfig;

public class IntegrationTesting {
    private IntegrationTesting(){}

    private static final String tempPath = "src/main/java/assets/testing/temp_DB.script";
    private static final String dbPath = "src/main/java/assets/db/AH_DB.script";

    public static void setupDB() throws IOException {
        byte[] buffer = new byte[8192];
        int count;

        String executionPath = System.getProperty("user.dir").replace("\\", "/");

        File inFile = new File(executionPath + "/" + dbPath);
        File outFile = new File(executionPath + "/" + tempPath);

        if (!outFile.exists()) {
            outFile.getParentFile().mkdir();
        }

        InputStream in = new FileInputStream(inFile);
        OutputStream out = new FileOutputStream(outFile);

        while ((count = in.read(buffer)) > 0) {
            out.write(buffer, 0, count);
        }

        out.close();
        in.close();

        String path = outFile.getAbsolutePath().replace(".script", "");
        HSQLDBConfig.setPath(path);
    }

    public static void tearDown() throws IOException {
        String executionPath = System.getProperty("user.dir").replace("\\", "/");

        File test_directory = new File(executionPath + "/" + "src/main/java/assets/testing");
        deleteDirectory(test_directory);
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if(files!=null) {
            for(File f: files) {
                if(f.isDirectory()) {
                    deleteDirectory(f);
                } else if (f.exists()){
                    f.delete();
                }
            }
        }
        directory.delete();
    }
}
