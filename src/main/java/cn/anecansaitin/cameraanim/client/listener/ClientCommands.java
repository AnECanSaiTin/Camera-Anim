package cn.anecansaitin.cameraanim.client.listener;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.ClientPaths;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterClientCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CameraAnim.MODID, value = Dist.CLIENT)
public class ClientCommands {
    @SubscribeEvent
    public static void register(RegisterClientCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(
                Commands.literal("cameraanim")
                        .then(Commands.literal("client")
                                .then(Commands.literal("reload")
                                        .executes(context -> {
                                            ClientPaths.loadAllFromFile();

                                            if (context.getSource().isPlayer()) {
                                                context.getSource().getPlayer().sendSystemMessage(Component.literal("Anim reloading down!"));
                                            }
                                            return 1;
                                        })))
        );
    }
}
