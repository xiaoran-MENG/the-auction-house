package comp3350.group9.theauctionhouse.presentation.components.auctions;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import comp3350.group9.theauctionhouse.R;
import comp3350.group9.theauctionhouse.business.common.DateTime;
import comp3350.group9.theauctionhouse.databinding.PopupCreateAuctionBinding;
import comp3350.group9.theauctionhouse.presentation.BaseActivity;

public class CreateAuctionWindow extends PopupWindow {
    private final PopupCreateAuctionBinding binding;
    private final Context context;

    protected BaseActivity app;
    private static final int CAMERA_REQUEST = 1888;

    public CreateAuctionWindow(Context context, View parent, FragmentActivity contextActivity) {
        super(context);
        binding = PopupCreateAuctionBinding.inflate(LayoutInflater.from(context));
        this.context = context;
        setContentView(binding.getRoot());
        setOutsideTouchable(true);
        setFocusable(true);

        binding.popupcreateauctionCloseBtn.setOnClickListener((v) -> {
            if (isShowing()) dismiss();
        });

        binding.popupcreateauctionExpiryPickerBtn.setOnClickListener(v -> {
            DialogFragment newFragment = new DatePickerFragment((view,year,month,day) -> {
                DateTime date = DateTime.of(year, DateTime.Months.values()[month],day);
                binding.popupcreateauctionExpiryText.setText(date.toString());
            });
            newFragment.show(contextActivity.getSupportFragmentManager(), "datePicker");
        });

        // Opens an image
        binding.popupcreateauctionImageButton.setOnClickListener(v -> {
            ((BaseActivity)contextActivity).writePermissionCheckAndRequest();
            if (!((BaseActivity)contextActivity).hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(contextActivity, "Lack the required permissions", Toast.LENGTH_SHORT).show();
                return;
            }

            BaseActivity.setImagePath("");
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(contextActivity, cameraIntent, CAMERA_REQUEST, null);
        });

        // visual layout
        View parentView = ((ViewGroup) contextActivity.findViewById(android.R.id.content)).getChildAt(0);
        int parentWidth = parentView.getWidth();
        int parentHeight = parentView.getHeight();
        setWidth((int) (parentWidth * 0.90));
        setHeight((int) (parentHeight * 0.90));
        setAnimationStyle(android.R.style.Animation_Dialog);

        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    public void setSubmitButtonClickListener(OnCreateBtnClickListener handler, boolean closeOnFinish){
        binding.popupcreateauctionCreateBtn.setOnClickListener(v -> {
            handler.onClick(v, this, this.binding);
            if(closeOnFinish) dismiss();
        });
    }

    public void setCategoryTags(List<String> array){
        ChipGroup chipGroup = binding.popupcreateauctionChipGroup;
        chipGroup.isSelectionRequired();

        for (String tag : array){
            Chip chip = new Chip(context);
            chip.setText(tag);
            chip.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            chip.setVisibility(View.VISIBLE);
            chip.setChipBackgroundColorResource(R.color.secondary);
            chip.setCloseIconVisible(false);
            chip.setCheckable(true);
            chip.setTextSize(TypedValue.COMPLEX_UNIT_SP,22);
            chip.setTextColor(ContextCompat.getColor(context, R.color.white));

            chipGroup.addView(chip);
        }
    }

    public interface OnCreateBtnClickListener { void onClick(View var1, PopupWindow instance, PopupCreateAuctionBinding bindings); }
}


