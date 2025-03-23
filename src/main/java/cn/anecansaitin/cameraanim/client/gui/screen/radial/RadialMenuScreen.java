package cn.anecansaitin.cameraanim.client.gui.screen.radial;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.util.ClientUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class RadialMenuScreen extends Screen {
    private final float innerRadius;
    private final float outerRadius;
    private final float gapDistance;
    private final int baseColor;
    private final int gradientInnerColor;
    private final int gradientOuterColor;
    private final int maxOptions;
    private final int minOptions;

    private final List<RadialMenuOption> options;
    private int currentPage = 0;
    private int selectedIndex = -1;
    private Vector2f center;
    private final List<Quadrilateral> quads = new ArrayList<>();
    private int currentOptions;

    private int hoverIndex = -1;
    private long hoverStartTime = 0;
    private static final long HOVER_DELAY_MS = 1000;
    private boolean controlPressed = false;
    private boolean shiftPressed = false;
    private boolean altPressed = false;

    @Nullable
    private final Screen prevScreen;

    public RadialMenuScreen(@Nullable Screen prevScreen, List<RadialMenuOption> options, float innerRadius, float outerRadius, float gapDistance,
                            int baseColor, int gradientInnerColor, int gradientOuterColor,
                            int maxOptions, int minOptions) {
        super(Component.literal("Radial Menu"));
        this.prevScreen = prevScreen;
        this.options = options;
        this.innerRadius = innerRadius;
        this.outerRadius = outerRadius;
        this.gapDistance = gapDistance;
        this.baseColor = baseColor;
        this.gradientInnerColor = gradientInnerColor;
        this.gradientOuterColor = gradientOuterColor;
        this.maxOptions = maxOptions;
        this.minOptions = minOptions;
    }

    public RadialMenuScreen() {
        this(null, initializeTestOptions(), 30f, 120f, 2f, 0x80000000, 0x80FFFF00, 0x80000000, 8, 3);
    }

    private static List<RadialMenuOption> initializeTestOptions() {
        List<RadialMenuOption> options = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int index = i;
            options.add(new RadialMenuOption.RadialMenuOptionBuilder()
                    .itemStack(new ItemStack(getItemForIndex(index)))
                    .description("Option " + (index + 1) + " description\nThis is a second line\nAnd a third line!")
                    .leftClickDescription("Select Option " + (index + 1) + "\nAdditional details here")
                    .rightClickDescription("Inspect Option " + (index + 1) + "\nMore info on this action")
                    .middleClickDescription("Remove Option " + (index + 1) + "\nConfirm removal")
                    .onLeftClick(screen -> CameraAnim.LOGGER.info("Left click on option {}", (index + 1)))
                    .onRightClick(screen -> CameraAnim.LOGGER.info("Right click on option {}", (index + 1)))
                    .onMiddleClick(screen -> CameraAnim.LOGGER.info("Middle click on option {}", (index + 1)))
                    //.onMouseHover(screen -> CinematicCam.LOGGER.info("Mouse hovered on option {}", (index + 1)))
                    .build());
        }
        return options;
    }

    private static Item getItemForIndex(int index) {
        return switch (index % 8) {
            case 0 -> Items.DIAMOND_SWORD;
            case 1 -> Items.DIAMOND_PICKAXE;
            case 2 -> Items.PURPLE_BED;
            case 3 -> Items.POTION;
            case 4 -> Items.STICK;
            case 5 -> Items.APPLE;
            case 6 -> Items.BOW;
            case 7 -> Items.SHIELD;
            default -> Items.AIR;
        };
    }

    @Override
    protected void init() {
        center = new Vector2f((float) width / 2, (float) height / 2);
        currentOptions = Math.clamp(options.size(), minOptions, maxOptions);
        quads.clear();
        quads.addAll(calculateQuadrilaterals(center.x, center.y, innerRadius, outerRadius, currentOptions, gapDistance));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        super.render(graphics, mouseX, mouseY, delta);

        List<RadialMenuOption> currentPageOptions = getPage(options, currentPage);

        drawRadialSegments(graphics, mouseX, mouseY);

        drawOptions(graphics, currentPageOptions);

        drawPageIndicator(graphics);

        graphics.renderItem(Items.BARRIER.getDefaultInstance(), (int) (center.x - 8), (int) (center.y - 8));

        updateTooltip(graphics, mouseX, mouseY, currentPageOptions);
    }

    private void updateTooltip(GuiGraphics graphics, int mouseX, int mouseY, List<RadialMenuOption> currentPageOptions) {
        int newHoverIndex = -1;
        for (int i = 0; i < currentPageOptions.size(); i++) {
            Quadrilateral quad = quads.get(i);
            if (isMouseOverQuad(mouseX, mouseY, quad)) {
                newHoverIndex = i;
                break;
            }
        }

        if (newHoverIndex != hoverIndex) {
            hoverIndex = newHoverIndex;
            hoverStartTime = System.currentTimeMillis();
        }

        if (hoverIndex != -1 && (System.currentTimeMillis() - hoverStartTime) >= HOVER_DELAY_MS) {
            RadialMenuOption option = currentPageOptions.get(hoverIndex);
            List<Component> tooltipLines = getTooltipLines(option);
            graphics.renderComponentTooltip(ClientUtil.font(), tooltipLines, mouseX, mouseY);
        }
    }

    private List<RadialMenuOption> getPage(List<RadialMenuOption> allOptions, int pageIndex) {
        int pageSize = Math.clamp(currentOptions, 0, allOptions.size());
        int startIndex = pageIndex * pageSize;
        int endIndex = Math.clamp((long) startIndex + pageSize, 0, allOptions.size());
        if (startIndex >= endIndex) return new ArrayList<>();
        return allOptions.subList(startIndex, endIndex);
    }

    private void drawRadialSegments(GuiGraphics graphics, int mouseX, int mouseY) {
        int numOptions = Math.clamp(currentOptions, 0, options.size());
        selectedIndex = -1;

        MultiBufferSource.BufferSource vertexConsumers = Minecraft.getInstance().renderBuffers().bufferSource();

        for (int i = 0; i < numOptions; i++) {
            Quadrilateral quad = quads.get(i);
            if (isMouseOverQuad(mouseX, mouseY, quad)) {
                selectedIndex = i;
                renderQuadWithGradient(graphics, quad, gradientInnerColor, gradientOuterColor);
                // Ejecutar onMouseHover cuando el mouse está sobre el slot
                List<RadialMenuOption> currentPageOptions = getPage(options, currentPage);
                RadialMenuOption option = currentPageOptions.get(i);
                if (option.onMouseHover() != null) {
                    option.onMouseHover().accept(this);
                }
            } else {
                renderQuad(graphics, quad, baseColor);
            }
        }

        vertexConsumers.endBatch();
    }

    private void renderQuad(GuiGraphics graphics, Quadrilateral quad, int color) {
        PoseStack poseStack = graphics.pose();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.GUI);

        int a = (color >> 24) & 0xFF;
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;

        Matrix4f matrix4f = poseStack.last().pose();
        
        buffer.addVertex(matrix4f, quad.p1().x, quad.p1().y, 0.0f)
                .setColor(r, g, b, a);

        buffer.addVertex(matrix4f, quad.p2().x, quad.p2().y, 0.0f)
                .setColor(r, g, b, a);

        buffer.addVertex(matrix4f, quad.p3().x, quad.p3().y, 0.0f)
                .setColor(r, g, b, a);

        buffer.addVertex(matrix4f, quad.p4().x, quad.p4().y, 0.0f)
                .setColor(r, g, b, a);
    }

    private void renderQuadWithGradient(GuiGraphics graphics, Quadrilateral quad, int colorInner, int colorOuter) {
        PoseStack poseStack = graphics.pose();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.GUI);

        int aInner = (colorInner >> 24) & 0xFF;
        int rInner = (colorInner >> 16) & 0xFF;
        int gInner = (colorInner >> 8) & 0xFF;
        int bInner = colorInner & 0xFF;
        int aOuter = (colorOuter >> 24) & 0xFF;
        int rOuter = (colorOuter >> 16) & 0xFF;
        int gOuter = (colorOuter >> 8) & 0xFF;
        int bOuter = colorOuter & 0xFF;

        float distP1 = quad.p1().distance(center);
        float distP2 = quad.p2().distance(center);
        float distP3 = quad.p3().distance(center);
        float distP4 = quad.p4().distance(center);
        float maxDist = outerRadius;

        int[] colorP1 = interpolateColor(distP1, maxDist, rInner, gInner, bInner, rOuter, gOuter, bOuter, aInner, aOuter);
        int[] colorP2 = interpolateColor(distP2, maxDist, rInner, gInner, bInner, rOuter, gOuter, bOuter, aInner, aOuter);
        int[] colorP3 = interpolateColor(distP3, maxDist, rInner, gInner, bInner, rOuter, gOuter, bOuter, aInner, aOuter);
        int[] colorP4 = interpolateColor(distP4, maxDist, rInner, gInner, bInner, rOuter, gOuter, bOuter, aInner, aOuter);

        Matrix4f matrix4f = poseStack.last().pose();
        buffer.addVertex(matrix4f, quad.p1().x, quad.p1().y, 0.0f)
                .setColor(colorP1[0], colorP1[1], colorP1[2], colorP1[3]);

        buffer.addVertex(matrix4f, quad.p2().x, quad.p2().y, 0.0f)
                .setColor(colorP2[0], colorP2[1], colorP2[2], colorP2[3]);

        buffer.addVertex(matrix4f, quad.p3().x, quad.p3().y, 0.0f)
                .setColor(colorP3[0], colorP3[1], colorP3[2], colorP3[3]);

        buffer.addVertex(matrix4f, quad.p4().x, quad.p4().y, 0.0f)
                .setColor(colorP4[0], colorP4[1], colorP4[2], colorP4[3]);
    }

    private int[] interpolateColor(float dist, float maxDist, int rInner, int gInner, int bInner, int rOuter, int gOuter, int bOuter, int aInner, int aOuter) {
        float t = Math.clamp(dist / maxDist, 0.0f, 1.0f);
        int r = (int) (rInner + (rOuter - rInner) * t);
        int g = (int) (gInner + (gOuter - gInner) * t);
        int b = (int) (bInner + (bOuter - bInner) * t);
        int a = (int) (aInner + (aOuter - aInner) * t);
        return new int[]{r, g, b, a};
    }

    private void drawOptions(GuiGraphics matrices, List<RadialMenuOption> pageOptions) {
        for (int i = 0; i < pageOptions.size(); i++) {
            Quadrilateral quad = quads.get(i);
            float centerX = (quad.p1().x + quad.p2().x + quad.p3().x + quad.p4().x) / 4;
            float centerY = (quad.p1().y + quad.p2().y + quad.p3().y + quad.p4().y) / 4;
            Vector2f position = new Vector2f(centerX, centerY);
            drawOption(matrices, position.x, position.y, pageOptions.get(i));
        }
    }

    private void drawOption(GuiGraphics graphics, float x, float y, RadialMenuOption option) {
        if (option.texture() != null) {
            graphics.blitInscribed(option.texture(), (int) (x - 8), (int) (y - 8), 0, 0, 16, 16);
        } else if (option.itemStack() != null) {
            graphics.renderItem(option.itemStack(), (int) (x - 8), (int) (y - 8));
        }
    }

    private void drawPageIndicator(GuiGraphics graphics) {
        int maxPage = (options.size() + currentOptions - 1) / currentOptions - 1;
        if (maxPage > 0) {
            float boxX = center.x - 10;
            float boxY = center.y + 15;
            float boxWidth = 20;
            float boxHeight = 2;
            float widthPerPage = (boxWidth - maxPage + 1) / (maxPage + 1);

            for (int i = 0; i <= maxPage; i++) {
                float x = boxX + i * (widthPerPage + 1);
                graphics.fill((int) x, (int) boxY, (int) (x + widthPerPage), (int) (boxY + boxHeight),
                        i == currentPage ? 0xFFFFFFFF : 0x59FFFFFF);
            }
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        int maxPage = (options.size() + currentOptions - 1) / currentOptions - 1;
        currentPage = Math.clamp(currentPage - (long) vertical, 0, maxPage);
        selectedIndex = -1;
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (selectedIndex >= 0) {
            List<RadialMenuOption> currentPageOptions = getPage(options, currentPage);
            RadialMenuOption selectedOption = currentPageOptions.get(selectedIndex);

            switch (button) {
                case 0:
                    if (selectedOption.onLeftClick() != null) {
                        selectedOption.onLeftClick().accept(this);
                    }
                    break;
                case 1:
                    if (selectedOption.onRightClick() != null) {
                        selectedOption.onRightClick().accept(this);
                    }
                    break;
                case 2:
                    if (selectedOption.onMiddleClick() != null) {
                        selectedOption.onMiddleClick().accept(this);
                    }
                    break;
                default:
                    return false;
            }
            return true;
        }

        if (isMouseOverCenter((int) mouseX, (int) mouseY)) {
            this.onClose();
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    private boolean isMouseOverQuad(int mouseX, int mouseY, Quadrilateral quad) {
        Vector2f mouse = new Vector2f(mouseX, mouseY);
        float distanceToCenter = mouse.distance(center);
        return quad.contains(mouse) && distanceToCenter >= innerRadius && distanceToCenter <= outerRadius;
    }

    private boolean isMouseOverCenter(int mouseX, int mouseY) {
        Vector2f mouse = new Vector2f(mouseX, mouseY);
        return mouse.distance(center) <= innerRadius;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private List<Quadrilateral> calculateQuadrilaterals(float centerX, float centerY, float rInner, float rOuter, int options, float gapDistance) {
        if (options < minOptions)
            throw new IllegalArgumentException("A minimum of " + minOptions + " options is required.");

        List<Quadrilateral> quadrilaterals = new ArrayList<>();
        float angleStep = (float) (2 * Math.PI / options);
        float startAngle = -angleStep - (angleStep / 2);
        float endAngle = startAngle + angleStep;

        for (int i = 0; i < options; i++) {
            Vector2f p1 = calculateNormalLinePoints(centerX, centerY, endAngle, rInner, gapDistance, false);
            Vector2f p2 = calculateNormalLinePoints(centerX, centerY, endAngle, rOuter, gapDistance, false);
            Vector2f p3 = calculateNormalLinePoints(centerX, centerY, startAngle, rOuter, gapDistance, true);
            Vector2f p4 = calculateNormalLinePoints(centerX, centerY, startAngle, rInner, gapDistance, true);

            quadrilaterals.add(new Quadrilateral(p1, p2, p3, p4));
            startAngle += angleStep;
            endAngle += angleStep;
        }
        return quadrilaterals;
    }

    private Vector2f calculateNormalLinePoints(float centerX, float centerY, float angle, float r, float length, boolean up) {
        float rPointX = centerX + r * (float) Math.cos(angle);
        float rPointY = centerY + r * (float) Math.sin(angle);
        float halfLength = length / 2;
        float perpX = -(float) Math.sin(angle);
        float perpY = (float) Math.cos(angle);

        if (up) {
            return new Vector2f(rPointX + halfLength * perpX, rPointY + halfLength * perpY);
        } else {
            return new Vector2f(rPointX - halfLength * perpX, rPointY - halfLength * perpY);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            controlPressed = true;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            shiftPressed = true;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            altPressed = true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            controlPressed = false;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            shiftPressed = false;
        } else if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            altPressed = false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    private List<Component> getTooltipLines(RadialMenuOption option) {
        List<Component> lines = new ArrayList<>();

        String[] descriptionLines = option.description().split("\n");
        for (String line : descriptionLines) {
            lines.add(Component.literal(line));
        }

        lines.add(Component.literal(""));
        lClickTooltip(option, lines);
        rClickTooltip(option, lines);
        mClickTooltip(option, lines);

        return lines;
    }

    private void mClickTooltip(RadialMenuOption option, List<Component> lines) {
        if (option.middleClickDescription() != null) {
            if (!altPressed && !shiftPressed && !controlPressed) {
                lines.add(Component.literal("Alt: ").withStyle(ChatFormatting.YELLOW).append(Component.literal("M-Click info").withStyle(ChatFormatting.GRAY)));
            }
            if (altPressed) {
                lines.clear();
                String[] middleLines = option.middleClickDescription().split("\n");
                for (String line : middleLines) {
                    lines.add(Component.literal(line));
                }
            }
        }
    }

    private void rClickTooltip(RadialMenuOption option, List<Component> lines) {
        if (option.rightClickDescription() != null) {
            if (!altPressed && !shiftPressed && !controlPressed) {
                lines.add(Component.literal("Shift: ").withStyle(ChatFormatting.YELLOW).append(Component.literal("R-Click info").withStyle(ChatFormatting.GRAY)));
            }
            if (shiftPressed) {
                lines.clear();
                String[] rightLines = option.rightClickDescription().split("\n");
                for (String line : rightLines) {
                    lines.add(Component.literal(line));
                }
            }
        }
    }

    private void lClickTooltip(RadialMenuOption option, List<Component> lines) {
        if (option.leftClickDescription() != null) {
            if (!altPressed && !shiftPressed && !controlPressed) {
                lines.add(Component.literal("Ctrl: ").withStyle(ChatFormatting.YELLOW).append(Component.literal("L-Click info").withStyle(ChatFormatting.GRAY)));
            }
            if (controlPressed) {
                lines.clear();
                String[] leftLines = option.leftClickDescription().split("\n");
                for (String line : leftLines) {
                    lines.add(Component.literal(line));
                }
            }
        }
    }

    @Override
    public void onClose() {
        if (prevScreen != null) {
            minecraft.setScreen(prevScreen);
        } else {
            super.onClose();
        }
    }
}