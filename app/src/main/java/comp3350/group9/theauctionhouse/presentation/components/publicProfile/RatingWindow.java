package comp3350.group9.theauctionhouse.presentation.components.publicProfile;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import comp3350.group9.theauctionhouse.databinding.PopupRatingBinding;
public class RatingWindow extends PopupWindow {
    private final PopupRatingBinding binding;
    private final Context context;

    public RatingWindow(Context context, View parent, Activity contextActivity) {
        super(context);
        binding = PopupRatingBinding.inflate(LayoutInflater.from(context));
        this.context = context;
        setContentView(binding.getRoot());
        setOutsideTouchable(true);
        setFocusable(true);


        parent.setOnTouchListener((v, event) -> {
            // if the user clicks outside the pop-up window, dismiss it
            if (event.getAction() == MotionEvent.ACTION_DOWN && isShowing()) {
                dismiss();
                return true;
            }
            return false;
        });

        // visual layout
        View parentView = ((ViewGroup) contextActivity.findViewById(android.R.id.content)).getChildAt(0);
        int parentWidth = parentView.getWidth();
        int parentHeight = parentView.getHeight();
        setWidth((int) (parentWidth * 0.75));
        setHeight(parentHeight / 2);
        setAnimationStyle(android.R.style.Animation_Dialog);

        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }


    public void setRatingBtnClickListener(
            RatingWindow.OnRatingBtnClickListener handler,
            boolean closeOnFinish){

        binding.rating1.setOnClickListener(v -> {
            handler.onClick(1);
            if(closeOnFinish) dismiss();
        });

        binding.rating2.setOnClickListener(v -> {
            handler.onClick(2);
            if(closeOnFinish) dismiss();
        });

        binding.rating3.setOnClickListener(v -> {
            handler.onClick(3);
            if(closeOnFinish) dismiss();
        });

        binding.rating4.setOnClickListener(v -> {
            handler.onClick(4);
            if(closeOnFinish) dismiss();
        });

        binding.rating5.setOnClickListener(v -> {
            handler.onClick(5);
            if(closeOnFinish) dismiss();
        });
    }

    public interface OnRatingBtnClickListener { void onClick(int value); }
}
