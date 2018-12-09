/**
 * 统一设定自定义模块的路径,每个模块都应用这个js即可直接使用自定义模块
 */

layui.config({
  base : "/myjs/",//设定扩展的Layui模块的所在目录，这个目录是其他扩展模块的根目录,如果其他模块都直接在根目录,即可直接使用
	version : '1.0.0'
});

layui.use(['layer','jquery','form','element'],function () {
    var layer = layui.layer;

    //配置弹出层全局效果
    layer.config({
        shade: 0.1,//遮罩
    });

})