package cn.anecansaitin.cameraanim.client.gui.screen;

import cn.anecansaitin.cameraanim.client.ide.CameraAnimIdeCache;
import cn.anecansaitin.cameraanim.client.gui.widget.NumberEditBox;
import cn.anecansaitin.cameraanim.client.network.ClientPayloadSender;
import cn.anecansaitin.cameraanim.client.util.ClientUtil;
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
    private static final Component PAGE = Component.translatable("gui.camera_anim.remote_path_search.page");
    private static final Component SEARCH = Component.translatable("gui.camera_anim.remote_path_search.search");
    private static final Component LOAD = Component.translatable("gui.camera_anim.remote_path_search.load");
    private static final Component LOAD_ID = Component.translatable("gui.camera_anim.remote_path_search.load_id");
    private static final Component SAVE = Component.translatable("gui.camera_anim.remote_path_search.save");
    private static final Component SAVE_ID = Component.translatable("gui.camera_anim.remote_path_search.save_id");
    private static final Component DELETE = Component.translatable("gui.camera_anim.remote_path_search.delete");
    private static final Component DELETE_ID = Component.translatable("gui.camera_anim.remote_path_search.delete_id");
    private static final Component LOCAL_MODE = Component.translatable("gui.camera_anim.remote_path_search.local_mode");
    private static final Component PATH_ID = Component.translatable("gui.camera_anim.remote_path_search.path_id");
    private static final Component MODIFIER = Component.translatable("gui.camera_anim.remote_path_search.modifier");
    private static final Component TIME = Component.translatable("gui.camera_anim.remote_path_search.time");
    private static final Component NO_SERVER = Component.translatable("gui.camera_anim.remote_path_search.no_server");
    private static final Component TIP = Component.translatable("gui.camera_anim.remote_path_search.tip");
    private static final Component PAGE_ERROR_POSITIVE = Component.translatable("gui.camera_anim.remote_path_search.page_error_positive");
    private static final Component PAGE_ERROR = Component.translatable("gui.camera_anim.remote_path_search.page_error");
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
        GlobalCameraPath currentPath = CameraAnimIdeCache.getPath();
        newId.setValue(currentPath != null ? currentPath.getId() : "");

        addRenderableWidget(new ExtendedButton(45, 20, 100, 20, SEARCH, b -> {
            if (REMOTE) {
                try {
                    int pageNumber = Integer.parseInt(page.getValue().trim());
                    if (pageNumber <= 0) {
                        ClientUtil.pushGuiLayer(new InfoScreen(PAGE_ERROR_POSITIVE));
                        return;
                    }
                    ClientPayloadSender.checkGlobalPath(Integer.parseInt(page.getValue()), 16);
                } catch (NumberFormatException e) {
                    ClientUtil.pushGuiLayer(new InfoScreen(Component.literal("Invalid page number")));
                }
            }
        }));

        addRenderableWidget(new ExtendedButton(150, 20, 50, 20, LOAD, b -> {
            if (REMOTE) {
                ClientPayloadSender.getGlobalPath(path.getValue(), 0);
            }
        }));

        addRenderableWidget(new ExtendedButton(260, 20, 50, 20, SAVE, b -> {
            if (REMOTE) {
                GlobalCameraPath track = currentPath;

                if (!track.getId().equals(newId.getValue())) {
                    track = track.resetID(newId.getValue());
                }

                if (track.isNativeMode()) {
                    track = track.toNative(CameraAnimIdeCache.getNativePos(), CameraAnimIdeCache.getNativeRot().y);
                }

                ClientPayloadSender.putGlobalPath(track);
            }
        }));

        addRenderableWidget(new ExtendedButton(260, 60, 50, 20, DELETE, b -> {
            if (REMOTE) {
                ClientPayloadSender.removeGlobalPath(removeId.getValue());
            }
        }));

        addRenderableWidget(new ExtendedButton(260, 80, 70, 20, LOCAL_MODE, b -> Minecraft.getInstance().setScreen(new LocalPathSearchScreen())));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(font, PAGE, 25, 10, 0xffffffff);
        guiGraphics.drawString(font, LOAD_ID, 215, 10, 0xffffffff);
        guiGraphics.drawString(font, SAVE_ID, 325, 10, 0xffffffff);
        guiGraphics.drawString(font, DELETE_ID, 325, 50, 0xffffffff);

        int headerY = 49;
        int columnStartX = 5;
        guiGraphics.drawScrollingString(font, PATH_ID, columnStartX + 5, columnStartX + 80, headerY, 0xffffffff);
        guiGraphics.drawScrollingString(font, MODIFIER, columnStartX + 85, columnStartX + 140, headerY, 0xffffffff);
        guiGraphics.drawScrollingString(font, TIME, columnStartX + 140, columnStartX + 300, headerY, 0xffffffff);

        if (!list.isEmpty()) {
            for (int i = 0, listSize = list.size(); i < listSize; i++) {
                Triplet<Component, Component, Component> info = list.get(i);
                int rowY = 70 + i * 10;

                guiGraphics.drawScrollingString(font, info.getA(), columnStartX + 5, columnStartX + 80, rowY, 0xffffffff);
                guiGraphics.drawScrollingString(font, info.getB(), columnStartX + 85, columnStartX + 140, rowY, 0xffffffff);
                guiGraphics.drawScrollingString(font, info.getC(), columnStartX + 140, columnStartX + 300, rowY, 0xffffffff);
            }
        } else {
            guiGraphics.drawCenteredString(font, TIP, width / 2, height / 2, 0xffffffff);
        }

        int tableWidth = 250;
        int tableHeight = list.size() * 10;
        int tableX = columnStartX;
        int tableY = 69;
        guiGraphics.hLine(tableX - 1, tableX + tableWidth, tableY - 1, 0xff95e1d3);
        guiGraphics.hLine(tableX - 1, tableX + tableWidth, tableY + tableHeight, 0xff95e1d3);
        guiGraphics.vLine(tableX - 1, tableY - 1, tableY + tableHeight, 0xff95e1d3);
        guiGraphics.vLine(tableX + tableWidth, tableY - 1, tableY + tableHeight, 0xff95e1d3);

        if (!REMOTE) {
            guiGraphics.drawCenteredString(font, NO_SERVER, 100, 100, 0xffE11414);
        }
    }

    public void setInfo(List<GlobalCameraPathInfo> list) {
        this.list.clear();

        for (GlobalCameraPathInfo info : list) {
            Component id = Component.literal(info.id());
            Player player = Minecraft.getInstance().level.getPlayerByUUID(info.lastModifier());
            Component playerName;

            if (player == null) {
                playerName = Component.literal("Null");
            } else {
                playerName = player.getDisplayName();
            }

            LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(info.version()), ZONE_ID);
            Component time = Component.literal(FORMATTER.format(localDateTime));
            this.list.add(new Triplet<>(id, playerName, time));
        }
    }
}
