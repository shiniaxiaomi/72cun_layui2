/**
 * 统一设定自定义模块的路径,每个模块都应用这个js即可直接使用自定义模块
 */

layui.config({
    //本地环境
    dir:"/layui/",//layui内置模块的所在目录的前缀
    extendDir:"",//本地开发的时候使用该前缀

    //测试环境
    // dir:"https://www.usetools.cn/layui/",//layui内置模块的所在目录的前缀
    // extendDir:"https://www.usetools.cn",//其他Layui模块的所在目录的前缀


    version : '1.0.1',
    base : "/myjs/",//设定扩展的Layui模块的所在目录，这个目录是其他扩展模块的根目录,如果其他模块都直接在根目录,即可直接使用
});