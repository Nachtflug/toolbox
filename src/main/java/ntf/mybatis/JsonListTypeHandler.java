package ntf.mybatis;

import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import com.google.gson.Gson;
import ntf.core.GenericTypeIdentified;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.lang.reflect.Type;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


public abstract class JsonListTypeHandler<T>
        extends BaseTypeHandler<List<T>>
        implements GenericTypeIdentified<T> {

	protected Gson gson = new Gson();

    protected Class<T> clazz = getGenericTypeClass();

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<T> parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, this.toJson(parameter));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return this.toObject(rs.getString(columnName));
    }

    @Override
    public List<T> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return this.toObject(rs.getString(columnIndex));
    }

    @Override
    public List<T> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return this.toObject(cs.getString(columnIndex));
    }

    private String toJson(List<T> object) {
        try {
            return gson.toJson(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
	private List<T> toObject(String content) {
        if (content != null && !content.isEmpty()) {
            try {
                return (List<T>) gson.fromJson(content, getActualListTypeToken());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }


    protected Type getActualListTypeToken() {
        // 伪造一个List<T> 并注入
        return new ParameterizedTypeImpl(new Type[]{clazz}, null, List.class);

    }


}
