package scripts.SPXAIOMiner.API.Game.Banking;

import org.tribot.api.Clicking;
import org.tribot.api.General;
import org.tribot.api.Timing;
import org.tribot.api.types.generic.Condition;
import org.tribot.api2007.*;
import org.tribot.api2007.types.*;

/**
 * Created by Sphiinx on 1/10/2016.
 */
public class Banking07 {

    public static final int BANKING_INTERFACE = 12;
    public static final int NOTE_INTERFACE = 24;

    /**
     * Deposits all items in your inventory using the 'Deposit inventory' button.
     *
     * @return The amount of items deposited. Takes item stack into account.
     */
    public static boolean depositAll() {
        return Banking.depositAll() > 0;
    }

    /**
     * Deposits all of the item specified using the 'Deposit-All' button.
     *
     * @return True if successful; false otherwise.
     */
    public static boolean depositAllItem(String... itemName) {
        return Banking.deposit(0, itemName);
    }

    /**
     * Deposits all of the itemID specified using the 'Deposit-All' button.
     *
     * @return True if successful; false otherwise.
     */
    public static boolean depositAllItem(int... itemID) {
        return Banking.deposit(0, itemID);
    }

    /**
     * Opens the bank in the Grand Exchange.
     *
     * @return True if successful; false otherwise.
     */
    public static boolean openGrandExchangeBank() {
        RSNPC[] banker = NPCs.findNearest("Banker");
        if (banker.length > 0) {
            if (banker[0].isOnScreen()) {
                return Clicking.click("Bank Banker", banker[0]);
            }
        } else {
            Camera.turnToTile(banker[0]);
            Timing.waitCondition(new Condition() {
                @Override
                public boolean active() {
                    General.sleep(100);
                    return banker[0].isOnScreen();
                }
            }, General.random(1000, 1200));
        }
        return false;
    }

    /**
     * Checks if the Banking screen has Note selected or not.
     *
     * @return True if Note is selected; false otherwise.
     */
    public static boolean isNotedSelected() {
        return Game.getSetting(115) == 1;
    }

    /**
     * Turns item noting on in the Banking interface.
     *
     * @return True if the Note interface was clicked; false otherwise;
     */
    public static boolean selectNote() {
        if (Banking.isBankScreenOpen()) {
            if (Interfaces.get(BANKING_INTERFACE) != null) {
                final RSInterfaceChild noteInterface = Interfaces.get(BANKING_INTERFACE, NOTE_INTERFACE);
                if (noteInterface != null && !noteInterface.isHidden(true)) {
                    return noteInterface.click();
                }
            }
        }
        return false;
    }

    /**
     * Checks if the RSPlayer is in a bank.
     *
     * @return True if the RSplayer is in a bank; false otherwise.
     * */
    public static boolean isInBank() {
        final String[] banks = new String[] {
                "Bank booth",
                "Banker"
        };
        RSObject[] bank = Objects.findNearest(15, banks);
        return bank.length > 0 && bank[0].isOnScreen() && bank[0].isClickable();
    }

    /**
     * Gets the current bank space in the Players bank.
     *
     * @return The amount of space in the Players bank.
     */
    public static int getCurrentBankSpace() {
        RSInterface amount = Interfaces.get(12, 5);
        if (amount != null) {
            String text = amount.getText();
            if (text != null) {
                try {
                    int parse = Integer.parseInt(text);
                    if (parse > 0) {
                        return parse;
                    }
                } catch (NumberFormatException e) {
                    return -1;
                }
            }
        }
        return -1;
    }

    /**
     * Checks if the bank is loaded.
     *
     * @return True if the bank is loaded; false otherwise.
     */
    public static boolean isBankItemsLoaded() {
        return getCurrentBankSpace() == Banking.getAll().length;
    }

    /**
     * Checks if the given itemIDs are equal to the given ID.
     *
     * @param items The items to check the IDs of.
     * @param id The ID to check the itemIDs of.
     * @return The item with the matching ID.
     * */
    private static RSItem getItem(int id, RSItem[] items) {
        for (RSItem item : items) {
            if (item.getID() == id) {
                return item;
            }
        }
        return null;
    }

    /**
     * Withdraws the given ID with the given amount.
     * Caches the Bank to make sure it's completely loaded before checking if the given items are in the bank.
     *
     * @param id The ID in which to withdraw.
     * @param amount The amount in which to withdraw.
     * @return True if the withdraw was successful; false otherwise.
     * */
    public static boolean withdrawItem(int id, int amount) {
        RSItem[] itemCache;
        if (getCurrentBankSpace() == (itemCache = Banking.getAll()).length) {
            RSItem itemToWithdraw = getItem(id, itemCache);
            if (itemToWithdraw != null) {
                if (Banking.withdrawItem(itemToWithdraw, amount)) {
                    return true;
                }
            }
        }
        return false;
    }

}

