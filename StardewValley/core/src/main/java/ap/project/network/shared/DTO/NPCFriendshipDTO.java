package ap.project.network.shared.DTO;

import ap.project.model.player_data.FriendshipWithNpcData;

public class NPCFriendshipDTO
{
    public NpcDTO npcDTO;

    public int xp;

    public boolean hasTalked;
    public boolean hasGifted;

    public NPCFriendshipDTO() {}

    public NPCFriendshipDTO(FriendshipWithNpcData friendship)
    {
        this.npcDTO = new NpcDTO(friendship.getNpc());

        this.xp = friendship.getXp();

        this.hasTalked = friendship.isHasTalked();
        this.hasGifted = friendship.isHasGifted();
    }
}
