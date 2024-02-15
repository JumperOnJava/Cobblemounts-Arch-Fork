package net.ioixd.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonServerDelegate;
import com.cobblemon.mod.common.entity.pokemon.ai.PokemonNavigation;
import net.ioixd.MountIsMoving;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to control movement animation
 */
@Mixin(PokemonServerDelegate.class)
public class PokemonServerDelegateMixin {
    @Shadow(remap = false) public PokemonEntity entity;

    @Inject(method = "updatePoseType", at= @At(value = "HEAD"),remap = false)
    void isNotMoving(CallbackInfo ci){
        //System.out.println("isMoving in delegate>>"+((MountIsMoving)(Object)entity).mount_isMoving());
    }
}
