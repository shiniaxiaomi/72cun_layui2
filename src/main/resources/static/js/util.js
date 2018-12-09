
//将数据转化成树形结构数组
function fn(data,pid) {
    var result = [], temp;
    for (var i = 0; i < data.length; i++) {
        if (data[i].pid == pid) {
            var obj = {
                "id": data[i].id,
                "label": data[i].label,
                "pid": data[i].pid
            };
            temp = fn(data, data[i].id);
            if (temp.length > 0) {
                obj.children = temp;
            }
            result.push(obj);
        }
    }
    return result;
}
//将数据转化成树形结构数组(data1是服务器端直接传回来的数据)
function buildTree(data1) {
    var data=data1.data;//取到里面的json数据
    return fn(data,0);
}


//工具对象
var util={
    //异步的ajax请求
    ajax:function (url,data,func) {
        $.ajax({
            type: 'post',
            url: url,
            dataType: 'json',
            data: data,
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
        $.ajax({
            type: 'get',
            url: url,
            dataType: 'json',
            data: data,
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
}