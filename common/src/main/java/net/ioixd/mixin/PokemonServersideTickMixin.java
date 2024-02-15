package net.ioixd.mixin;


import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonBehaviourFlag;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.ioixd.Cobblemounts;
import net.ioixd.MountIsMoving;
import net.ioixd.client.CobblemountsClient;
import net.minecraft.block.Block;
import net.minecraft.block.FluidBlock;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

/*
serverside logic here
 */
@Mixin(PokemonEntity.class)
public abstract class PokemonServersideTickMixin extends LivingEntity {

    protected PokemonServersideTickMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "tick",at = @At("TAIL"))
    protected void tick(CallbackInfo ci) {
        var pokemon = (PokemonEntity) (Object) this;
        var envcfg = pokemon.getWorld() instanceof ServerWorld ? Cobblemounts.CONFIG : CobblemountsClient.SYNCED_CONFIG;
        var pokemonData = pokemon.getPokemon();
        if (!(this.getFirstPassenger() instanceof PlayerEntity))
            return;
        pokemon.fallDistance = 0;
        var mount = (MountIsMoving) (Object) this;
        Block water = pokemon.getBlockStateAtPos().getBlock();
        boolean inLiquid = water instanceof FluidBlock;
        var isMoving = this.getFirstPassenger().getVelocity().multiply(1, 0, 1).length() > 0.01;
        System.out.println("ismoveing>> "+isMoving);
        mount.mount_setMoving(isMoving);
        var pokemonName = pokemonData.getSpecies().getName().toLowerCase();
        List<ElementalType> typesList = new ArrayList<>();
        pokemonData.getTypes().forEach(x->typesList.add(x));
        if(envcfg.alsoFlyList.contains(pokemonName))
            typesList.add(ElementalTypes.INSTANCE.getFLYING());
        typesList.forEach(ty -> {
            var name = ty.getName();
            switch (name) {
                case "water":
                case "flying":
                    boolean condition;
                    EntityPose animation;
                    EntityPose idleAnimation;
                    boolean flying;
                    switch (ty.getName()) {
                        case "water":
                            if (!envcfg.allowSwimming) {
                                return;
                            }
                            condition = inLiquid;
                            flying = false;
                            break;
                        case "flying":
                            if (!envcfg.allowSwimming) {
                                return;
                            }
                            condition = !pokemon.isOnGround() && !inLiquid;
                            flying = true;
                            break;
                        // We will never hit this part but we need to set the values anyways
                        // to make the compiler happy.
                        default:
                            condition = false;
                            animation = null;
                            flying = false;
                            break;
                    }
                    ;
                    if (flying) {
                        if (condition) {
                            pokemon.setBehaviourFlag(PokemonBehaviourFlag.FLYING, true);

                        }
                        else{
                            pokemon.setBehaviourFlag(PokemonBehaviourFlag.FLYING, false);
                        }
                    }
                    break;
            }
        });
        if (envcfg.allowFlying) {
            typesList.forEach(ty -> {
                var name = ty.getName();
                if (name.equals("flying")) {
                    pokemon.setNoGravity(!pokemon.isOnGround());
                    if (pokemon.isOnGround()){
                        pokemon.setBehaviourFlag(PokemonBehaviourFlag.FLYING, false);
                        //pokemon.updateVelocity(1.0f, pokemon.getFirstPassenger().getRotationVector());
                    }
                }
            });
        }
    }
}
