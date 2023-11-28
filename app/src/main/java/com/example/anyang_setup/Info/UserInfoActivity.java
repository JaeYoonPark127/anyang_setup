package com.example.anyang_setup.Info;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.anyang_setup.EmploymentDocuments.SubActivity.Personal.PersonalMainActivity;
import com.example.anyang_setup.EmploymentDocuments.SubActivity.Spec.SpecActivity;
import com.example.anyang_setup.GlobalVariables;
import com.example.anyang_setup.Info.SubActivity.DiagnosisActivity;
import com.example.anyang_setup.LoginDB.LoginRequest;
import com.example.anyang_setup.R;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.anyang_setup.EmploymentDocuments.Chatting.ChatActivity;
import com.example.anyang_setup.Resume.Resume_MainActivity;
import com.example.anyang_setup.Setting.SettingActivity;
import com.example.anyang_setup.MakingResume.MainActivity_start;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;


public class UserInfoActivity extends AppCompatActivity {
    private Button button;
    private TextView getScoreText, remainScoreText, majorScoreText, generalScoreText;
    private TextView stdNameText, stdIdText, majorText;
    private TextView asd;
    private String STID;
    private String userInfoStr;

    //homeactivity.java
    private NavigationBarView navigationBarView;

    private String userinfo;

    private JSONObject userInfoJson;

    private String ID; // 학번
    private String NAME; //이름

    // Firebase Storage 레퍼런스 추가
    private StorageReference mStorageRef;

    // 갤러리에서 이미지를 선택하기 위한 요청 코드
    private static final int GALLERY_REQUEST_CODE = 123;
    private ImageView profileImageView;

    @SuppressLint({"MissingInflatedId", "ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userInfoStr = getIntent().getStringExtra("userinfo");
        button = findViewById(R.id.diagnosis);
        getScoreText = findViewById(R.id.Earned_Credits);
        remainScoreText = findViewById(R.id.Remaining_Credits);
        majorScoreText = findViewById(R.id.major_Credits);
        generalScoreText = findViewById(R.id.Liberal_Arts_Credits);
        stdNameText = findViewById(R.id.Name);
        stdIdText = findViewById(R.id.StudentID);
        majorText = findViewById(R.id.Major);

        // 버튼을 찾아서 클릭 리스너를 설정합니다.
        RelativeLayout button11 = findViewById(R.id.personal_button);// 자기소개서 버튼
        RelativeLayout button12 = findViewById(R.id.spec_button);// 스펙 버튼
        RelativeLayout button13 = findViewById(R.id.resume_button);// 이력서 버튼
        RelativeLayout employmentButton = findViewById(R.id.employment_button);


        Intent intent_STID = getIntent();
        STID = intent_STID.getStringExtra("STID");

        try{
            JSONObject jsonObject = new JSONObject(userInfoStr);
            JSONObject dataObj = jsonObject.getJSONObject("data");
            ID = dataObj.getString("stdId");
            NAME = dataObj.getString("stdName");

            Response.Listener<String> responseListener = new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject( response );
                        boolean success = jsonObject.getBoolean( "success" );

                        if(success) {//로그인 성공시

                            //Toast.makeText( getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT ).show();
                            //Intent intent = new Intent( LoginActivity.this, UserInfoActivity.class );
                            //startActivity(intent);


                        } else {//로그인 실패시
                            // Toast.makeText( getApplicationContext(), "로그인 실패", Toast.LENGTH_SHORT ).show();
                            //return;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            };

            LoginRequest loginRequest = new LoginRequest(ID,NAME, responseListener);
            RequestQueue queue = Volley.newRequestQueue( UserInfoActivity.this );
            queue.add( loginRequest );
        }
        catch (JSONException e) {
            e.printStackTrace();
        }


        button.setOnClickListener(view -> {
            String name = stdNameText.getText().toString();
            String major = majorText.getText().toString();
            GlobalVariables.setGlobalVariable_Name(name);
            GlobalVariables.setGlobalVariable_Major(major);
            Intent intent = new Intent(UserInfoActivity.this, DiagnosisActivity.class);
            intent.putExtra("userinfo", userInfoStr);
            startActivity(intent);
        });

        try {
            JSONObject jsonObject = new JSONObject(userInfoStr);
            JSONObject dataObj = jsonObject.getJSONObject("data");
            JSONObject creditStatus = dataObj.getJSONObject("creditStatus");
            getScoreText.setText(Integer.toString(creditStatus.getInt("total_current")));
            remainScoreText.setText(Integer.toString(creditStatus.getInt("total_remain")));
            majorScoreText.setText(Integer.toString(creditStatus.getInt("major_current")));
            generalScoreText.setText(Integer.toString(creditStatus.getInt("general_current")));
            stdNameText.setText(dataObj.getString("stdName"));
            stdIdText.setText(dataObj.getString("stdId"));
            majorText.setText(dataObj.getString("stdDepart"));
            asd.setText(dataObj.getString("stdsigan"));
            String name = stdNameText.getText().toString();
            String major = majorText.getText().toString();
            GlobalVariables.setGlobalVariable_Name(name);
            GlobalVariables.setGlobalVariable_Major(major);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        userinfo = getIntent().getStringExtra("userinfo");

        try {
            userInfoJson = new JSONObject(userinfo);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Log.d("rkdqudtjs", "userinfo: " + userinfo);

        // 사용자 정보와 프로필 사진을 로드합니다.
        loadUserProfile();

        profileImageView = findViewById(R.id.ID_Picture); // 프로필 이미지 뷰
        mStorageRef = FirebaseStorage.getInstance().getReference(); // Firebase Storage 초기화

        ImageView idPictual = findViewById(R.id.ID_Picture);
        idPictual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        button11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Personal Statement Activity로 이동하는 코드를 추가합니다.
                Intent intent = new Intent(UserInfoActivity.this, PersonalMainActivity.class);
                intent.putExtra("STID",STID);
                intent.putExtra("userinfo", userInfoStr);
                startActivity(intent);
            }
        });

        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // SpecActivity로 이동하는 코드를 추가합니다.
                Intent intent = new Intent(UserInfoActivity.this, SpecActivity.class);
                intent.putExtra("userinfo", userInfoStr);
                startActivity(intent);
            }
        });

        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ResumeMainActivity로 이동하는 코드를 추가합니다.
                Intent intent = new Intent(UserInfoActivity.this, Resume_MainActivity.class);
                intent.putExtra("userinfo", userInfoStr);
                startActivity(intent);
            }
        });

        employmentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent를 사용하여 브라우저 열기
                String url = "https://www.jobkorea.co.kr/";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            }
        });

        navigationBarView = findViewById(R.id.bottom_navigationview);

        navigationBarView.setItemIconTintList(null);

        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener()
        {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.home :
                    {
                        /*Intent intent = new Intent(UserInfoActivity.this, UserInfoActivity.class);
                        intent.putExtra("userinfo", userinfo);
                        startActivity(intent);*/
                        break;
                    }
                    case R.id.setting :
                    {
                        Intent intent = new Intent(UserInfoActivity.this, SettingActivity.class);
                        intent.putExtra("userinfo", userinfo);
                        startActivity(intent);
                        break;
                    }
                    case R.id.info :
                    {
                        try {
                            JSONObject data = userInfoJson.getJSONObject("data");
                            String Name = data.getString("stdId");
                            Log.e("Telechips", Name);

                            Intent intent = new Intent(UserInfoActivity.this, ChatActivity.class);
                            intent.putExtra("name", Name);
                            startActivity(intent);
                        }
                        catch (JSONException e)
                        {
                            throw new RuntimeException(e);
                        }

                        break;
                    }
                }

                return true;
            }
        });
    }

    private void loadUserProfile() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");

        usersRef.child(ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // 프로필 이미지 URL을 가져오기
                    String imageUrl = dataSnapshot.child("imageUrl").getValue(String.class);
                    // ImageView에 이미지를 로드
                    loadImageIntoView(imageUrl);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(UserInfoActivity.this, "데이터 로드 실패: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 갤러리를 여는 메소드
    private void openGallery() {
        // 권한 확인
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        } else {
            // 권한이 있을 경우 갤러리를 바로 연다.
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, GALLERY_REQUEST_CODE);
        }
    }

    // 권한 요청 결과를 처리하는 메소드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "갤러리 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // 갤러리에서 이미지를 선택한 결과를 처리하는 메소드
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == GALLERY_REQUEST_CODE) {
            Uri selectedImage = data.getData();
            uploadImageToFirebase(selectedImage);
        }
    }

    // 이미지를 Firebase Storage에 업로드하는 메소드
    private void uploadImageToFirebase(Uri imageUri) {
        if (imageUri != null) {
            StorageReference fileRef = mStorageRef.child("users/" + System.currentTimeMillis() + ".jpg");
            fileRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                // 업로드된 이미지 URL과 함께 사용자 정보를 Realtime Database에 저장
                String imageUrl = uri.toString();
                updateUserInfo(ID, NAME, imageUrl);
                // 업로드 성공 후 ImageView에 이미지 로드
                loadImageIntoView(imageUrl);
            })).addOnFailureListener(e -> {
                Toast.makeText(UserInfoActivity.this, "업로드 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    // 이미지 URL을 받아 ImageView에 로드하는 메소드
    private void loadImageIntoView(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .into(profileImageView);
    }

    // 사용자 정보를 Firebase Realtime Database에 업데이트하는 메소드
    private void updateUserInfo(String userId, String name, String imageUrl) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference usersRef = database.getReference("users");
        DatabaseReference currentUserRef = usersRef.child(userId);

        Map<String, Object> profileUpdates = new HashMap<>();
        profileUpdates.put("name", name);
        profileUpdates.put("imageUrl", imageUrl);

        currentUserRef.updateChildren(profileUpdates).addOnSuccessListener(aVoid -> {
            // 정보 업데이트 성공 시 행동
            Toast.makeText(UserInfoActivity.this, "정보 업데이트 성공", Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            // 정보 업데이트 실패 시 행동
            Toast.makeText(UserInfoActivity.this, "정보 업데이트 실패: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}