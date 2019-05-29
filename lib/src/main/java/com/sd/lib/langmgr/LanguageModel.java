package com.sd.lib.langmgr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class LanguageModel
{
    /**
     * 简体中文，对应{@link Locale#SIMPLIFIED_CHINESE}
     */
    public static final LanguageModel SIMPLIFIED_CHINESE = new LanguageModel(Locale.SIMPLIFIED_CHINESE, "简体中文");
    /**
     * 繁体中文，对应{@link Locale#TRADITIONAL_CHINESE}
     */
    public static final LanguageModel TRADITIONAL_CHINESE = new LanguageModel(Locale.TRADITIONAL_CHINESE, "繁體中文");
    /**
     * 英文，对应{@link Locale#ENGLISH}
     */
    public static final LanguageModel ENGLISH = new LanguageModel(Locale.ENGLISH, "English");

    private static final String PERSISTENT_KEY = LanguageModel.class.getName();

    private final String mLanguage;
    private final String mCountry;

    private final String mName;

    public LanguageModel(Locale locale, String name)
    {
        this(locale.getLanguage(), locale.getCountry(), name);
    }

    private LanguageModel(String language, String country, String name)
    {
        if (TextUtils.isEmpty(language) || TextUtils.isEmpty(name))
            throw new IllegalArgumentException("language or name is empty when create LanguageModel");

        if (country == null)
            country = "";

        mLanguage = language;
        mCountry = country;
        mName = name;
    }

    /**
     * 返回语言名称
     *
     * @return
     */
    public String getName()
    {
        return mName;
    }

    /**
     * 转{@link Locale}
     *
     * @return
     */
    public Locale toLocale()
    {
        return new Locale(mLanguage, mCountry);
    }

    /**
     * 将Resources修改为当前语言
     *
     * @param resources
     */
    public void apply(Resources resources)
    {
        final Locale locale = toLocale();

        final Configuration configuration = resources.getConfiguration();
        if (Build.VERSION.SDK_INT >= 17)
        {
            configuration.setLocale(locale);
        } else
        {
            configuration.locale = locale;
        }

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof LanguageModel))
            return false;

        final LanguageModel other = (LanguageModel) obj;

        return mLanguage.equals(other.mLanguage)
                && mCountry.equals(other.mCountry)
                && mName.equals(other.mName);
    }

    @Override
    public int hashCode()
    {
        return (mLanguage + "_" + mCountry + "_" + mName).hashCode();
    }

    /**
     * 返回当前App的语言
     *
     * @param context
     * @return
     */
    public static LanguageModel getCurrent(Context context)
    {
        LanguageModel model = queryModel(context);
        if (model == null)
        {
            final Locale locale = context.getResources().getConfiguration().locale;
            model = LanguageManager.getInstance().getLanguageModel(locale);
        } else
        {
            if (LanguageManager.getInstance().containsLanguage(model))
            {
                return model;
            } else
            {
                // 保存的对象已经不在注册列表中了，清空保存的对象，并返回默认的处理对象
                clearModel(context);
                return LanguageManager.getInstance().getDefaultLanguageModel();
            }
        }

        return model;
    }

    /**
     * 设置当前App的语言对象
     *
     * @param model
     * @param context
     * @return
     */
    public static boolean setCurrent(Context context, LanguageModel model)
    {
        if (LanguageManager.getInstance().containsLanguage(model))
        {
            return saveModel(context, model);
        } else
        {
            throw new IllegalArgumentException("LanguageModel is not registered: " + model);
        }
    }

    /**
     * 保存对象到本地
     *
     * @param context
     * @param model
     * @return
     */
    private static boolean saveModel(Context context, LanguageModel model)
    {
        if (model == null)
            throw new IllegalArgumentException("LanguageModel is null when save to local");

        final String saveString = toJson(model);
        if (TextUtils.isEmpty(saveString))
            return false;

        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPreferences.edit().putString(PERSISTENT_KEY, saveString).commit();
    }

    /**
     * 查询本地保存的对象
     *
     * @param context
     * @return
     */
    private static LanguageModel queryModel(Context context)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        final String json = sharedPreferences.getString(PERSISTENT_KEY, null);
        return fromJson(json);
    }

    /**
     * 清空本地保存的对象
     *
     * @param context
     * @return
     */
    private static boolean clearModel(Context context)
    {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (sharedPreferences.contains(PERSISTENT_KEY))
            return sharedPreferences.edit().remove(PERSISTENT_KEY).commit();

        return false;
    }

    private static String toJson(LanguageModel model)
    {
        try
        {
            final JSONObject jsonObject = new JSONObject();
            jsonObject.put("language", model.mLanguage);
            jsonObject.put("country", model.mCountry);
            jsonObject.put("name", model.mName);
            return jsonObject.toString();
        } catch (JSONException e)
        {
            return null;
        }
    }

    private static LanguageModel fromJson(String json)
    {
        try
        {
            final JSONObject jsonObject = new JSONObject(json);
            final String language = jsonObject.getString("language");
            final String country = jsonObject.getString("country");
            final String name = jsonObject.getString("name");
            return new LanguageModel(language, country, name);
        } catch (Exception e)
        {
            return null;
        }
    }
}
