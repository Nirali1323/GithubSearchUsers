package com.codebosses.githubsearchusers.activities;

import androidx.appcompat.app.AppCompatActivity;
import com.codebosses.githubsearchusers.adapter.UserAdapter;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import com.codebosses.githubsearchusers.R;
import com.codebosses.githubsearchusers.adapter.UserAdapter;
import com.codebosses.githubsearchusers.databinding.ActivityFollowersBinding;
import com.codebosses.githubsearchusers.databinding.ActivityRepositoryBinding;
import com.codebosses.githubsearchusers.endpoints.EndpointKeys;
import com.codebosses.githubsearchusers.pojo.user.UserData;
import com.codebosses.githubsearchusers.pojo.user.UserMainObject;
import com.codebosses.githubsearchusers.retrofit.RetrofitClient;
import com.codebosses.githubsearchusers.retrofit.RetrofitInterface;

import java.util.ArrayList;
import java.util.List;
public class Repository extends AppCompatActivity implements UserAdapter.UserClickListener {
    private ActivityRepositoryBinding repositoryBinding;
    //    Android fields....
    //private ActivityRepositorysBinding repositoryBinding;

    //    Retrofit fields....
    private RetrofitInterface retrofitInterface;
    private Call<List<UserData>> userMainObjectCall;


    //    Instance fields....
    private List<UserData> userDataList = new ArrayList<>();

    //    Adapter fields....
    UserAdapter userAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        repositoryBinding = DataBindingUtil.setContentView(this, R.layout.activity_repository);

//        Setting custom action bar....
        Toolbar toolbar = findViewById(R.id.toolbarUserRepository);
       // toolbar.setOnClickListener((View.OnClickListener) this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.repositories);
        }

        userAdapter = new UserAdapter(this, userDataList);
        userAdapter.setUserClickListener(this);
        repositoryBinding.recyclerViewUserRepository.setLayoutManager(new LinearLayoutManager(this));
        repositoryBinding.recyclerViewUserRepository.setItemAnimator(new DefaultItemAnimator());
        repositoryBinding.recyclerViewUserRepository.setAdapter(userAdapter);

//        Getting followers....
        if (getIntent() != null) {
            Intent intent = getIntent();
            String userName = intent.getStringExtra(EndpointKeys.USER_NAME);

//        Retrofit instance....
            retrofitInterface = RetrofitClient.getClient().create(RetrofitInterface.class);

            getRepository(userName);

        }
    }

    private void getRepository(String repoName) {
        repositoryBinding.progressBarUserRepository.setVisibility(View.VISIBLE);
        userMainObjectCall = retrofitInterface.getRepository(repoName);
        userMainObjectCall.enqueue(new Callback<List<UserData>>() {
            @Override
            public void onResponse(Call<List<UserData>> call, Response<List<UserData>> response) {
                repositoryBinding.progressBarUserRepository.setVisibility(View.GONE);
                if (response != null && response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().size() > 0) {
                            repositoryBinding.textViewNoRepositoryFound.setVisibility(View.GONE);
                            userDataList.addAll(response.body());
                            userAdapter.notifyItemRangeChanged(0, userDataList.size());
                        } else {
                            repositoryBinding.textViewNoRepositoryFound.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<UserData>> call, Throwable t) {
                repositoryBinding.progressBarUserRepository.setVisibility(View.GONE);
                repositoryBinding.textViewNoRepositoryFound.setVisibility(View.VISIBLE);
                repositoryBinding.textViewNoRepositoryFound.setText(t.getMessage());
            }
        });
    }

    @Override
    public void onUserClick(View view, int position) {
        Intent intent = new Intent(Repository.this, UserDetailActivity.class);
        intent.putExtra(EndpointKeys.USER_ID, userDataList.get(position).getId());
        intent.putExtra(EndpointKeys.USER_NAME, userDataList.get(position).getLogin());
        intent.putExtra(EndpointKeys.USER_AVATAR, userDataList.get(position).getAvatarUrl());
        startActivity(intent);

    }

    @Override
    public void onFollowerClick(View view, int position) {

    }
    @Override
    public void onRepositoryClick(View view, int position){


    }
}
