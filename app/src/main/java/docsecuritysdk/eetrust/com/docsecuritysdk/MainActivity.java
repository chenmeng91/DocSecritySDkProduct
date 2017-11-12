package docsecuritysdk.eetrust.com.docsecuritysdk;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.eetrust.bean.DeptBean;
import com.eetrust.bean.IsSecureDocBean2;
import com.eetrust.bean.UserBean;

import com.eetrust.securedocsdk.CryptoUtil;
import com.eetrust.securedocsdk.MSD;
import com.eetrust.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import docsecuritysdk.eetrust.com.docsecuritysdk.HttpUtilsDemo.HttpFieldsDemo;
import docsecuritysdk.eetrust.com.docsecuritysdk.HttpUtilsDemo.HttpUtilDemo;

public class MainActivity extends Activity implements View.OnClickListener {
    private Button is_scure_doc;
    private Button btn_get_ticket;
    private Button btn_encrypt;
    private Button issecdoc;

    private String TAG = "MainActivity";
    private Button decrypt_erplace;
    private Button jnitest;
    private Button config1;
    private Button config2;
    private Button config_button;
    private String postTicket;
    private String decryptCipherPath;
    private String decryptPlainPath;
    private String encryptPlainPath;
    private String encryptCipherPath;
    private Context context;
    private CheckBox is_net_request;
    private Button test_void;
    private Button author_button;
    private Button test_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, 1);
        is_net_request = (CheckBox) findViewById(R.id.is_net_request);
        context = getApplicationContext();
        config1 = (Button) findViewById(R.id.config1);
        config2 = (Button) findViewById(R.id.config2);
        config2.setOnClickListener(this);
        is_scure_doc = (Button) findViewById(R.id.is_net_request);
        btn_get_ticket = (Button) findViewById(R.id.btn_get_ticket);
        btn_encrypt = (Button) findViewById(R.id.btn_encrypt);
        decrypt_erplace = (Button) findViewById(R.id.decrypt_erplace);
        jnitest = (Button) findViewById(R.id.jnitest);
        config_button = (Button) findViewById(R.id.config_button);
        config_button.setOnClickListener(this);
        jnitest.setOnClickListener(this);
        decrypt_erplace.setOnClickListener(this);
        btn_get_ticket.setOnClickListener(this);
        btn_encrypt.setOnClickListener(this);
        test_void = (Button) findViewById(R.id.test_void);
        test_void.setOnClickListener(this);
        test_button = (Button) findViewById(R.id.test_button);
        test_button.setOnClickListener(this);
        issecdoc = (Button) findViewById(R.id.issecdoc);
        issecdoc.setOnClickListener(this);
        config1.setOnClickListener(this);
        author_button = (Button) findViewById(R.id.author_button);
        author_button.setOnClickListener(this);
        is_net_request.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                     if(isChecked){
                         netRequestLayout(true);
                     }else {
                         netRequestLayout(false);
                     }
            }
        });
        decryptCipherPath = getSDPath() + "/a_encode/考核14.docx";   //解密密文路径
//        decryptCipherPath = getSDPath() + "/1M.doc";   //解密密文路径
        decryptPlainPath = getSDPath() + "/1Ma.docx";   //解密明文路径
        encryptPlainPath = getSDPath() + "/aa.docx";    //加密明文路径
        encryptPlainPath = getSDPath() + "/考核14.docx";    //加密明文路径
        encryptCipherPath = getSDPath() + "/考核15.docx";    //加密密文路径
//        encryptCipherPath = getSDPath() + "/aadncode.docx";    //加密密文路径

    }

    private void netRequestLayout(boolean b) {
        if(b){
            config2.setVisibility(View.VISIBLE);
            config1.setVisibility(View.GONE);
            btn_get_ticket.setVisibility(View.VISIBLE);
            config_button.setVisibility(View.VISIBLE);
        }else {
            config2.setVisibility(View.GONE);
            config1.setVisibility(View.VISIBLE);
            btn_get_ticket.setVisibility(View.GONE);
            config_button.setVisibility(View.GONE);
        }
    }

    /**
     * 判断是否是加密文件
     */
    private void isCiphertext() {
        MSD.getInstance(getApplicationContext()).sds_isCiphertext(getSDPath() + "/ppp.docx", new MSD.IsCiphertextCallBack() {
            @Override
            public void CallBackFinish(boolean isCiphertext) {
                if (isCiphertext) {
                    issecdoc.setText("是加密文件");
                    Log.d(TAG, "isSecureDoc:" + "是加密文件");
                } else {
                    issecdoc.setText("不是加密文件");
                    Log.d(TAG, "isSecureDoc:" + "不是加密文件");
                }
            }

            @Override
            public void CallBackError(int errorCode, String ErrorInfo) {
                Log.d(TAG, "isSecureDoc:：" + ErrorInfo + " errorCode:" + errorCode);
            }
        });
    }



    private String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();//获取跟目录
        }
        return sdDir.toString();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.config1://配置无网络请求
                configNoNet();
                break;
            case R.id.config2://配置有网络请求
                configHaveNet();
                break;
            case R.id.btn_get_ticket:
                getTicket();
                break;
            case R.id.config_button:
                //身份认证
                configInform();
                break;
            case R.id.btn_encrypt:
                encrypt();
                break;
            case R.id.issecdoc:
                //判断是否是加密文件
                isCiphertext();
                break;
            case R.id.decrypt_erplace:
                decryptAndReplace();
                break;
            case R.id.jnitest:
                IsSecureDocBean2 isSecureDocBean2 = CryptoUtil.getInstance().isSecureDoc(getSDPath() + "/22.doc");
                Log.d(TAG, isSecureDocBean2.appId + "");
                break;
            case R.id.test_void:
                File file = new File(decryptPlainPath);
                if(file.exists()){
                    FileUtils.startActionFile(this,new File(decryptPlainPath),"image/jpg");
                }

                break;
            case R.id.author_button:
                authorize();
                break;
            case R.id.test_button:
                test();
                break;

        }
    }

    private void test() {
        List<UserBean> userBeens = new ArrayList<>();
        List<DeptBean> deptBeanList = new ArrayList<>();
//        List<DeptBean> deptBeanList = null;
//        String user_name_String ="238" ;//238:申亚男，孙航：270
//        String user_rights_String= "1,2,3,4,5,6,7,8,9,10,";
//        String dep_id_String = "14";
//        String dep_rights_String = "1,2";
//        if(!TextUtils.isEmpty(user_name_String)&&!TextUtils.isEmpty(user_rights_String)){
            UserBean u2 = new UserBean("1","1,2,3,");
            userBeens.add(u2);
//        }
//        if(!TextUtils.isEmpty(dep_id_String)&&!TextUtils.isEmpty(dep_rights_String)){
            DeptBean deptBean = new DeptBean("11","1,2,");
            deptBeanList.add(deptBean);
//        }
//        UserBean u = new UserBean(Config.LoginName,"1,2,3,4,5,6,7,8,9,10,");
//        userBeens.add(u);

       boolean iscan = StringUtils.isCanAccerdit("1,2,3,4,5,6,7,8,9,10,",userBeens,deptBeanList);
        if(iscan){
            Log.d("aa","cc");
        }else {
            Log.d("aa","cc");
        }
    }

    private void authorize() {
        List<UserBean> userBeens = new ArrayList<>();
//        List<DeptBean> deptBeanList = new ArrayList<>();
        List<DeptBean> deptBeanList = null;
//        String user_name_String ="238" ;//238:申亚男，孙航：270
//        String user_rights_String= "1,2,3,4,5,6,7,8,9,10,";
//        String dep_id_String = "14";
//        String dep_rights_String = "1,2";
//        if(!TextUtils.isEmpty(user_name_String)&&!TextUtils.isEmpty(user_rights_String)){
//            UserBean u2 = new UserBean(user_name_String,user_rights_String);
//            userBeens.add(u2);
//        }
//        if(!TextUtils.isEmpty(dep_id_String)&&!TextUtils.isEmpty(dep_rights_String)){
//            DeptBean deptBean = new DeptBean(dep_id_String,dep_rights_String);
//            deptBeanList.add(deptBean);
//        }
//        UserBean u = new UserBean(Config.LoginName,"1,2,3,4,5,6,7,8,9,10,");
//        userBeens.add(u);

        MSD.getInstance(context).sds_accredit(decryptCipherPath, userBeens, deptBeanList, new MSD.AuthorizeCallBack() {
            @Override
            public void CallBackFinish() {
                Log.d(TAG,"aa");
                ShowToast("授权成功");
            }

            @Override
            public void CallBackError(int errorCode, String errorInfo) {
//                Log.d(TAG,"授权失败:");
                ShowToast("授权失败");
                Log.d(TAG,"授权失败："+errorInfo);
            }
        });
    }

    private void configHaveNet() {
       MSD.getInstance(context).sds_config(Config.LoginName,"270",Config.LoginIP,Config.LoginPort,Config.authorities,false);
    }

    private void decryptAndReplace() {
        MSD.getInstance(context).sds_openFile(decryptCipherPath,decryptPlainPath, true,new MSD.OpenFileCallBack() {
            @Override
            public void CallBackFinish() {
                ShowToast("解密成功");
            }

            @Override
            public void CallBackError(int errorCode, String errorInfo) {
                ShowToast("解密失败");
            }
        });
    }

    private void configNoNet() {
        MSD.getInstance(context).sds_config(Config.LoginName,"270", Config.authorities,SecurityDocumentMethod.getInstance(context));
    }

    private void getTicket() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                final String[] post = {null};
                HttpUtilDemo.getInstance(getApplicationContext()).login(new HttpUtilDemo.CallBackData() {
                    @Override
                    public void CallBackFinish(Map<String, Object> stringObjectMap) {
                        String result = (String) stringObjectMap.get(HttpFieldsDemo.NET_RESULT);
                        if (result != null && result.equals(HttpFieldsDemo.NET_REQUEST_SUCESS)) {
                            String ticket = (String) stringObjectMap.get("ticket");
                            post[0] = ticket;
                        } else if (result != null && result.equals(HttpFieldsDemo.NET_REQUEST_FAIL)) {
                            post[0] = (String) stringObjectMap.get("error");
                        }
                    }

                    @Override
                    public void CallBackError(String error) {
                        post[0] = error;
                    }
                });
                return post[0];
            }

            @Override
            protected void onPostExecute(String post) {
                if (post != null && post.length() == 10) {
                    postTicket = post;
                    btn_get_ticket.setBackgroundColor(getResources().getColor(R.color.green));
                    btn_get_ticket.setText(post);
                    configInform();
                } else {
                    btn_get_ticket.setBackgroundColor(getResources().getColor(R.color.red));
                    btn_get_ticket.setText(post);
                }
            }
        }.execute(new Void[]{});
    }

    private void encrypt() {
        List<UserBean> userBeens = new ArrayList<>();
         UserBean u = new UserBean("270","1,2,3,4,5,6,7,8,9,10,");
        userBeens.add(u);
        MSD.getInstance(getApplicationContext()).sds_encrypt(encryptPlainPath, encryptCipherPath, 1,"1",userBeens,null,new MSD.EncryptCallBack() {
            @Override
            public void CallBackFinish() {
                btn_encrypt.setBackgroundColor(getResources().getColor(R.color.green));
                btn_encrypt.setText("操作成功");
            }

            @Override
            public void CallBackError(int errorCode, String ErrorInfo) {
                btn_encrypt.setBackgroundColor(getResources().getColor(R.color.red));
                btn_encrypt.setText(errorCode + ErrorInfo);
            }
        });


    }

    private void logout() {
        MSD.getInstance(getApplicationContext()).sds_logout(new MSD.LogoutCallBack() {
            @Override
            public void CallBackFinish() {
                Log.d(TAG, "退出成功");
            }

            @Override
            public void CallBackError(int errorCode, String ErrorInfo) {
                Log.d(TAG, "退出失败 ， " + "errorCode: " + errorCode + ", ErrorInfo:" + ErrorInfo);
            }
        });

    }

    /**
     * 身份认证
     */
    private void configInform() {

        MSD.getInstance(context).sds_authenticate(postTicket, new MSD.AuthenticateCallBack() {
            @Override
            public void CallBackFinish() {
                Toast.makeText(MainActivity.this,"身份认证成功",Toast.LENGTH_LONG).show();
            }

            @Override
            public void CallBackError(int errorCode, String errorInfo) {
                Toast.makeText(MainActivity.this,"身份认证失败："+errorInfo,Toast.LENGTH_LONG).show();
            }
        });

//        final Handler handler = new Handler() {//切换到主线程
//            public void handleMessage(android.os.Message msg) {
//                switch (msg.what) {
//                    case SecureDocError.E_OK://成功
//                        ShowToast("登录成功");
//                        break;
//                    case SecureDocError.E_AuthenticationFail:
//                    case SecureDocError.E_NetReqTimeout:
//                    case SecureDocError.E_NetReqFail:
//                    case SecureDocError.E_ConnectionFail:
//                        ShowToast("登录失败：" + (String) msg.obj);
//                        break;
//                    default:
//                        break;
//                }
//            }
//        };
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                HttpUtils.getInstance(getApplication()).toLoginServer("sunh", postTicket, new com.eetrust.http.HttpUtils.CallBackData() {
//                    @Override
//                    public void CallBackFinish(Map<String, Object> stringObjectMap) {
//                        Log.i(TAG, "CallBackFinish: " + stringObjectMap.toString());
//                        String result = (String) stringObjectMap.get(HttpFields.NET_RESULT);
//                        if (result != null && result.equals(HttpFields.NET_REQUEST_SUCESS)) {
//                            sendMsg(SecureDocError.E_OK, "");
//                        } else {
//                            sendMsg(SecureDocError.E_AuthenticationFail, (String) stringObjectMap.get(HttpFields.NET_ERROR));
//                        }
//                    }
//
//                    @Override
//                    public void CallBackError(String error) {
//                        if ("请求超时".equals(error)) {
//                            sendMsg(SecureDocError.E_NetReqTimeout, SecureDocError.EDesc[SecureDocError.E_NetReqTimeout]);
//                        } else if ("网络连接失败，请重新配置".equals(error)) {
//                            sendMsg(SecureDocError.E_ConnectionFail, error);
//                        } else {
//                            sendMsg(SecureDocError.E_NetReqFail, error);
//                        }
//                    }
//                });
//            }
//
//            private void sendMsg(int errorCode, String errorInfo) {
//                Message msg = Message.obtain();
//                msg.what = errorCode;
//                msg.obj = errorInfo;
//                handler.sendMessage(msg);
//            }
//        }).start();


    }



    private void ShowToast(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }


}
