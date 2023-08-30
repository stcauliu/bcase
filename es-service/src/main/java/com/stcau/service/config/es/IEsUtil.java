package com.stcau.service.config.es;


import com.stcau.service.config.es.entity.EsQuery;
import org.elasticsearch.action.get.GetResponse;

import java.io.IOException;
import java.util.List;

/**
 * @DES:
 * @auth: 01437949
 * 2020/2/12
 */
public interface  IEsUtil {


    /**
     *  验证index 是否存在
     * @param index
     * @return
     * @throws IOException
     */
    boolean existsIndex(String index) throws IOException;

    /**
     *  创建索引
     * @param index
     * @throws IOException
     */
    void createIndex(String index,String type) throws IOException ;


    /**
     * 新增记录
     * @param index
     * @param type
     * @param id
     * @param entity
     * @throws IOException
     */
    void addRecord(String index, String type,String id, Object entity) throws IOException ;

    /**
     * 查询记录
     * @param index
     * @param type
     * @param id
     * @throws IOException
     */
    GetResponse getRecord(String index, String type, String id) throws IOException ;

    /**
     * 更新记录
     * @param index
     * @param type
     * @param id
     * @param entity
     * @throws IOException
     */
    void updateRecord(String index, String type, String id,Object entity) throws IOException ;

    /**
     * 删除记录
     * @param index
     * @param type
     * @param id
     * @throws IOException
     */
    void deleteRecord(String index, String type, String id) throws IOException ;


    void search(String index, String type, String name) throws IOException ;
    /**
     * 批量操作
     * @param index
     * @param type
     * @param list
     * @throws IOException
     */
    void bulk(String index,String type,List<?> list) throws IOException ;

    /**
     * 公共查询方法
     * @param esQuery
     * @return
     * @throws IOException
     */
    EsQuery getIndexInfo(EsQuery esQuery) throws IOException;

    /**
     * 深度查询符合条件的所有信息
     * @param esQuery
     * @return
     */
    List<String> getAllIndexInfo(EsQuery esQuery) throws IOException;

    /**
     * 分组查询
     * @param esQuery
     * @param groupCodeList
     * @return
     */

    List<String> getGroupIndex(EsQuery esQuery, List<String> groupCodeList);


}
