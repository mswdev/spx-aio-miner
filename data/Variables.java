package scripts.SPXAIOMiner.data;

import org.tribot.api2007.types.RSArea;
import org.tribot.api2007.types.RSTile;
import scripts.SPXAIOMiner.data.enums.Mode;
import scripts.SPXAIOMiner.data.enums.OreType;
import scripts.SPXAIOMiner.data.enums.Pickaxe;
import scripts.SPXAIOMiner.data.enums.Worlds;

import java.io.File;
import java.util.Properties;

/**
 * Created by Sphiinx on 1/15/2016.
 */
public class Variables {

    public boolean drawObjects;
    public boolean disablePaint;
    public boolean worldHop;
    public boolean oresHop;
    public boolean shouldWeHop;
    public boolean dropGems;
    public boolean guiComplete;
    public boolean stopScript;
    public boolean upgradePickaxe;
    public boolean radiusMine;
    public double version;
    public int radius = 30;
    public int playersToHop;
    public String status;
    public Worlds worlds;
    public OreType oreType;
    public Mode mode;
    public Pickaxe pickaxe;
    public Properties properties = new Properties();
    public File path;
    public RSTile area;
    
}

