/**
 * Created by LuYingJie on 2019/3/3.
 */


layui.define(['flow','layer','util','element','form','myUtil'], function(exports){

    var flow = layui.flow;
    var layer = layui.layer;
    var utils = layui.util;
    var form = layui.form;
    var myUtil = layui.myUtil;

    var user=undefined;
    var addLayer=undefined;


    var obj={
        //动态添加热点网址数据
        addUrlData:function (id, url, param, next) {
            myUtil.ajax(url,param,function (data) {
                var o = document.getElementById(id);
                var width = o.offsetWidth-200; //div的宽度

                var lis = [];
                var list=data.data;
                for(var i = 0; i < list.length; i++){
                    lis.push(`
                                <a href="javascript:;" class="media-left"><img src="/images/code.png" height="46px" width="46px" style="display: block;float: left;vertical-align: top;padding-right: 10px;"></a>
                                <div class="media-body">
                                    <div class="pad-btm">
                                        <p class="fontColo "><a href="/home/`+list[i].userName+`"><b>`+list[i].userName+`</b></a><span>共享了一个链接</span></p>
                                        <p>`+list[i].shareTime+`</p>
                                    </div>
                                    <b><p style="font-size: 15px">标题：<span><a href="`+list[i].url+`" target="_blank" ><span style="color: #01AAED" class="urlClick" urlId="`+list[i].id+`">`+list[i].label+`</span></a></span></p></b>
                                    <p>位置：<span>`+list[i].pidName+`</span></p>
                                    <div class="longtext" style="width:`+width+`px;">链接：<a href="`+list[i].url+`" target="_blank" ><span class="urlClick" urlId="`+list[i].id+`">`+list[i].url+`</span></a></div>

                                    <div class="media">
                                        <div class="media-right">

                                            <ul class="list-inline">
                                                <li><span>点击量：</span><span>`+list[i].clickNumber+`</span></li>
                                                <li><span>点赞量：</span><span>`+list[i].goodNumber+`</span></li>
                                                <li class="goodClick" urlId="`+list[i].id+`"><i class="layui-icon layui-icon-praise" style="font-size: 25px;" onmousemove="$(this).css('color','#1E9FFF')" onmouseout="$(this).css('color','')"></i></li>
                                            </ul>

                                        </div>
                                        <!--<div class="media-left"></div>-->
                                    </div>
                                </div>
                                <hr>
                        `)
                }

                //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
                //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
                next(lis.join(''), param.page < data.pages); //假设总页数为 10
                $(".urlClick").unbind("click");//先删除之前所有的点击事件，防止绑定多次事件
                $(".urlClick").on("click",function (arg) {//绑定链接点击事件
                    var url=obj.getUrlData(arg.target);
                    myUtil.ajax("/hotUrl/incrClickNumber",url,function (data) {
                    })
                })

                $(".goodClick").unbind("click");//先删除之前所有的点击事件，防止绑定多次事件
                //绑定点赞事件
                $(".goodClick").on("click",function (arg) {
                    //检查是否登入
                    if(user==undefined){
                        layer.msg("请登入后再操作！");
                        return;
                    }

                    var url=obj.getUrlData(arg.target);
                    var urlId=$(arg.target).closest("li").attr("urlId");
                    url.likeUrlId=urlId;
                    url.id=urlId;

                    //检查用户是否已经点赞过
                    myUtil.ajax("/hotUrl/isIncredGoodNumber",{userId:user.id,likeUrlId:urlId},function (data) {
                        if(data.code==0){//增加点赞量
                            //点赞递增
                            myUtil.ajax("/hotUrl/incrGoodNumber",url,function (data) {
                                if(data.code==0){//增加点赞量
                                    layer.msg(data.message);
                                    //标记该用户以点赞
                                    myUtil.ajax("/hotUrl/markIsIncredGoodNumber",{userId:user.id,likeUrlId:urlId},function (data) {
                                        if(data.code==1){
                                            console.log(data.message);//标记失败的话，静默打印
                                        }
                                    })
                                }else{
                                    layer.msg(data.message);
                                }
                            })
                        }else{
                            layer.msg(data.message);
                        }
                    })
                })
            })
        },
        //动态添加用户数据
        addUserData:function (url,param,next,flag) {
            var buff1="";
            myUtil.ajax(url,param,function (data) {
                var userData=data.data;
                var lis = [];
                for(var i=0;i<userData.length;i++){
                    if(flag=="分享"){
                        buff1="共分享了"+userData[i].shareNumber+"条链接";
                    }else if(flag=="点赞"){
                        buff1="共获得了"+userData[i].goodNumber+"个点赞";
                    }
                    lis.push(`
                         <a href="/home/`+userData[i].userName+`" class="layadmin-privateletterlist-item">
                            <div class="meida-left">
                                <img src="/images/code.png">
                            </div>
                            <div class="meida-right">
                                <p>`+userData[i].userName+`</p>
                                <mdall>`+buff1+`</mdall>
                            </div>
                         </a>
                         <hr>
                    `);
                }

                //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
                //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
                next(lis.join(''), param.page < data.pages); //假设总页数为 10
            })
        },
        //通过点击节点获取点击对应的url数据
        getUrlData:function (arg) {
            var div=$(arg).closest(".media-body");
            //获取时间
            var id=$(arg).attr("urlId")
            var url=$(div).find("div a span").text();
            var label=$(div).find("b a span").text();
            var pidName=$($(div).find("p span").get(3)).text();
            var userName=$($(div).find("div p").get(0)).find("b").text();
            var clickNumber=$($(div).find("ul li span").get(1)).text();
            var goodNumber=$($(div).find("ul li span").get(3)).text();

            var shareTime=$($(div).find("div p").get(1)).text();

            var url={
                id:id,
                url:url,
                label:label,
                pidName:pidName,
                userName:userName,
                time:shareTime,
                clickNumber:clickNumber,
                goodNumber:goodNumber
            }
            return url;
        },
        //修改登入的状态
        changeLoginStatus:function (data) {
            $("#userDiv").html(`
                    <li style="float:right;margin-left: 10px"><a href="/exit">退出</a></li>
                    <i class="layui-badge fly-badge-vip layui-hide-xs" style="float: right;margin-top: 19px">VIP</i>
                    <div class="dropdown" style="float:right;margin-right: 10px">
                        <a href="javascript:;" class="dropbtn">`+data.userName+`</a>
                        <div class="dropdown-content">
                            <!--<a class="subMenu" href="#1">链接 1</a>-->
                            <!--<a class="subMenu" href="#2">链接 2</a>-->
                            <!--<a class="subMenu" href="#3">链接 3</a>-->
                        </div>
                    </div>
                    <img src="/images/code.png" style="height: 40px;margin-top: 6px;float: right">
            `);
        },
        //加载url数据，flow流动的数据（加载更多）
        loadUrlDataByFlow:function (id,url,param) {
            flow.load({
                elem: '#'+id, //热帖
                isAuto: false,
                done: function(page, next){ //执行下一页的回调
                    param.page=page;
                    obj.addUrlData(id,url,param,next);
                }
            });
        },
        //加载user数据
        loadUserDataByFlow:function (id,url,param,flag) {
            flow.load({
                elem: '#'+id, //热帖
                isAuto: false,
                done: function(page, next){ //执行下一页的回调
                    param.page=page;
                    obj.addUserData(url,param,next,flag);
                }
            });
        },
        //初始化函数
        init:function () {
            //绑定个人主页点击事件
            $("#personalPage").click(function () {
                if(user!=undefined){
                    window.location.href="/home/"+user.userName;
                }else{
                    layer.prompt({title: '输入用户名就可以直接访问个人主页', formType: 0,maxlength: 15,shadeClose: true}, function(pass, index){
                        window.location.href="/home/"+pass;
                    });
                }
            })
            //查询是否已经登入
            myUtil.ajax("/getUserFromSession",{},function (data) {
                if(data.userName!=null){
                    user=data;//保存user的信息
                    obj.changeLoginStatus(data);//修改登入状态
                }
            })

            obj.loadUserDataByFlow("shareUser","/user/getShareUserOrder",{limit:8},"分享");//获取分享达人
            obj.loadUserDataByFlow("goodUser","/user/getGoodUserOrder",{limit:8},"点赞");//获取点赞达人

            //返回页面最上方
            utils.fixbar({
                showHeight:200,
            });

            //表单参数检验
            form.verify({
                myUserName: [/^.{1,20}$/,'长度在1~20之间']
                ,myPassword: [/^.{1,20}$/,'长度在1~20之间']
            });

            //登入事件
            form.on('submit(loginSubmit)', function(formData){
                //在主页的登入操作
                myUtil.ajax("/homeLogin",formData.field,function (data) {
                    if(data.code==0){
                        user=data.data;
                        layer.close(addLayer);//关闭登入弹窗
                        layer.msg("登入成功！");
                        if(data.message!=null){
                            eval(data.message);//展示公告
                        }
                        obj.changeLoginStatus(data.data);//修改登入状态
                    }else {
                        layer.msg(data.message);
                    }
                })
                return false;
            });

            //使用ajax请求登入页面(静态资源)
            $("#login").click(function () {
                //页面层
                addLayer=layer.open({
                    type: 1,
                    title:'用户登入',
                    shadeClose: true,
                    area: ['380px', '220px'], //宽高
                    content: $("#loginDiv")
                });
            })

            //输入框获得焦点
            window.onload=function() {
                $("#keywords").focus();
            }

            //检测是否已经学习过快速入门
            var isStudied = layui.data('72cun',{key: 'isStudied'});//查询本地数据
            if(isStudied==undefined ||isStudied!="true"){
                //询问框
                var confirmLayer=layer.confirm('检测到你首次访问，是否打开<strong>快速入门</strong>进行学习？', {
                    btn: ['可以学习一波','我想自己摸索'] //按钮
                }, function(){
                    window.open("/html/quickStart.html","_blank");
                    layui.data('72cun', {key: 'isStudied',value: 'true'});//写入本地数据
                    layer.close(confirmLayer);
                    layer.alert('以后的使用过程中如果有疑问，可以到<br>导航栏中的<strong>快速入门</strong>进行再次学习',{title:"学习成功！"});
                }, function(){
                    layui.data('72cun', {key: 'isStudied',value: 'true'});//写入本地数据
                    layer.close(confirmLayer);
                    layer.alert('以后的使用过程中如果有疑问，可以到<br>导航栏中的<strong>快速入门</strong>进行再次学习',{title:"欢迎再次学习！"});
                });
            }

        }

    }

    exports('homePageJs',obj);//导出的名字要文件名相同
});