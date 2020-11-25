package com.example.diary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.diary.MainActivity;
import com.example.diary.R;


public class Login extends AppCompatActivity {
    private SharedPreferences.Editor editor;
    private Button bt_1,bt_2;
    public EditText ex,ex_1;
    private SharedPreferences preferences;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ex_1=findViewById(R.id.password_text);
        ex = findViewById(R.id.phone_text);
        bt_1=findViewById(R.id.signUp1_btn);
        bt_2=findViewById(R.id.signIn1_btn);
        //得到preferences对象
        preferences= PreferenceManager.getDefaultSharedPreferences(Login.this);
        editor=preferences.edit();//得到编译器对象
        bt_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //上传数据
                editor.putString("number",ex.getText().toString());
                editor.putString("password",ex_1.getText().toString());
                editor.commit();//提交
                Toast.makeText(Login.this,"注册成功",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(Login.this,Login.class);
                ex.setText(ex.getText());
                startActivity(intent);
            }
        });
        bt_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(preferences.getString("number","").equals(ex.getText().toString().trim())
                        &&preferences.getString("password","").equals(ex_1.getText().toString().trim()))
                //是否匹配,去掉空格
                {
                    Toast.makeText(Login.this,"登陆成功",Toast.LENGTH_SHORT).show();
                    //Intent intent_1=new Intent(login.this,MainActivity.class);
                    Intent intent_1=new Intent(Login.this, MainActivity.class);
                    startActivity(intent_1);
                }
                else
                {
                    Toast.makeText(Login.this,"登录失败，请重新登陆",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}



