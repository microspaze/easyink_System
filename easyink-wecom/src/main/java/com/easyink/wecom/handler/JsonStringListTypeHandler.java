package com.easyink.wecom.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * JSON字符串与List<String>互转的TypeHandler
 * 数据库中存储为VARCHAR,Java中为List<String>
 *
 * @author easyink
 */
@MappedTypes(List.class)
@MappedJdbcTypes(JdbcType.VARCHAR)
public class JsonStringListTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, JSON.toJSONString(parameter));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return parseList(rs.getString(columnName));
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return parseList(rs.getString(columnIndex));
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return parseList(cs.getString(columnIndex));
    }

    private List<String> parseList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return Collections.emptyList();
        }
        try {
            return JSONArray.parseArray(json, String.class);
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
