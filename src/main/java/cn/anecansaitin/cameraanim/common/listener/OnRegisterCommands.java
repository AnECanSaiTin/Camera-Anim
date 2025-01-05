package cn.anecansaitin.cameraanim.common.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.common.GlobalCameraSavedData;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.network.ServerPayloadSender;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = CameraAnim.MODID)
public class OnRegisterCommands {
    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("cameraanim")
                        .then(Commands.literal("play")
                                .requires(source -> {
                                    if (source.source instanceof BaseCommandBlock) {
                                        return true;
                                    } else if (source.isPlayer()) {
                                        return source.getServer().getProfilePermissions(source.getPlayer().getGameProfile()) > 1;
                                    } else {
                                        return false;
                                    }
                                })
                                .then(Commands.argument("Target", EntityArgument.players())
                                        .then(Commands.argument("Anim Id", StringArgumentType.greedyString())
                                                .executes(context -> {
                                                    EntitySelector target = context.getArgument("Target", EntitySelector.class);
                                                    ServerPlayer player = target.findSinglePlayer(context.getSource());
                                                    String animId = context.getArgument("Anim Id", String.class);
                                                    GlobalCameraSavedData data = GlobalCameraSavedData.getData((ServerLevel) player.level());
                                                    GlobalCameraPath path = data.getPath(animId);

                                                    if (path == null) {
                                                        player.sendSystemMessage(Component.literal("动画" + animId + "不存在"));
                                                    } else {
                                                        ServerPayloadSender.sendGlobalPath(path, player, 1);
                                                    }

                                                    return 1;
                                                })
                                        ))
                        )

        );
    }
}