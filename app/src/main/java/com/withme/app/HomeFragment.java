package com.withme.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.withme.app.adapters.PostRvAdapter;
import com.withme.app.models.Post;

import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    private RecyclerView rvPost;
    private List<Post> posts;
    private PostRvAdapter postAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        rvPost = view.findViewById(R.id.rv_post_home);

        // create few dummy posts
        posts = new ArrayList<>();

        posts.add(new Post("", "https://dynamic-media-cdn.tripadvisor.com/media/photo-o/12/ee/ab/bc/paneer-veggie-platter.jpg?w=600&h=400&s=1",
                100, 50, "Canada", "123",
                "Join me on a delicious journey as we explore local eateries, hidden gems, and mouthwatering recipes from around the world! Taste the adventure with every bite."));

        posts.add(new Post("", "https://www.expertbakeryconsultant.com/upload/category/1672754722fast-food-restaurants.jpg",
                50, 100, "Germany", "123",
                "Savor the flavor with us! From street food to gourmet dining, we’re on a mission to find the tastiest meals and the stories behind them. Come hungry!"));

        posts.add(new Post("", "https://d4t7t8y8xqo0t.cloudfront.net/resized/750X436/eazytrendz%2F2930%2Ftrend20200903104959.jpg",
                45, 500, "Switzerland", "123",
                "Get ready to tantalize your taste buds! Join me as I cook, taste, and review a variety of dishes while exploring culinary traditions from every corner of the globe."));

        // add 10 posts with same contest change only likes and comments count


        // Set the adapter and layout manager
        postAdapter = new PostRvAdapter(posts, getActivity());
        rvPost.setAdapter(postAdapter);
        rvPost.setLayoutManager(new LinearLayoutManager(getActivity()));

        return view;
    }
}