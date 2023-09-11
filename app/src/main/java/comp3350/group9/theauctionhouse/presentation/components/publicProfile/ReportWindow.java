package comp3350.group9.theauctionhouse.presentation.components.publicProfile;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.PopupWindow;
import android.widget.Spinner;
import java.util.List;
import comp3350.group9.theauctionhouse.databinding.PopupReportingBinding;

public class ReportWindow extends PopupWindow {
    private final PopupReportingBinding binding;
    private final Context context;

    public ReportWindow(Context context, View parent, Activity contextActivity) {
        super(context);
        binding = PopupReportingBinding.inflate(LayoutInflater.from(context));
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

    public void setReportBtnClickListener(OnReportBtnClickListener handler, boolean closeOnFinish){
        binding.reportButton.setOnClickListener(v -> {
            String reportType = binding.reportTypeSpinner.getSelectedItem().toString();
            String description = binding.reportDescriptionEdittext.getText().toString();
            handler.onClick(v, reportType, description);
            if(closeOnFinish) dismiss();
        });
    }

    public void setReportTypes(List<String> array){
        Spinner reportTypes = binding.reportTypeSpinner;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_spinner_item,
                array
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportTypes.setAdapter(adapter);
    }


    public interface OnReportBtnClickListener { void onClick(View var1, String reportType, String description); }
}

