package cn.anecansaitin.cameraanim.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class InfoScreen extends Screen {
    private final Component info;

    public InfoScreen(Component info) {
        super(Component.literal("info"));
        this.info = info;
    }

    public InfoScreen(String info) {
        this(Component.literal(info));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int midY = height / 2;
        int midX = width / 2;
        guiGraphics.vLine(midX - 80, midY - 30, midY + 30, 0xffffffff);
        guiGraphics.vLine(midX + 80, midY - 30, midY + 30, 0xffffffff);
        guiGraphics.hLine(midX - 80, midX + 80, midY - 30, 0xffffffff);
        guiGraphics.hLine(midX - 80, midX + 80, midY + 30, 0xffffffff);
        guiGraphics.fill(midX - 79, midY - 29, midX + 80, midY + 30, 0x7fB8B8B8);
        guiGraphics.drawCenteredString(font, info, midX, midY, 0xffffffff);
    }
}
