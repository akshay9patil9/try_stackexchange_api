package com.example.akshay.stackexchange;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    Long toDate = System.currentTimeMillis()/1000;
    Long fromDate = toDate - 24*60*60*1000;;
    Button viewMore = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final LinearLayout container = (LinearLayout)findViewById(R.id.questions_container);

        viewMore = new Button(this);
        viewMore.setText(R.string.view_more);

        viewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getQuestions(getUrl(), container);
                container.removeView(viewMore);
            }
        });

        getQuestions(getUrl(), container);


    }

//    private LinearLayout getQuestionCard2(JSONObject question){
//
//        LinearLayout questionCard = null;
//
//        try{
//
//            questionCard = new LinearLayout(getApplicationContext());
//            questionCard.setOrientation(LinearLayout.VERTICAL);
//
//            TextView questionTitleView = new TextView(getApplicationContext());
//            TextView questionViewCountView = new TextView(getApplicationContext());
//            questionTitleView.setText(question.getString("title"));
//            questionViewCountView.setText("Views : "+question.getString("view_count"));
//            questionCard.addView(questionTitleView);
//            questionCard.addView(questionViewCountView);
//
//            GradientDrawable gd = new GradientDrawable();
//            gd.setColor(0xFF0000FA);
//            gd.setCornerRadius(10);
//            gd.setStroke(10, 0xFF000000);
//            questionCard.setBackground(gd);
//
//
//            ViewGroup.LayoutParams questionCardParams = questionCard.getLayoutParams();
//            questionCardParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//            questionCardParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
//            questionCard.setLayoutParams(questionCardParams);
//            questionCard.setPadding(50,50,50,50);
//
//        }catch (JSONException e){
//
//        }finally {
//            return questionCard;
//        }
//
//
//    }

    private ConstraintLayout getQuestionCard(JSONObject question){

        ConstraintLayout questionCard = null;

        try{

            questionCard = (ConstraintLayout) View.inflate(
                    this,
                    R.layout.question_card, null);

            JSONObject user = question.getJSONObject("owner");
            final String avatarUrl = user.getString("profile_image");
            final String userName = user.getString("display_name");
            final String questionTitle = question.getString("title");
            final String questionId = question.getString("question_id");
            final String views = question.getString("view_count");
            final String question_body = question.getString("body");

            TextView questionTitleView = questionCard.findViewById(R.id.question_tile);
            questionTitleView.setText(question.getString("title"));

            TextView viewCountView = questionCard.findViewById(R.id.view_count_view);
            viewCountView.setText(views+" views");

            TextView userNameView = questionCard.findViewById(R.id.user_name_view);
            userNameView.setText(userName);

            ImageView avatarView =  questionCard.findViewById(R.id.avatar_view);
            new ImageLoadTask(avatarUrl, avatarView).execute();

            questionCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AnswersActivity.class);
                Bundle b= new Bundle();
                b.putString("question_title", questionTitle);
                b.putString("question_id", questionId);
                b.putString("user_name", userName);
                b.putString("views", views);
                b.putString("avatar_url",avatarUrl);
//                Log.d("QBODY",question_body);
                b.putString("question_body",question_body);
                i.putExtras(b);
                startActivity(i);
                }
            });



        }catch (JSONException e){

        }finally {
            return questionCard;
        }


    }

    private void getQuestions(String url, final LinearLayout container){

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE","Response: " + response.toString());
                        try{

                            JSONArray questions = response.getJSONArray("items");
                            for(int i =0; i< questions.length(); i++){
                                ConstraintLayout questionCard = getQuestionCard(questions.getJSONObject(i));
                                if(questionCard!=null){

                                    container.addView(questionCard);

                                }

                            }
                            container.addView(viewMore);
                        }catch (JSONException e){

                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


        queue.add(jsonObjectRequest);
    }

    private String getUrl(){
        String url ="http://api.stackexchange.com/2.2/questions?order=desc&sort=activity&site=stackoverflow&fromdate="+fromDate+"&todate="+toDate+"&filter=withbody";
        toDate = fromDate;
        fromDate = toDate - 24*60*60*1000;;
        return  url;
    }

}

