package com.lyj.model.linkModel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/2/24.
 */


/**
 * user和url的中间表
 */
@NoArgsConstructor(force = true) //生成无参构造方法
@Getter
@Setter
@Accessors(chain = true)
public class User_HotUrl implements Serializable {

    private int userId;

    private int likeUrlId;//点赞的urlId

}
