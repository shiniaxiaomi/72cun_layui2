package com.lyj.model;

/**
 * Created by Yingjie.Lu on 2019/1/22.
 */

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 反馈消息的实体类
 */
@NoArgsConstructor(force = true) //生成无参构造方法
@Getter
@Setter
public class Message implements Serializable {

    int id;

    @NotEmpty(message = "内容不能为空")
    String detail;//详细内容

    int userId;//发送者id

    String userName;//发送者名称

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    Date sendTime;//发送时间

    int rootId;//根节点id

    int parentId;//父节点id

}
