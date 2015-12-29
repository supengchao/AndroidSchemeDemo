package scheme.base;

import java.util.Map;

/**
 * Each business must implement this interface to deal with their own product logic. Use {@link SchemeDispatcher}
 * to dispatch uri data to each business.
 *
 * Created by wangzhiyuan on 2015/10/19.
 */
public interface ISchemeDealer {

    String ORIGINAL_URI = "original_uri";

    void deal(String type, Map<String, String> map);
}
