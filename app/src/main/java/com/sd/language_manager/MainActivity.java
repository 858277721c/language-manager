package com.sd.language_manager;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sd.lib.langmgr.LanguageModel;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final LanguageModel model = LanguageModel.getCurrent(this);
        model.apply(getResources());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (LanguageModel.SIMPLIFIED_CHINESE.equals(model))
        {
            ((TextView) (findViewById(R.id.btn_simplified_chinese))).setTextColor(Color.RED);
        } else if (LanguageModel.TRADITIONAL_CHINESE.equals(model))
        {
            ((TextView) (findViewById(R.id.btn_traditional_chinese))).setTextColor(Color.RED);
        } else if (LanguageModel.ENGLISH.equals(model))
        {
            ((TextView) (findViewById(R.id.btn_english))).setTextColor(Color.RED);
        }
    }

    @Override
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.btn_simplified_chinese:
                LanguageModel.setCurrent(this, LanguageModel.SIMPLIFIED_CHINESE);
                recreate();
                break;
            case R.id.btn_traditional_chinese:
                LanguageModel.setCurrent(this, LanguageModel.TRADITIONAL_CHINESE);
                recreate();
                break;
            case R.id.btn_english:
                LanguageModel.setCurrent(this, LanguageModel.ENGLISH);
                recreate();
                break;
        }
    }
}