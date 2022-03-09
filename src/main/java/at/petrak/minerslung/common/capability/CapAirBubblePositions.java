package at.petrak.minerslung.common.capability;

import at.petrak.minerslung.common.breath.AirQualityLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Stores positions of air bubble projections as a struct of arrays
 */
public class CapAirBubblePositions implements ICapabilitySerializable<CompoundTag> {
    public static final String CAP_NAME = "air_bubble_positions";
    public static final String TAG_POSITIONS = "positions", TAG_QUALITY = "air_quality", TAG_RADIUS = "radius";

    public Map<BlockPos, Entry> entries;

    public CapAirBubblePositions() {
        this.entries = new HashMap<>();
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.AIR_BUBBLE_POSITIONS.orEmpty(cap, LazyOptional.of(() -> this));
    }

    @Override
    public CompoundTag serializeNBT() {
        var positions = new ListTag();
        var qualitiesArr = new byte[this.entries.size()];
        var radiusesArr = new long[this.entries.size()];

        var i = 0;
        for (var pos : this.entries.keySet()) {
            var entry = this.entries.get(pos);
            positions.add(NbtUtils.writeBlockPos(pos));
            qualitiesArr[i] = (byte) entry.airQuality.ordinal();
            radiusesArr[i] = Double.doubleToRawLongBits(entry.radius);
            i++;
        }

        var out = new CompoundTag();
        out.put(TAG_POSITIONS, positions);
        out.put(TAG_QUALITY, new ByteArrayTag(qualitiesArr));
        out.put(TAG_RADIUS, new LongArrayTag(radiusesArr));
        return out;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        var positions = nbt.getList(TAG_POSITIONS, Tag.TAG_COMPOUND);
        var qualities = nbt.getByteArray(TAG_QUALITY);
        var radiuses = nbt.getLongArray(TAG_RADIUS);

        this.entries = new HashMap<>(positions.size());
        for (int i = 0; i < positions.size(); i++) {
            entries.put(NbtUtils.readBlockPos(positions.getCompound(i)),
                new Entry(AirQualityLevel.values()[qualities[i]], Double.longBitsToDouble(radiuses[i])));
        }
    }

    public record Entry(AirQualityLevel airQuality, double radius) {
    }
}
