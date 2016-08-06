package powercrystals.powerconverters.power.systems;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.Constants;
import powercrystals.powerconverters.power.PowerSystem;
import powercrystals.powerconverters.power.systems.steam.BlockSteam;
import powercrystals.powerconverters.power.systems.steam.ItemBlockSteam;
import powercrystals.powerconverters.power.systems.steam.TileEntitySteamConsumer;
import powercrystals.powerconverters.power.systems.steam.TileEntitySteamProducer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Steam power support. Adds steam consumer and producer.
 */
public class PowerSteam extends PowerSystem {
    public static String id = "STEAM";

    private HashMap<String, SteamType> steamTypes = new HashMap<String, SteamType>();
    private HashMap<String, SteamType> serverSteamTypes = new HashMap<String, SteamType>();
    private List<String> serverSteamTypeNames = new ArrayList<String>();
    public static class SteamType {
        public String name;
        public String displayName;
        public float defaultEnergyPerInput;
        public float defaultEnergyPerOutput;
        public float energyPerInput;
        public float energyPerOutput;

        public SteamType(String name, String displayName, float input, float output) {
            this.name = name;
            this.displayName = displayName;
            this.defaultEnergyPerInput = input;
            this.defaultEnergyPerOutput = output;
            this.energyPerInput = input;
            this.energyPerOutput = output;
        }
    }

    public static final String CATEGORY_STEAM = POWERSYSTEM_CATEGORY + ".steam";

    private static final int THROTTLE_CONSUMER_DEFAULT = 1000;
    private static final int THROTTLE_PRODUCER_DEFAULT = 1000;
    private int throttleConsumer = THROTTLE_CONSUMER_DEFAULT;
    private int throttleProducer = THROTTLE_PRODUCER_DEFAULT;

    public PowerSteam() {
        name = "Steam";
        _unit = "mB";

        block = new BlockSteam();
        itemBlock = ItemBlockSteam.class;
        consumer = TileEntitySteamConsumer.class;
        producer = TileEntitySteamProducer.class;
    }

    @Override
    public void readEnergyValues(NBTTagCompound nbt) {
        serverSteamTypes.clear();
        serverSteamTypeNames.clear();
        NBTTagList types = nbt.getTagList("steamTypes", Constants.NBT.TAG_COMPOUND);
        for(int i = 0; i < types.tagCount(); i++) {
            NBTTagCompound type = types.getCompoundTagAt(i);
            SteamType newSteam = new SteamType(
                    type.getString("name"),
                    type.getString("display"),
                    type.getFloat("Input"),
                    type.getFloat("Output")
            );
            serverSteamTypes.put(newSteam.name, newSteam);
            serverSteamTypeNames.add(newSteam.name);
        }
        Collections.sort(serverSteamTypeNames, String.CASE_INSENSITIVE_ORDER);
    }

    @Override
    public NBTTagCompound writeEnergyValues() {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagList types = new NBTTagList();
        for(SteamType type : steamTypes.values()) {
            NBTTagCompound data = new NBTTagCompound();
            data.setString("name", type.name);
            data.setString("display", type.displayName);
            data.setFloat("Input", type.energyPerInput);
            data.setFloat("Output", type.energyPerOutput);
            types.appendTag(data);
        }
        nbt.setTag("steamTypes", types);

        return nbt;
    }

    public void addSteamType(String name, String displayName, float input, float output) {
        if(!steamTypes.containsKey(name)) {
            steamTypes.put(name, new SteamType(name, displayName, input, output));
        }
    }

    public SteamType getSteamType(String name) {
        return serverSteamTypes.get(name);
    }

    public SteamType getSteamType(int index) {
        return 0 <= index && index < serverSteamTypeNames.size() ? serverSteamTypes.get(serverSteamTypeNames.get(index)) : null;
    }

    public int getSteamSubtype(SteamType steamType) {
        return serverSteamTypeNames.indexOf(steamType.name);
    }

    @Override
    public float getInternalEnergyPerInput(int meta) {
        SteamType type = getSteamType(meta);
        return type == null ? 0 : type.energyPerOutput;
    }

    @Override
    public float getInternalEnergyPerOutput(int meta) {
        if(meta < 0) return 0;
        if(meta == 0) meta++;

        SteamType type = getSteamType(meta - 1);
        return type == null ? 0 : type.energyPerOutput;
    }

    public String getUnit(int subtype) {
        if(subtype == -1) {
            return super.getUnit(subtype);
        }
        else {
            return String.format("%s (%s)", super.getUnit(subtype), getSteamType(subtype).displayName);
        }
    }

    public int getSteamTypeCount() {
        return steamTypes.size();
    }

    public void registerBlocks() {
        GameRegistry.registerBlock(block, itemBlock, "converter.steam");
        GameRegistry.registerTileEntity(consumer, "powerConverterSteamConsumer");
        GameRegistry.registerTileEntity(producer, "powerConverterSteamProducer");
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void registerCommonRecipes() {
        for(int i = 0; i < steamTypes.size(); i++) {
            GameRegistry.addShapelessRecipe(new ItemStack(block, 1, i), new ItemStack(block, 1, i + 1));
        }
        GameRegistry.addShapelessRecipe(new ItemStack(block, 1, steamTypes.size()), new ItemStack(block, 1, 0));
    }

    @Override
    public void loadConfig(Configuration c) {
        for(String typeName : steamTypes.keySet()) {
            SteamType updatedType = steamTypes.get(typeName);
            updatedType.energyPerInput = (float) c.get(
                    CATEGORY_STEAM, String.format("%s.internalEnergyPerInput", updatedType.name),
                    updatedType.defaultEnergyPerInput).getDouble(updatedType.defaultEnergyPerInput);
            updatedType.energyPerOutput = (float) c.get(
                    CATEGORY_STEAM, String.format("%s.internalEnergyPerOutput", updatedType.name),
                    updatedType.defaultEnergyPerOutput).getDouble(updatedType.defaultEnergyPerOutput);
            steamTypes.put(typeName, updatedType);
        }
        throttleConsumer = c.get(CATEGORY_STEAM, "throttle.steamConsumer", THROTTLE_CONSUMER_DEFAULT, "mB/t").getInt(THROTTLE_CONSUMER_DEFAULT);
        throttleProducer = c.get(CATEGORY_STEAM, "throttle.steamProducer", THROTTLE_PRODUCER_DEFAULT, "mB/t\n (Suggested value for mod expoit handling = 1; this does not diminish steam return)").getInt(THROTTLE_PRODUCER_DEFAULT);
    }

    @Override
    public void saveConfig(Configuration c) {
        for(SteamType type : steamTypes.values()) {
            c.getCategory(CATEGORY_STEAM).get(String.format("%s.internalEnergyPerInput", type.name)).set(type.energyPerInput);
            c.getCategory(CATEGORY_STEAM).get(String.format("%s.internalEnergyPerOutput", type.name)).set(type.energyPerOutput);
        }
        c.getCategory(CATEGORY_STEAM).get("throttle.steamConsumer").set(throttleConsumer);
        c.getCategory(CATEGORY_STEAM).get("throttle.steamProducer").set(throttleProducer);
    }

    public int getThrottleConsumer() {
        return throttleConsumer;
    }

    public int getThrottleProducer() {
        return throttleProducer;
    }
}
