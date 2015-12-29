package scheme.base;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.supengchao.myapplication.SpcAty;

import java.util.Map;

/**
 * Created by supengchao on 2015/10/22.
 */
public class SpcSchemeDeal implements ISchemeDealer {
    private Context context;

    public SpcSchemeDeal(Context context) {
        this.context=context;
    }

    @Override
    public void deal(String type, Map<String, String> map) {
        if(SchemeConstants.SPC_TEST_NAME.equalsIgnoreCase(type)){

            String orderId = map.get("id");
            Bundle bundle = new Bundle();
            bundle.putString("id", orderId);

            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setClass(context, SpcAty.class);
            context.startActivity(intent);
        }
    }
}
