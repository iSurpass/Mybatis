package bmatch.tech.hello.dao;

import bmatch.tech.hello.dataobject.Folder;
import java.util.List;

import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

@Mapper
public interface FolderMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table folder
     *
     * @mbg.generated Sun Dec 09 22:55:28 GMT+08:00 2018
     */
    @Delete({
        "delete from folder",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table folder
     *
     * @mbg.generated Sun Dec 09 22:55:28 GMT+08:00 2018
     */
    @Insert({
        "insert into folder (id, name, ",
        "gmtCreated, gmtModified)",
        "values (#{id,jdbcType=INTEGER}, #{name,jdbcType=VARCHAR}, ",
        "now(), now())"
    })
    int insert(Folder record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table folder
     *
     * @mbg.generated Sun Dec 09 22:55:28 GMT+08:00 2018
     */
    @Select({
        "select",
        "id, name, gmtCreated, gmtModified",
        "from folder",
        "where id = #{id,jdbcType=INTEGER}"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="gmtCreated", property="gmtcreated", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmtModified", property="gmtmodified", jdbcType=JdbcType.TIMESTAMP)
    })
    Folder selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table folder
     *
     * @mbg.generated Sun Dec 09 22:55:28 GMT+08:00 2018
     */
    @Select({
        "select",
        "id, name, gmtCreated, gmtModified",
        "from folder"
    })
    @Results({
        @Result(column="id", property="id", jdbcType=JdbcType.INTEGER, id=true),
        @Result(column="name", property="name", jdbcType=JdbcType.VARCHAR),
        @Result(column="gmtCreated", property="gmtcreated", jdbcType=JdbcType.TIMESTAMP),
        @Result(column="gmtModified", property="gmtmodified", jdbcType=JdbcType.TIMESTAMP)
    })
    List<Folder> selectAll();

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table folder
     *
     * @mbg.generated Sun Dec 09 22:55:28 GMT+08:00 2018
     */
    @Update({
        "update folder",
        "set name = #{name,jdbcType=VARCHAR},",
          "gmtModified = now()",
        "where id = #{id,jdbcType=INTEGER}"
    })
    int updateByPrimaryKey(Folder record);
}