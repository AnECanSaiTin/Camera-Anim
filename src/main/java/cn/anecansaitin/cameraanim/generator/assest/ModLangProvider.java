package cn.anecansaitin.cameraanim.generator.assest;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.ModKeyMapping;
import cn.anecansaitin.cameraanim.common.animation.PointInterpolationType;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class ModLangProvider extends LanguageProvider {
    private final boolean isZhCn;

    public ModLangProvider(PackOutput output, String locale, boolean isZhCn) {
        super(output, CameraAnim.MODID, locale);
        this.isZhCn = isZhCn;
    }

    @Override
    protected void addTranslations() {
        add(ModKeyMapping.EDIT_MODE.get().getCategory(), "相机大师", "Camera Anim");
        add(ModKeyMapping.ADD_GLOBAL_CAMERA_POINT.get().getName(), "添加全局相机点", "Add Global Camera Point");
        add(ModKeyMapping.EDIT_MODE.get().getName(), "编辑模式", "Edit Mode");
        add(ModKeyMapping.VIEW_MODE.get().getName(), "查看模式", "View Mode");
        add(ModKeyMapping.POINT_SETTING.get().getName(), "设置点属性", "Point Setting");

        add(PointInterpolationType.STEP.getDisplayNameKey(), "步", "Step");
        add(PointInterpolationType.SMOOTH.getDisplayNameKey(), "平滑", "Smooth");
        add(PointInterpolationType.LINEAR.getDisplayNameKey(), "线性", "Linear");
        add(PointInterpolationType.BEZIER.getDisplayNameKey(), "贝塞尔", "Bezier");

        add("gui.camera_anim.point_setting.pos", "坐标", "Pos");
        add("gui.camera_anim.point_setting.rot", "旋转", "Rot");
        add("gui.camera_anim.point_setting.zoom", "缩放", "Zoom");
        add("gui.camera_anim.point_setting.time", "时间", "Time");
        add("gui.camera_anim.point_setting.save", "保存", "Save");
        add("gui.camera_anim.point_setting.type", "类型", "Type");
        add("gui.camera_anim.point_setting.pos_error", "坐标格式错误", "Pos Format Error");
        add("gui.camera_anim.point_setting.rot_error", "旋转格式错误", "Rot Format Error");
        add("gui.camera_anim.point_setting.zoom_error", "缩放格式错误", "Zoom Format Error");
        add("gui.camera_anim.point_setting.time_error", "时间格式错误", "Time Format Error");
        add("gui.camera_anim.point_setting.tip", "Gui 暂未设计完毕，当前为临时措施", "Gui is not designed yet, this is a temporary measure");
    }

    private void add(String key, String cn, String en) {
        if (isZhCn) {
            add(key, cn);
        } else {
            add(key, en);
        }
    }
}
