/**
 * Created by LuYingJie on 2019/3/4.
 */


layui.define([], function(exports){


    var obj={

        addHtml:function () {
            $("body").append(`
                <!-- url 添加 弹窗 -->
                <div class="layui-form form-dialog" id="addText" lay-filter="addText">
                    <div class="layui-row">
                        <div class="layui-col-md11">
                            <div class="layui-form-item">
                                <label class="layui-form-label">网址名称</label>
                                <div class="layui-input-block">
                                    <input id="add1" lay-verify="label" type="text" name="label" placeholder="请输入网址名称" autocomplete="off" class="layui-input">
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <label class="layui-form-label">网址链接</label>
                                <div class="layui-input-block">
                                    <input id="add2" lay-verify="myUrl" type="text" name="url"  placeholder="请输入网址链接" autocomplete="off" class="layui-input">
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <label class="layui-form-label">文件夹</label>
                                <div id="location_add" class="layui-input-block">
                                    <input id="folderName_add" class="layui-input layui-input-inline selectTree" lay-verify="required" type="text" name="pidName" placeholder="请选择文件夹" readonly="readonly">
                                    <button class="layui-btn layui-btn-primary selectTree" >选择</button>
                                    <button class="layui-btn layui-btn-primary customFolderSetting">自定义</button>
                                    <input id="folderId_add" class="layui-input hide" lay-verify="required" type="text" name="pid"   style="" >
                                </div>
                            </div>
                            <div class="layui-form-item">
                                <label class="layui-form-label">链接状态</label>
                                <div id="isShare" class="layui-input-block">
                                    <input type="checkbox" name="isShare" lay-skin="switch" checked lay-text="共享|私有"><!-- 默认是共享 -->
                                </div>
                            </div>
                            <div class="layui-form-item" >
                                <div class="layui-input-block">
                                    <button class="layui-btn" lay-submit lay-filter="addSubmit">添加</button>
                                </div>
                            </div>
                        </div>
                
                    </div>
                
                </div>
            `);
        }


    }

    exports('myDialog',obj);//导出的名字要文件名相同

});

