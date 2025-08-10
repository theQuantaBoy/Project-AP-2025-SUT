package ap.project.network.shared.messages;

import ap.project.network.shared.DTO.BackPackDTO;
import ap.project.network.shared.enums.MessageType;

public class BackPackDTOMessage extends Message {
    public BackPackDTO backPackDTO;
    public int userID;

    public BackPackDTOMessage() {
    }

    public BackPackDTOMessage(int userID, BackPackDTO backPackDTO) {
        this.backPackDTO = backPackDTO;
        this.userID = userID;
    }

    @Override
    public MessageType getType() {
        return MessageType.BACKPACK_DTO;
    }
}
