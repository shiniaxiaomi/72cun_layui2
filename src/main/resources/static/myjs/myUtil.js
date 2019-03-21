/**
 * Created by LuYingJie on 2019/3/11.
 */


layui.define(['layer'], function(exports){

    var layer=layui.layer;

    //isShowWait为false时，不显示等待图标
    //isShowWait不填，则默认显示等待图标
    var obj={
        //异步的ajax请求
        ajax:function (url,data,func,isShowWait) {
            if(isShowWait==undefined){
                var waitLayer=layer.msg('加载中...', {
                    icon: 16
                    ,shade: 0.01
                    ,time: 0
                });
            }

            $.ajax({
                type: 'post',
                url: url,
                dataType: 'json',
                data: data,
                complete:function (data) {
                    if(isShowWait==undefined){
                        layer.close(waitLayer);
                    }
                },
                error: function (data) {
                    if(data.status==309){//自己设置的错误码,表示session失效
                        console.dir("session 失效")
                        top.window.location.href='/';//发生错误之后,就直接重定向到登入页面,一般是session失效了
                    }
                    console.dir("传输失败!")
                    console.dir(data);//请求失败时被调用的函数
                    console.dir("传输失败!")
                },
                success: function (data) {
                    func(data);
                }
            });
        },
        ajax_get:function (url,data,func) {
            var waitLayer=layer.msg('加载中...', {
                icon: 16
                ,shade: 0.01
                ,time: 0
            });

            $.ajax({
                type: 'get',
                url: url,
                dataType: 'json',
                data: data,
                complete:function (data) {
                    layer.close(waitLayer);
                },
                error: function (data) {
                    if(data.status==309){//自己设置的错误码,表示session失效
                        console.dir("session 失效")
                        top.window.location.href='/';//发生错误之后,就直接重定向到登入页面,一般是session失效了
                    }
                    console.dir("传输失败!")
                    console.dir(data);//请求失败时被调用的函数
                    console.dir("传输失败!")
                },
                success: function (data) {
                    func(data);
                }
            });
        },

        //将数据转化成树形结构数组
        fn:function(data,pid) {
            var result = [], temp;
            for (var i = 0; i < data.length; i++) {
                if (data[i].pid == pid) {
                    var obj = {
                        "value": data[i].id,
                        "label": data[i].name,
                        "pid": data[i].pid
                    };
                    temp = this.fn(data, data[i].id);
                    if (temp.length > 0) {
                        obj.children = temp;
                    }
                    result.push(obj);
                }
            }
            return result;
        },
        //将数据转化成树形结构数组(data1是服务器端直接传回来的数据)
        buildTree:function(data1) {
            var data=data1.data;//取到里面的json数据
            return obj.fn(data,0);
        },
        //时间转化工具
        beautifyTime:function (time) {
            var d = new Date(time)
            var mistiming = Math.round((Date.now() - d.getTime(d)) / 1000);
            var arrr = ['年', '个月', '星期', '天', '小时', '分钟', '秒'];
            var arrn = [31536000, 2592000, 604800, 86400, 3600, 60, 1];
            for (var i = 0; i < arrn.length; i++) {
                var inm = Math.floor(mistiming / arrn[i]);
                if (inm != 0) {
                    return inm + arrr[i] + '前';
                }
            }
        }



    }

    exports('myUtil',obj);//导出的名字要文件名相同
});
