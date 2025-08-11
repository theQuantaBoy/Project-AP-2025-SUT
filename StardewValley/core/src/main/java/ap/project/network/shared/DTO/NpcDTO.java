package ap.project.network.shared.DTO;

import ap.project.model.enums.NpcDetails;
import ap.project.model.game.NPC;
import ap.project.model.game.Point;

import java.util.ArrayList;

public class NpcDTO
{
    public Point location;
    public NpcDetails npcDetails;

    public boolean firstQuestDone;
    public boolean secondQuestDone;
    public boolean thirdQuestDone;

    public String firstQuestUser;
    public String secondQuestUser;
    public String thirdQuestUser;

    public NpcDTO() {}

    public NpcDTO(NPC npc)
    {
        this.location = npc.getLocation();
        this.npcDetails = npc.getNpcDetails();

        this.firstQuestDone = !npc.isFirstQuestAvailable();
        this.secondQuestDone = !npc.isSecondQuestAvailable();
        this.thirdQuestDone = !npc.isThirdQuestAvailable();

        this.firstQuestUser = npc.getFirstQuestUser();
        this.secondQuestUser = npc.getSecondQuestUser();
        this.thirdQuestUser = npc.getThirdQuestUser();
    }
}
