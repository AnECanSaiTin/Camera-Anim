package cn.anecansaitin.cameraanim;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(CameraAnim.MODID)
public class CameraAnim {
    public static final String MODID = "camera_anim";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CameraAnim(IEventBus modEventBus, ModContainer modContainer) {

    }
}
