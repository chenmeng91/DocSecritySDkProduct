package docsecuritysdk.eetrust.com.docsecuritysdk;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.eetrust.bean.UserLog;
import com.eetrust.db_operation.DbService;

import java.util.List;

/**
 * Created by chenmeng on 2017/8/2.
 */

public class TestDBActivity extends Activity implements View.OnClickListener{
    private Button insert_button;
    private Button delete_button;
    private Button select_button;
    private DbService dbService;
    private EditText delete_id;
    private String TAG = "TestDBActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_db_activity);
        dbService = DbService.getInstance(getApplicationContext());
        initView();
    }

    private void initView() {
        insert_button = (Button) findViewById(R.id.insert_button);
        insert_button.setOnClickListener(this);
        delete_button = (Button) findViewById(R.id.delete_button);
        delete_button.setOnClickListener(this);
        select_button = (Button) findViewById(R.id.select_button);
        select_button.setOnClickListener(this);
        delete_id = (EditText) findViewById(R.id.delete_id);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.insert_button:
                  insertData();
                break;
            case R.id.delete_button:
               deleteData();
                break;
            case R.id.select_button:
                 selectData();
                break;
        }
    }

    private void deleteData() {
        dbService.deleteLog(Long.parseLong(delete_id.getText().toString()));
    }

    private void selectData() {
       List<UserLog> userLogs =dbService.selectAllLog("");
        for (UserLog userLog:userLogs){
            Log.d(TAG,"ID:"+userLog.getId()+"用户名："+userLog.getLoginName());
        }
    }

    private void insertData() {
        UserLog userLog = new UserLog();
        userLog.setArchivesID(100L);
        userLog.setDocID(120L);
        userLog.setOpertime("11:11:111");
        userLog.setOper(1);
        userLog.setClientIP("192.168.1.1");
        userLog.setMemo("a");
        userLog.setResult(1);
        userLog.setOnline(1);
        userLog.setLoginName("黎明");
         dbService.insertOrUpdateLog(userLog);

    }
}
