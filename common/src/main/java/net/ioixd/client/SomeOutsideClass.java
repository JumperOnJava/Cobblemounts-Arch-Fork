package net.ioixd.client;

import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.api.types.ElementalTypes;
import com.cobblemon.mod.common.entity.pokemon.PokemonBehaviourFlag;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FluidBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SomeOutsideClass {
    public static void move(PokemonEntity this_, PlayerEntity player, Vec3d movement) {
        //PlayerEntity player = (PlayerEntity) (Object) this;
        //Entity entity = player.getVehicle();
        var pokemon = (PokemonEntity) (Object) this_;
        //if (entity == null) return;
        //if (!(entity instanceof PokemonEntity pokemon)) return;

        System.out.println("12312321");
        movement = new Vec3d(player.sidewaysSpeed,0,player.forwardSpeed);
        World world = pokemon.getWorld();
        Pokemon pokemonData = pokemon.getPokemon();
        pokemon.setYaw(player.getYaw());
        pokemon.setPitch(pokemon.getPitch());
        pokemon.setHeadYaw(player.getYaw());

        Block water = pokemon.getBlockStateAtPos().getBlock();
        boolean inLiquid = water instanceof FluidBlock;

        float speedModifier = pokemonData.isLegendary() ? 0.0f : (float) CobblemountsClient.SYNCED_CONFIG.legendaryModifier;
        AtomicBoolean isFlying = new AtomicBoolean(false);
        Vec3d moveXZ = movement;//movement.rotateY((float) Math.toRadians(-player.getYaw()));

        Vec3d forward = player.getRotationVector().normalize().multiply(movement.z);

        Vec3d left = movement.multiply(1, 0, 0).rotateY((float) Math.toRadians(-player.getYaw()));

        Vec3d flyMove = forward.add(left);

        double movementSpeed_ = (pokemonData.getSpeed() / 500.0f) + speedModifier;
        if (CobblemountsClient.SYNCED_CONFIG.cappedSpeed) {
            if (movementSpeed_ >= CobblemountsClient.SYNCED_CONFIG.flyingSpeedCap) {
                movementSpeed_ = CobblemountsClient.SYNCED_CONFIG.flyingSpeedCap;
            }
        }

        double[] movementSpeed = new double[]{movementSpeed_};
        var pokemonName = pokemonData.getSpecies().getName().toLowerCase();
        List<ElementalType> typesList = new ArrayList<>();
        pokemonData.getTypes().forEach(x->typesList.add(x));
        if(CobblemountsClient.SYNCED_CONFIG.alsoFlyList.contains(pokemonName))
            typesList.add(ElementalTypes.INSTANCE.getFLYING());

        typesList.forEach(ty -> {
            var name = ty.getName();
            switch (name) {
                case "water":
                case "flying":
                    boolean condition;
                    boolean flying;
                    switch (name) {
                        case "water":
                            if (!CobblemountsClient.SYNCED_CONFIG.allowSwimming) {
                                return;
                            }
                            condition = inLiquid;
                            flying = false;
                            break;
                        case "flying":
                            if (!CobblemountsClient.SYNCED_CONFIG.allowFlying) {
                                return;
                            }
                            condition = !pokemon.isOnGround() && !inLiquid;
                            flying = true;
                            break;
                        // We will never hit this part but we need to set the values anyways
                        // to make the compiler happy.
                        default:
                            condition = false;
                            flying = false;
                            break;
                    }
                    ;
                    if (condition) {
                        if (flyMove.z != 0.0) {
                            pokemon.move(MovementType.SELF, flyMove.multiply(movementSpeed[0]));
                            isFlying.set(true);
                        }
                    } else {
                        pokemon.setPose(EntityPose.STANDING);
                    }
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
        if (!isFlying.get() ) {
            if (!(player instanceof ServerPlayerEntity) && MinecraftClient.getInstance().options.jumpKey.isPressed() && pokemon.isOnGround()) {
                pokemon.addVelocity(0, 0.7, 0);
            }
            pokemon.travel(moveXZ);
            BlockPos forwardPos = getBlockPos(pokemon, player);
            if (!isBlockPosTransparent(forwardPos, world)) {
                BlockPos upperPos = new BlockPos(forwardPos.getX(), forwardPos.getY() + 1, forwardPos.getZ());
                if (isBlockPosTransparent(upperPos, world)) {
                    BlockPos upperUpperPos = new BlockPos(upperPos.getX(), upperPos.getY() + 1,
                            upperPos.getZ());
                    if (isBlockPosTransparent(upperUpperPos, world)) {
                        pokemon.teleport(upperPos.getX(), upperPos.getY(), upperPos.getZ());
                    }
                }
            }
        }
    }
    private static boolean isBlockPosTransparent(BlockPos pos, World world) {
        BlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        return block.isTransparent(state, world, pos) && !(block instanceof FluidBlock);
    }
    private static BlockPos getBlockPos(PokemonEntity living, PlayerEntity player) {
        BlockPos forwardPos = living.getBlockPos();
        int width = (int) Math.floor(living.getWidth());
        for (int i = 0; i < width; i++) {
            forwardPos = switch (player.getMovementDirection()) {
                case NORTH -> forwardPos.north();
                case SOUTH -> forwardPos.south();
                case EAST -> forwardPos.east();
                case WEST -> forwardPos.west();
                default -> forwardPos;
            };
        }
        return forwardPos;
    }
}
