package bcaf.bcafsmpmod;

import java.util.ArrayList;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.reactivestreams.client.MongoCollection;

import bcaf.bcafsmpmod.database.codec.BcafAccount;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;

public class ChatLink {
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private static void makeRequest (String body) {
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(Bcafsmpmod.CONFIG.chatLinkWebhookUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();

        try {
            HTTP_CLIENT.sendAsync(request, BodyHandlers.discarding());
        } catch (Exception exception) {
            Bcafsmpmod.LOGGER.error("Failed to send POST request.");
            exception.printStackTrace();
        }
    }

    public static void sendMessage (String content) {
        String postBody = "{\"content\":\"" + content + "\",\"username\":\"🅱CAF SMP\",\"avatar_url\":\"https://cdn.discordapp.com/avatars/1098658997684412476/79a3f39d9ccb4800eeaca6ef4f72f838\"}";
        makeRequest(postBody);
    }

    public static void forwardPlayerMessage (SignedMessage message, ServerPlayerEntity player, MessageType.Parameters messageTypeParams) {
        MongoCollection<BcafAccount> accounts = Bcafsmpmod.BCAFDB.getCollection("accounts", BcafAccount.class).withCodecRegistry(Bcafsmpmod.CODEC_REGISTRY);
        MongoSubscriber<BcafAccount> subscriber = new MongoSubscriber<BcafAccount>();
        subscriber.setCompleteCallback((ArrayList<BcafAccount> a) -> {
            BcafAccount account = null;
            if (!a.isEmpty())
                account = a.get(0);
            String postBody;
            if (account == null) {
                postBody = "{\"content\":\"" + message.getContent().getLiteralString() + "\",\"username\":\"" + player.getNameForScoreboard() + "\"}";
            } else {
                postBody = "{\"content\":\"" + message.getContent().getLiteralString() + "\",\"username\":\"" + account.name + " (" + player.getNameForScoreboard() + ")" + "\",\"avatar_url\":\"" + account.avatarUrl + "\"}";
            }
            makeRequest(postBody);
        });
        accounts.find(eq("profile.minecraftUuid", player.getUuid().toString())).subscribe(subscriber);
    }
}