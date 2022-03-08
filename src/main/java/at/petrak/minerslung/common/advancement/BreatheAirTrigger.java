package at.petrak.minerslung.common.advancement;

import at.petrak.minerslung.MinersLungMod;
import at.petrak.minerslung.common.breath.AirQualityLevel;
import com.google.gson.JsonObject;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class BreatheAirTrigger extends SimpleCriterionTrigger<BreatheAirTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(MinersLungMod.MOD_ID, "breathe_bad_air");

    private static final String TAG_AIR_QUALITY = "air_quality";
    private static final String TAG_PROTECTION = "protection";
    private static final String TAG_UNDERWATER_OK = "underwater_ok";
    private static final String TAG_BLADDER_OK = "bladder_ok";

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    protected Instance createInstance(JsonObject json, EntityPredicate.Composite predicate,
        DeserializationContext ctx) {
        return new Instance(
            predicate,
            AirQualityLevel.valueOf(json.get(TAG_AIR_QUALITY).getAsString()),
            json.get(TAG_PROTECTION).getAsBoolean(),
            json.get(TAG_UNDERWATER_OK).getAsBoolean(),
            json.get(TAG_BLADDER_OK).getAsBoolean());
    }

    public void trigger(ServerPlayer player, AirQualityLevel airBreathed, boolean wasProtected, boolean wasUnderwater,
        boolean usedBladder) {
        super.trigger(player, inst -> inst.test(airBreathed, wasProtected, wasUnderwater, usedBladder));
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        protected final AirQualityLevel airQuality;
        protected final boolean protection;
        protected final boolean underwaterOk;
        protected final boolean bladderOk;

        public Instance(EntityPredicate.Composite predicate, AirQualityLevel airQuality, boolean protection,
            boolean underwaterOk, boolean bladderOk) {
            super(ID, predicate);
            this.protection = protection;
            this.airQuality = airQuality;
            this.underwaterOk = underwaterOk;
            this.bladderOk = bladderOk;
        }

        @Override
        public ResourceLocation getCriterion() {
            return ID;
        }

        @Override
        public JsonObject serializeToJson(SerializationContext ctx) {
            var json = super.serializeToJson(ctx);
            json.addProperty(TAG_AIR_QUALITY, this.airQuality.toString());
            json.addProperty(TAG_PROTECTION, this.protection);
            json.addProperty(TAG_UNDERWATER_OK, this.underwaterOk);
            json.addProperty(TAG_BLADDER_OK, this.bladderOk);
            return json;
        }

        private boolean test(AirQualityLevel qualityBreathed, boolean wasProtected, boolean wasUnderwater,
            boolean usedBladder) {
            // another good use of the implies operator
            // IF I HAD ONE
            return this.protection == wasProtected
                && this.airQuality == qualityBreathed
                && (!wasUnderwater || this.underwaterOk)
                && (!usedBladder || this.bladderOk);
        }
    }
}
