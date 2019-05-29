package com.sd.lib.langmgr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LanguageManager
{
    private static final Map<Locale, LanguageModel> MAP_LOCALE_LANGUAGE = new HashMap<>();
    private static final Map<LanguageModel, String> MAP_LANGUAGE = new LinkedHashMap<>();

    private static final LanguageManager INSTANCE = new LanguageManager();
    private LanguageModel mDefaultLanguageModel;

    private LanguageManager()
    {
        // 简体中文
        register(Locale.SIMPLIFIED_CHINESE, LanguageModel.SIMPLIFIED_CHINESE);
        register(Locale.CHINESE, LanguageModel.SIMPLIFIED_CHINESE);

        // 繁体中文
        register(Locale.TRADITIONAL_CHINESE, LanguageModel.TRADITIONAL_CHINESE);
        register(new Locale("zh", "HK"), LanguageModel.TRADITIONAL_CHINESE);

        // 英文
        register(Locale.ENGLISH, LanguageModel.ENGLISH);
        register(Locale.US, LanguageModel.ENGLISH);
        register(Locale.UK, LanguageModel.ENGLISH);
        register(Locale.CANADA, LanguageModel.ENGLISH);
    }

    public static LanguageManager getInstance()
    {
        return INSTANCE;
    }

    /**
     * 返回默认的语言处理对象
     * <p>
     * 第一次调用{@link #register(Locale, LanguageModel)}方法的时候绑定的对象会被当做默认的语言处理对象
     *
     * @return
     */
    public LanguageModel getDefaultLanguageModel()
    {
        if (mDefaultLanguageModel == null)
            throw new RuntimeException("You must invoke register() method before this");

        return mDefaultLanguageModel;
    }

    /**
     * 注册
     * <p>
     * 第一次调用此方法的时候绑定的对象会被当做默认的语言处理对象
     *
     * @param locale        目标语言
     * @param languageModel 目标语言的处理对象
     */
    public void register(Locale locale, LanguageModel languageModel)
    {
        if (locale == null || languageModel == null)
            throw new IllegalArgumentException("local or languageModel is null when register");

        MAP_LOCALE_LANGUAGE.put(locale, languageModel);
        MAP_LANGUAGE.put(languageModel, null);

        if (mDefaultLanguageModel == null)
            mDefaultLanguageModel = languageModel;
    }

    /**
     * 情况所有注册的语言
     */
    public void clearRegister()
    {
        MAP_LOCALE_LANGUAGE.clear();
        MAP_LANGUAGE.clear();
        mDefaultLanguageModel = null;
    }

    /**
     * 返回目标语言对应的处理对象
     *
     * @param locale 目标语言
     * @return
     */
    public LanguageModel getLanguageModel(Locale locale)
    {
        if (locale == null)
            throw new IllegalArgumentException("local is null");

        LanguageModel languageModel = MAP_LOCALE_LANGUAGE.get(locale);
        if (languageModel == null)
            languageModel = getDefaultLanguageModel();

        return languageModel;
    }

    /**
     * 是否支持某个语言
     *
     * @param model
     * @return
     */
    public boolean containsLanguage(LanguageModel model)
    {
        return MAP_LANGUAGE.containsKey(model);
    }

    /**
     * 返回所有已注册的{@link LanguageModel}
     *
     * @return
     */
    public List<LanguageModel> getAllLanguageModel()
    {
        final List<LanguageModel> list = new ArrayList<>(MAP_LANGUAGE.size());
        for (Map.Entry<LanguageModel, String> item : MAP_LANGUAGE.entrySet())
        {
            list.add(item.getKey());
        }
        return list;
    }
}
