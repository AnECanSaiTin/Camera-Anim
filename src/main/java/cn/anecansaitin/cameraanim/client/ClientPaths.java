package cn.anecansaitin.cameraanim.client;

import cn.anecansaitin.cameraanim.client.gui.screen.InfoScreen;
import cn.anecansaitin.cameraanim.common.animation.CameraKeyframe;
import cn.anecansaitin.cameraanim.common.animation.GlobalCameraPath;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import net.minecraftforge.fml.loading.FMLPaths;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.TreeMap;

public class ClientPaths {
    private static final Gson GSON = new Gson();
    public static final HashMap<String, GlobalCameraPath> PATHS = new HashMap<>();
    private static final SimpleFileVisitor<Path> fileVisitor = new SimpleFileVisitor<>() {
        @Override
        public @NotNull FileVisitResult visitFile(@NotNull Path file, @NotNull BasicFileAttributes attrs) throws IOException {
            String fileName = file.getFileName().toString();

            if (fileName.endsWith(".json")) {
                try {
                    String content = new String(Files.readAllBytes(file));
                    JsonObject jsonObject = GSON.fromJson(content, JsonObject.class);
                    TypeToken<TreeMap<Integer, CameraKeyframe>> type = new TypeToken<>() {
                    };
                    TreeMap<Integer, CameraKeyframe> map = GSON.fromJson(jsonObject.get("anim"), type.getType());
                    GlobalCameraPath anim = new GlobalCameraPath(map, fileName.replace(".json", ""));

                    if (jsonObject.has("native") && jsonObject.get("native").getAsBoolean()) {
                        anim.setNativeMode(true);
                    }

                    PATHS.put(anim.getId(), anim);
                } catch (IOException | JsonSyntaxException e) {

                }
            }

            return FileVisitResult.CONTINUE;
        }
    };

    public static void loadAllFromFile() {
        Path path = FMLPaths.GAMEDIR.get().resolve("camera-anim");
        File file = path.toFile();

        if (!file.isDirectory()) {
            return;
        }

        try {
            Files.walkFileTree(path, fileVisitor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void play(String id) {
        GlobalCameraPath anim = PATHS.get(id);

        if (anim == null) {
            ClientUtil.pushGuiLayer(new InfoScreen("Anim \"" + id + "\" not found"));
            return;
        }

        if (anim.isNativeMode()) {
            ClientUtil.pushGuiLayer(new InfoScreen("The anim \"" + id + "\" is native mode"));
            return;
        }

        Animator.INSTANCE.setPathAndPlay(anim);
        ClientUtil.toThirdView();
    }

    public static void play(String id, Vector3f center, float yRot) {
        GlobalCameraPath anim = PATHS.get(id);

        if (anim == null) {
            ClientUtil.pushGuiLayer(new InfoScreen("Anim \"" + id + "\" not found"));
            return;
        }

        if (!anim.isNativeMode()) {
            ClientUtil.pushGuiLayer(new InfoScreen("The anim \"" + id + "\" is not native mode"));
            return;
        }

        Animator.INSTANCE.setPathAndPlay(anim, center, new Vector3f(0, yRot, 0));
        ClientUtil.toThirdView();
    }
}
