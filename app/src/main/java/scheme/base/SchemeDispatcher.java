package scheme.base;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by wangzhiyuan on 2015/10/19.
 */
public class SchemeDispatcher {

    public static final String TAG = SchemeDispatcher.class.getSimpleName();

    public static HashMap<String, Class<? extends ISchemeDealer>> sds = new HashMap<String, Class<? extends ISchemeDealer>>();

    static {
        try {
            sds.put(AuthorityKey.data.name(), SpcSchemeDeal.class);
        } catch (Throwable e) {
        }
    }

    private Context context;

    public SchemeDispatcher(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("-----WTF-----:context can not be null");
        }
        this.context = context;
    }

    /**
     * Dispatch uri data to different business.
     *
     * @param uri
     */
    public void dispatchRequest(Uri uri) {
        //get scheme
        String scheme = uri.getScheme();
        if (!SchemeConstants.SPC_TEST_SCHEME_NAME.equals(scheme)) {
            return;
        }
        //get host
        String host = uri.getEncodedAuthority();
        //get param
        HashMap<String, String> map = IntentUtils.splitParams1(uri);
        //add original uri string to params
        map.put(ISchemeDealer.ORIGINAL_URI, uri.toString());
        //get segment
        String type = uri.getLastPathSegment();
        Log.d("TAG","==scheme=="+scheme+",host="+host+",type="+type);
        try {
            sds.get(host).getConstructor(Context.class).newInstance(context).deal(type, map);
        } catch (Throwable e) {
        }

    }

}
