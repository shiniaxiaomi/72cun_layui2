/**
 * Created by LuYingJie on 2019/3/4.
 */


layui.define([], function(exports){


    var obj={

        //url添加弹窗
        urlAddDialog:function (title,url) {
            var urlAddDialog=$(`
                <!-- url 添加 弹窗 -->
                <div class="layui-form form-dialog" id="addText" lay-filter="addText" style="display: none">
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
                    <!--<button class="layui-btn layui-btn-primary customFolderSetting">自定义</button>-->
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
            $("body").append(urlAddDialog);
            //是否直接显示

            //打开添加弹窗
            $("#addBtn").on("click",function () {
                //页面层
                addLayer=layer.open({
                    type: 1,
                    shadeClose: true,
                    area: ['515px', '351px'], //宽高]
                    content: $("#addText")
                });

                //查询默认文件夹id
                myUtil.ajax("/user/getCustomFolder",[],function (data) {
                    if(data.code==0){
                        //清空表单
                        form.val("addText", {
                            "label": "",
                            "url": "",
                            "pidName": data.data.customFolderName,
                            "pid": data.data.customFolderId
                        })
                    }else{
                        //清空表单
                        form.val("addText", {
                            "label": "",
                            "url": "",
                            "pidName": "默认文件夹",
                            "pid": data.data.rootFolderId
                        })
                    }

                })
            })

            //url添加事件
            form.on('submit(addSubmit)', function(data){
                //判断链接是否共享
                if(data.field.isShare!=undefined && data.field.isShare=="on"){
                    data.field.isShare="true";
                }else{
                    data.field.isShare="false";
                }

                myUtil.ajax("/url/add",data.field,function (data) {
                    tableIns.reload(tableOption);//刷新表格
                    layer.close(addLayer);
                    layer.msg("添加成功");
                })
                return false;
            });
        },
        //选择文件夹弹窗
        selectfolderDialog:function () {
            var folderDialog=$(`
                <!-- 文件夹树弹窗 -->
                <div id="folderTree" class="folderTree" style="display: none">
    <input id='searchFolder' type='text' autocomplete='off' placeholder='输入关键字进行文件夹过滤' class='layui-input' >
    <div id="ele" class="eleTree ele1 selectFoldertree" lay-filter="data"></div>

    <script>

        layui.use(["eleTree",'myUtil'], function() {
            eleTree = layui.eleTree;
            var myUtil = layui.myUtil;


            //绑定输入框搜索事件
            $("#searchFolder").on("input",function (e) {
                if($("#searchFolder").val()!=""){
                    var parentDataArrBuff=$.extend(true,[],parentDataArr)
                    var searchTableDataObjBuff=parentDataArrBuff[0];
                    filterTree(searchTableDataObjBuff,$("#searchFolder").val(),parentDataArrBuff,0);
                    el.reload(parentDataArrBuff);//重新渲染filter文件夹后的数据
                }else{
                    el.reload(parentDataArr);//重新渲染原始数据
                }
            })

            //打开文件夹树弹窗
            $(".selectTree").on("click",function () {
                el.reload();//每次打开都重新请求最新的数据和重新渲染

                folderTree_layer=layer_folderTree.open({
                    type: 1,
                    shadeClose: true,
                    area: ['515px', '351px'], //宽高]
                    content: $("#folderTree")
                });

                $("#searchFolder").val("");//清空数据
                $("#searchFolder").focus();//获取焦点
            })

            //请求自定义文件夹id
            $(".customFolderSetting").on("click",function () {
                myUtil.ajax("/user/getCustomFolder",{},function (data) {
                    if(data.code==0){
                        $("#folderId_add").val(data.data.customFolderId);
                        $("#folderName_add").val(data.data.customFolderName);
                        $("#folderId_edit").val(data.data.customFolderId);
                        $("#folderName_edit").val(data.data.customFolderName);
                    }else{
                        layer.msg(data.message);
                    }
                })
            })

            //请求数据,初始化
            el = eleTree.render({
                elem: '#ele',
                url: '/folder/query',//请求数据的url
                expandOnClickNode: false,//点击节点前的图标才收缩
                defaultExpandAll:true,//默认展开所有节点
                done: function(res){
                    var buff=myUtil.buildTree(res);//转化成树结构
                    searchTableDataObj=buff[0];
                    parentDataArr=buff;
                    return buff;//将平面数据转换成tree数据,返回给data
                }
            });


            eleTree.on("nodeClick(data)", function (d) {
                clickNodeId=d.data.currentData.value;
                $("#folderId_add").val(clickNodeId);
                $("#folderName_add").val(d.data.currentData.label);
                $("#folderId_edit").val(clickNodeId);
                $("#folderName_edit").val(d.data.currentData.label);
                layer_folderTree.close(folderTree_layer);
            });

            //过滤文件夹
            function filterTree(data,keyword,parentData,index) {
                if(data.children==undefined){
                    if(data.label.indexOf(keyword) == -1){//不包含,则删除自身
                        parentData.splice(index,1);
                    }
                    return;
                }

                for(var i=data.children.length-1;i>=0;i--){
                    filterTree(data.children[i],keyword,data.children,i);
                }

                if(data.children.length==0){
                    if(data.label.indexOf(keyword) == -1){//自身不包含,删除自身
                        parentData.splice(index,1);
                    }else{
                        delete data["children"];//自身包含,则删除children字段
                    }
                }
            }

        })
    </script>
</div>
            `);
            $("body").append(folderDialog);
        },
        //url更改弹窗
        urlUpdateDialog:function () {
            var urlUpdateDialog=$(`
                <!-- url 编辑 弹窗 -->
                <div class="layui-form form-dialog" lay-filter="editText" id="editText" style="display: none">
    <div class="layui-row">
        <div class="layui-col-md11">
            <input class="layui-input hide" lay-verify="required" type="text" name="id" >
            <div class="layui-form-item">
                <label class="layui-form-label">网址名称</label>
                <div class="layui-input-block">
                    <input id="edit1" lay-verify="label" type="text" name="label" placeholder="请输入网址名称" autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">网址链接</label>
                <div class="layui-input-block">
                    <input id="edit2" lay-verify="myUrl" type="text" name="url"  placeholder="请输入网址链接" autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label">文件夹</label>
                <div id="location_edit" class="layui-input-block">
                    <input id="folderName_edit" class="layui-input layui-input-inline selectTree" lay-verify="required" type="text" name="pidName" placeholder="请选择文件夹" readonly="readonly">
                    <button class="layui-btn layui-btn-primary selectTree" >选择</button>
                    <button class="layui-btn layui-btn-primary customFolderSetting">自定义</button>
                    <input id="folderId_edit" class="layui-input hide" lay-verify="required" type="text" name="pid" >
                </div>
            </div>

            <div class="layui-form-item">
                <div class="layui-input-block">
                    <button class="layui-btn" lay-submit lay-filter="editSubmit">更改</button>
                </div>
            </div>
        </div>
    </div>
</div>
            `);
            $("body").append(urlUpdateDialog);

            //url更改事件
            form.on('submit(editSubmit)', function(formData){
                myUtil.ajax("/url/update",formData.field,function (data) {
                    tableIns.reload(tableOption);//刷新表格
                    layer.close(editLayer);
                    layer.msg("更改成功");
                })
                return false;
            });
        },
        checkData:function () {
            //参数长度校验
            form.verify({
                label: [/^.{1,150}$/,'长度在1~150之间']

                //我们既支持上述函数式的方式，也支持下述数组的形式
                //数组的两个值分别代表：[正则匹配、匹配不符时的提示文字]
                ,myUrl: [/^.{1,500}$/,'长度在1~500之间']
            });
        },
        init:function (title,url) {
            obj.urlAddDialog(title,url);//添加弹窗
            obj.urlUpdateDialog();//更改弹窗
            obj.selectfolderDialog();//文件夹选择弹窗
            obj.checkData();//添加参数校验
        }


    }

    exports('myDialog',obj);//导出的名字要文件名相同

});

