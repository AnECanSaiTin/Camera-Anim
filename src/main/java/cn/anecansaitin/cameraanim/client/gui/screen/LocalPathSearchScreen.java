package cn.anecansaitin.cameraanim.client.gui.screen;

import cn.anecansaitin.cameraanim.client.ide.CameraAnimIdeCache;
import cn.anecansaitin.cameraanim.client.util.ClientUtil;
import cn.anecansaitin.cameraanim.client.gui.widget.NumberEditBox;
import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
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
    private static final Component PAGE = Component.translatable("gui.camera_anim.local_path_search.page");
    private static final Component SEARCH = Component.translatable("gui.camera_anim.local_path_search.search");
    private static final Component LOAD = Component.translatable("gui.camera_anim.local_path_search.load");
    private static final Component LOAD_ID = Component.translatable("gui.camera_anim.local_path_search.load_id");
    private static final Component SAVE = Component.translatable("gui.camera_anim.local_path_search.save");
    private static final Component SAVE_ID = Component.translatable("gui.camera_anim.local_path_search.save_id");
    private static final Component DELETE = Component.translatable("gui.camera_anim.local_path_search.delete");
    private static final Component DELETE_ID = Component.translatable("gui.camera_anim.local_path_search.delete_id");
    private static final Component REMOTE_MODE = Component.translatable("gui.camera_anim.local_path_search.remote_mode");
    private static final Component PATH_ID = Component.translatable("gui.camera_anim.local_path_search.path_id");
    private static final Component MODIFIER = Component.translatable("gui.camera_anim.local_path_search.modifier");
    private static final Component TIME = Component.translatable("gui.camera_anim.local_path_search.time");
    private static final Component TIP = Component.translatable("gui.camera_anim.local_path_search.tip");
    private static final Component LOCAL_FILE = Component.translatable("gui.camera_anim.local_path_search.local_file");
    private static final Component LOAD_ERROR = Component.translatable("gui.camera_anim.local_path_search.load_error");
    private static final Component VERSION_ERROR = Component.translatable("gui.camera_anim.local_path_search.version_error");
    private static final Component FILE_LOAD_ERROR = Component.translatable("gui.camera_anim.local_path_search.file_load_error");
    private static final Component FILE_FORMAT_ERROR = Component.translatable("gui.camera_anim.local_path_search.file_format_error");
    private static final Component FILE_EXIST_ERROR = Component.translatable("gui.camera_anim.local_path_search.file_exist_error");
    private static final Component FILE_SAVE_ERROR = Component.translatable("gui.camera_anim.local_path_search.file_save_error");
    private static final Component FILE_LOAD_SUCCESS = Component.translatable("gui.camera_anim.local_path_search.file_load_success");
    private static final Component FILE_DELETE_SUCCESS = Component.translatable("gui.camera_anim.local_path_search.file_delete_success");
    private static final Component FILE_DELETE_ERROR = Component.translatable("gui.camera_anim.local_path_search.file_delete_error");

    public LocalPathSearchScreen() {
        super(Component.literal("local path search"));
    }

    @Override
    protected void init() {
        NumberEditBox page = addRenderableWidget(new NumberEditBox(font, 20, 20, 20, 20, 1, Component.literal("page")));
        EditBox path = addRenderableWidget(new EditBox(font, 205, 20, 50, 20, Component.literal("path id")));
        EditBox newId = addRenderableWidget(new EditBox(font, 315, 20, 50, 20, Component.literal("new id")));
        EditBox removeId = addRenderableWidget(new EditBox(font, 315, 60, 50, 20, Component.literal("remove id")));

        newId.setValue(CameraAnimIdeCache.getPath().getId());
        addRenderableWidget(new ExtendedButton(45, 20, 100, 20, SEARCH, b -> searchFromFile(Integer.parseInt(page.getValue()), 16)));
        addRenderableWidget(new ExtendedButton(150, 20, 50, 20, LOAD, b -> getFromFile(path.getValue())));
        addRenderableWidget(new ExtendedButton(260, 20, 50, 20, SAVE, b -> saveToFile(newId.getValue())));
        addRenderableWidget(new ExtendedButton(260, 60, 50, 20, DELETE, b -> {
            String removeIdText = removeId.getValue().trim();

            if (!removeIdText.isEmpty()) {
                deleteFromFile(removeIdText);
            } else {
                ClientUtil.pushGuiLayer(new InfoScreen(Component.literal("Remove ID cannot be empty")));
            }
        }));
        addRenderableWidget(new ExtendedButton(260, 80, 70, 20, REMOTE_MODE, b -> Minecraft.getInstance().setScreen(new RemotePathSearchScreen())));
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
                                    LOCAL_FILE,
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
                ClientUtil.pushGuiLayer(new InfoScreen(LOAD_ERROR));
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
                    TypeToken<TreeMap<Integer, CameraKeyframe>> type = new TypeToken<>() {
                    };
                    TreeMap<Integer, CameraKeyframe> map = GSON.fromJson(jsonObject.get("anim"), type.getType());
                    CameraAnimIdeCache.setPath(new GlobalCameraPath(map, id));
                    ClientUtil.pushGuiLayer(new InfoScreen(FILE_LOAD_SUCCESS));
                } else {
                    ClientUtil.pushGuiLayer(new InfoScreen(VERSION_ERROR));
                }
            } catch (IOException e) {
                ClientUtil.pushGuiLayer(new InfoScreen(FILE_LOAD_ERROR));
            } catch (JsonSyntaxException | NullPointerException e) {
                ClientUtil.pushGuiLayer(new InfoScreen(FILE_FORMAT_ERROR));
            }
        } else {
            ClientUtil.pushGuiLayer(new InfoScreen(FILE_EXIST_ERROR));
        }
    }

    private void saveToFile(String id) {
        Path path = FMLPaths.GAMEDIR.get().resolve("camera-anim").resolve(id + ".json");
        try {
            JsonObject json = GSON.fromJson(CameraAnimIdeCache.getPath().toJsonString(GSON), JsonObject.class);
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("version", SERIALIZER_VERSION);
            jsonObject.add("anim", json);
            Files.writeString(path, jsonObject.toString());
        } catch (IOException e) {
            ClientUtil.pushGuiLayer(new InfoScreen(FILE_SAVE_ERROR));
        }
    }

    private void deleteFromFile(String id) {
        try {
            Path filePath = FMLPaths.GAMEDIR.get().resolve("camera-anim").resolve(id + ".json");
            if (Files.exists(filePath)) {
                Files.delete(filePath);
                list.clear();
                searchFromFile(1, 16);
                ClientUtil.pushGuiLayer(new InfoScreen(FILE_DELETE_SUCCESS));
            } else {
                ClientUtil.pushGuiLayer(new InfoScreen(FILE_EXIST_ERROR));
            }
        } catch (IOException e) {
            ClientUtil.pushGuiLayer(new InfoScreen(FILE_DELETE_ERROR));
        }
    }
}
