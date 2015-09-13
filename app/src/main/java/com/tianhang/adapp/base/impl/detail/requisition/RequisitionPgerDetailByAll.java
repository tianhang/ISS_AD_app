package com.tianhang.adapp.base.impl.detail.requisition;

import android.app.Activity;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.tianhang.adapp.R;
import com.tianhang.adapp.base.BaseDetailPager;
import com.tianhang.adapp.domain.RequisitionBean;
import com.tianhang.adapp.rest.RestClient;
import com.tianhang.adapp.widget.RefreshListView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by student on 31/8/15.
 */
public class RequisitionPgerDetailByAll extends BaseDetailPager {

    private RefreshListView refreshListView;
    private ArrayList<RequisitionBean> list = new ArrayList<RequisitionBean>();
    private MyAdapter adapter;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(android.os.Message msg) {
            //更新UI
            adapter.notifyDataSetChanged();
            refreshListView.completeRefresh();
        };
    };

    public RequisitionPgerDetailByAll(Activity activity) {
        super(activity);
    }

    @Override
    public View initView() {
        View view = View.inflate(mActivity, R.layout.pager_requisition_detail_by_all, null);
        ViewUtils.inject(this, view); // 把当前的View对象注入到xUtils框架中
        refreshListView = (RefreshListView)view.findViewById(R.id.refreshListView);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        for(int i =0;i<30;i++){
            //list.add("listview data ->"+i);
        }
        // addHeadView must be operated before set adapter
        //View headView = View.inflate(mActivity,R.layout.layout_header,null);
        //refreshListView.addHeaderView(headView);

        adapter = new MyAdapter();
        refreshListView.setAdapter(adapter);

        refreshListView.setOnRefreshListener(new RefreshListView.OnRefreshListener() {
            @Override
            public void onPullRefresh() {
                //需要联网请求服务器的数据，然后更新UI
                requestDataFromServer(false);
            }

            @Override
            public void onLoadingMore() {
                requestDataFromServer(true);
            }
        });
        //------get------
        getAllRequisition();
    }

    /**
     * 模拟向服务器请求数据
     */
    private void requestDataFromServer(final boolean isLoadingMore){
        new Thread(){
            public void run() {
                SystemClock.sleep(3000);//模拟请求服务器的一个时间长度

                if(isLoadingMore){
                    //list.add("加载更多的数据-1");
                   // list.add("加载更多的数据-2");
                    //list.add("加载更多的数据-3");
                }else {
                    //list.add(0, "下拉刷新的数据");
                }

                //在UI线程更新UI
                handler.sendEmptyMessage(0);
            };
        }.start();
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
                view = View.inflate(mActivity,R.layout.pager_requisition_item,null);
            }
            TextView tv_depa = (TextView)view.findViewById(R.id.listview_requisition_item_depa);
            TextView tv_requ_id = (TextView)view.findViewById(R.id.listview_requisition_item_requ_id);
            TextView tv_date = (TextView)view.findViewById(R.id.listview_requisition_item_date);
            TextView tv_status = (TextView)view.findViewById(R.id.listview_requisition_item_status);

            // get position
            tv_date.setText(list.get(position).getRequestDate());
            tv_requ_id.setText(list.get(position).getRequisitionID());
            tv_depa.setText(list.get(position).getDepartmentID());
            tv_status.setText(list.get(position).getStatus());
            return view;
        }
    }

    public void getAllRequisition(){

        RestClient.get("/getAllRequisition", null, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int i, Header[] headers, byte[] bytes) {

                String result = new String(bytes);
                JSONObject jsonObject = new JSONObject();

                try {
                    JSONArray jsonArray = new JSONArray(result);
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jObject = new JSONObject(jsonArray.get(j).toString());
                        RequisitionBean requisitionBean = new RequisitionBean();
                        requisitionBean.setDepartmentID(jObject.getString("department"));
                        requisitionBean.setRequisitionID(jObject.getString("requisitionId"));
                        requisitionBean.setRejectReason(jObject.getString("rejectReason"));
                        requisitionBean.setStatus(jObject.getString("status"));
                        requisitionBean.setRequestDate(jObject.getString("requestDate"));
                        requisitionBean.setUserID(jObject.getString("userId"));
                        list.add(requisitionBean);
                        Log.i("bean", requisitionBean.toString());
                    }
                } catch (Exception e) {
                    Log.e("JSON Parser", "Error psrsing data" + e.toString());
                }


                Toast.makeText(mActivity, "result" + result, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {

                Toast.makeText(mActivity, "request network failed !", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
