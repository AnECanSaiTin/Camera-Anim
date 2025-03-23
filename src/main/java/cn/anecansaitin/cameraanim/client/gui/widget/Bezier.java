package cn.anecansaitin.cameraanim.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.joml.Math;
import org.joml.Vector2f;

public class Bezier extends AbstractWidget {
    private final Vector2f point;
    private final float zeroX, zeroY;

    public Bezier(Vector2f point, float x, float y) {
        super((int) (point.x * 80 + x - 2), (int) (point.y * -80 + y - 2), 5, 5, Component.literal("Bezier"));
        this.point = point;
        this.zeroX = x;
        this.zeroY = y;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawSpecial(b -> {
            PoseStack.Pose last = guiGraphics.pose().last();
            VertexConsumer buffer = b.getBuffer(RenderType.GUI);
            buffer.addVertex(last, point.x * 80 + zeroX + 2, point.y * -80 + zeroY + 2, 0).setColor(0xFFc4c4c4);
            buffer.addVertex(last, point.x * 80 + zeroX + 2, point.y * -80 + zeroY - 2, 0).setColor(0xFFc4c4c4);
            buffer.addVertex(last, point.x * 80 + zeroX - 2, point.y * -80 + zeroY - 2, 0).setColor(0xFFc4c4c4);
            buffer.addVertex(last, point.x * 80 + zeroX - 2, point.y * -80 + zeroY + 2, 0).setColor(0xFFc4c4c4);
        });
    }

    @Override
    protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
        point.add((float) dragX / 100, (float) -dragY / 100);
        point.x = Math.clamp(0, 1, point.x);
        update();
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    public void update() {
        setX((int) (point.x * 80 + zeroX - 2));
        setY((int) (point.y * -80 + zeroY - 2));
    }
}
