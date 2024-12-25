package cn.anecansaitin.cameraanim.generator.assest;

import cn.anecansaitin.cameraanim.CameraAnim;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public abstract class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output, String locale) {
        super(output, CameraAnim.MODID, locale);
    }

    public static class ZhCn extends ModLangProvider {
        public ZhCn(PackOutput output) {
            super(output, "zh_cn");
        }

        @Override
        protected void addTranslations() {
            add("key.categories." + CameraAnim.MODID, "相机大师");
            add("key." + CameraAnim.MODID + ".add_global_camera_point", "添加全局相机点");
            add("key." + CameraAnim.MODID + ".edit_mode", "编辑模式");
            add("key." + CameraAnim.MODID + ".view_mode", "查看模式");
        }
    }

    public static class EnUs extends ModLangProvider {
        public EnUs(PackOutput output) {
            super(output, "en_us");
        }

        @Override
        protected void addTranslations() {
            add("key.categories." + CameraAnim.MODID, "Camera Anim");
            add("key." + CameraAnim.MODID + ".add_global_camera_point", "Add Global Camera Point");
            add("key." + CameraAnim.MODID + ".edit_mode", "Edit Mode");
            add("key." + CameraAnim.MODID + ".view_mode", "View Mode");
       }
    }
}
