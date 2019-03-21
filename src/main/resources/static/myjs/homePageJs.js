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

        //修改登入的状态
        changeLoginStatus:function (data) {
            var html=`<li style="float:right;margin-left: 10px"><a href="/exit">退出</a></li>`;
            if(data.deadline!=null && Date.parse(data.deadline)>new Date()){
                html+=`<i class="layui-badge fly-badge-vip layui-hide-xs" style="float: right;margin-top: 19px">VIP</i>`;
            }
            html+=`
                <div class="dropdown" style="float:right;margin-right: 10px">
                    <a href="javascript:;" class="dropbtn">`+data.userName+`</a>
                    <div class="dropdown-content">
                        <a class="subMenu" href="/home/`+data.userName+`">个人首页</a>
                        <a class="subMenu" href="/html/personalInfo.html">个人信息</a>
                        <!--<a class="subMenu" href="/membership">会员</a>-->
                    </div>
                </div>
                <img src="https://www.usetools.cn/images/code.png" style="height: 40px;margin-top: 6px;float: right">
            `;
            $("#userDiv").html(html);
        },
        //动态添加热点网址数据
        addUrlData:function (id, url, param, next) {
            myUtil.ajax(url,param,function (data) {
                var o = document.getElementById(id);
                var width = o.offsetWidth-200; //div的宽度

                var lis = [];
                var list=data.data;
                for(var i = 0; i < list.length; i++){
                    var shareTime=myUtil.beautifyTime(list[i].shareTime);
                    lis.push(`
                                <ul class="feedlist_mod web" style="padding-top: 0px;">
                                    <li class="clearfix" urlId="`+list[i].id+`">
                                        <div class="list_con">
                                            <div class="title">
                                                <h2>
                                                    <a href="`+list[i].url+`" target="_blank">
                                                        <span class="urlClick">`+list[i].label+`</span>
                                                    </a>
                                                </h2>
                                            </div>
                                            <div class="summary oneline">链接:`+list[i].url+`</div>
                                            <dl class="list_userbar">
                                                <dt>
                                                    <a href="/home/`+list[i].userName+`" class="user_img">
                                                        <img src="https://www.usetools.cn/images/code.png">
                                                    </a>
                                                </dt>
                                                <dd class="name">
                                                    <a href="/home/`+list[i].userName+`">`+list[i].userName+`</a>
                                                </dd>
                                                <div class="interval"></div>
                                                <dd class="time">`+shareTime+`</dd>
                                                <div class="interactive floatR">
                                                    <dd class="common_num">
                                                            <span class="text">阅读数</span>
                                                            <span class="num">`+list[i].clickNumber+`</span>
                                                    </dd>
                                                    <div class="interval"></div>
                                                    <dd class="read_num">
                                                        <a href="javascript:;" title="点赞" class="goodClick">
                                                            <span class="text">点赞数</span>
                                                            <span class="num">`+list[i].goodNumber+`</span>
                                                        </a>
                                                    </dd>
                                                </div>
                                            </dl>
                                        </div>
                                    </li>
                                </ul> 
                        `)
                }

                //执行下一页渲染，第二参数为：满足“加载更多”的条件，即后面仍有分页
                //pages为Ajax返回的总页数，只有当前页小于总页数的情况下，才会继续出现加载更多
                next(lis.join(''), param.page < data.pages); //假设总页数为 10
                $(".urlClick").unbind("click");//先删除之前所有的点击事件，防止绑定多次事件
                $(".urlClick").on("click",function (arg) {//绑定链接点击事件
                    var url=obj.getClickUrl(arg.target);
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

                    var url=obj.getClickUrl(arg.target);
                    url.likeUrlId=url.id;

                    //检查用户是否已经点赞过
                    myUtil.ajax("/hotUrl/isIncredGoodNumber",{userId:user.id,likeUrlId:url.urlId},function (data) {
                        if(data.code==0){//增加点赞量
                            //点赞递增
                            myUtil.ajax("/hotUrl/incrGoodNumber",url,function (data) {
                                if(data.code==0){//增加点赞量
                                    layer.msg(data.message);
                                    //标记该用户以点赞
                                    myUtil.ajax("/hotUrl/markIsIncredGoodNumber",{userId:user.id,likeUrlId:url.urlId},function (data) {
                                        if(data.code==1){
                                            console.log(data.message);//标记失败的话，静默打印
                                        }
                                    },false)
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
                        buff1="共享了"+userData[i].shareNumber+"条链接";
                    }else if(flag=="点赞"){
                        buff1="获得了"+userData[i].goodNumber+"个点赞";
                    }
                    lis.push(`
                         <a href="/home/`+userData[i].userName+`" class="layadmin-privateletterlist-item">
                            <div class="meida-left">
                                <img src="https://www.usetools.cn/images/code.png">
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
        //获取点击链接的id
        getClickUrl:function (arg) {
            var urlId=$(arg).closest("li").attr("urlId");
            var userName=$(arg).closest("li").find(".name a").text();
            var url={
                id:urlId,
                userName:userName,
            }
            return url;
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
        //添加登入弹窗
        addLoginDialog:function () {
            var addLoginDialog=$(`
                <div class="layui-form form-dialog" id="loginDiv" lay-filter="loginDiv" style="display: none;">
    <div class="layui-row">
        <div class="layui-col-md11">
            <div class="layui-form-item" style="margin-top: 10px">
                <label class="layui-form-label" style="padding: 9px 0px;">账号</label>
                <div class="layui-input-block">
                    <input id="userName" lay-verify="myUserName" type="text" name="userName" placeholder="手机号/用户名" autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item">
                <label class="layui-form-label" style="padding: 9px 0px;">密码</label>
                <div class="layui-input-block">
                    <input type="password" id="password" lay-verify="myPassword" type="text" name="password"  placeholder="" autocomplete="off" class="layui-input">
                </div>
            </div>
            <div class="layui-form-item" >
                <div class="layui-input-block">
                    <button class="layui-btn" id="loginBtn" lay-submit lay-filter="loginSubmit">登入</button>
                </div>
            </div>
        </div>
    </div>
</div>
            `);
            $("body").append(addLoginDialog);

            $("#userName").keyup(function(event){
                if(event.keyCode==13){
                    $('#loginBtn').click();
                }
            });

            $("#password").keyup(function(event){
                if(event.keyCode==13){
                    $('#loginBtn').click();
                }
            });

        },
        //初始化函数
        init:function () {
            //添加登入弹窗
            obj.addLoginDialog();

            //查询是否已经登入，如果没有登入则尝试登入
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

                $("#userName").focus();//显示登入弹窗后，获取焦点
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