package com.xjconvenience.vege.vege.modules.orderlist;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.gprinter.aidl.GpService;
import com.gprinter.command.EscCommand;
import com.gprinter.command.GpCom;
import com.gprinter.command.GpUtils;
import com.gprinter.command.LabelCommand;
import com.gprinter.command.LabelCommand.DIRECTION;
import com.gprinter.command.LabelCommand.MIRROR;
import com.gprinter.command.EscCommand.ENABLE;
import com.gprinter.command.LabelCommand.FONTTYPE;
import com.gprinter.command.LabelCommand.ROTATION;
import com.gprinter.command.LabelCommand.FONTMUL;

import com.gprinter.command.EscCommand.JUSTIFICATION;
import com.gprinter.command.EscCommand.FONT;
import com.gprinter.command.EscCommand.HRI_POSITION;
import com.gprinter.io.GpDevice;
import com.gprinter.io.PortParameters;
import com.gprinter.service.GpPrintService;
import com.xjconvenience.vege.vege.Constants;
import com.xjconvenience.vege.vege.R;
import com.xjconvenience.vege.vege.VegeApplication;
import com.xjconvenience.vege.vege.adapters.OrderListAdapter;
import com.xjconvenience.vege.vege.models.Order;
import com.xjconvenience.vege.vege.models.OrderItem;
import com.xjconvenience.vege.vege.modules.login.LoginActivity;
import com.xjconvenience.vege.vege.modules.orderdetail.OrderDetailActivity;
import com.xjconvenience.vege.vege.modules.share.ShareActivity;
import com.xjconvenience.vege.vege.utils.PackUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

import static android.bluetooth.BluetoothAdapter.ACTION_REQUEST_ENABLE;

public class MainActivity extends AppCompatActivity implements MainContract.IMainView {
    private static final int REQUEST_ENABLE_BT = 2;
    public static boolean isForeground = false;
    public static final String MESSAGE_RECEIVED_ACTION = "MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    private GpService mGpService = null;
    public static final String CONNECT_STATUS = "connect.status";
    private static final String DEBUG_TAG = "MainActivity";
    private PrinterServiceConnection conn = null;
    private int mPrinterIndex = 0;
    private int mTotalCopies = 0;
    private static final int MAIN_QUERY_PRINTER_STATUS = 0xfe;
    private static final int REQUEST_PRINT_RECEIPT = 0xfc;
    private Order currPrintOrder;
    @BindView(R.id.order_list)
    RecyclerView order_list;
    @BindView(R.id.progress)
    ProgressBar mProgressBar;
    @BindView(R.id.swipeRefresh)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Inject
    MainContract.IMainPresenter mPresenter;
    private int lastVisibleIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        mToolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("生鲜派送");

        order_list.setLayoutManager(new LinearLayoutManager(this));
        DaggerMainComponent.builder().vegeServicesComponent(((VegeApplication) getApplication()).getVegeServicesComponent())
                .mainModule(new MainModule(this))
                .build()
                .inject(this);
        mPresenter.loadOrders(lastVisibleIndex);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.refreshOrders();
            }
        });

        order_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = order_list.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    lastVisibleIndex = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                }
                if (layoutManager instanceof GridLayoutManager) {
                    lastVisibleIndex = ((GridLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
                }
                if (layoutManager instanceof StaggeredGridLayoutManager) {
                    int[] lastList = null;
                    StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) layoutManager;
                    lastList = new int[manager.getSpanCount()];
                    int[] lastVisibleItemPositions = manager.findLastCompletelyVisibleItemPositions(lastList);

                    for (int i : lastList) {
                        lastVisibleIndex = i > lastVisibleIndex ? i : lastVisibleIndex;
                    }
                }

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    mPresenter.loadOrders(lastVisibleIndex);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        connection();
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_DEVICE_REAL_STATUS));
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.menu_search:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.dialog_search, null);
                EditText keyword = (EditText) view.findViewById(R.id.keyword);
                ((MainPresenter) mPresenter).setKeyword(keyword.getText().toString());
                Spinner state = (Spinner) view.findViewById(R.id.state);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.state_list, android.R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                state.setAdapter(adapter);

                state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        ((MainPresenter) mPresenter).setState(String.valueOf(i - 1));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                builder.setView(view);
                builder.setTitle("查询");
                builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.refreshOrders();
                    }
                });
                builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.create().show();
                return true;
            case R.id.menu_exit:

                SharedPreferences pref = getSharedPreferences(Constants.PREF_NAME, 0);
                SharedPreferences.Editor editor = pref.edit();
                editor.putString(Constants.TOKEN_KEY, "");
                editor.commit();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            case R.id.menu_share:
                Intent shareIntent = new Intent(this, ShareActivity.class);
                startActivity(shareIntent);
                return true;
            case R.id.menu_bluetooth:
                getBluetoothDevice();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null) {
            unbindService(conn);
        }
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void showProgress() {
        mSwipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void setItems(OrderListAdapter adapter) {
        order_list.setAdapter(adapter);
    }

    @Override
    public void navigateToDetail(Order order) {
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra(OrderDetailActivity.ITEM_LIST, order.getProducts());
        startActivity(intent);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    @Override
    public void dialUser(String phone) {
        Uri uri = Uri.parse("tel:" + phone);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        startActivity(intent);
    }

    @Override
    public void callMap(double latitude, double longitude) {
        if (PackUtil.isInstalled(this, "com.baidu.BaiduMap")) {
            Uri uri = Uri.parse("baidumap://map/marker?location=" + latitude + "," + longitude + "&title=用户位置&content=&traffic=on");
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            startActivity(intent);
        } else if (PackUtil.isInstalled(this, "com.autonavi.minimap")) {
            Uri uri = Uri.parse("androidamap://viewMap?sourceApplication=Vege&poiname=&lat=" + latitude + "&lon=" + longitude + "&dev=1");
            Intent intent = new Intent("android.intent.action.VIEW", uri);
            startActivity(intent);
        } else {
            Toast.makeText(this, "请安装高德地图或百度地图", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void printOrder(Order order) {
        try {
            int type = mGpService.getPrinterCommandType(mPrinterIndex);
            if (type == GpCom.ESC_COMMAND) {
                currPrintOrder = order;
                mGpService.queryPrinterStatus(mPrinterIndex, 1000, REQUEST_PRINT_RECEIPT);
            } else {
                Toast.makeText(this, "Printer is not receipt mode", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e1) {
            e1.printStackTrace();
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(DEBUG_TAG, "Action is : " + action);

            if (action.equals(GpCom.ACTION_DEVICE_REAL_STATUS)) {
                // 业务逻辑的请求码，对应哪里查询做什么操作
                int requestCode = intent.getIntExtra(GpCom.EXTRA_PRINTER_REQUEST_CODE, -1);
                // 判断请求码，是则进行业务操作

                if (requestCode == MAIN_QUERY_PRINTER_STATUS) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    String str;
                    if (status == GpCom.STATE_NO_ERR) {
                        str = "打印机正常";
                    } else {
                        str = "打印机 ";
                        if ((byte) (status & GpCom.STATE_OFFLINE) > 0) {
                            str += "脱机";
                        }
                        if ((byte) (status & GpCom.STATE_PAPER_ERR) > 0) {
                            str += "缺纸";
                        }
                        if ((byte) (status & GpCom.STATE_COVER_OPEN) > 0) {
                            str += "开盖";
                        }
                        if ((byte) (status & GpCom.STATE_ERR_OCCURS) > 0) {
                            str += "出错";
                        }
                        if ((byte) (status & GpCom.STATE_TIMES_OUT) > 0) {
                            str += "查询超时";
                        }
                    }
                    Toast.makeText(getApplicationContext(), "打印机状态：" + str, Toast.LENGTH_LONG).show();
                } else if (requestCode == REQUEST_PRINT_RECEIPT) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status == GpCom.STATE_NO_ERR) {
                        if (currPrintOrder != null) {
                            sendReceipt();
                        } else {
                            showMessage("打印的订单为空");
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "查询打印机状态错误", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    void sendReceipt() {
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("方便生活\n");

        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);// 取消倍高倍宽
        esc.addPrintAndFeedLines((byte) 1);
        esc.addSelectJustification(JUSTIFICATION.LEFT);
        esc.addText(String.valueOf(currPrintOrder.getId() + "       " + currPrintOrder.getCreateTime() + "\n"));
        esc.addPrintAndFeedLines((byte) 1);

        esc.addText(currPrintOrder.getName() + "     " + currPrintOrder.getPhone() + "\n");
        esc.addPrintAndFeedLines((byte) 1);

        esc.addText(currPrintOrder.getStreet() + "\n");
        esc.addPrintAndFeedLines((byte) 1);

        esc.addText("名称     单价     数量     小计\n");
        esc.addPrintAndFeedLines((byte) 1);

        double totalCost = 0;
        for (int i = 0; i < currPrintOrder.getProducts().size(); i++) {
            esc.addSelectJustification(JUSTIFICATION.LEFT);
            OrderItem item = currPrintOrder.getProducts().get(i);
            double cost = item.getPrice() * item.getCount();
            totalCost += cost;
            esc.addText(item.getName() + "  " + item.getPrice() + "/" + item.getUnitName() + "   " + item.getCount() + "    ￥" + String.valueOf(cost) + "\n");
            esc.addPrintAndFeedLines((byte) 1);
        }
        esc.addSelectJustification(JUSTIFICATION.LEFT);
        esc.addText("总计：");
        String totalStr = "￥" + totalCost;
        if (currPrintOrder.getDeliveryCharge() > 0) {
            totalStr += "(含运费￥" + currPrintOrder.getDeliveryCharge() + ")";
        }
        esc.addText(totalStr);
        esc.addPrintAndFeedLines((byte) 6);

        Vector<Byte> datas = esc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String sss = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rs;
        try {
            rs = mGpService.sendEscCommand(mPrinterIndex, sss);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rs];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
            currPrintOrder = null;
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void connection() {
        conn = new PrinterServiceConnection();
        Intent intent = new Intent(this, GpPrintService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE); // bindService
    }

    class PrinterServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mGpService = GpService.Stub.asInterface(iBinder);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mGpService = null;
        }
    }

    public void getBluetoothDevice() {
        // Get local Bluetooth adapter
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter
                .getDefaultAdapter();
        // If the adapter is null, then Bluetooth is not supported
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "设备不支持蓝牙", Toast.LENGTH_LONG).show();
        } else {
            // If BT is not on, request that it be enabled.
            // setupChat() will then be called during onActivityResult

            if (!bluetoothAdapter.isEnabled()) {
                //如果蓝牙没开则要求打开蓝牙
                Intent enableIntent = new Intent(ACTION_REQUEST_ENABLE);

                startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            } else {
                final List<String> deviceNameList = new ArrayList<>();
                Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    for (BluetoothDevice device : pairedDevices) {
                        deviceNameList.add(device.getName() + "\n" + device.getAddress());
                    }
                    ListAdapter devicesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, deviceNameList);
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("选择打印机蓝牙设备");
                    builder.setAdapter(devicesAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String nameAndAddr = deviceNameList.get(i);
                            String addr = nameAndAddr.split("\n")[1];
                            try {
                                mGpService.openPort(0, PortParameters.BLUETOOTH, addr, 0);
                                Toast.makeText(MainActivity.this, "打印机连接成功", Toast.LENGTH_LONG).show();
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.create().show();
                }
            }
        }
    }
}
