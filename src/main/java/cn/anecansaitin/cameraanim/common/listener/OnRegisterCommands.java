package cn.anecansaitin.cameraanim.common.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.common.GlobalCameraSavedData;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.network.ServerPayloadSender;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = CameraAnim.MODID)
public class OnRegisterCommands {
    private static final Component PLAY_ANIM_FAILURE = Component.translatable("commands.cameraanim.play.failure");
    private static final Component PLAY_NATIVE_ANIM_FAILURE = Component.translatable("commands.cameraanim.play.native.failure");

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("cameraanim")
                        .then(Commands.literal("play")
                                .requires(source -> source.hasPermission(2))
                                .then(Commands.argument("Target", EntityArgument.players())
                                        .then(Commands.argument("Anim Id", StringArgumentType.string())
                                                .suggests(OnRegisterCommands::suggestAnimationIds)
                                                .executes(context -> {
                                                    EntitySelector target = context.getArgument("Target", EntitySelector.class);
                                                    List<ServerPlayer> players = target.findPlayers(context.getSource());

                                                    if (players.isEmpty()) {
                                                        return 0;
                                                    }

                                                    ServerPlayer player = players.getFirst();
                                                    String animId = context.getArgument("Anim Id", String.class);
                                                    GlobalCameraSavedData data = GlobalCameraSavedData.getData((ServerLevel) player.level());
                                                    GlobalCameraPath path = data.getPath(animId);

                                                    if (path == null) {
                                                        context.getSource().sendFailure(PLAY_ANIM_FAILURE.copy().append(animId));
                                                    }

                                                    for (ServerPlayer serverPlayer : players) {
                                                        ServerPayloadSender.sendGlobalPath(path, serverPlayer, 1);
                                                    }

                                                    return 1;
                                                })
                                                .then(Commands.argument("Center", EntityArgument.entity())
                                                        .executes(context -> {
                                                            EntitySelector target = context.getArgument("Target", EntitySelector.class);
                                                            List<ServerPlayer> players = target.findPlayers(context.getSource());

                                                            if (players.isEmpty()) {
                                                                return 0;
                                                            }

                                                            ServerPlayer player = players.getFirst();
                                                            String animId = context.getArgument("Anim Id", String.class);
                                                            GlobalCameraSavedData data = GlobalCameraSavedData.getData((ServerLevel) player.level());
                                                            GlobalCameraPath path = data.getPath(animId);

                                                            if (path == null) {
                                                                context.getSource().sendFailure(PLAY_ANIM_FAILURE.copy().append(animId));
                                                                return 0;
                                                            } else if (!path.isNativeMode()) {
                                                                context.getSource().sendFailure(PLAY_NATIVE_ANIM_FAILURE.copy().append(animId));
                                                                return 0;
                                                            }

                                                            EntitySelector center = context.getArgument("Center", EntitySelector.class);
                                                            Entity centerEntity = center.findSingleEntity(context.getSource());

                                                            for (ServerPlayer serverPlayer : players) {
                                                                ServerPayloadSender.sendNativePath(path, serverPlayer, centerEntity);
                                                            }

                                                            return 1;
                                                        }))
                                        ))
                        )

        );
    }

    private static CompletableFuture<Suggestions> suggestAnimationIds(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        ServerLevel level = context.getSource().getLevel();
        GlobalCameraSavedData storage = GlobalCameraSavedData.getData(level);
        storage.getPaths().forEach(path -> builder.suggest(path.getId()));
        return builder.buildFuture();
    }
}
