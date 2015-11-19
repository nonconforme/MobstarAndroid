package com.mobstar.api;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.mobstar.api.responce.*;
import com.mobstar.api.responce.Error;

/**
 * Created by lipcha on 08.09.15.
 */
public abstract class ConnectCallback<T extends BaseResponse> {

    public abstract void onSuccess(T object);
    public abstract void onFailure(String error);
    public abstract void onServerError(Error error);

    public void parse(JSONObject o) {
        T object = null;
        try {
            object = getTypeParameterClass().newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        if (object == null)
            return;
        try {
            object.configure(o);
        } catch (JSONException e) {
            if (e != null) {
                e.printStackTrace();
                onFailure(e.toString());
            }
            return;
        }
        if (object.hasError())
            onServerError(object.getError());
        else
            onSuccess(object);
    }

    @SuppressWarnings ("unchecked")
    public Class<T> getTypeParameterClass()
    {
        final Type type = getClass().getGenericSuperclass();
        final ParameterizedType paramType = (ParameterizedType) type;
        return (Class<T>) paramType.getActualTypeArguments()[0];
    }
}
