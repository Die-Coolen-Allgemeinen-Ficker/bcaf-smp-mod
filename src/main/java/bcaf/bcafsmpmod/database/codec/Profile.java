package bcaf.bcafsmpmod.database.codec;

import java.util.List;

public class Profile {
    public float level;
    public String color;
    public String backgroundImageUrl;
    public String foregroundImageUrl;
    public String minecraftUuid;
    public SocialCredit socialCredit;
    public Games games;
    public MessageStats messageStats;
    public List<Achievement> achievements;
    public Profile () {}
}