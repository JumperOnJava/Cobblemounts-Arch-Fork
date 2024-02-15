package net.ioixd.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import net.ioixd.MountIsMoving;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MoveControl.class)
public class MoveControlMixin {
    @Shadow @Final protected MobEntity entity;

    @Inject(method = "isMoving",at=@At("RETURN"),cancellable = true)
    void isMovingOverride(CallbackInfoReturnable<Boolean> cir){
        //System.out.println(" called override for >>"+this.entity);
        if(this.entity instanceof PokemonEntity)
        {
            //System.out.println(" pokemon moving>> "+ ((MountIsMoving) entity).mount_isMoving()+" >> firstPassanger >> "+ entity.getFirstPassenger());
            if(((MountIsMoving) entity).mount_isMoving() && entity.getFirstPassenger() !=null){
                //System.out.println("changed pokemon is moveing");
                cir.setReturnValue(true);
            }
        }
    }
}
