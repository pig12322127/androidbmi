package com.gasolin.android.abmi;

import java.text.DecimalFormat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//aBMI is for British system, gBMI is for metric system
public class Abmi extends Activity {
	//private static final String TAG = "aBmi";
	public static final String PREF_FEET = "BMI_Feet";
	public static final String PREF_INCH = "BMI_Inch";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViews();
        setListensers();
        restorePrefs();        
    }
    
    private Button button_calc;
    private Spinner field_feet;
    private Spinner field_inch;
    //private EditText field_feet;
    //private EditText field_inch;
    private EditText field_weight;
    private TextView view_result;
    private TextView view_suggest;
    
    private void findViews()
    {
    	//Log.d(TAG, "find Views");
    	button_calc = (Button) findViewById(R.id.submit);
    	field_feet = (Spinner) findViewById(R.id.feet);
    	field_inch = (Spinner) findViewById(R.id.inch);
    	//field_feet = (EditText) findViewById(R.id.feet);
        //field_inch = (EditText) findViewById(R.id.inch);
    	field_weight = (EditText) findViewById(R.id.weight);
    	view_result = (TextView) findViewById(R.id.result);
    	view_suggest = (TextView) findViewById(R.id.suggest);

    	ArrayAdapter<CharSequence> adapter_feet = ArrayAdapter.createFromResource(
                this, R.array.feets, android.R.layout.simple_spinner_item);
    	adapter_feet.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        field_feet.setAdapter(adapter_feet);

        ArrayAdapter<CharSequence> adapter_inch = ArrayAdapter.createFromResource(
                this, R.array.inches, android.R.layout.simple_spinner_item);
        adapter_inch.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        field_inch.setAdapter(adapter_inch);
    }
 
    //Listen for button clicks
    private void setListensers() {
    	//Log.d(TAG, "set Listensers");
    	//listen Array setOnItemClickListener
    	field_feet.setOnItemSelectedListener(getFeet);
    	field_inch.setOnItemSelectedListener(getInch);
    	button_calc.setOnClickListener(calcUsBMI);
    	//field_feet.setSelection(3);
    }

    // Restore preferences
    private void restorePrefs()
    {
    	SharedPreferences settings = getSharedPreferences(PREF_FEET, 0);
    	Integer pref_feet = settings.getInt(PREF_FEET, 5);
    	//if(pref_feet)
    	{
    		field_feet.setSelection(pref_feet-2);
    		field_inch.requestFocus();
    	}
    	SharedPreferences settings2 = getSharedPreferences(PREF_INCH, 0);
    	Integer pref_inch = settings2.getInt(PREF_INCH, 0);
    	//if(pref_inch)
    	{
    		field_inch.setSelection(pref_inch);
    		field_weight.requestFocus();
    	}
    }
    
    private int feet;
    private int inch;
    
    private Spinner.OnItemSelectedListener getFeet = new Spinner.OnItemSelectedListener()
    {
    	public void onItemSelected(AdapterView parent, View v, int position, long id) {
    		feet = parent.getSelectedItemPosition()+2;
    	}
    	public void onNothingSelected(AdapterView parent) {
    	}
    };

    private Spinner.OnItemSelectedListener getInch = new Spinner.OnItemSelectedListener()
    {
    	public void onItemSelected(AdapterView parent, View v, int position, long id) {
    		inch = parent.getSelectedItemPosition()+1;
    	} 
    	public void onNothingSelected(AdapterView parent) {
    		
    	}
    };
    
    private Button.OnClickListener calcUsBMI = new Button.OnClickListener()
    {
    	public void onClick(View v)
    	{
    		DecimalFormat nf = new DecimalFormat("0.00");
    		try{
	    		double height = (feet*12+inch)*2.54/100;
    			//double height = (Double.parseDouble(field_feet.getText().toString())*12+Double.parseDouble(field_inch.getText().toString()))*2.54/100;
	    		double weight = Double.parseDouble(field_weight.getText().toString())*0.45359;
	    		double BMI = weight / (height * height);
	            //Present result 
	            view_result.setText(getText(R.string.bmi_result) + nf.format(BMI));
	 
	            //Give health advice 
	            if(BMI>27){
	            	view_suggest.setText(R.string.advice_fat);
	            }else if(BMI>25){
	                view_suggest.setText(R.string.advice_heavy);
	            }else if(BMI<20){
	                view_suggest.setText(R.string.advice_light);
	            }else{
	                view_suggest.setText(R.string.advice_average);
	            }
	            Uri uri = Uri.parse("geo:25.047581, 121.517286");
	            final Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	            startActivity(intent);
    		}
    		catch(Exception obj)
    		{
    			Toast.makeText(Abmi.this, getString(R.string.input_error), Toast.LENGTH_SHORT).show();
    		}
    	}
    };
    
    protected static final int MENU_ABOUT = Menu.FIRST;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		//Log.d(TAG, "open Menu");
		menu.add(0, MENU_ABOUT, 0, R.string.about_label);
		return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item)
	{
		super.onOptionsItemSelected(item);
		//Log.d(TAG, "select Menu Item");
		switch(item.getItemId()){
			case MENU_ABOUT:
				openOptionsDialog();
				break;
		}
		return true;
	}

	private void openOptionsDialog() {
		//Log.d(TAG, "open Dialog");
		new AlertDialog.Builder(this)
		.setTitle(R.string.about_title)//.setView(view)
		.setMessage(R.string.about_msg)
		.setPositiveButton(R.string.ok_label,
				new DialogInterface.OnClickListener(){
					public void onClick(
							DialogInterface dialoginterface, int i){
					}
				})
		.show();
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		// Save user preferences. We need an Editor object to
		// make changes. All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREF_FEET, 3);
		SharedPreferences settings2 = getSharedPreferences(PREF_INCH, 0);
		//SharedPreferences.Editor editor = settings.edit();
		//editor.putString(PREF_HEIGHT, field_height.getText().toString());
		//commit edits
		//editor.commit();
		settings.edit()
			.putInt(PREF_FEET, feet)
			.commit();
		settings2.edit()
			.putInt(PREF_INCH, inch)
			.commit();
	}
}