package ru.mail.park.awesome_android;

import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewManager;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private Button addButton;
    private EditText enterIngredient;
    private LinearLayout addedIngredients;
    private ArrayList ingredientsArray = new ArrayList();

    private String ingredient;

    private View.OnClickListener onRemoveButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout parent = (LinearLayout)((LinearLayout)v.getParent()).getParent();
            LinearLayout childLayoutWithButton = (LinearLayout)v.getParent();
            TextView childText = (TextView)parent.getChildAt(0);

            ((ViewManager)parent).removeView(childText);
            ((ViewManager)parent).removeView(childLayoutWithButton);
            ((ViewManager)parent).removeView(parent);
        }
    };

    private View.OnClickListener onAddButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            ingredient = enterIngredient.getText().toString();
            if (ingredient.length() == 0)  {
                return;
            }
            enterIngredient.setText(""); //убрать хардкод

            LinearLayout layoutWithIngredientAndButton = new LinearLayout(getBaseContext());
            layoutWithIngredientAndButton.setOrientation(LinearLayout.HORIZONTAL);

            LinearLayout layoutWithButton = new LinearLayout(getBaseContext());
            layoutWithButton.setGravity(Gravity.RIGHT);

            Button remove = new Button(getBaseContext());
            remove.setText("-"); //убрать хардкод
            remove.setWidth(200);
            remove.setOnClickListener(onRemoveButtonClickListener);
            layoutWithButton.addView(remove);



            TextView text = new TextView(getBaseContext());
            text.setText(ingredient);
            text.setGravity(Gravity.LEFT);
            text.setTextSize(20); //убрать хардкод
            text.setWidth(800);
            layoutWithIngredientAndButton.addView(text);
            layoutWithIngredientAndButton.addView(layoutWithButton);

            addedIngredients.addView(layoutWithIngredientAndButton);


        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterIngredient = (EditText)findViewById(R.id.ingredient);
        addButton = (Button)findViewById(R.id.add_button);


        addedIngredients = (LinearLayout) findViewById(R.id.add_ingredient_layout);

        addButton.setOnClickListener(onAddButtonClickListener);
    }
}
