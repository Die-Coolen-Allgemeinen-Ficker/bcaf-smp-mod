package bcaf.bcafsmpmod;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

import bcaf.bcafsmpmod.database.codec.BcafAccount;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.network.ServerPlayerEntity;

public class ChatLink {
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private static void makeRequest (String body) {
        HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create(Bcafsmpmod.config.chatLinkWebhookUrl))
        .header("Content-Type", "application/json")
        .POST(HttpRequest.BodyPublishers.ofString(body))
        .build();

        try {
            httpClient.send(request, BodyHandlers.discarding());
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
        BcafAccount account = BcafAccount.fetchByUuid(player.getUuid());
        String postBody;
        if (account == null) {
            postBody = "{\"content\":\"" + message.getContent().getLiteralString() + "\",\"username\":\"" + player.getNameForScoreboard() + "\"}";
        } else {
            postBody = "{\"content\":\"" + message.getContent().getLiteralString() + "\",\"username\":\"" + account.name + " (" + player.getNameForScoreboard() + ")" + "\",\"avatar_url\":\"" + account.avatarUrl + "\"}";
        }
        makeRequest(postBody);
    }
}