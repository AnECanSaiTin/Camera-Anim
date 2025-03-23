package cn.anecansaitin.cameraanim.client.gui.screen.radial;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public record RadialMenuOption(ItemStack itemStack, ResourceLocation texture, String description,
                               String leftClickDescription,
                               String rightClickDescription, String middleClickDescription,
                               Consumer<RadialMenuScreen> onLeftClick,
                               Consumer<RadialMenuScreen> onRightClick,
                               Consumer<RadialMenuScreen> onMiddleClick,
                               Consumer<RadialMenuScreen> onMouseHover) {

    public RadialMenuOption(ItemStack itemStack, ResourceLocation texture, String description,
                            String leftClickDescription, String rightClickDescription, String middleClickDescription,
                            Consumer<RadialMenuScreen> onLeftClick,
                            Consumer<RadialMenuScreen> onRightClick,
                            Consumer<RadialMenuScreen> onMiddleClick,
                            Consumer<RadialMenuScreen> onMouseHover) {
        this.itemStack = itemStack;
        this.texture = texture;
        this.description = description != null ? description : "";
        this.leftClickDescription = leftClickDescription;
        this.rightClickDescription = rightClickDescription;
        this.middleClickDescription = middleClickDescription;
        this.onLeftClick = onLeftClick != null ? onLeftClick : screen -> {
        };
        this.onRightClick = onRightClick != null ? onRightClick : screen -> {
        };
        this.onMiddleClick = onMiddleClick != null ? onMiddleClick : screen -> {
        };
        this.onMouseHover = onMouseHover != null ? onMouseHover : screen -> {
        };

        if (itemStack == null && texture == null) {
            throw new IllegalStateException("Either an ItemStack or a texture must be specified.");
        }
    }

    public static final class RadialMenuOptionBuilder {
        private ItemStack itemStack;
        private ResourceLocation texture;
        private String description;
        private String leftClickDescription;
        private String rightClickDescription;
        private String middleClickDescription;
        private Consumer<RadialMenuScreen> onLeftClick;
        private Consumer<RadialMenuScreen> onRightClick;
        private Consumer<RadialMenuScreen> onMiddleClick;
        private Consumer<RadialMenuScreen> onMouseHover;

        public RadialMenuOptionBuilder() {
        }

        public RadialMenuOptionBuilder itemStack(ItemStack itemStack) {
            this.itemStack = itemStack;
            return this;
        }

        public RadialMenuOptionBuilder texture(ResourceLocation texture) {
            this.texture = texture;
            return this;
        }

        public RadialMenuOptionBuilder description(String description) {
            this.description = description;
            return this;
        }

        public RadialMenuOptionBuilder leftClickDescription(String leftClickDescription) {
            this.leftClickDescription = leftClickDescription;
            return this;
        }

        public RadialMenuOptionBuilder rightClickDescription(String rightClickDescription) {
            this.rightClickDescription = rightClickDescription;
            return this;
        }

        public RadialMenuOptionBuilder middleClickDescription(String middleClickDescription) {
            this.middleClickDescription = middleClickDescription;
            return this;
        }

        public RadialMenuOptionBuilder onLeftClick(Consumer<RadialMenuScreen> onLeftClick) {
            this.onLeftClick = onLeftClick;
            return this;
        }

        public RadialMenuOptionBuilder onRightClick(Consumer<RadialMenuScreen> onRightClick) {
            this.onRightClick = onRightClick;
            return this;
        }

        public RadialMenuOptionBuilder onMiddleClick(Consumer<RadialMenuScreen> onMiddleClick) {
            this.onMiddleClick = onMiddleClick;
            return this;
        }

        public RadialMenuOptionBuilder onMouseHover(Consumer<RadialMenuScreen> onMouseHover) {
            this.onMouseHover = onMouseHover;
            return this;
        }

        public RadialMenuOption build() {
            return new RadialMenuOption(itemStack, texture, description, leftClickDescription, rightClickDescription, middleClickDescription, onLeftClick, onRightClick, onMiddleClick, onMouseHover);
        }
    }
}