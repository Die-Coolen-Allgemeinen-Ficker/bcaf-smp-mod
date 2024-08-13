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

//import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class Bcafsmpmod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("bcafsmpmod");
	public static BcafsmpmodConfig config = BcafsmpmodConfig.load();
	private static MongoClient mongoClient = MongoClients.create(config.mongodbConnectionString);
	public static MongoDatabase bcafDb = mongoClient.getDatabase("bcaf-user-data");
	private static CodecProvider codecProvider = PojoCodecProvider.builder().register("bcaf.bcafsmpmod.database.codec").build();
	public static CodecRegistry codecRegistry = fromRegistries(getDefaultCodecRegistry(), fromProviders(codecProvider));

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
	}
}