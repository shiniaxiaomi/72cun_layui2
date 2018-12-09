/**
 * Created by LuYingJie on 2018/12/6.
 */

//隐藏和显示侧边栏
layui.define(function(exports){ //提示：模块也可以依赖其它模块，如：layui.define('layer', callback);

    var obj={
        side : $('.layui-side'),
        body : $('.layui-body'),
        footer : $('.layui-footer'),

        // 导航栏收缩
        navHide:function (t, st) {
            var time = t ? t : 50;
            st ? localStorage.log = 1 : localStorage.log = 0;
            this.side.animate({'left': -200}, time);
            this.body.animate({'left': 0}, time);
            this.footer.animate({'left': 0}, time);
        },
        // 导航栏展开
        navShow:function (t, st) {
            var time = t ? t : 50;
            st ? localStorage.log = 0 : localStorage.log = 1;
            this.side.animate({'left': 0}, time);
            this.body.animate({'left': 200}, time);
            this.footer.animate({'left': 200}, time);
        },
        // 监听导航栏收缩
        listen:function () {
            $(".layui-logo").on('click',function () {
                if (localStorage.log == 0) {
                    obj.navShow(50);
                } else {
                    obj.navHide(50);
                }
            })
        },

    }

    obj.listen();

    //输出test接口
    exports('leftSide', obj);
});