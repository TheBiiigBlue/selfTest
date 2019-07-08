package com.bigblue.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @Author: TheBigBlue
 * @Description:
 * @Date: 2019/5/28
 */
@Data
@TableName(value = "S_USER")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private Integer age;
    private Date time;
}
