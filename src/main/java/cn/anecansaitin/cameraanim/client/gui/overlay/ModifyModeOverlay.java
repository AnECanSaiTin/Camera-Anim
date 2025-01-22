package cn.anecansaitin.cameraanim.client.gui.overlay;

import cn.anecansaitin.cameraanim.client.CameraAnimIdeCache;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static cn.anecansaitin.cameraanim.client.ClientUtil.font;
import static cn.anecansaitin.cameraanim.client.register.ModKeyMapping.*;

public class ModifyModeOverlay implements LayeredDraw.Layer {
    private static final MutableComponent OPEN = Component.translatable("hud.camera_anim.modify_mode.open");
    private static final MutableComponent CLOSE = Component.translatable("hud.camera_anim.modify_mode.close");
    private static final MutableComponent SELECT = Component.translatable("hud.camera_anim.modify_mode.select");
    private static final MutableComponent MOVE = Component.translatable("hud.camera_anim.modify_mode.move");
    private static final MutableComponent DRAG = Component.translatable("hud.camera_anim.modify_mode.drag");

    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        if (CameraAnimIdeCache.EDIT) {
            int i = 0;
            KeyMapping edit = EDIT_MODE.get();
            guiGraphics.drawString(font(), Component.translatable(edit.getName()).append(": ").append(edit.getTranslatedKeyMessage()).append(OPEN), 0, 10 * i++, 0xffffff);
            KeyMapping preview = PREVIEW_MODE.get();
            guiGraphics.drawString(font(), Component.translatable(preview.getName()).append(": ").append(preview.getTranslatedKeyMessage()).append(CameraAnimIdeCache.PREVIEW ? OPEN : CLOSE), 0, 10 * i++, 0xffffff);
            KeyMapping forward = FORWARD.get();
            guiGraphics.drawString(font(), Component.translatable(forward.getName()).append(": ").append(forward.getTranslatedKeyMessage()), 0, 10 * i++, 0xffffff);
            KeyMapping back = BACK.get();
            guiGraphics.drawString(font(), Component.translatable(back.getName()).append(": ").append(back.getTranslatedKeyMessage()), 0, 10 * i++, 0xffffff);
            KeyMapping reset = RESET.get();
            guiGraphics.drawString(font(), Component.translatable(reset.getName()).append(": ").append(reset.getTranslatedKeyMessage()), 0, 10 * i++, 0xffffff);
            KeyMapping play = PLAY.get();
            guiGraphics.drawString(font(), Component.translatable(play.getName()).append(": ").append(play.getTranslatedKeyMessage()), 0, 10 * i++, 0xffffff);
            KeyMapping add = ADD_GLOBAL_CAMERA_POINT.get();
            guiGraphics.drawString(font(), Component.translatable(add.getName()).append(": ").append(add.getTranslatedKeyMessage()), 0, 10 * i++, 0xffffff);
            KeyMapping clean = CLEAN.get();
            guiGraphics.drawString(font(), Component.translatable(clean.getName()).append(": ").append(clean.getTranslatedKeyMessage()), 0, 10 * i++, 0xffffff);
            KeyMapping set = POINT_SETTING.get();
            guiGraphics.drawString(font(), Component.translatable(set.getName()).append(": ").append(set.getTranslatedKeyMessage()), 0, 10 * i++, 0xffffff);
            KeyMapping time = SET_CAMERA_TIME.get();
            guiGraphics.drawString(font(), Component.translatable(time.getName()).append(": ").append(time.getTranslatedKeyMessage()), 0, 10 * i++, 0xffffff);
            InputConstants.Key mouseLeft = InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_LEFT);
            InputConstants.Key mouseRight = InputConstants.Type.MOUSE.getOrCreate(InputConstants.MOUSE_BUTTON_RIGHT);
            guiGraphics.drawString(font(), SELECT.copy().append(": ").append(mouseLeft.getDisplayName()), 0, 10 * i++, 0xffffff);
            guiGraphics.drawString(font(), MOVE.copy().append(": ").append(mouseLeft.getDisplayName()), 0, 10 * i++, 0xffffff);
            guiGraphics.drawString(font(), DRAG.copy().append(": ").append(mouseRight.getDisplayName()), 0, 10 * i++, 0xffffff);
            KeyMapping delete = DELETE_GLOBAL_CAMERA_POINT.get();
            guiGraphics.drawString(font(), Component.translatable(delete.getName()).append(": ").append(delete.getTranslatedKeyMessage()), 0, 10 * i++, 0xffffff);
        } else if (CameraAnimIdeCache.VIEW) {
            KeyMapping view = VIEW_MODE.get();
            guiGraphics.drawString(font(), Component.translatable(view.getName()).append(": ").append(view.getTranslatedKeyMessage()).append(OPEN), 0, 0, 0xffffff);
        }
    }
}
