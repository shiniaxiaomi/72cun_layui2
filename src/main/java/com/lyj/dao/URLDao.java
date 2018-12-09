package com.lyj.dao;

import com.lyj.model.URL;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by 陆英杰
 * 2018/9/25 9:42
 */
@Repository
public interface URLDao{

    //add,delete.update,get
    //增
    @Insert("insert into url (url,label,pid,createTime,userId,pidName) values (#{url},#{label},#{pid},#{createTime},#{userId},#{pidName})")
    int addUrl(URL url);

    //删
    @Delete("delete from url where id=#{id}")
    int deleteUrlByUrlId(Integer id);

    @Delete("delete from url where pid=#{pid}")
    void deleteUrlByPid(Integer pid);

    //改
    //xml---根据id更新url
    int updateUrl(URL url);

    //查
    //xml---根据folderId查询url
    List<URL> getUrlsByPid(@Param("userId") Integer userId, @Param("pid") int pid, RowBounds rowBounds);//分页
    //xml---根据folderId查询总数
    int getUrlsCountByPid(@Param("userId") Integer userId, @Param("pid") int pid);//查询总数

    //xml---根据keywords查询url
    List<URL> getUrlsByKeywords(@Param("userId") Integer userId, @Param("keywords") String keywords, RowBounds rowBounds);//分页
    //xml---根据keywords查询总数
    int getUrlsCountByKeywords(@Param("userId") Integer userId, @Param("keywords") String keywords);//查询总数

    //xml---根据pidName查询url
    List<URL> getUrlsByPidName(@Param("userId") Integer userId, @Param("keywords") String keywords, RowBounds rowBounds);//分页
    //xml---根据pidName查询总数
    int getUrlsCountByPidName(@Param("userId") Integer userId, @Param("keywords") String keywords);//查询总数

    //xml---根据label和pidName查询url
    List<URL> getUrlsByLabelAndPidName(@Param("userId") Integer userId, @Param("label") String label, @Param("pidName") String pidName);
    //xml---根据urlName和pidNamee查询总数
    int getUrlsCountByLabelAndPidName(@Param("userId") Integer userId, @Param("label") String label, @Param("pidName") String pidName);






}
