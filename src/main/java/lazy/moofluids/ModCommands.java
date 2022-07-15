package lazy.moofluids;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.utils.MooFluidReg;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.entity.EntityTypeTest;

import java.util.Objects;

public class ModCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> findCowsNearby() {
        return Commands.literal(MooFluids.MOD_ID)
                .requires(cmd -> cmd.hasPermission(2))
                .then(Commands.literal("find")
                        .executes((stackCommandContext) -> {
                                    var level = stackCommandContext.getSource().getLevel();
                                    var cows = level.getEntities(EntityTypeTest.forClass(MooFluidEntity.class), (b) -> true);

                                    if(cows.isEmpty()) {
                                        stackCommandContext.getSource().sendFailure(new TextComponent("0 fluids cows nearby."));
                                        return 1;
                                    }

                                    for (var cow : cows) {
                                        stackCommandContext.getSource().sendSuccess(
                                                new TextComponent("Found cow at: "
                                                        + cow.blockPosition().getX() + ", "
                                                        + cow.blockPosition().getX() + ", "
                                                        + cow.blockPosition().getZ() +
                                                        " Fluid: " + cow.getFluidStack().getDisplayName().getString()
                                                ),
                                                false
                                        );
                                    }

                                    return 1;
                                }
                        )
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> spawnCow() {
        return Commands.literal(MooFluids.MOD_ID)
                .requires(cmd -> cmd.hasPermission(2))
                .then(Commands.literal("create")
                        .then(Commands.argument("fluid", BlockStateArgument.block())
                                .executes((stackCommandContext) -> {
                                    var level = stackCommandContext.getSource().getLevel();
                                    var input = stackCommandContext.getArgument("fluid", BlockInput.class);

                                    if (input.getState().getBlock() instanceof LiquidBlock liquidBlock) {
                                        if (MooFluidReg.exists(liquidBlock.getFluid())) {
                                            var entity = new MooFluidEntity(Setup.MOO_FLUID.get(), level);
                                            entity.setPos(stackCommandContext.getSource().getPosition());
                                            entity.setFluid(Objects.requireNonNull(liquidBlock.getFluid().getRegistryName()).toString());
                                            entity.setCustomName(entity.getFluidStack().getDisplayName());
                                            entity.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null, null);

                                            level.addFreshEntity(entity);

                                            stackCommandContext.getSource().sendSuccess(new TextComponent("Spawned MooFluidCow with " + entity.getFluidStack().getDisplayName().getString() + "."), false);
                                            return 1;
                                        }
                                    } else {
                                        stackCommandContext.getSource().sendFailure(new TextComponent("Given block state isn't a valid fluid"));
                                        return 0;
                                    }
                                    return 0;
                                }))
                );
    }
}
