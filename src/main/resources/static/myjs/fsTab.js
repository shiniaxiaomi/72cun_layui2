/**
 * tab的js
 */
layui.define(['element','layer'], function(exports){
  var element = layui.element
	  ,layer=layui.layer;

  FsTab = function (){
  	this.config = {
        	sideBarFilter:"fsSideBarMenu",//左边菜单id
  			topLeftMenuFilter:"fsTopLeftMenu",//头部左侧菜单id
  			topRightMenuFilter:"fsTopRightMenu",//头部右侧菜单id
			tabFilter:"fsTab"//选项卡filter的id
		}
	};

	FsTab.prototype.render = function(options){
		var thisTab = this;
		$.extend(true, thisTab.config, options);

		thisTab.bindTabFilter();

		//绑定侧边栏菜单点击。
		element.on('nav('+thisTab.config.sideBarFilter+')', function(elem){
			thisTab.add(elem);
			$('body').removeClass('site-mobile');
		});
		//绑定顶部左边菜单点击。
		element.on('nav('+thisTab.config.topLeftMenuFilter+')', function(elem){
            $('#'+thisTab.config.topLeftMenuFilter+' dl').removeClass("layui-show");
			thisTab.add(elem);
			$('body').removeClass('site-mobile');
		});
		//绑定顶部右边菜单点击。
		element.on('nav('+thisTab.config.topRightMenuFilter+')', function(elem){
            $('#'+thisTab.config.topRightMenuFilter+' dl').removeClass("layui-show");
			thisTab.add(elem);
			$('body').removeClass('site-mobile');
		});

		//菜单的提示信息
        $('.layui-nav .layui-nav-item dd').hover(
            function(){
                layer.tips($(this).text(),this);
            },
            function(){
                layer.closeAll('tips');
            }
        )

		//给首页添加滑过样式(首页是第一个)
		$("#fsTabMenu li").eq(0).hover(
            function(){
				$(this).css('font-size','19px')
            },
            function(){
                $(this).css('font-size','17px')
            }
        )


	};

	FsTab.prototype.uuid = function () {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
            var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
            return v.toString(16);
        });
    }

	/**
	 * 新增tab
	 */
	FsTab.prototype.add = function(elem) {
		var thisTab = this;
		var layId = $(elem).attr("lay-id");

		if(layId==undefined){
            layId = this.uuid();
		}
		//判断导航栏是否存在
		if($('#fsTabMenu>li[lay-id="'+layId+'"]').length==0){
			$(elem).attr("lay-id",layId);
			var dom =$(elem);
			var title = $(elem).find('cite').html();
			var dataUrl = dom.attr("url");
			if(dataUrl!=undefined){
				element.tabAdd(thisTab.config.tabFilter, {
				  title: title
				  ,content: '<iframe src="'+dom.attr("url")+'"></iframe>' //支持传入html
				  ,id: layId
				});

				//给新增加的tab添加滑过的样式
                var buff=$('#fsTabMenu li');
                $.each(buff,function (index, value) {
                	if($(value).attr('lay-id')==layId){
                		$(value).hover(
                            function(){
                                $(this).css('font-size','19px')
                            },
                            function(){
                                $(this).css('font-size','17px')
                            }
                        )
                        return false;//退出遍历
					}
                })

			}
		}
		thisTab.tabChange(layId);

	}

	/**
	 * 切换tab
	 */
	FsTab.prototype.tabChange = function(layId) {
		element.tabChange(this.config.tabFilter, layId);
	}

	/**
	 * 删除
	 */
	FsTab.prototype.del = function(layId) {
		element.tabDelete(this.config.tabFilter, layId);
	};


	/**
   * 删除监听
   */
	FsTab.prototype.bindDeleteFilter = function(){
		element.on('tabDelete('+this.config.tabFilter+')', function(data){
	  	var layId = $(this).parentsUntil().attr("lay-id");
	  	$('#'+this.config.sideBarFilter+' .layui-nav-child>dd[lay-id="'+ layId +'"],#'+this.config.sideBarFilter+'>li[lay-id="'+ layId +'"]').removeAttr("lay-id");
		});
	}

	/**
	 * 监听tab切换，处理菜单选中
	 */
	FsTab.prototype.bindTabFilter = function(){
		var thisTab = this;
		element.on('tab('+this.config.tabFilter+')', function(data){
			var layId = $(this).attr("lay-id");
            thisTab.menuSelectCss(layId);
		});
	}

	/**
	 * 菜单选中样式
	 */
	FsTab.prototype.menuSelectCss = function(layId){
		if(layId!=undefined){
			$(".layui-nav .layui-this").removeClass("layui-this");//清除所有菜单栏的选中样式
            var buff=$(".layui-nav .layui-nav-item a");
            for(var i=0;i<buff.length;i++){
                if($(buff[i]).attr('lay-id')==layId){
                    $(buff[i]).parent().addClass("layui-this");//追加样式

					//只有侧边栏才进行打开样式的控制
					if($(buff[i]).parents('ul').attr('id')==this.config.sideBarFilter){
                        $(buff[i]).parents('ul').find('li').removeClass('layui-nav-itemed');//先移除所有的打开样式
                        $(buff[i]).parents('li').addClass('layui-nav-itemed');//给这个a标签所在的li标记打开样式
					}
					break;
				}
			}
		}
	}


	var fsTab = new FsTab();

    //渲染tab
    fsTab.render();

  //绑定按钮
	exports("fsTab",fsTab);

});
