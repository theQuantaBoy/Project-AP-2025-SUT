package ap.project.network.shared.DTO;

import ap.project.model.enums.GameObjectType;
import ap.project.model.game.GameObject;

public class GameObjectDTO
{
    public boolean initialized =  false;

    public GameObjectType objectType;
    public int number = 1;
    public int price = 0;

    public GameObjectDTO() {}

    public GameObjectDTO(GameObject gameObject)
    {
        if (gameObject != null)
        {
            this.objectType = gameObject.getObjectType();
            this.number = gameObject.getNumber();
            this.price = gameObject.getPrice();

            this.initialized = true;
        }
    }
}
