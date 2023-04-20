package lazy.moofluids;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lazy.moofluids.entity.MooFluidEntity;
import lazy.moofluids.utils.MooFluidReg;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraftforge.registries.ForgeRegistries;

public class ModCommands {

    public static LiteralArgumentBuilder<CommandSourceStack> findCowsNearby() {
        return Commands.literal(MooFluids.MOD_ID)
                .requires(cmd -> cmd.hasPermission(2))
                .then(Commands.literal("find")
                        .executes((stackCommandContext) -> {
                                    var level = stackCommandContext.getSource().getLevel();
                                    var cows = level.getEntities(EntityTypeTest.forClass(MooFluidEntity.class), (b) -> true);
                                    if (cows.isEmpty()) {
                                        stackCommandContext.getSource().sendFailure(Component.literal("0 fluids cows nearby."));
                                        return 1;
                                    }
                                    for (var cow : cows) {
                                        var blockpos = cow.blockPosition();
                                        var component = Component.literal("Found cow with "
                                                + cow.getFluidStack().getDisplayName().getString()
                                                + " at "
                                        );
                                        component.append(ComponentUtils.wrapInSquareBrackets(Component.translatable("chat.coordinates", blockpos.getX(), "~", blockpos.getZ())).withStyle((p_214489_) -> {
                                            return p_214489_.withColor(ChatFormatting.GREEN).withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockpos.getX() + " " + "~" + " " + blockpos.getZ())).withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.translatable("chat.coordinates.tooltip")));
                                        }));
                                        stackCommandContext.getSource().sendSuccess(
                                                component,
                                                false
                                        );
                                    }

                                    return 1;
                                }
                        )
                );
    }

    public static LiteralArgumentBuilder<CommandSourceStack> spawnCow(CommandBuildContext commandBuildContext) {
        return Commands.literal(MooFluids.MOD_ID)
                .requires(cmd -> cmd.hasPermission(2))
                .then(Commands.literal("create")
                        //TODO: Would be great if only was given fluids instead of all blocks.
                        .then(Commands.argument("fluid", BlockStateArgument.block(commandBuildContext))
                                .executes((stackCommandContext) -> {
                                    var level = stackCommandContext.getSource().getLevel();
                                    var input = stackCommandContext.getArgument("fluid", BlockInput.class);

                                    if (input.getState().getBlock() instanceof LiquidBlock liquidBlock) {
                                        if (MooFluidReg.exists(liquidBlock.getFluid())) {
                                            var entity = new MooFluidEntity(Setup.MOO_FLUID.get(), level);
                                            entity.setPos(stackCommandContext.getSource().getPosition());
                                            entity.setFluid(ForgeRegistries.FLUIDS.getKey(liquidBlock.getFluid()).toString());
                                            entity.setCustomName(entity.getFluidStack().getDisplayName());
                                            entity.finalizeSpawn(level, level.getCurrentDifficultyAt(entity.blockPosition()), MobSpawnType.COMMAND, null, null);

                                            level.addFreshEntity(entity);

                                            stackCommandContext.getSource().sendSuccess(Component.literal("Spawned MooFluidCow with " + entity.getFluidStack().getDisplayName().getString() + "."), false);
                                            return 1;
                                        }
                                    } else {
                                        stackCommandContext.getSource().sendFailure(Component.literal("Given block state isn't a valid fluid"));
                                        return 0;
                                    }
                                    return 0;
                                }))
                );
    }
}
