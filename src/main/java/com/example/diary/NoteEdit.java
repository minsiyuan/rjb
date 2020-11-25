package com.example.diary;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.Intent;
import android.database.Cursor;



public class NoteEdit extends AppCompatActivity implements View.OnClickListener{
    private TextView time;
    private NoteDB dop;
    private EditText Title;
    private EditText Author;
    private EditText Content;
    private Button btn_save;
    private Button btn_photo;
    private ImageView imageView;
    Intent intent;
    String editModel = null;
    int item_Id;
    private SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit);
        InitView();
    }
    //按钮，文本事件
    private void InitView() {
        time = (TextView) findViewById(R.id.TIME);
        Title = (EditText) findViewById(R.id.TITLE);
        Author = (EditText) findViewById(R.id.AUTHOR);
        Content= (EditText) findViewById(R.id.CONTEXT);
        btn_save = (Button) findViewById(R.id.ButtonSave);
        btn_photo=findViewById(R.id.ButtonPhoto);
        imageView=findViewById(R.id.IMG);
        dop = new NoteDB(this,db);
        intent = getIntent();
        editModel = intent.getStringExtra("editModel");
        item_Id = intent.getIntExtra("noteId", 0);
        loadData();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");//时间
        String dateString = sdf.format(date);
        time.setText(dateString);
        btn_save.setOnClickListener(this);
        btn_photo.setOnClickListener(this);
    }
    //按钮点击处理
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ButtonSave: //取EditText中的内容
                String title= Title.getText().toString();
                String author= Author.getText().toString();
                String context = Content.getText().toString();
                String title1;
                //截取EditText中的前10个作为title
                if(context.length()<10){
                    title1 = title;
                }
                else{
                    title1 = title.substring(0,10);
                }
                if(context.isEmpty()){
                    Toast.makeText(NoteEdit.this, "记事不能为空!", Toast.LENGTH_LONG).show();
                }
                else{
                    //获取时间
                    SimpleDateFormat   formatter   =   new   SimpleDateFormat   ("yyyy-MM-dd HH:mm");
                    Date   curDate   =   new   Date(System.currentTimeMillis());//时间
                    String   time   =   formatter.format(curDate);
                    dop.create_db(); //打开数据库
                    // 判断是更新还是新增
                    if(editModel.equals("newAdd")){
                        //新增的话将title,author，context,time放进数据库
                        dop.insert_db(title1,author,context,time);
                        //图片保存，读取图片
                        SavePic(lastid(db),imageView);

                    }
                    //更新的话将title,author，context,time,放入数据库，并加入item_Id
                    else if(editModel.equals("update")){
                        dop.update_db(title1,author,context,time,item_Id);
                        SavePic(item_Id,imageView);
                    }
                    dop.close_db();//关闭数据库
                    NoteEdit.this.finish();//结束
                }
                break;
            case R.id.ButtonPhoto:
                Intent intent = new Intent(Intent.ACTION_PICK, null);
                intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        "image/*");
                startActivityForResult(intent, 0x1);
                break;
        }
    }
    private void loadData(){
        //获取数据
        if(editModel.equals("newAdd")){//新建清空EDITTEXT
            Title.setText("");
        }
        else if(editModel.equals("update")){//如果已存在的话，将数据从数据库中取出，放在editText中
            dop.create_db();
            Cursor cursor = dop.query_db(item_Id);
            cursor.moveToFirst();
            String title = cursor.getString(cursor.getColumnIndex("title"));
            Title.append(title);
            String author = cursor.getString(cursor.getColumnIndex("author"));
            Author.append(author);
            String context = cursor.getString(cursor.getColumnIndex("context"));
            Content.append(context);
            //显示图片
            ShowPic(item_Id,imageView);
            dop.close_db();
        }
    }
    //保存图片，从SharedPreferences保存
    //保存路径
    protected void SavePic(int id,ImageView imageView){
        SharedPreferences sharedPreferences = getSharedPreferences("image_file",MODE_PRIVATE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ((BitmapDrawable)imageView.getDrawable()).getBitmap()
                .compress(Bitmap.CompressFormat.JPEG,50,stream);
        String imageBase64 = new String(Base64.encodeToString(stream.toByteArray(), Base64.DEFAULT));
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("SaveImage"+id,imageBase64);
        editor.commit();
    }
    //显示图片
    protected void ShowPic(int id,ImageView imageView){
        SharedPreferences sharedPreferences = getSharedPreferences("image_file",MODE_PRIVATE);

        String imageBase64 = sharedPreferences.getString("SaveImage"+id,"");
        byte[] base64byte = Base64.decode(imageBase64,Base64.DEFAULT);
        Bitmap bitmap= BitmapFactory.decodeByteArray(base64byte,0,base64byte.length);
        imageView.setImageBitmap(bitmap);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        if (requestCode == 0x1 && resultCode == RESULT_OK) {
            if (data != null) {
                imageView.setImageURI(data.getData());

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    private int lastid( SQLiteDatabase db){
        String sql = "select last_insert_rowid() from notes" ;
        Cursor cursor = db.rawQuery(sql, null);
        int a = -1;
        if(cursor.moveToFirst()){
            a = cursor.getInt(0);
        }
        return a;

    }
}

