package cn.anecansaitin.cameraanim;

import cn.anecansaitin.cameraanim.common.ModNetwork;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

@Mod(CameraAnim.MODID)
public class CameraAnim {
    public static final String MODID = "camera_anim";
    private static final Logger LOGGER = LogUtils.getLogger();

    public CameraAnim() {
        ModNetwork.init();
    }
}
