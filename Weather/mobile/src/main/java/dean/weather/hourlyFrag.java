package dean.weather;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by DeanF on 9/16/2016.
 */
//public class hourlyFrag extends Fragment {
//    GridLayout gridLayout;
//    LinearLayout hourlyLayout;
//    TextView hourlyDetailsBtn;
//    ImageView hourlyConditions1;
//    ImageView hourlyConditions2;
//    ImageView hourlyConditions3;
//    ImageView hourlyConditions4;
//    Typeface robotoLight;
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        return inflater.inflate(R.layout.hourly_fragment, container, false);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    @Override
//    public void onViewCreated(View view, Bundle savedInstanceState) {
//        //Setup references
//        gridLayout = (GridLayout) getView().findViewById(R.id.hourlyGrid);
//        hourlyConditions1 = (ImageView) getView().findViewById(R.id.hourlyConditions1);
//        hourlyConditions2 = (ImageView) getView().findViewById(R.id.hourlyConditions2);
//        hourlyConditions3 = (ImageView) getView().findViewById(R.id.hourlyConditions3);
//        hourlyConditions4 = (ImageView) getView().findViewById(R.id.hourlyConditions4);
//        robotoLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
//        //Gather all TextViews and attach a reference
//        ArrayList<TextView> textViewArrayList = new ArrayList<>();
//        for(int i = 0; i < gridLayout.getChildCount(); i++ ){
//            if(gridLayout.getChildAt(i) instanceof TextView){
//                textViewArrayList.add((TextView) gridLayout.getChildAt(i));
//            }
//        }
//
//        //Change font on each TextView
//        for(TextView tv: textViewArrayList){
//            tv.setTypeface(robotoLight);
//        }
//
//        //Setup click events
//        hourlyLayout = (LinearLayout) getView().findViewById(R.id.dailyLayout);
//        hourlyDetailsBtn = (TextView) getView().findViewById(R.id.hourlyDetailsBtn);
//
//        hourlyLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Open hourly activity
//                Intent hourlyIntent = new Intent(getContext(), hourlyActivity.class);
//                startActivity(hourlyIntent);
//            }
//        });
//
//        hourlyDetailsBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Open hourly activity
//                Intent hourlyIntent = new Intent(getContext(), hourlyActivity.class);
//                startActivity(hourlyIntent);
//            }
//        });
//    }
//}

