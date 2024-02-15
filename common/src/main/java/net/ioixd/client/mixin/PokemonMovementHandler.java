package net.ioixd.client.mixin;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonBehaviourFlag;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.ioixd.client.CobblemountsClient;
import net.ioixd.client.SomeOutsideClass;
import net.minecraft.block.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(PokemonEntity.class)
public abstract class PokemonMovementHandler extends LivingEntity {
    @Shadow public abstract void playAmbientSound();

    int ticksInLiquid = 0;

    protected PokemonMovementHandler(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    protected void tickControlled(PlayerEntity player, Vec3d movement) {
       SomeOutsideClass.move((PokemonEntity)(Object)this,player,movement);
    }


}
