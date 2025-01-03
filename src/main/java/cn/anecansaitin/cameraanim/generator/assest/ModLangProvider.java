package cn.anecansaitin.cameraanim.generator.assest;

import cn.anecansaitin.cameraanim.CameraAnim;
import cn.anecansaitin.cameraanim.client.ModKeyMapping;
import cn.anecansaitin.cameraanim.common.animation.PathInterpolator;
import cn.anecansaitin.cameraanim.common.animation.TimeInterpolator;
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
        add(ModKeyMapping.ADD_GLOBAL_CAMERA_POINT.get().getName(), "添加全局相机关键帧", "Add Global Camera Keyframes");
        add(ModKeyMapping.DELETE_GLOBAL_CAMERA_POINT.get().getName(), "删除全局相机关键帧", "Delete Global Camera Keyframes");
        add(ModKeyMapping.EDIT_MODE.get().getName(), "编辑模式", "Edit Mode");
        add(ModKeyMapping.VIEW_MODE.get().getName(), "查看模式", "View Mode");
        add(ModKeyMapping.POINT_SETTING.get().getName(), "设置点属性", "Point Setting");
        add(ModKeyMapping.PREVIEW_MODE.get().getName(), "预览模式", "Preview Mode");
        add(ModKeyMapping.PLAY.get().getName(), "播放", "Play");
        add(ModKeyMapping.RESET.get().getName(), "复位", "Reset");
        add(ModKeyMapping.SET_CAMERA_TIME.get().getName(), "设置相机时间", "Set Camera Time");
        add(ModKeyMapping.BACK.get().getName(), "后退", "Back");
        add(ModKeyMapping.FORWARD.get().getName(), "前进", "Forward");
        add(ModKeyMapping.MANAGER.get().getName(), "控制中心", "Manager");

        add(PathInterpolator.STEP.getDisplayNameKey(), "步", "Step");
        add(PathInterpolator.SMOOTH.getDisplayNameKey(), "平滑", "Smooth");
        add(PathInterpolator.LINEAR.getDisplayNameKey(), "线性", "Linear");
        add(PathInterpolator.BEZIER.getDisplayNameKey(), "贝塞尔", "Bezier");

        add(TimeInterpolator.LINEAR.getDisplayNameKey(), "线性", "Linear");
        add(TimeInterpolator.BEZIER.getDisplayNameKey(), "贝塞尔", "Bezier");

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
        add("gui.camera_anim.point_setting.interpolation", "插值", "Interpolation");
        add("gui.camera_anim.interpolation_setting.interpolation", "插值", "Interpolation");
        add("gui.camera_anim.interpolation_setting.type", "类型", "Type");
        add("gui.camera_anim.interpolation_setting.easy", "平滑", "Easy");
        add("gui.camera_anim.interpolation_setting.easy_in", "缓入", "Easy In");
        add("gui.camera_anim.interpolation_setting.easy_out", "缓出", "Easy Out");
        add("gui.camera_anim.interpolation_setting.easy_in_out", "缓入缓出", "Easy In Out");
        add("gui.camera_anim.interpolation_setting.save", "保存", "Save");
        add("gui.camera_anim.interpolation_setting.time", "时间", "Time");
        add("gui.camera_anim.interpolation_setting.distance", "距离", "Distance");
    }

    private void add(String key, String cn, String en) {
        if (isZhCn) {
            add(key, cn);
        } else {
            add(key, en);
        }
    }
}
