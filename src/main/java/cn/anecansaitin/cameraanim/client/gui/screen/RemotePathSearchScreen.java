package cn.anecansaitin.cameraanim.client.gui.screen;

import cn.anecansaitin.cameraanim.client.PathCache;
import cn.anecansaitin.cameraanim.client.gui.widget.NumberEditBox;
import cn.anecansaitin.cameraanim.client.network.ClientPayloadSender;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.data_entity.GlobalCameraPathInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import oshi.util.tuples.Triplet;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RemotePathSearchScreen extends Screen {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    public static boolean REMOTE;

    private final List<Triplet<Component, Component, Component>> list = new ArrayList<>();

    public RemotePathSearchScreen() {
        super(Component.literal("remote path search"));
    }

    @Override
    protected void init() {
        NumberEditBox page = addRenderableWidget(new NumberEditBox(font, 20, 20, 20, 20, 1, Component.literal("page")));
        EditBox path = addRenderableWidget(new EditBox(font, 205, 20, 50, 20, Component.literal("path id")));
        EditBox newId = addRenderableWidget(new EditBox(font, 315, 20, 50, 20, Component.literal("new id")));
        EditBox removeId = addRenderableWidget(new EditBox(font, 315, 60, 50, 20, Component.literal("remove id")));
        newId.setValue(PathCache.getTrack().getId());
        addRenderableWidget(new ExtendedButton(45, 20, 100, 20, Component.literal("从服务端查询"), b -> {
            if (REMOTE) {
                ClientPayloadSender.checkGlobalPath(Integer.parseInt(page.getValue()), 16);
            }
        }));
        addRenderableWidget(new ExtendedButton(150, 20, 50, 20, Component.literal("加载"), b -> {
            if (REMOTE) {
                ClientPayloadSender.getGlobalPath(path.getValue());
            }
        }));
        addRenderableWidget(new ExtendedButton(260, 20, 50, 20, Component.literal("保存"), b -> {
            if (REMOTE) {
                GlobalCameraPath track = PathCache.getTrack();

                if (!track.getId().equals(newId.getValue())) {
                    track = track.resetID(newId.getValue());
                }

                ClientPayloadSender.putGlobalPath(track);
            }
        }));
        addRenderableWidget(new ExtendedButton(260, 60, 50, 20, Component.literal("删除"), b -> {
            if (REMOTE) {
                ClientPayloadSender.removeGlobalPath(removeId.getValue());
            }
        }));
        addRenderableWidget(new ExtendedButton(260, 80, 50, 20, Component.literal("本地模式"), b -> {
            Minecraft.getInstance().setScreen(new LocalPathSearchScreen());
        }));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(font, Component.literal("页"), 25, 10, 0xffffffff);
        guiGraphics.drawString(font, Component.literal("查询名"), 215, 10, 0xffffffff);
        guiGraphics.drawString(font, Component.literal("保存名"), 325, 10, 0xffffffff);
        guiGraphics.drawString(font, Component.literal("删除名"), 325, 50, 0xffffffff);
        guiGraphics.drawScrollingString(font, Component.literal("路径名"), 20, 50, 49, 0xffffffff);
        guiGraphics.drawScrollingString(font, Component.literal("编辑者"), 60, 90, 49, 0xffffffff);
        guiGraphics.drawScrollingString(font, Component.literal("编辑时间"), 100, 210, 49, 0xffffffff);

        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Triplet<Component, Component, Component> info = list.get(i);
            guiGraphics.drawScrollingString(font, info.getA(), 20, 50, 60 + i * 11, 0xffffffff);
            guiGraphics.drawScrollingString(font, info.getB(), 60, 90, 60 + i * 11, 0xffffffff);
            guiGraphics.drawScrollingString(font, info.getC(), 100, 210, 60 + i * 11, 0xffffffff);
        }

        if (!REMOTE) {
            guiGraphics.drawCenteredString(font, Component.literal("服务端未安装“相机大师”"), 100, 100, 0xffE11414);
        }
    }

    public void setInfo(List<GlobalCameraPathInfo> list) {
        this.list.clear();

        for (GlobalCameraPathInfo info : list) {
            Component id = Component.literal(info.id());
            Player player = Minecraft.getInstance().level.getPlayerByUUID(info.lastModifier());
            Component playerName;

            if (player == null) {
                playerName = Component.literal("未知");
            } else {
                playerName = player.getDisplayName();
            }

            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(info.version()), ZONE_ID);
            Component time = Component.literal(FORMATTER.format(localDateTime));
            this.list.add(new Triplet<>(id, playerName, time));
        }
    }
}
