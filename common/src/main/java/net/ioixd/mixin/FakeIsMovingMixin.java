package net.ioixd.mixin;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.entity.pokemon.PokemonServerDelegate;
import net.ioixd.MountIsMoving;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.mob.MobEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PokemonServerDelegate.class)
public class FakeIsMovingMixin {
    @Shadow(remap = false) public PokemonEntity entity;

    @ModifyVariable(method = "updatePoseType",at= @At(value = "INVOKE", target = "Lcom/cobblemon/mod/common/entity/pokemon/PokemonEntity;getIsSubmerged()Z",shift = At.Shift.AFTER))
    Boolean isMovingOverride(Boolean value){
        //System.out.println("DD called override for >>"+this.entity);
        if(this.entity instanceof PokemonEntity)
        {
            //System.out.println("DD pokemon moving>> "+ ((MountIsMoving) (Object)entity).mount_isMoving()+" >> firstPassanger >> "+ entity.getFirstPassenger());
            if(((MountIsMoving) (Object)entity).mount_isMoving() && entity.getFirstPassenger() !=null){
                //System.out.println("DD changed pokemon is moveing");
                return true;
            }
        }
        return value;
    }
}
