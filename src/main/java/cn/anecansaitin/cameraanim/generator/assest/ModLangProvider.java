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
        add("gui.camera_anim.remote_path_search.page", "页", "Page");
        add("gui.camera_anim.remote_path_search.search", "从服务端查询", "Search Server");
        add("gui.camera_anim.remote_path_search.load", "加载", "Load");
        add("gui.camera_anim.remote_path_search.load_id", "加载名", "Load ID");
        add("gui.camera_anim.remote_path_search.save", "保存", "Save");
        add("gui.camera_anim.remote_path_search.save_id", "保存名", "Save ID");
        add("gui.camera_anim.remote_path_search.delete", "删除", "Delete");
        add("gui.camera_anim.remote_path_search.delete_id", "删除名", "Delete Id");
        add("gui.camera_anim.remote_path_search.local_mode", "本地模式", "Local Mode");
        add("gui.camera_anim.remote_path_search.path_id", "路径名", "Path ID");
        add("gui.camera_anim.remote_path_search.modifier", "编辑者", "Modifier");
        add("gui.camera_anim.remote_path_search.time", "编辑时间", "Time");
        add("gui.camera_anim.remote_path_search.no_server", "服务端未安装“相机大师”", "Server does not install \"Camera Anim\"");
        add("gui.camera_anim.remote_path_search.tip", "Gui 暂未设计完毕，当前为临时措施", "Gui is not designed yet, this is a temporary measure");
        add("gui.camera_anim.local_path_search.page", "页", "Page");
        add("gui.camera_anim.local_path_search.search", "从本地查询", "Search Local");
        add("gui.camera_anim.local_path_search.load", "加载", "Load");
        add("gui.camera_anim.local_path_search.load_id", "加载名", "Load ID");
        add("gui.camera_anim.local_path_search.save", "保存", "Save");
        add("gui.camera_anim.local_path_search.save_id", "保存名", "Save ID");
        add("gui.camera_anim.local_path_search.remote_mode", "远程模式", "Remote Mode");
        add("gui.camera_anim.local_path_search.path_id", "路径名", "Path ID");
        add("gui.camera_anim.local_path_search.modifier", "编辑者", "Modifier");
        add("gui.camera_anim.local_path_search.time", "编辑时间", "Time");
        add("gui.camera_anim.local_path_search.tip", "Gui 暂未设计完毕，当前为临时措施", "Gui is not designed yet, this is a temporary measure");
        add("gui.camera_anim.local_path_search.load_error", "获取动画失败", "Get Animation Failed");
        add("gui.camera_anim.local_path_search.local_file", "本地文件", "Local File");
        add("gui.camera_anim.local_path_search.version_error", "动画版本不兼容", "Animation Version Not Compatible");
        add("gui.camera_anim.local_path_search.file_load_error", "文件读取失败", "File Load Error");
        add("gui.camera_anim.local_path_search.file_format_error", "文件格式错误", "File Format Error");
        add("gui.camera_anim.local_path_search.file_exist_error", "文件不存在", "File Not Exist");
        add("gui.camera_anim.local_path_search.file_save_error", "文件保存失败", "File Save Error");
        add("gui.camera_anim.local_path_search.file_load_success", "文件加载成功", "File Load Success");
        add("gui.camera_anim.client_payload_manager.put_global_path_success", "添加路径成功", "Put Global Path Success");
        add("gui.camera_anim.client_payload_manager.put_global_path_failure", "添加路径失败", "Put Global Path Failure");
        add("gui.camera_anim.client_payload_manager.get_global_path_success", "获取路径成功", "Get Global Path Success");
        add("gui.camera_anim.client_payload_manager.get_global_path_failure", "获取路径失败", "Get Global Path Failure");
        add("gui.camera_anim.client_payload_manager.delete_global_path_success", "删除路径成功", "Delete Global Path Success");
        add("gui.camera_anim.client_payload_manager.delete_global_path_failure", "删除路径失败", "Delete Global Path Failure");

        add("freecamera.modifier." + CameraAnim.MODID + "_player", "相机动画播放器", "Camera Anim Player");
        add("freecamera.modifier." + CameraAnim.MODID + "_ide", "相机动画集成开发播放器", "Camera Anim IDE Player");
    }

    private void add(String key, String cn, String en) {
        if (isZhCn) {
            add(key, cn);
        } else {
            add(key, en);
        }
    }
}
