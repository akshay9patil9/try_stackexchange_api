package com.example.akshay.stackexchange;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AnswersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answers);

        Bundle b = getIntent().getExtras();

        String questionId = b.getString("question_id");
        String questionTitle = b.getString("question_title");
        String userName = b.getString("user_name");
        String views = b.getString("views");
        String avatarUrl = b.getString("avatar_url");
        final String questionBody = b.getString("question_body");


        final LinearLayout answers_container = (LinearLayout)findViewById(R.id.answers_container);
        ConstraintLayout questionCard = (ConstraintLayout) View.inflate(
                this,
                R.layout.question_card, null);
        TextView questionTitleView = questionCard.findViewById(R.id.question_tile);
        questionTitleView.setText(Html.fromHtml(questionTitle));


        TextView userNameView = questionCard.findViewById(R.id.user_name_view);
        userNameView.setText(userName);
//
        TextView viewCountView = questionCard.findViewById(R.id.view_count_view);
        viewCountView.setText(views+" views");
//
        ImageView avatarView =  questionCard.findViewById(R.id.avatar_view);
        new ImageLoadTask(avatarUrl, avatarView).execute();

        questionCard.setElevation(50);
        questionCard.setBackgroundColor(0xFFFAFAFA);

        TextView questionT = new TextView(getApplicationContext());
        questionT.setText("Question - ");
        answers_container.addView(questionT);
        questionCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), DetailView.class);
                Bundle b= new Bundle();
                b.putString("body",questionBody);
                i.putExtras(b);
                startActivity(i);
            }
        });

        answers_container.addView(questionCard);


        String url = "https://api.stackexchange.com/2.2/questions/"+questionId+"/answers?order=desc&sort=activity&site=stackoverflow&filter=withbody";

        RequestQueue queue = Volley.newRequestQueue(this);


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("API_RESPONSE_ANSWERS","Response: " + response.toString());
                        try{

                            JSONArray answers = response.getJSONArray("items");
                            if(answers.length()==0){
                                TextView noAnswers = new TextView(getApplicationContext());
                                noAnswers.setText("No Answers Found!");
                                answers_container.addView(noAnswers);
                            }
                            else{
                                TextView noAnswers = new TextView(getApplicationContext());
                                noAnswers.setText("Answers - ");
                                answers_container.addView(noAnswers);
                            }


                            for(int i =0; i< answers.length(); i++){

//                                String answerBody = answers.getJSONObject(i).getString("body");

                                Log.d("API_ANSWERS",answers.getJSONObject(i).getString("body"));

                                ConstraintLayout answerCard = getQuestionCard(answers.getJSONObject(i));
                                if(answerCard!=null){

                                    Log.d("Added","Added");
                                    answers_container.addView(answerCard);

                                }
                            }

                        }catch (JSONException e){
                                Log.d("Exception",e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });


        queue.add(jsonObjectRequest);
    }

    private ConstraintLayout getQuestionCard(JSONObject question){

        ConstraintLayout questionCard = null;

        try{

            questionCard = (ConstraintLayout) View.inflate(
                    this,
                    R.layout.question_card, null);

            JSONObject user = question.getJSONObject("owner");
            final String avatarUrl = user.getString("profile_image");
            final String userName = user.getString("display_name");
//            final String questionTitle = question.getString("title");
//            final String questionId = question.getString("question_id");
            final String views = question.getString("score");

            TextView questionTitleView = questionCard.findViewById(R.id.question_tile);
            Log.d("BODY",question.getString("body")+questionTitleView.getText());
            final String body = question.getString("body");
            questionTitleView.setText(Html.fromHtml(body));

            TextView viewCountView = questionCard.findViewById(R.id.view_count_view);
            viewCountView.setText("score : "+views);
//
            TextView userNameView = questionCard.findViewById(R.id.user_name_view);
            userNameView.setText(userName);
//
            ImageView avatarView =  questionCard.findViewById(R.id.avatar_view);
            new ImageLoadTask(avatarUrl, avatarView).execute();

            questionCard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(getApplicationContext(), DetailView.class);
                    Bundle b= new Bundle();
                    b.putString("body",body);
                    i.putExtras(b);
                    startActivity(i);
                }
            });



        }catch (JSONException e){

            Log.d("EXCEPTION", e.getMessage());

        }finally {
            return questionCard;
        }


    }

}
