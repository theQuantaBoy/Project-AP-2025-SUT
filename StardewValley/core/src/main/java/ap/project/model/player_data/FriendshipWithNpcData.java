package ap.project.model.player_data;

import ap.project.model.game.NPC;
import ap.project.model.game.Player;
import ap.project.view.GameMenu;

public class FriendshipWithNpcData
{
    private final NPC npc;

    private int xp = 0;

    private boolean hasTalked = false;
    private boolean hasGifted = false;

    public FriendshipWithNpcData(NPC npc)
    {
        this.npc = npc;
    }

    public int getXp()
    {
        return xp - (200 * getLevel());
    }

    public int getLevel()
    {
        return (xp / 200);
    }

    public void talk()
    {
        if (!hasTalked)
        {
            GameMenu.println("You talked to " + npc.getName() + " for the first time today.");
            GameMenu.println("You received 20 xp.");
            addXp(20);
            hasTalked = true;
        }
    }

    public void gift(boolean favorite)
    {
        if (!hasGifted)
        {
            GameMenu.println("Oh, you gifted " + npc.getName() + " for the first time today!");

            if (favorite)
            {
                GameMenu.println(npc.getName() + " loves your gift! You just received 200 xp!");
                addXp(200);
            } else
            {
                GameMenu.println("You received 50 xp.");
                addXp(50);
            }

            hasGifted = true;
        }
    }

    public void addXp(int amount)
    {
        this.xp += amount;
    }

    public void reset()
    {
        hasTalked = false;
        hasGifted = false;
    }

    public boolean isHasTalked()
    {
        return hasTalked;
    }

    public boolean isHasGifted()
    {
        return hasGifted;
    }

    public NPC getNpc()
    {
        return npc;
    }

    public void setXp(int xp)
    {
        this.xp = xp;
    }

    public void setHasTalked(boolean hasTalked)
    {
        this.hasTalked = hasTalked;
    }

    public void setHasGifted(boolean hasGifted)
    {
        this.hasGifted = hasGifted;
    }
}
