package powercrystals.powerconverters.power;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to manage the power systems.
 */
public class PowerSystemManager {
    private static volatile PowerSystemManager instance;

    private Map<String, PowerSystem> powerSystems = new HashMap<String, PowerSystem>();

    private List<String> systemIds = new ArrayList<String>();
    private List<String> serverSystemIds = new ArrayList<String>();

    public static PowerSystemManager getInstance() {
        if(instance == null) {
            instance = new PowerSystemManager();
        }
        return instance;
    }

    public void setServerSystemIds() {
        serverSystemIds = systemIds;
        for(PowerSystem powerSystem : powerSystems.values()) {
            powerSystem.readEnergyValues(powerSystem.writeEnergyValues());
        }
    }

    public void setServerSystemIds(ArrayList<String> ids) {
        serverSystemIds = ids;
        for(PowerSystem powerSystem : powerSystems.values()) {
            powerSystem.readEnergyValues(powerSystem.writeEnergyValues());
        }
    }

    public void readPowerData(NBTTagCompound nbt) {
        // Ensure system ids don't double up when reading. (i.e. multiple connects)
        serverSystemIds.clear();
        NBTTagList ids = nbt.getTagList("systemIds", Constants.NBT.TAG_STRING);
        for(int i = 0; i < ids.tagCount(); i++) {
            serverSystemIds.add(ids.getStringTagAt(i));
        }
        for(PowerSystem powerSystem : powerSystems.values()) {
            NBTTagCompound powerSystemNBT = nbt.getCompoundTag(powerSystem.getId());
            powerSystem.readEnergyValues(powerSystemNBT);
        }
    }

    public NBTTagCompound writePowerData() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList ids = new NBTTagList();
        for(String systemId : systemIds) {
            ids.appendTag(new NBTTagString(systemId));
        }
        nbt.setTag("systemIds", ids);
        for(PowerSystem powerSystem : powerSystems.values()) {
            nbt.setTag(powerSystem.getId(), powerSystem.writeEnergyValues());
        }

        return nbt;
    }

    public void registerPowerSystem(PowerSystem powerSystem) {
        registerPowerSystem(powerSystem.getId(), powerSystem);
        systemIds.add(powerSystem.getId());
    }

    public void registerPowerSystem(String id, PowerSystem powerSystem) {
        powerSystems.put(id, powerSystem);
    }

    public PowerSystem getPowerSystemByName(String name) {
        return powerSystems.containsKey(name) ? powerSystems.get(name) : null;
    }

    public int getPowerSystemId(String name) {
        return serverSystemIds.lastIndexOf(name);
    }

    public PowerSystem getPowerSystemById(int id) {
        String name = serverSystemIds.get(id);
        return getPowerSystemByName(name);
    }

    public void registerBlocks() {
        for(PowerSystem powerSystem : powerSystems.values()) {
            powerSystem.registerBlocks();
        }
    }

    public void registerCommonRecipes() {
        for(PowerSystem powerSystem : powerSystems.values()) {
            powerSystem.registerCommonRecipes();
        }
    }

    public void loadConfigs(Configuration c) {
        for(PowerSystem powerSystem : powerSystems.values()) {
            powerSystem.loadConfig(c);
        }
    }

    public void saveConfigs(Configuration c) {
        for(PowerSystem powerSystem : powerSystems.values()) {
            powerSystem.saveConfig(c);
        }
    }
}
