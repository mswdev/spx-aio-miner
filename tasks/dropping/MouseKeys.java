package scripts.spxaiominer.tasks.dropping;

import org.tribot.api.General;
import org.tribot.api2007.Inventory;
import org.tribot.api2007.types.RSItem;
import scripts.task_framework.framework.Task;
import scripts.tribotapi.game.inventory.Inventory07;
import scripts.tribotapi.game.skills.mining.enums.Pickaxe;
import scripts.tribotapi.game.timing.Timing07;

/**
 * Created by Sphiinx on 8/5/2016.
 */
public class MouseKeys implements Task {


    @Override
    public boolean validate() {
        return Inventory.isFull();
    }

    @Override
    public void execute() {
        final RSItem[] inventory_cache = Inventory.getAll();
        if (Inventory07.mouseKeysDropAllExcept(Pickaxe.getItemIDs()))
            Timing07.waitCondition(() -> inventory_cache.length != Inventory.getAll().length, General.random(1500, 2000));
    }

    @Override
    public String toString() {
        return "Dropping ore";
    }

}

