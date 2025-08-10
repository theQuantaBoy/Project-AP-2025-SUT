package ap.project.network.shared.messages;

import ap.project.network.shared.DTO.SkillDTO;
import ap.project.network.shared.enums.MessageType;

public class ScoreBoardDataMessage extends Message
{
    public String gameId;
    public int userId;

    public double money;
    public int completedQuests;

    public SkillDTO farmingSkillDTO;
    public SkillDTO miningSkillDTO;
    public SkillDTO foragingSkillDTO;
    public SkillDTO fishingSkillDTO;

    public ScoreBoardDataMessage() {}

    public ScoreBoardDataMessage(String gameId, int userId, double money, int completedQuests,
                                 SkillDTO farmingSkillDTO, SkillDTO miningSkillDTO,
                                 SkillDTO foragingSkillDTO, SkillDTO fishingSkillDTO)
    {
        this.gameId = gameId;
        this.userId = userId;
        this.money = money;
        this.completedQuests = completedQuests;
        this.farmingSkillDTO = farmingSkillDTO;
        this.miningSkillDTO = miningSkillDTO;
        this.foragingSkillDTO = foragingSkillDTO;
        this.fishingSkillDTO = fishingSkillDTO;
    }

    @Override
    public MessageType getType()
    {
        return MessageType.SCORE_BOARD_DATA;
    }
}
