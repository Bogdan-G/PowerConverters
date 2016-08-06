package powercrystals.powerconverters.power.systems;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import powercrystals.powerconverters.common.TileEntityCharger;
import powercrystals.powerconverters.power.PowerSystem;

import powercrystals.powerconverters.power.systems.fortron.BlockFortron;
import powercrystals.powerconverters.power.systems.fortron.ItemBlockFortron;
import powercrystals.powerconverters.power.systems.fortron.TileEntityFortronConsumer;
import powercrystals.powerconverters.power.systems.fortron.TileEntityFortronProducer;

/**
 * Fortron Energy (MFFS) support.
 */
public class PowerFortron extends PowerSystem {
    public static final String id = "Fortron"; 

    // Maybe the Ratio should be discussed with calclavia,
    // i asume that 1rf = 1joule(@mffs-api@resonant-engine)
    // so 1kL/s -> 50L/t = 50rf/t

    public static final float DEFAULT_ENERGY_PER_INPUT = 1000;
    public static final float DEFAULT_ENERGY_PER_OUTPUT = 1000;
    public static final String CATEGORY_MFFS = POWERSYSTEM_CATEGORY + ".mffs";

    public PowerFortron() {
        name = "Fortron";
        _internalEnergyPerInput = DEFAULT_ENERGY_PER_INPUT;
        _internalEnergyPerOutput = DEFAULT_ENERGY_PER_OUTPUT;
        _unit = "L";

        block = new BlockFortron();
        itemBlock = ItemBlockFortron.class;
        consumer = TileEntityFortronConsumer.class;
        producer = TileEntityFortronProducer.class;

    }

    @Override
    public String getId() {
        return id;
    }

    public void registerBlocks() {
        GameRegistry.registerBlock(block, itemBlock, "converter.fotron");
        GameRegistry.registerTileEntity(consumer, "powerConverterFortronConsumer");
        GameRegistry.registerTileEntity(producer, "powerConverterFortronProducer");
    }

    @Override
    public void registerCommonRecipes() {
        GameRegistry.addShapelessRecipe(new ItemStack(block, 1, 1), new ItemStack(block, 1, 0));
        GameRegistry.addShapelessRecipe(new ItemStack(block, 1, 0), new ItemStack(block, 1, 1));
    }

    @Override
    public void loadConfig(Configuration c) {
        _internalEnergyPerInput = (float) c.get(CATEGORY_MFFS, "internalEnergyPerInput", DEFAULT_ENERGY_PER_INPUT).getDouble(DEFAULT_ENERGY_PER_INPUT);
        _internalEnergyPerOutput = (float) c.get(CATEGORY_MFFS, "internalEnergyPerOutput", DEFAULT_ENERGY_PER_OUTPUT).getDouble(DEFAULT_ENERGY_PER_OUTPUT);
    }

    @Override
    public void saveConfig(Configuration c) {
        c.getCategory(CATEGORY_MFFS).get("internalEnergyPerInput").set(_internalEnergyPerInput);
        c.getCategory(CATEGORY_MFFS).get("internalEnergyPerOutput").set(_internalEnergyPerOutput);
    }
}
