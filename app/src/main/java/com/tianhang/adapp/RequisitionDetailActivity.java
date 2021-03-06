package com.tianhang.adapp;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tianhang.adapp.domain.RequisitionBean;
import com.tianhang.adapp.domain.RequisitionDetailBean;
import com.tianhang.adapp.rest.RestClient;
import com.tianhang.adapp.util.ConvertJSONDate;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class RequisitionDetailActivity extends ActionBarActivity {

    private Toolbar mToolbar;
    private CharSequence mTitle;
    private ArrayList<RequisitionDetailBean> list = new ArrayList<RequisitionDetailBean>();
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //intent
        Intent intent = getIntent();
        String ID = intent.getStringExtra("requisitionID");
        getAllRequisitionDetail(ID);

        overridePendingTransition(R.animator.right_to_left, R.animator.left_to_right);
        setContentView(R.layout.activity_requisition_detail);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(new MyAdapter());
        setTitle();

        //Toast.makeText(RequisitionDetailActivity.this, ID, Toast.LENGTH_SHORT).show();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_requisition_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.animator.back_enter,R.animator.back_exit);
    }
    public void setTitle() {
        mTitle = "Requisition";
        getSupportActionBar().setTitle(mTitle);
    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View view;
            if(convertView != null){
                view = convertView;
            }else {
                view = View.inflate(RequisitionDetailActivity.this,R.layout.activity_requisition_detail_item,null);
            }
            TextView tv_depa_name = (TextView)view.findViewById(R.id.tv_depa_name);
            TextView tv_reqDate = (TextView)view.findViewById(R.id.tv_reqDate);
            TextView tv_number = (TextView)view.findViewById(R.id.tv_number);
            TextView tv_description = (TextView)view.findViewById(R.id.tv_description);

            tv_depa_name.setText(list.get(position).getName());
            tv_reqDate.setText(list.get(position).getReqDate());
            tv_number.setText(list.get(position).getNumber()+"");
            tv_description.setText(list.get(position).getDescription());
            return view;
        }
    }

    public void getAllRequisitionDetail(String rid){

        RestClient.get("/getRequisitionDetail/"+rid, null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String result = new String(bytes);
                JSONObject jsonObject = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int j = 0; j < jsonArray.length(); j++) {

                        JSONObject jObject = new JSONObject(jsonArray.get(j).toString());

                        RequisitionDetailBean requisitionDetailBean = new RequisitionDetailBean();
                        requisitionDetailBean.setName(jObject.getString("Name"));
                        requisitionDetailBean.setDescription(jObject.getString("Description"));
                        requisitionDetailBean.setNumber(jObject.getInt("Number"));
                        requisitionDetailBean.setReqDate(ConvertJSONDate.convert(jObject.getString("ReqDate")));

                        list.add(requisitionDetailBean);
                        //Log.i("bean25", ConvertJSONDate.convert(jObject.getString("requestDate")));

                    }
                } catch (Exception e) {
                    Log.e("JSON Parser", "Error psrsing data" + e.toString());
                }
                //Toast.makeText(mActivity, "result" + result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                Toast.makeText(RequisitionDetailActivity.this, "request network failed !", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
