/**
 * Created by LuYingJie on 2018/12/5.
 */

//右键菜单模块
layui.define('fsTab',function(exports){ //提示：模块也可以依赖其它模块，如：layui.define('layer', callback);
    var fsTab=layui.fsTab;

    var obj={
        createConextMenu:function () {
            $.contextMenu({
                selector: '.layui-tab-title li',
                callback: function(key, options) {
                    var layId = $(this).attr("lay-id");
                    switch (key) {
                        case "refresh":
                            $('.layui-show iframe').attr('src', $('.layui-show iframe').attr('src'));
                            break;
                        case "close":
                            fsTab.del(layId);
                            break;
                        case "closeOther":
                            $(this).parent().children("li").each(function(i,e){

                                if($(this).find(".layui-tab-close").is(":visible")){

                                    var newLayId = $(this).attr("lay-id");
                                    if(layId != newLayId ){
                                        fsTab.del(newLayId);
                                    }
                                }
                            });
                            break;
                        case "closeAll":
                            $(this).parent().children("li").each(function(i,e){
                                if($(this).find(".layui-tab-close").is(":visible")){
                                    var newLayId = $(this).attr("lay-id");
                                    fsTab.del(newLayId);
                                }
                            });
                            break;
                        default:
                            break;
                    }
                },
                items: {
                    "refresh": {name: "刷新标签",icon:"fa-refresh"},
                    "close": {name: "关闭标签",icon:"fa-close",disabled: function(){
                        if($(this).find(".layui-tab-close").is(":visible")){
                            return false;
                        }
                        return true;
                    }},
                    "closeOther": {name: "关闭其他",icon:"fa-ban"},
                    "closeAll": {name: "关闭全部",icon:"fa-window-close"}
                }
            });
        }
    }

    obj.createConextMenu();


    //输出test接口
    exports('fsContextMenu', obj);
});