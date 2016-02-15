package com.lgcampos.doodlz;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

/**
 * @author Lucas Gonçalves de Campos
 * @since 1.0.0
 * 12/02/2016
 */
public class LineWidthDialogFragment extends DialogFragment {

    private ImageView widthImageView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle bundle) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View lineWidthDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_line_width, null);
        builder.setView(lineWidthDialogView);
        builder.setTitle(R.string.title_line_width_dialog);

        widthImageView = (ImageView) lineWidthDialogView.findViewById(R.id.width_image_view);

        final DoodlzView doodlzView = getDoodlzFragment().getDoodlzView();

        final SeekBar widthSeekBar = (SeekBar) lineWidthDialogView.findViewById(R.id.width_seekbar);
        widthSeekBar.setOnSeekBarChangeListener(lineWidthChanged);
        widthSeekBar.setProgress(doodlzView.getLineWidth());

        builder.setPositiveButton(R.string.button_set_line_width, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doodlzView.setLineWidth(widthSeekBar.getProgress());

            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivityFragment fragment = getDoodlzFragment();

        if (fragment != null) {
            fragment.setDialogOnScreen(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivityFragment fragment = getDoodlzFragment();

        if (fragment != null) {
            fragment.setDialogOnScreen(false);
        }
    }


    private MainActivityFragment getDoodlzFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.doodlz_fragment);
    }

    private SeekBar.OnSeekBarChangeListener lineWidthChanged = new SeekBar.OnSeekBarChangeListener() {
        final Bitmap bitmap = Bitmap.createBitmap(400, 100, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            Paint paint = new Paint();
            paint.setColor(getDoodlzFragment().getDoodlzView().getDrawingColor());
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(progress);

            bitmap.eraseColor(getResources().getColor(android.R.color.transparent));

            canvas.drawLine(30, 50, 370, 50, paint);
            widthImageView.setImageBitmap(bitmap);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };
}
