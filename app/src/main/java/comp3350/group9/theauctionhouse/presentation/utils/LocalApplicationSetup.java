package comp3350.group9.theauctionhouse.presentation.utils;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import comp3350.group9.theauctionhouse.exception.android.ConfigsAccessException;
import comp3350.group9.theauctionhouse.persistence.HSQLDBConfig;
import comp3350.group9.theauctionhouse.persistence.executor.HSQLQueryExecutor;

public class LocalApplicationSetup {
    public static void copyDatabaseToDevice(Context context, AssetManager assetManager, boolean testing) throws IOException, ConfigsAccessException {
        final String DB_PATH = "db";

        String[] assetNames;
        File dataDirectory = !testing ? context.getDir(HSQLDBConfig.USER_DB_DIR, MODE_PRIVATE)
                : context.getDir(HSQLDBConfig.USER_TEST_DB_DIR, MODE_PRIVATE);

        assetNames = assetManager.list(DB_PATH);
        for (int i = 0; i < assetNames.length; i++) {
            assetNames[i] = DB_PATH + "/" + assetNames[i];
        }

        SharedPreferences prefs = context.getSharedPreferences("application_configs", MODE_PRIVATE);
        String currAppVersion = getAppVersion(assetManager);
        String deviceAppVersion = prefs.getString("app_version", null);

        boolean isNewAppVersion = !currAppVersion.equals(deviceAppVersion);
        boolean newSetup = copyAssetsToDirectory(assetNames, dataDirectory, context, isNewAppVersion);

        HSQLDBConfig.setPath(dataDirectory.toString() + "/" + HSQLDBConfig.getDb_name());

        if (newSetup) seed(assetNames, context);

        if (isNewAppVersion)
            prefs.edit().putString("app_version", currAppVersion).apply();
    }

    private static boolean copyAssetsToDirectory(String[] assets, File directory, Context context, boolean overwrite) throws IOException {
        AssetManager assetManager = context.getAssets();
        boolean copied = false;
        for (String asset : assets) {
            String[] components = asset.split("/");
            String copyPath = directory.toString() + "/" + components[components.length - 1];

            char[] buffer = new char[1024];
            int count;

            if (copyPath.endsWith(".sql")) continue;

            File outFile = new File(copyPath);

            if (!outFile.exists() || overwrite) {
                copied = true;
                InputStreamReader in = new InputStreamReader(assetManager.open(asset));
                FileWriter out = new FileWriter(outFile);

                count = in.read(buffer);
                while (count != -1) {
                    out.write(buffer, 0, count);
                    count = in.read(buffer);
                }

                out.close();
                in.close();
            }
        }

        return copied;
    }

    public static void destroyTestDB(Context context) {
        File dataDirectory = context.getDir(HSQLDBConfig.USER_TEST_DB_DIR, MODE_PRIVATE);
        deleteDirectory(dataDirectory);
    }

    private static String getAppVersion(AssetManager assetManager) throws IOException, ConfigsAccessException {
        try {
            InputStream is = assetManager.open("application/configs.json");
            byte[] buffer = new byte[is.available()];
            if (is.read(buffer) > 0) {
                is.close();
                JSONObject obj = new JSONObject(new String(buffer, StandardCharsets.UTF_8));
                return obj.getString("app_version");
            } else {
                return null;
            }
        } catch (JSONException e) {
            throw new ConfigsAccessException(e);
        }
    }

    private static void deleteDirectory(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else if (f.exists()) {
                    f.delete();
                }
            }
        }
        directory.delete();
    }

    static void seed(String[] assets, Context context) throws IOException {
        try {
            AssetManager assetManager = context.getAssets();
            for (String asset : assets) {

                if (!asset.endsWith(".sql")) continue;

                InputStreamReader in = new InputStreamReader(assetManager.open(asset));
                BufferedReader br = new BufferedReader(in);

                Connection conn = HSQLQueryExecutor.getConnection(HSQLDBConfig.getPath());
                Statement stmt = conn.createStatement();

                for (String query = br.readLine(); query != null; query = br.readLine()) {
                    query = query.trim();
                    if (query.length() > 0 && !query.startsWith("--"))
                        stmt.addBatch(query);
                }

                stmt.executeBatch();
                stmt.close();
                conn.close();
            }

        }
        catch (IOException e)
        {
            throw new IOException(e);
        }
        catch (SQLException e)
        {
            Log.e(LocalApplicationSetup.class.getName(),"SQLException while seeding database: "+ e.getMessage());
            throw new IOException(e.getCause());
        }
    }
}