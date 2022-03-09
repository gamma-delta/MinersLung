package at.petrak.minerslung.common.breath;

import net.minecraft.util.StringRepresentable;

import java.util.function.ToIntFunction;

public enum AirQualityLevel implements StringRepresentable {
    /**
     * Full freedom to breathe
     */
    GREEN("green"),
    /**
     * No loss, no gain
     */
    BLUE("blue"),
    /**
     * Slowly lose oxygen
     */
    YELLOW("yellow"),
    /**
     * Completely unable to breathe (like underwater)
     */
    RED("red");

    public final String serializedName;

    private AirQualityLevel(String serializedName) {
        this.serializedName = serializedName;
    }

    @Override
    public String getSerializedName() {
        return this.serializedName;
    }

    public boolean bubbleBeats(AirQualityLevel other) {
        ToIntFunction<AirQualityLevel> idx = aq -> switch (aq) {
            case RED -> 4;
            case YELLOW -> 3;
            case GREEN -> 2;
            case BLUE -> 1;
        };
        return idx.applyAsInt(this) > idx.applyAsInt(other);
    }
}
