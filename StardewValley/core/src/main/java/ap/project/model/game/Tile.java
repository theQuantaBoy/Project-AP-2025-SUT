package ap.project.model.game;

import ap.project.model.App.App;
import ap.project.model.animal.Fish;
import ap.project.model.enums.GameObjectType;
import ap.project.model.enums.TileTexture;
import ap.project.model.enums.resources_enums.ResourceItem;
import ap.project.model.resources.*;

import java.util.ArrayList;

public class Tile
{
    private final Point point;

    private boolean hitByThunder = false;
    private TileTexture texture = null;

    private GameObject object = null;
    private boolean isPloughed = false;
    private boolean isFertilized = false;

    private int wateringChance = 0;
    private boolean growFaster = false;

    private boolean isImmuneFromCrows = false;
    private boolean isRandomForaging = false;

    private boolean isInCity = false;
    private Fish fish = null;

    private String typeName;

    public void setInCity()
    {
        isInCity = true;
    }

    public Tile(Point point, String typeName)
    {
        this.point = point;
        this.typeName = typeName;
    }

    public String getTypeName()
    {
        return typeName;
    }

    public Tile(TileTexture texture, Point point)
    {
        this.texture = texture;
        this.point = point;
    }

    public Tile(Point point)
    {
        this.point = point;
    }

    public int getY()
    {
        return point.getY();
    }

    public int getX()
    {
        return point.getX();
    }

    public boolean isHitByThunder()
    {
        return hitByThunder;
    }

    public void hitByThunder()
    {
        hitByThunder = true;
        Object object = this.object;

        unPlant();

        if (object != null)
        {
            if (object instanceof Tree || object instanceof ForagingTree)
            {
                this.object = new GameObject(GameObjectType.COAL, 3);
            }
            else if (object instanceof ForagingCrop || object instanceof ForagingSeed || object instanceof Plant)
            {
                this.object = null;
            }
        }
    }

    public void ploghInverse() {
        isPloughed = false;
    }

    public TileTexture getTexture()
    {
        return texture;
    }

    public void setTexture(TileTexture texture) {
        this.texture = texture;
    }

    public void setType(TileTexture texture)
    {
        this.texture = texture;
    }

    public GameObject getObject()
    {
        return object;
    }

    public void setObject(GameObject object)
    {
        this.object = object;
        if (object == null)
        {
            isRandomForaging = false;
        }
    }

    public Point getPoint()
    {
        return point;
    }

    public void plough()
    {
        isPloughed = true;
        Map map = App.getCurrentGame().getCurrentPlayer().getCurrentMap();

        if (map instanceof Farm)
        {
            Farm farm = (Farm)map;
            farm.addPlantingTile(this);
        }
    }

    public boolean isPloughed()
    {
        return isPloughed;
    }

    public boolean isFertilized()
    {
        return isFertilized;
    }

    public void fertilize()
    {
        isFertilized = true;
    }

    public boolean hasPlants()
    {
        if (object != null)
        {
            if (object instanceof Tree || object instanceof Crop)
            {
                return true;
            }
        }

        return false;
    }

    public void unPlant()
    {
        if (object != null && object instanceof GiantCrop)
        {
            GiantCrop crop = (GiantCrop) object;
            ArrayList<Tile> tiles = GiantCrop.get2x2Tiles(crop.getRootTile());
            for (Tile t : tiles)
            {
                t.setObject(null);
                t.ploghInverse();
                t.unFertilize();
                t.resetGrowFaster();
                t.resetWateringChance();
            }
        }

        setObject(null);
        ploghInverse();
        unFertilize();
        resetGrowFaster();
        resetWateringChance();

    }

    public void unFertilize()
    {
        isFertilized = false;
    }

//    public String getAppearance()
//    {
//        // TODO: uncomment this later
//        if (App.getCurrentGame().getCurrentPlayer().getLocation().equals(point))
//        {
//            return App.getCurrentGame().getCurrentPlayer().getApperance();
//        }
//
//        if (App.getCurrentGame().getCurrentPlayer().isInCity())
//        {
//            Point[] points = App.getCurrentGame().getCity().getPlayerPoints();
//            ArrayList<Player> players = App.getCurrentGame().getPlayers();
//
//            for (int i = 0; i < points.length; i++)
//            {
//                Point p = points[i];
//                if (p != null && p.getX() == point.getX() && p.getY() == point.getY())
//                {
//                    return players.get(i).getApperance();
//                }
//            }
//        }
//
//        if (object == null)
//        {
//            if (hitByThunder)
//            {
//                return "⬛";
//            } else if (texture.equals(TileTexture.LAND))
//            {
//                if (isPloughed)
//                {
//                    return "\uD83D\uDFEB";
//                } else
//                {
//                    return "\uD83D\uDFE8";
//                }
//            } else if (texture.equals(TileTexture.LAKE))
//            {
////                if (Math.random() < 0.5)
////                {
////                    return "\uD83C\uDF0A"; // wave emoji
////                } else
////                {
////                    return "\uD83D\uDFE6";
////                }
//                return "\uD83D\uDFE6";
//            } else if (texture.equals(TileTexture.GRASS))
//            {
//                return "\uD83D\uDFE9";
//            } else if (texture.equals(TileTexture.CABIN))
//            {
////                return "\uD83C\uDFE0"; // maybe (?)
//                return "⬜";
//            } else if (texture.equals(TileTexture.GREEN_HOUSE))
//            {
////                return "\uD83E\uDE9F";
//                return "⬜";
//            } else if (texture.equals(TileTexture.QUARRY))
//            {
////                return "\uD83E\uDE76";
//                return "❤\uFE0F";
//            } else if (texture.equals(TileTexture.CABIN_WALL) || texture.equals(TileTexture.GREEN_HOUSE_WALL) ||
//            texture.equals(TileTexture.WALL))
//            {
//                return "\uD83D\uDFEB";
//            } else if (texture.equals(TileTexture.CABIN_INTERIOR_FLOOR) || texture.equals(TileTexture.FLOOR))
//            {
//                return "\uD83D\uDFE9";
//            } else if (texture.equals(TileTexture.GREEN_HOUSE_FLOOR))
//            {
//                return "\uD83D\uDFE9";
//            } else if (texture.equals(TileTexture.GREEN_HOUSE_WOOD))
//            {
//                return "\uD83D\uDFE7";
//            } else if (texture.equals(TileTexture.BED_TILE))
//            {
//                return "\uD83D\uDFE6";
//            } else if (texture.equals(TileTexture.DECOR_TILE))
//            {
//                return "\uD83D\uDFEA";
//            } else if (texture.equals(TileTexture.VILLAGE_GRASS))
//            {
//                return "\uD83D\uDFE9";
//            } else if (texture.equals(TileTexture.ROAD))
//            {
//                return "\uD83D\uDD33";
//            } else if (texture.equals(TileTexture.FENCE))
//            {
//                return "⬜";
//            } else if (texture.equals(TileTexture.BUILDING))
//            {
//                return "\uD83E\uDE9F";
//            } else if (texture.equals(TileTexture.SHOP_DOOR))
//            {
//                return "\uD83D\uDEAA";
//            } else if (texture.equals(TileTexture.CITY_BOARD))
//            {
//                return "\uD83D\uDFE5";
//            } else if (texture.equals(TileTexture.GARDEN))
//            {
//                return "\uD83D\uDFE9";
//            } else if (texture.equals(TileTexture.TREE))
//            {
////                return "\uD83C\uDF34";
//                return "❤\uFE0F";
//            } else if (texture.equals(TileTexture.BOOK))
//            {
////                return "\uD83D\uDCDA";
//                return "\uD83D\uDFEA";
//            } else if (texture.equals(TileTexture.LAMP))
//            {
////                return "\uD83D\uDCA1";
//                return "⬜";
//            } else if (texture.equals(TileTexture.TABLE))
//            {
//                return "\uD83E\uDEB4";
//            }else if (texture.equals(TileTexture.COMPUTER))
//            {
//                return "\uD83D\uDCBB";
//            }else if (texture.equals(TileTexture.SHOP_BLACKSMITH) ||
//                    texture.equals(TileTexture.SHOP_JOJAMART) ||
//                    texture.equals(TileTexture.SHOP_SALOON) ||
//                    texture.equals(TileTexture.SHOP_MARNIE) ||
//                    texture.equals(TileTexture.SHOP_FISH) ||
//                    texture.equals(TileTexture.SHOP_PIERRE) ||
//                    texture.equals(TileTexture.SHOP_CARPENTER))
//            {
//                return "\uD83D\uDFE7";
//            }else if (texture.equals(TileTexture.NPC_BLACKSMITH) ||
//                    texture.equals(TileTexture.NPC_JOJAMART) ||
//                    texture.equals(TileTexture.NPC_SALOON) ||
//                    texture.equals(TileTexture.NPC_MARNIE) ||
//                    texture.equals(TileTexture.NPC_FISH) ||
//                    texture.equals(TileTexture.NPC_PIERRE) ||
//                    texture.equals(TileTexture.NPC_CARPENTER))
//            {
//                return "\uD83E\uDD13";
//            }
//            else
//            {
//                return "\uD83D\uDFE5"; // ERROR
//            }
//        } else
//        {
//            if (object instanceof Tree)
//            {
////                if (Math.random() < 0.5)
////                {
////                    return "\uD83C\uDF32"; // tree emoji type 1
////                } else
////                {
////                    return "\uD83C\uDF33"; // tree emoji type 2
////                }
//                return "\uD83D\uDFEA";
//            } else if (object instanceof Crop)
//            {
////                return "\uD83C\uDF31"; // seed emoji
//                return "\uD83D\uDFE5";
//            } else if (object instanceof ForagingCrop || object instanceof ForagingSeed || object instanceof ForagingTree)
//            {
////                return "\uD83C\uDF32";
//                return "⬛";
//            } else if (object instanceof Resource)
//            {
//                Resource r = (Resource) object;
//                if (r.getResourceType().equals(ResourceItem.STONE))
//                {
//                    return "\uD83D\uDFE7";
//                } else if (r.getResourceType().equals(ResourceItem.WOOD))
//                {
////                    return "\uD83E\uDEB5";
//                    return "\uD83D\uDFE7";
//                }
//            } else if (object instanceof ForagingMineral)
//            {
//                return "\uD83D\uDFE7";
//            } else
//            {
//                return "\uD83D\uDFE5"; // ERROR
//            }
//        }
//
//        return "\uD83D\uDFE5"; // ERROR
//    }

    public String getAppearance()
    {
        // TODO: uncomment this later

        Player player = App.getCurrentGame().getCurrentPlayer();

        Point playerLocation = player.getLocation();
        Point zeidyLocation = null;

        if (player.getZeidy() != null && player.getZeidy().isInZeidiesFarm())
        {
            zeidyLocation = player.getZeidy().getLocation();
        }

        if (playerLocation.equals(point) && zeidyLocation != null && zeidyLocation.equals(point))
        {
            return "❤\uFE0F"; // ❤️
        }

        if (playerLocation.equals(point))
        {
            return App.getCurrentGame().getCurrentPlayer().getApperance();
        }

        if (zeidyLocation != null && zeidyLocation.equals(point))
        {
            return App.getCurrentGame().getCurrentPlayer().getZeidy().getApperance();
        }

        if (App.getCurrentGame().getCurrentPlayer().isInCity())
        {
            Point[] points = App.getCurrentGame().getCity().getPlayerPoints();
            ArrayList<Player> players = App.getCurrentGame().getPlayers();

            for (int i = 0; i < points.length; i++)
            {
                Point p = points[i];
                if (p != null && p.getX() == point.getX() && p.getY() == point.getY())
                {
                    return players.get(i).getApperance();
                }
            }
        }

        if (isInCity && hasNpc())
        {
            return getNpc().getAppearance();
        }

        if (hitByThunder)
        {
            return "⬛";
        }

        if (fish != null)
        {
            if (fish.getType().isLegendary())
            {
                return "\uD83D\uDC21"; // 🐡
            } else
            {
                return "\uD83D\uDC20"; // 🐠
            }
        }

        if (object == null)
        {
            if (texture.equals(TileTexture.LAND))
            {
                if (isPloughed)
                {
                    return "\uD83D\uDFEB"; // 🟫
                } else
                {
                    return "\uD83D\uDFE8"; // 🟨
                }
            } else if (texture.equals(TileTexture.LAKE))
            {
                    if (Math.random() < 0.5)
                    {
                        return "\uD83C\uDF0A"; // 🌊
                    } else
                    {
                        return "\uD83D\uDFE6"; // 🟦
                    }
//                return "\uD83D\uDFE6";
            } else if (texture.equals(TileTexture.GRASS))
            {
                if (isPloughed)
                {
                    return "\uD83D\uDFEB"; // 🟫
                } else
                {
                    return "\uD83D\uDFE9"; // 🟩
                }
            } else if (texture.equals(TileTexture.CABIN))
            {
                    return "\uD83C\uDFE0"; // 🏠
//                return "⬜";
            } else if (texture.equals(TileTexture.GREEN_HOUSE))
            {
                    return "\uD83E\uDE9F"; // 🪟
//                return "⬜";
            } else if (texture.equals(TileTexture.QUARRY))
            {
                    return "\uD83E\uDE76"; // 🩶
//                return "❤\uFE0F";
            } else if (texture.equals(TileTexture.CABIN_WALL) || texture.equals(TileTexture.GREEN_HOUSE_WALL) ||
            texture.equals(TileTexture.WALL))
            {
//                return "\uD83D\uDFEB";
                return "\uD83E\uDDF1"; // 🧱
            } else if (texture.equals(TileTexture.CABIN_INTERIOR_FLOOR) || texture.equals(TileTexture.FLOOR))
            {
                return "\uD83D\uDFE9"; // 🟩
            } else if (texture.equals(TileTexture.GREEN_HOUSE_FLOOR))
            {
                return "\uD83D\uDFE9"; // 🟩
            } else if (texture.equals(TileTexture.GREEN_HOUSE_WOOD))
            {
                return "\uD83D\uDFE7"; // 🟧
            } else if (texture.equals(TileTexture.BED_TILE))
            {
                return "\uD83D\uDFE6"; // 🟦
            } else if (texture.equals(TileTexture.DECOR_TILE))
            {
                return "\uD83D\uDFEA"; // 🟪
            } else if (texture.equals(TileTexture.VILLAGE_GRASS))
            {
                return "\uD83D\uDFE9"; // 🟩
            } else if (texture.equals(TileTexture.ROAD))
            {
                return "\uD83D\uDD33"; // 🔳
            } else if (texture.equals(TileTexture.FENCE))
            {
                return "⬜"; // ⬜
            } else if (texture.equals(TileTexture.BUILDING))
            {
                return "\uD83E\uDE9F"; // 🪟
            } else if (texture.equals(TileTexture.SHOP_DOOR))
            {
                return "\uD83D\uDEAA"; // 🚪
            } else if (texture.equals(TileTexture.FLOWER))
            {
                return "\uD83C\uDF39"; // 🌹
            } else if (texture.equals(TileTexture.CITY_BOARD))
            {
                return "\uD83D\uDFE5"; // 🟥
            } else if (texture.equals(TileTexture.GARDEN))
            {
                return "\uD83D\uDFE9"; // 🟩
            } else if (texture.equals(TileTexture.TREE))
            {
                    return "\uD83C\uDF34"; // 🌴
//                return "❤\uFE0F";
            } else if (texture.equals(TileTexture.BOOK))
            {
                    return "\uD83D\uDCDA"; // 📚
//                return "\uD83D\uDFEA";
            } else if (texture.equals(TileTexture.LAMP))
            {
                    return "\uD83D\uDCA1"; // 💡
//                return "⬜";
            } else if (texture.equals(TileTexture.TABLE))
            {
                return "\uD83E\uDEB4"; // 🪴
            }else if (texture.equals(TileTexture.COMPUTER))
            {
                return "\uD83D\uDCBB"; // 💻
            }else if (texture.equals(TileTexture.SHOP_BLACKSMITH) ||
                    texture.equals(TileTexture.SHOP_JOJAMART) ||
                    texture.equals(TileTexture.SHOP_SALOON) ||
                    texture.equals(TileTexture.SHOP_MARNIE) ||
                    texture.equals(TileTexture.SHOP_FISH) ||
                    texture.equals(TileTexture.SHOP_PIERRE) ||
                    texture.equals(TileTexture.SHOP_CARPENTER))
            {
                return "\uD83D\uDFE7"; // 🟧
            }else if (texture.equals(TileTexture.NPC_BLACKSMITH) ||
                    texture.equals(TileTexture.NPC_JOJAMART) ||
                    texture.equals(TileTexture.NPC_SALOON) ||
                    texture.equals(TileTexture.NPC_MARNIE) ||
                    texture.equals(TileTexture.NPC_FISH) ||
                    texture.equals(TileTexture.NPC_PIERRE) ||
                    texture.equals(TileTexture.NPC_CARPENTER))
            {
                return "\uD83E\uDD13"; // 🤓
            } else if (texture.equals(TileTexture.ANIMAL_BUILDING))
            {
                return "⬜"; // ⬜
            }
            else
            {
                return "\uD83D\uDFE5"; // ERROR
            }
        } else
        {
            if (object instanceof Tree)
            {

                return "\uD83C\uDF33"; // 🌳
//                return "\uD83D\uDFEA";
            } else if (object instanceof Crop)
            {
                if (object instanceof GiantCrop)
                {
                    return "\uD83C\uDF44"; // 🍄
                }
                else
                {
                    return "\uD83C\uDF31"; // 🌱 emoji
//                return "\uD83D\uDFE5";
                }
            } else if (object instanceof ForagingCrop)
            {
                    return "\uD83E\uDD6C"; // 🥬
//                return "⬛";
            } else if (object instanceof ForagingTree)
            {
                return "\uD83C\uDF32"; // 🌲
            }
            else if (object instanceof Resource)
            {
                Resource r = (Resource) object;
                if (r.getResourceType().equals(ResourceItem.STONE))
                {
                    return "\uD83E\uDEA8"; // 🪨
                } else if (r.getResourceType().equals(ResourceItem.WOOD))
                {
                        return "\uD83E\uDEB5"; // 🪵
//                    return "\uD83D\uDFE7";
                }
            } else if (object.getObjectType().equals(GameObjectType.COAL))
            {
              return "⬛";
            } else if (object instanceof ForagingMineral)
            {
                return "\uD83D\uDC8E"; // 💎
            } else
            {
                return "\uD83D\uDFE5"; // 🟥 ERROR
            }
        }

        return "\uD83D\uDFE5"; // ERROR
    }

    public boolean isImmuneFromCrows()
    {
        return isImmuneFromCrows;
    }

    public void makeImmuneFromCrows()
    {
        isImmuneFromCrows = true;
    }

    public void setImmunityFromCrows()
    {
        isImmuneFromCrows = false;
    }

    public void unHitByThunder()
    {
        hitByThunder = false;
    }

    public int getWateringChance()
    {
        return wateringChance;
    }

    public void setWateringChance(int wateringChance)
    {
        this.wateringChance = wateringChance;
    }

    public boolean isGrowFaster()
    {
        return growFaster;
    }

    public void resetWateringChance()
    {
        wateringChance = 0;
    }

    public void resetGrowFaster()
    {
        growFaster = false;
    }

    public void setGrowFaster()
    {
        growFaster = true;
    }

    public boolean isRandomForaging()
    {
        return isRandomForaging;
    }

    public void setRandomForaging(boolean randomForaging)
    {
        isRandomForaging = randomForaging;
    }

    public boolean hasNpc()
    {
        for (NPC npc : App.getCurrentGame().getNPCs())
        {
            if (npc.getLocation().equals(point))
            {
                return true;
            }
        }
        return false;
    }

    public NPC getNpc()
    {
        for (NPC npc : App.getCurrentGame().getNPCs())
        {
            if (npc.getLocation().equals(point))
            {
                return npc;
            }
        }
        return null;
    }

    public Fish getFish()
    {
        return fish;
    }

    public void setFish(Fish fish)
    {
        this.fish = fish;
    }
}
