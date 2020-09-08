package cookie04.digitalitems;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final String CATEGORY_POWER = "power";
    public static final String SUBCATEGORY_ITEM_DIGITIZER = "item_digitizer";

    public static ForgeConfigSpec.IntValue ITEM_DIGITIZER_POWER_MAX;
    public static ForgeConfigSpec.IntValue ITEM_DIGITIZER_DIGITIZE_COST;
    public static ForgeConfigSpec.IntValue ITEM_DIGITIZER_REMATERIALISE_COST;
    public static ForgeConfigSpec.IntValue ITEM_DIGITIZER_CHECK_COST;
    public static ForgeConfigSpec.BooleanValue ITEM_DIGITIZER_POWER_ENABLED;

    public static ForgeConfigSpec SERVER_CONFIG;

    static {
        ForgeConfigSpec.Builder SERVER_BUILDER = new ForgeConfigSpec.Builder();
        SERVER_BUILDER.comment("Power settings").push(CATEGORY_POWER);
        setupItemDigitizerConfig(SERVER_BUILDER);
        SERVER_BUILDER.pop();
        SERVER_CONFIG = SERVER_BUILDER.build();
    }

    private static void setupItemDigitizerConfig(ForgeConfigSpec.Builder SERVER_BUILDER) {
        SERVER_BUILDER.comment("Item digitizer settings").push(SUBCATEGORY_ITEM_DIGITIZER);
        ITEM_DIGITIZER_POWER_MAX = SERVER_BUILDER.comment("Maximum power for the item digitizer").defineInRange("maxPower", 2000, 0, Integer.MAX_VALUE);
        ITEM_DIGITIZER_DIGITIZE_COST = SERVER_BUILDER.comment("Power usage for the item digitizer to digitize an item stack").defineInRange("digitizeCost", 20, 0, Integer.MAX_VALUE);
        ITEM_DIGITIZER_REMATERIALISE_COST = SERVER_BUILDER.comment("Power usage for the item digitizer to rematerialize an item stack").defineInRange("rematerializeCost", 20, 0, Integer.MAX_VALUE);
        ITEM_DIGITIZER_CHECK_COST = SERVER_BUILDER.comment("Power usage for the item digitizer to get information about an item stack").defineInRange("checkCost", 10, 0, Integer.MAX_VALUE);
        ITEM_DIGITIZER_POWER_ENABLED = SERVER_BUILDER.comment("Toggles wether or not the item digitizer needs power to function").define("needsPower", false);
        SERVER_BUILDER.pop();
    }
}