package powercrystals.powerconverters.crafting.mods;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.ShapedOreRecipe;
import powercrystals.powerconverters.crafting.RecipeProvider;
import powercrystals.powerconverters.power.PowerSystem;
import powercrystals.powerconverters.power.PowerSystemManager;
import powercrystals.powerconverters.power.systems.PowerFortron;

/**
 * Add recipes for MFFS/Fortron
 */
public class RecipeMFFS extends RecipeProvider {
    private boolean enableRecipes = true;
    public static final String RECIPE_FORTRON_CATEGORY = RECIPE_CATEGORY + ".mffs";

    @Override
    public void registerRecipes() {

        PowerSystem fortron = PowerSystemManager.getInstance().getPowerSystemByName(PowerFortron.id);
        if(fortron != null) {
        	Block converterBlock = fortron.block;

            ItemStack stackGold = new ItemStack(GameRegistry.findItem("minecraft", "gold_ingot"));
            ItemStack stackDeriver = new ItemStack(GameRegistry.findItem("MFFS", "coercionDeriver"));

        	GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(converterBlock, 1, 0), true, new Object[]{ // Consumer
        		"G G",
        		" D ",
        		"G G",
        		'G', stackGold,
        		'D', stackDeriver
        	}));

        }
    }

    @Override
    public void loadConfig(Configuration c) {
        enableRecipes = c.get(RECIPE_FORTRON_CATEGORY, "enableRecipes", true).getBoolean(true);
    }

    @Override
    public void saveConfig(Configuration c) {
        c.getCategory(RECIPE_FORTRON_CATEGORY).get("enableRecipes").set(enableRecipes);
    }
}
