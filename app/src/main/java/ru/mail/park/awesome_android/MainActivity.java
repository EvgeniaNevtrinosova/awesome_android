package ru.mail.park.awesome_android;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private Button addButton;
    private EditText enterIngredient;
    private TextView addedIngredients;
    private ArrayList ingredientsArray = new ArrayList();

    private StringBuilder ingredients = new StringBuilder("");

    private String ingredient;

    private View.OnClickListener onAddButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            ingredient = enterIngredient.getText().toString();
            ingredientsArray.add(ingredient);

            ingredients.append(ingredientsArray.get(ingredientsArray.size()-1));
            ingredients.append("\n");

            enterIngredient.setText("");
            addedIngredients.setText(ingredients.toString());
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enterIngredient = (EditText)findViewById(R.id.ingredient);
        addButton = (Button)findViewById(R.id.add_button);
        addedIngredients = (TextView)findViewById(R.id.ingredient_name);

        addButton.setOnClickListener(onAddButtonClickListener);
    }
}
