package bcaf.bcafsmpmod.database.codec;

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
    public BcafAccount () {}
}