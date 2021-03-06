package at.petrak.minerslung.common.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CapIsProtectedFromBadAir implements ICapabilitySerializable<CompoundTag> {
    public static final String CAP_NAME = "is_protected_from_bad_air";
    public static final String TAG_IS_USING_BLADDER = "is_using_bladder";

    public boolean isUsingBladder;

    public CapIsProtectedFromBadAir(boolean isUsingBladder) {
        this.isUsingBladder = isUsingBladder;
    }

    public boolean isProtected() {
        return this.isUsingBladder;
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return ModCapabilities.IS_PROTECTED_FROM_AIR.orEmpty(cap, LazyOptional.of(() -> this));
    }

    @Override
    public CompoundTag serializeNBT() {
        var tag = new CompoundTag();
        tag.putBoolean(TAG_IS_USING_BLADDER, this.isUsingBladder);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.isUsingBladder = nbt.getBoolean(TAG_IS_USING_BLADDER);
    }
}
