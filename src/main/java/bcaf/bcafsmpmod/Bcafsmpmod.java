package bcaf.bcafsmpmod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;

import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static com.mongodb.MongoClientSettings.getDefaultCodecRegistry;

import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import com.mongodb.reactivestreams.client.MongoDatabase;

import bcaf.bcafsmpmod.database.codec.Smp;

public class Bcafsmpmod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("bcafsmpmod");
	public static final BcafsmpmodConfig CONFIG = BcafsmpmodConfig.load();
	private static final MongoClient MONGO_CLIENT = MongoClients.create(CONFIG.mongodbConnectionString);
	public static final MongoDatabase BCAFDB = MONGO_CLIENT.getDatabase("bcaf-user-data");
	private static final CodecProvider CODEC_PROVIDER = PojoCodecProvider.builder().register("bcaf.bcafsmpmod.database.codec").build();
	public static final CodecRegistry CODEC_REGISTRY = fromRegistries(getDefaultCodecRegistry(), fromProviders(CODEC_PROVIDER));

	@Override
	public void onInitialize() {
		LOGGER.info("Haiiiii BCAF :333 i mean yo whats up gang");

		// Chat link
		ServerLifecycleEvents.SERVER_STARTED.register((MinecraftServer server) -> {
			ChatLink.sendMessage("Server ist online.");
		});
		ServerLifecycleEvents.SERVER_STOPPED.register((MinecraftServer server) -> {
			ChatLink.sendMessage("Server ist offline.");
		});
		ServerMessageEvents.CHAT_MESSAGE.register(ChatLink::forwardPlayerMessage);
		ServerMessageEvents.GAME_MESSAGE.register((MinecraftServer server, Text text, boolean bool) -> {
			ChatLink.sendMessage(text.getString());
		});

		// Server config
		Smp.configureWhitelist();
	}
}