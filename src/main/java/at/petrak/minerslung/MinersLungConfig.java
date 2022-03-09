package at.petrak.minerslung;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class MinersLungConfig {
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> poorOxygenDimensions;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> noOxygenDimensions;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> poorOxygenWithThresholdDimensions;
    public static ForgeConfigSpec.BooleanValue enableSignalTorches;
    public static ForgeConfigSpec.IntValue drownedChoking;
    public static ForgeConfigSpec.DoubleValue soulFireRange;
    public static ForgeConfigSpec.DoubleValue soulCampfireRange;
    public static ForgeConfigSpec.DoubleValue soulTorchRange;

    private static Map<String, Integer> poorOxygenThresholdDims = null;

    private static final Pattern THRESHOLD_PATTERN = Pattern.compile("([-_a-zA-Z0-9./:]+)=(-?\\d+)");

    public MinersLungConfig(ForgeConfigSpec.Builder builder) {
        poorOxygenDimensions = builder
            .comment("Dimensions in which a respirator is always required to breathe")
            .defineList("poorOxygenDimensions", Lists.newArrayList(
                "minecraft:the_nether"
            ), o -> true);

        noOxygenDimensions = builder
            .comment("Dimensions in which a water breathing potion is always required to breathe")
            .defineList("noOxygenDimensions", Lists.newArrayList(
                "minecraft:the_end"
            ), o -> true);

        poorOxygenWithThresholdDimensions = builder
            .comment(
                "Dimensions in which a respirator is required below a certain Y level",
                "Syntax: `dimension_name=ylevel`")
            .defineList("poorOxygenWithThresholdDimensions", Lists.newArrayList(
                "minecraft:overworld=0"
            ), o -> o instanceof String s && THRESHOLD_PATTERN.matcher(s).matches());

        enableSignalTorches = builder
            .comment("Whether to allow right-clicking torches to make them spray particle effects")
            .define("enableSignalTorches", true);

        drownedChoking = builder
            .comment("How much air a Drowned attack removes")
            .defineInRange("drownedChoking", 30, Integer.MIN_VALUE, Integer.MAX_VALUE);

        soulTorchRange = builder
            .comment("The radius a soul torch projects a bubble of breathable air around it",
                "Peculiar things may happen if you change this, but breaking and replacing should fix it")
            .defineInRange("soulCampfireRange", 2.0, 0.0, 32.0);
        soulFireRange = builder
            .comment("The radius a soul fire projects a bubble of breathable air around it")
            .defineInRange("soulCampfireRange", 5.0, 0.0, 32.0);
        soulCampfireRange = builder
            .comment("The radius a soul campfire projects a bubble of breathable air around it")
            .defineInRange("soulCampfireRange", 9.0, 0.0, 32.0);
    }

    public static Map<String, Integer> getPoorOxygenThresholdDims() {
        if (poorOxygenThresholdDims == null) {
            var list = poorOxygenWithThresholdDimensions.get();
            poorOxygenThresholdDims = new HashMap<>(list.size());
            for (var s : list) {
                var matcher = THRESHOLD_PATTERN.matcher(s);
                // god i hate java
                if (matcher.find()) {
                    var key = matcher.group(1);
                    var valStr = matcher.group(2);
                    var val = Integer.parseInt(valStr);
                    poorOxygenThresholdDims.put(key, val);
                }
            }
        }
        return poorOxygenThresholdDims;
    }
}
