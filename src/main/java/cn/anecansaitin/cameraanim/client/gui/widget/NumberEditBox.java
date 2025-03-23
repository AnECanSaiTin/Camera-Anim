package cn.anecansaitin.cameraanim.client.gui.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;

public class NumberEditBox extends EditBox {
    private NumberEditBox(Font font, int x, int y, int width, int height, String defaultValue, Component message) {
        super(font, x, y, width, height, message);
        setValue(defaultValue);
    }

    public NumberEditBox(Font font, int x, int y, int width, int height, int defaultValue, Component message) {
        this(font, x, y, width, height, String.valueOf(defaultValue), message);
    }

    public NumberEditBox(Font font, int x, int y, int width, int height, float defaultValue, Component message) {
        this(font, x, y, width, height, String.format("%.2f", defaultValue), message);
    }

    @Override
    public void insertText(String textToWrite) {
        if (!textToWrite.matches("^-?\\d*[.,]?\\d*$")) {
            return;
        }

        super.insertText(textToWrite);
    }

    public float getFloatValue(float defaultValue) {
        try {
            String text = getValue().trim().replace(',', '.');
            if (text.isEmpty()) {
                return defaultValue;
            }
            return Float.parseFloat(text);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }
}
