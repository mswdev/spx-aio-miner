package scripts.SPXAIOMiner.tasks.Mine;

import org.tribot.api.Clicking;
import org.tribot.api.DynamicClicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.input.Mouse;
import org.tribot.api.types.generic.Condition;
import org.tribot.api.types.generic.Filter;
import org.tribot.api.util.Sorting;
import org.tribot.api2007.*;
import org.tribot.api2007.Player;
import org.tribot.api2007.ext.Filters;
import org.tribot.api2007.types.RSObject;
import org.tribot.api2007.types.RSTile;
import scripts.SPXAIOMiner.API.Framework.Task;
import scripts.SPXAIOMiner.API.Game.Area.Area07;
import scripts.SPXAIOMiner.API.Game.Mouse.Mouse07;
import scripts.SPXAIOMiner.API.Game.Utility.Utility07;
import scripts.SPXAIOMiner.API.Game.Walking.Walking07;
import scripts.SPXAIOMiner.AntiBan;
import scripts.SPXAIOMiner.data.*;
import scripts.SPXAIOMiner.data.Constants;

import java.util.ArrayList;


/**
 * Created by Sphiinx on 1/16/2016.
 */
public class MineOre extends Task {

    private RSObject targetOre;
    private RSTile anticipatedLocation;
    private final Filter<RSObject> ORE_FILTER = oreFilter();
    private ArrayList<RSTile> depletedOres = new ArrayList<>();
    private boolean resourceCheck;
    int createCache;
    int cacheToCheck;
    long check_time;

    public MineOre(Variables v) {
        super(v);
    }

    //<editor-fold defaultstate="collapsed" desc="Execution">
    @Override
    public void execute() {
        Mouse07.fixSelected();
        if (!isMining()) {
            isOreStolen();
            mineOre();
        } else {
            if (AntiBan.shouldSwitchResources(Area07.getPlayersInArea(vars.radius)) && Timing.currentTimeMillis() >= check_time) {
                getActualOre();
            }
            if (Mouse.isInBounds() && AntiBan.should_hover) {
                AntiBan.hoverNextObject(targetOre);
            }
            if (AntiBan.should_open_menu) {
                if (DynamicClicking.clickRSObject(targetOre, 3)) {
                    Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            return ChooseOption.isOpen();
                        }
                    }, General.random(750, 1000));
                }
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IsMining">
    private boolean isMining() {
        return Player.getAnimation() != -1 && !Player.isMoving();
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="MineOre">
    private void mineOre() {
        getActualOre();
        if (targetOre != null) {
            if (targetOre.isOnScreen()) {
                if (Clicking.click("Mine", targetOre)) {
                    if (Timing.waitCondition(new Condition() {
                        @Override
                        public boolean active() {
                            General.sleep(100);
                            return Player.getAnimation() != -1;
                        }
                    }, General.random(3000, 4000))) {
                        depletedOres.add(targetOre.getPosition());
                    }
                    check_time = Timing.currentTimeMillis() + General.random(20000, 30000);
                    createCache = Inventory.getAll().length;
                    resourceCheck = true;
                    AntiBan.resetShouldHover();
                    AntiBan.resetShouldOpenMenu();
                }
            } else {
                Walking07.sceenWalkToObject(targetOre);
            }
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="IsOreStolen">
    private void isOreStolen() {
        if (resourceCheck) {
            cacheToCheck = Inventory.getAll().length;
            if (createCache != 0 && cacheToCheck != 0) {
                if (createCache == cacheToCheck) {
                    AntiBan.incrementResourcesLost();
                    resourceCheck = false;
                }
            }
            createCache = 0;
            cacheToCheck = 0;
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="GetActualOre">
    private void getActualOre() {
        RSObject[] actualOres = getAllObjects();
        if (actualOres.length > 0) {
            if (vars.oresHop) {
                vars.shouldWeHop = false;
            }
            targetOre = AntiBan.selectNextTarget(actualOres);
            vars.oreToDraw = actualOres;
        } else {
            targetOre = null;
            if (vars.oresHop) {
                vars.shouldWeHop = true;
            }
            AntiBan.getReactionTime();
            AntiBan.goToAnticipated(anticipatedLocation);
        }
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="ColorCheck">
    private boolean colorCheck(RSObject object) {
        for (short color : object.getDefinition().getModifiedColors()) {
            if (color == vars.oreType.getColor()) {
                return true;
            }
        }
        return false;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="OreFilter">
    private Filter<RSObject> oreFilter() {
        return new Filter<RSObject>() {
            @Override
            public boolean accept(RSObject rsObject) {
                RSTile pos = rsObject.getPosition();
                if (depletedOres.size() > 0) {
                    for (RSObject object : Objects.getAt(depletedOres.get(0))) {
                        if (rsObject.equals(object)) {
                            depletedOres.remove(pos);
                            anticipatedLocation = pos;
                        }
                    }
                }
                return colorCheck(rsObject) && Player.getPosition().distanceTo(rsObject) <= vars.radius;
            }
        }.combine(Filters.Objects.nameContains("Rock"), true);
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="GetAllObjects">
    private RSObject[] getAllObjects() {
        RSObject[] objects = Objects.getAll(vars.radius, ORE_FILTER);
        Sorting.sortByDistance(objects, Player.getPosition(), true);
        return objects;
    }
    //</editor-fold>

    @Override
    public String toString() {
        return "Mining ore" + Utility07.loadingPeriods();
    }

    @Override
    public boolean validate() {
        return !vars.isSlaveSystemIsRunning &&
                !Inventory.isFull() && vars.area.distanceTo(Player.getPosition()) <= vars.radius &&
                (Inventory.getCount(Constants.PICKAXES) > 0 || Equipment.getItem(Equipment.SLOTS.WEAPON) != null);
    }

}

