package bcaf.bcafsmpmod.database.codec;

import static com.mongodb.client.model.Filters.ne;

import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.reactivestreams.client.MongoCollection;

import bcaf.bcafsmpmod.Bcafsmpmod;
import bcaf.bcafsmpmod.MongoSubscriber;
import net.fabricmc.loader.api.FabricLoader;

public class Smp {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public String id;

    private Smp () {

    }

    public static void configureWhitelist () {
        MongoCollection<BcafAccount> accounts = Bcafsmpmod.BCAFDB.getCollection("accounts", BcafAccount.class).withCodecRegistry(Bcafsmpmod.CODEC_REGISTRY);
        MongoSubscriber<BcafAccount> subscriber = new MongoSubscriber<BcafAccount>();
        subscriber.setCompleteCallback((ArrayList<BcafAccount> a) -> {
            String whitelist = "[";
            String ops = "[";
            ArrayList<String> uuids = new ArrayList<String>();

            for (BcafAccount account : a)
                uuids.add(account.profile.minecraftUuid);
            for (String uuid : Bcafsmpmod.CONFIG.whitelistUuids)
                uuids.add(uuid);
            for (String uuid : uuids) {
                HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid))
                .GET()
                .build();
                try {
                    HttpResponse<String> response = HTTP_CLIENT.send(request, BodyHandlers.ofString());
                    JsonObject body = JsonParser.parseString(response.body().toString()).getAsJsonObject();
                    String username = body.get("name").getAsString();
                    whitelist += "{\"uuid\":\"" + uuid + "\",\"name\":\"" + username + "\"},";
                    ops += "{\"uuid\":\"" + uuid + "\",\"name\":\"" + username + "\",\"level\":" + (uuid.equals("5ce339c6-bb60-4142-8a50-4aa5ef0ef256") ? "4" : "1") + ",\"bypassesPlayerLimit\":true},";
                    Bcafsmpmod.LOGGER.info("Configured whitelist and ops for " + username);
                } catch (Exception exception) {
                    Bcafsmpmod.LOGGER.error("Failed to request username for \"" + uuid + "\".");
                    exception.printStackTrace();
                }
            }

            whitelist = whitelist.substring(0, whitelist.length() - 1) + "]";
            ops = ops.substring(0, ops.length() - 1) + "]";

            File whitelistJson = new File(FabricLoader.getInstance().getGameDir().toFile(), "whitelist.json");
            File opsJson = new File(FabricLoader.getInstance().getGameDir().toFile(), "ops.json");
            try {
                BufferedWriter whitelistWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(whitelistJson), StandardCharsets.UTF_8));
                whitelistWriter.write(whitelist);
                whitelistWriter.close();
                BufferedWriter opsWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(opsJson), StandardCharsets.UTF_8));
                opsWriter.write(ops);
                opsWriter.close();
            } catch (Exception exception) {
                Bcafsmpmod.LOGGER.error("Couldn't save whitelist and ops.");
                exception.printStackTrace();
            }
        });
        accounts.find(ne("profile.minecraftUuid", null)).subscribe(subscriber);
    }
}