package at.petrak.minerslung.common.breath;

import net.minecraft.util.StringRepresentable;

public enum AirQualityLevel implements StringRepresentable {
    /**
     * Full freedom to breathe
     */
    GREEN("green"),
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
}
