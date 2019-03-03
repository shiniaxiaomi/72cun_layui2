package com.lyj.dao;

import com.lyj.model.Folder;
import com.lyj.model.URL;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 陆英杰
 * 2018/9/25 9:42
 */
@Repository
public interface URLDao{

    //add,delete.update,get
    //增
    @Insert("insert into url (url,label,pid,createTime,userId,pidName,isShare,shareTime) values (#{url},#{label},#{pid},#{createTime},#{userId},#{pidName},#{isShare},#{shareTime})")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id") //数据插入成功后，id值被反填到user对象中，调用getId()就可以获取
    int addUrl(URL url);

    //删
    @Delete("delete from url where id=#{id}")
    int deleteUrlByUrlId(Integer id);

    @Delete("delete from url where pid=#{pid}")
    int deleteUrlByPid(Integer pid);

    //根据userid删除所有url
    @Delete("delete from url where userId=#{userId}")
    int deleteUrlByUserId(Integer userId);

    //xml---批量删除
    int deleteUrlsByIds_Batch(List<Integer> ids);

    //改
    //xml---根据id更新url
    int updateUrl(URL url);
    //xml---批量更新
    int updateUrlsByIds_Batch(@Param("ids")List<Integer> ids,@Param("pid")int pid,@Param("pidName")String pidName);

    @Update("update url set isShare=#{isShare},shareTime=#{shareTime} where id=#{id}")//修改链接的分享状态和分享时间
    int changeShareStatus(URL url);

    //查
    @Select("select * from url where userId=#{userId} and pid=#{pid} order by createTime desc")
    List<URL> getUrlsByPidPage(@Param("userId") Integer userId, @Param("pid") int pid);//分页

    @Select("select * from url where userId=#{userId} and pid=#{id}")
    List<URL> getUrlsByFolderId(Folder folder);

    @Select("select * from url where userId=#{userId} and label like CONCAT('%',#{label},'%') order by createTime desc")
    List<URL> getUrlsByLabel(@Param("userId") Integer userId, @Param("label") String label);

    @Select("select * from url where userId=#{userId} and label like CONCAT('%',#{label},'%') and pidName like CONCAT('%',#{pidName},'%') order by createTime desc")
    List<URL> getUrlsByLabelAndPidName(@Param("userId") Integer userId, @Param("label") String label, @Param("pidName") String pidName);

    @Select("select * from url where userId=#{userId} and pidName like CONCAT('%',#{pidName},'%') order by createTime desc")
    List<URL> getUrlsByPidName(@Param("userId") Integer userId, @Param("pidName") String pidName);

    //通过userId来查询共享链接
    @Select("select url.*,user.userName from url " +
            "left join user on url.userId=user.id " +
            "where userId=#{userId} and label like CONCAT('%',#{keywords},'%') and isShare=true order by shareTime desc")
    List<URL> getShareUrlsByUserIdLike(@Param("keywords")String keywords,@Param("userId")int userId);

    //公共的查询共享链接
    @Select("select url.*,user.userName from url " +
            "left join user on url.userId=user.id " +
            "where label like CONCAT('%',#{keywords},'%') and isShare=true order by shareTime desc")
    List<URL> getShareUrlsLike(@Param("keywords")String keywords);

    @Select("select * from url where userId=#{userId} order by createTime desc")
    List<URL> getUrlsByUserId(int userId);

    //xml
    int addUrlBatch(List<URL> list);


    //在批量插入时根据label来判断是否存在相同的网址
    @Select("select count(*) from url where userId=#{userId} and label=#{label}")
    int isExistesUrl(URL url);

    //xml
    List<URL> getUrlsByIdBatch(List list);

    @Select("select id from url where pid=#{pid} and isShare=true")
    List<Integer> getShareUrlIdsByPid(Integer pid);

    @Select("select id from url where userId=#{userId}")
    List<Integer> getUrlIdByUserId(Integer userId);

    @Select("select id from url where userId=#{userId} and isShare=true")
    List<Integer> getShareUrlIdsByUserId(Integer userId);
}
