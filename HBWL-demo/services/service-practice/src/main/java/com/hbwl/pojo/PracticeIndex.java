package com.hbwl.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 查询问题的方法：
 * 1.查询单节课course的课后问题：在练习课程索引表中根据course_section_id和course_id进行查询获得练习id，再从练习问题索引表中进行查询获取问题组
 * 2.查询整个课程courseSection的练习问题：在练习课程索引表中根据course_section_id进行查询获得练习id组，再从练习问题索引表中进行查询获取问题组
 *
 * 练习课程索引表，通过课程id可以查询相关练习的id数组
 * 单节课练习：两个id都有；整个课程的练习：只有course_section_id，course_id为0
 * */

@Data
public class PracticeIndex {
    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer practiceId;
    private Integer courseSectionId;
    private Integer courseId; //课程对应id，若无则为0,即为整个课程的练习
}
