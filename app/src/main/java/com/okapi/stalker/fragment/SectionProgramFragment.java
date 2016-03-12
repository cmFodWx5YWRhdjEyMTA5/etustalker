package com.okapi.stalker.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.data.storage.type.Interval;
import com.okapi.stalker.data.storage.type.Section;
import com.okapi.stalker.data.storage.type.Student;

import java.io.Serializable;
import java.util.Set;


public class SectionProgramFragment extends Fragment {
    private View rootView;
    private Section section;

    public SectionProgramFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Stash stash = Stash.get();

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_program, container, false);

            int pixels = dpToPx(getContext(), 50);
            int pixels10 = dpToPx(getContext(), 10);
            int pixels2 = dpToPx(getContext(), 2);

            TextView[] buttons = new TextView[78];
            LinearLayout linearLayout =
                    (LinearLayout) rootView.findViewById(R.id.calendarSplitterRelativeLayout);

            LinearLayout.LayoutParams prm = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 2);
            prm.setMargins(pixels2, 0, 0, 0);

            RelativeLayout relativeLayout = null;
            for (int i = 0; i < buttons.length; i++) {
                TextView button = new TextView(getActivity());


                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, pixels10);
                if (i % 13 != 0) {
                    params.addRule(RelativeLayout.BELOW, i);
                } else {
                    relativeLayout = new RelativeLayout(getActivity());
                    relativeLayout.setLayoutParams(prm);
                    linearLayout.addView(relativeLayout);
                }

                button.setLayoutParams(params);
                button.setHeight(pixels);
                button.setGravity(Gravity.CENTER);
                button.setBackgroundColor(Color.LTGRAY);
                button.setId(i + 1);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO
                    }
                });

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }

                relativeLayout.addView(button);
                buttons[i] = button;
            }

            // Intervals are coming...
            String[] colors = {"#a5de5b", "#009AE3", "#f584d4", "#f74448", "#c3903f", "#63b526"};
            int color = Color.parseColor(colors[((int)(Math.random() * colors.length))]);
            Set<String> intervalKeys = section.getIntervalKeys();
            for (String intervalKey : intervalKeys) {
                    Interval interval = stash.getInterval(intervalKey);
                    int indeks = (interval.day.ordinal() * 13) + interval.time.ordinal();
                    buttons[indeks].setText(interval.classRoom.name);
                    buttons[indeks].setBackgroundColor(color);
                    buttons[indeks].setTextSize(13);
                    buttons[indeks].setTextColor(Color.WHITE);
            }


            rootView.findViewById(R.id.refreshLinearLayout).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.refreshView).setVisibility(View.INVISIBLE);
        }
        // Time line initializing
        Time time = new Time();
        time.setToNow();
        int minutes = (time.hour * 60) + time.minute - 510;
        LinearLayout currentTimeLine = (LinearLayout) rootView.findViewById(R.id.currentTimeMarkerLinearLayout);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(getActivity(), minutes), 0, 0);
        currentTimeLine.setLayoutParams(params);
        return rootView;
    }


    private int dpToPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public void setSection(Section section) {
        this.section = section;
    }
}