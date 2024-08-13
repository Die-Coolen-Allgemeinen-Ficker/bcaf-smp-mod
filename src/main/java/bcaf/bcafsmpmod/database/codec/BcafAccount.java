package bcaf.bcafsmpmod.database.codec;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoCollection;

import bcaf.bcafsmpmod.Bcafsmpmod;

public class BcafAccount {
    public String userId;
    public String name;
    public String avatarUrl;
    public Profile profile;
    public int bcafCoin;
    public boolean hasBoostedBefore;
    public boolean hasPlayedLeagueOfLegends;
    public long bcafJoinTimestamp;
    public boolean legacy;
    public long createdTimestamp;
    public long updatedTimestamp;

    public static BcafAccount fetchByUuid (UUID uuid) {
        MongoCollection<BcafAccount> accounts = Bcafsmpmod.bcafDb.getCollection("accounts", BcafAccount.class).withCodecRegistry(Bcafsmpmod.codecRegistry);
        BcafAccount account = accounts.find(eq("profile.minecraftUuid", uuid.toString())).first();
        return account;
    }
}