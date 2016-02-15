package com.lgcampos.doodlz;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.SeekBar;

/**
 * @author Lucas Gonçalves de Campos
 * @since 1.0.0
 * 12/02/2016
 */
public class ColorDialogFragment extends DialogFragment {

    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private View colorView;
    private int color;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View colorDialogView = getActivity().getLayoutInflater().inflate(R.layout.fragment_color, null);
        builder.setView(colorDialogView);
        builder.setTitle(R.string.title_color_dialog);

        alphaSeekBar = (SeekBar) colorDialogView.findViewById(R.id.alpha_seek_bar);
        alphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        redSeekBar = (SeekBar) colorDialogView.findViewById(R.id.red_seek_bar);
        redSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        greenSeekBar = (SeekBar) colorDialogView.findViewById(R.id.green_seek_bar);
        greenSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        blueSeekBar = (SeekBar) colorDialogView.findViewById(R.id.blue_seek_bar);
        blueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        colorView = colorDialogView.findViewById(R.id.color_view);


        final DoodlzView doodlzView = getDoodlzFragment().getDoodlzView();
        color = doodlzView.getDrawingColor();
        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        builder.setPositiveButton(R.string.button_set_color, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doodlzView.setDrawingColor(color);
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

    private final SeekBar.OnSeekBarChangeListener colorChangedListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                color = Color.argb(alphaSeekBar.getProgress(), redSeekBar.getProgress(), greenSeekBar.getProgress(), blueSeekBar.getProgress());
                colorView.setBackgroundColor(color);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    };

    private MainActivityFragment getDoodlzFragment() {
        return (MainActivityFragment) getFragmentManager().findFragmentById(R.id.doodlz_fragment);
    }
}
