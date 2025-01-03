package cn.anecansaitin.cameraanim.client.gui.screen;

import cn.anecansaitin.cameraanim.client.ClientUtil;
import cn.anecansaitin.cameraanim.client.PathCache;
import cn.anecansaitin.cameraanim.client.gui.widget.NumberEditBox;
import cn.anecansaitin.cameraanim.client.network.ClientPayloadSender;
import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import cn.anecansaitin.cameraanim.common.data_entity.GlobalCameraPathInfo;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.client.gui.widget.ExtendedButton;
import oshi.util.tuples.Triplet;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeMap;

public class LocalPathSearchScreen extends Screen {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
    private static final ZoneId ZONE_ID = ZoneId.systemDefault();
    private final List<Triplet<Component, Component, Component>> list = new ArrayList<>();
    private static final Gson GSON = new Gson();
    private static final String SERIALIZER_VERSION = "1.0.0";

    public LocalPathSearchScreen() {
        super(Component.literal("local path search"));
    }

    @Override
    protected void init() {
        NumberEditBox page = addRenderableWidget(new NumberEditBox(font, 20, 20, 20, 20, 1, Component.literal("page")));
        EditBox path = addRenderableWidget(new EditBox(font, 205, 20, 50, 20, Component.literal("path id")));
        EditBox newId = addRenderableWidget(new EditBox(font, 315, 20, 50, 20, Component.literal("new id")));
        newId.setValue(PathCache.getTrack().getId());
        addRenderableWidget(new ExtendedButton(45, 20, 100, 20, Component.literal("从本地查询"), b -> searchFromFile(Integer.parseInt(page.getValue()), 16)));
        addRenderableWidget(new ExtendedButton(150, 20, 50, 20, Component.literal("加载"), b -> getFromFile(path.getValue())));
        addRenderableWidget(new ExtendedButton(260, 20, 50, 20, Component.literal("保存"), b -> saveToFile(newId.getValue())));
        addRenderableWidget(new ExtendedButton(260, 80, 50, 20, Component.literal("远程模式"), b -> Minecraft.getInstance().setScreen(new RemotePathSearchScreen())));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawString(font, Component.literal("页"), 25, 10, 0xffffffff);
        guiGraphics.drawString(font, Component.literal("查询名"), 215, 10, 0xffffffff);
        guiGraphics.drawString(font, Component.literal("保存名"), 325, 10, 0xffffffff);
        guiGraphics.drawScrollingString(font, Component.literal("路径名"), 20, 50, 49, 0xffffffff);
        guiGraphics.drawScrollingString(font, Component.literal("编辑者"), 60, 90, 49, 0xffffffff);
        guiGraphics.drawScrollingString(font, Component.literal("编辑时间"), 100, 210, 49, 0xffffffff);

        for (int i = 0, listSize = list.size(); i < listSize; i++) {
            Triplet<Component, Component, Component> info = list.get(i);
            guiGraphics.drawScrollingString(font, info.getA(), 20, 50, 60 + i * 11, 0xffffffff);
            guiGraphics.drawScrollingString(font, info.getB(), 60, 90, 60 + i * 11, 0xffffffff);
            guiGraphics.drawScrollingString(font, info.getC(), 100, 210, 60 + i * 11, 0xffffffff);
        }
    }

    private void searchFromFile(int page, int size) {
        Path path = FMLPaths.GAMEDIR.get().resolve("camera-anim");
        list.clear();
        int start = (page - 1) * size;

        if (path.toFile().exists()) {
            try {
                Files.walkFileTree(path, new SimpleFileVisitor<>() {
                    private int count = 0;

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        String name = file.getFileName().toString();

                        if (name.endsWith(".json") && count++ >= start) {
                            list.add(new Triplet<>(Component.literal(name.substring(0, name.length() - 5)),
                                    Component.literal("本地文件"),
                                    Component.literal(FORMATTER.format(LocalDateTime.ofInstant(Instant.ofEpochMilli(attrs.lastModifiedTime().toMillis()), ZONE_ID)))));
                        }

                        if (list.size() >= size) {
                            return FileVisitResult.TERMINATE;
                        } else {
                            return FileVisitResult.CONTINUE;
                        }
                    }
                });
            } catch (IOException e) {
                ClientUtil.pushGuiLayer(new InfoScreen("读取文件失败"));
            }
        }
    }

    private void getFromFile(String id) {
        Path path = FMLPaths.GAMEDIR.get().resolve("camera-anim").resolve(id + ".json");
        File file = path.toFile();

        if (file.exists()) {
            try {
                String json = Files.readString(path);
                JsonObject jsonObject = GSON.fromJson(json, JsonObject.class);

                if (jsonObject.has("version") && jsonObject.get("version").getAsString().equals(SERIALIZER_VERSION)) {
                    TypeToken<TreeMap<Integer, CameraKeyframe>> type = new TypeToken<>(){};
                    TreeMap<Integer, CameraKeyframe> map = GSON.fromJson(jsonObject.get("anim"), type.getType());
                    PathCache.setTrack(new GlobalCameraPath(map, id));
                } else {
                    ClientUtil.pushGuiLayer(new InfoScreen("动画版本错误，当前版本不兼容！"));
                }
            } catch (IOException e) {
                ClientUtil.pushGuiLayer(new InfoScreen("读取文件失败"));
            } catch (JsonSyntaxException | NullPointerException e) {
                ClientUtil.pushGuiLayer(new InfoScreen("文件格式错误，无法加载为动画！"));
            }
        } else {
            ClientUtil.pushGuiLayer(new InfoScreen("文件不存在"));
        }
    }

    private void saveToFile(String id) {
        Path path = FMLPaths.GAMEDIR.get().resolve("camera-anim").resolve(id + ".json");
        try {
            JsonObject json = GSON.fromJson(PathCache.getTrack().toJsonString(GSON), JsonObject.class);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("version", SERIALIZER_VERSION);
            jsonObject.add("anim", json);
            Files.writeString(path, jsonObject.toString());
        } catch (IOException e) {
            ClientUtil.pushGuiLayer(new InfoScreen("文件保存失败"));
        }
    }
}
