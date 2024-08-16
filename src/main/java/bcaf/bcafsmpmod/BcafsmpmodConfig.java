package bcaf.bcafsmpmod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class BcafsmpmodConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // Config values
    public String serverId = UUID.randomUUID().toString();
    public String mongodbConnectionString = "";
    public String chatLinkWebhookUrl = "";
    public String[] whitelistUuids = {};

    public static BcafsmpmodConfig load () {
        try {
            BcafsmpmodConfig config;
            File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "bcaf.json");

            if (configFile.exists()) {
                String json = IOUtils.toString(new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8));
                config = GSON.fromJson(json, BcafsmpmodConfig.class);
            } else
                config = new BcafsmpmodConfig();

            saveConfig(config);
            return config;
        } catch (IOException exception) {
            Bcafsmpmod.LOGGER.error("Couldn't load config from \"bcaf.json\".");
            exception.printStackTrace();
            return new BcafsmpmodConfig();
        }
    }

    public static void saveConfig(BcafsmpmodConfig config) {
        File configFile = new File(FabricLoader.getInstance().getConfigDir().toFile(), "bcaf.json");
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), StandardCharsets.UTF_8));
            writer.write(GSON.toJson(config));
            writer.close();
        } catch (Exception exception) {
            Bcafsmpmod.LOGGER.error("Couldn't save config to \"bcaf.json\".");
            exception.printStackTrace();
        }
    }
}