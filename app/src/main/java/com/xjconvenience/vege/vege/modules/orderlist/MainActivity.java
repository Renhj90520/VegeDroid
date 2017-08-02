package com.xjconvenience.vege.vege.modules.orderlist;

import android.app.AlertDialog;
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

import java.util.Vector;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

public class MainActivity extends AppCompatActivity implements MainContract.IMainView {
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
    private static final int REQUEST_PRINT_LABEL = 0xfd;
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
        /**
         * 票据模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus()，在打印完成后会接收到
         * action为GpCom.ACTION_DEVICE_STATUS的广播，特别用于连续打印，
         * 可参照该sample中的sendReceiptWithResponse方法与广播中的处理
         **/
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_RECEIPT_RESPONSE));
        /**
         * 标签模式下，可注册该广播，在需要打印内容的最后加入addQueryPrinterStatus(RESPONSE_MODE mode)
         * ，在打印完成后会接收到，action为GpCom.ACTION_LABEL_RESPONSE的广播，特别用于连续打印，
         * 可参照该sample中的sendLabelWithResponse方法与广播中的处理
         **/
        registerReceiver(mBroadcastReceiver, new IntentFilter(GpCom.ACTION_LABEL_RESPONSE));
    }

    @Override
    protected void onResume() {
        isForeground = true;
        super.onResume();
        JPushInterface.resumePush(this);
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
                } else if (requestCode == REQUEST_PRINT_LABEL) {
                    int status = intent.getIntExtra(GpCom.EXTRA_PRINTER_REAL_STATUS, 16);
                    if (status == GpCom.STATE_NO_ERR) {
                        sendLabel();
                    } else {
                        Toast.makeText(MainActivity.this, "查询打印机状态错误", Toast.LENGTH_SHORT).show();
                    }
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
            } else if (action.equals(GpCom.ACTION_RECEIPT_RESPONSE)) {
                if (--mTotalCopies > 0) {
                    sendReceiptWithResponse();
                }
            } else if (action.equals(GpCom.ACTION_LABEL_RESPONSE)) {
                byte[] data = intent.getByteArrayExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE);
                int cnt = intent.getIntExtra(GpCom.EXTRA_PRINTER_LABEL_RESPONSE_CNT, 1);
                String d = new String(data, 0, cnt);
                /**
                 * 这里的d的内容根据RESPONSE_MODE去判断返回的内容去判断是否成功，具体可以查看标签编程手册SET
                 * RESPONSE指令
                 * 该sample中实现的是发一张就返回一次,这里返回的是{00,00001}。这里的对应{Status,######,ID}
                 * 所以我们需要取出STATUS
                 */
                Log.d("LABEL RESPONSE", d);

                if (--mTotalCopies > 0 && d.charAt(1) == 0x00) {
                    sendLabelWithResponse();
                }
            }
        }
    };

    void sendLabel() {
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(60, 60); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(0); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(DIRECTION.BACKWARD, MIRROR.NORMAL);// 设置打印方向
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区
        // 绘制简体中文
        tsc.addText(20, 20, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_0, FONTMUL.MUL_1, FONTMUL.MUL_1,
                "Welcome to use SMARNET printer!");
    }

    void sendReceipt() {
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectJustification(JUSTIFICATION.LEFT);
        esc.addText(String.valueOf(currPrintOrder.getId()));
        esc.addSelectJustification(JUSTIFICATION.RIGHT);
        esc.addText(currPrintOrder.getCreateTime() + "\n");

        esc.addSelectJustification(JUSTIFICATION.LEFT);
        esc.addText(currPrintOrder.getName());
        esc.addSelectJustification(JUSTIFICATION.RIGHT);
        esc.addText(currPrintOrder.getPhone() + "\n");

        esc.addSelectJustification(JUSTIFICATION.LEFT);
        esc.addText("名称     单价     数量");
        esc.addSelectJustification(JUSTIFICATION.RIGHT);
        esc.addText("小计\n");
        double totalCost = 0;
        for (int i = 0; i < currPrintOrder.getProducts().size(); i++) {
            esc.addSelectJustification(JUSTIFICATION.LEFT);
            OrderItem item = currPrintOrder.getProducts().get(i);
            esc.addText(item.getName() + "  " + item.getPrice() + "/" + item.getUnitName() + "  " + item.getCount());
            esc.addSelectJustification(JUSTIFICATION.RIGHT);
            double cost = item.getPrice() * item.getCount();
            totalCost += cost;
            esc.addText("￥" + String.valueOf(cost));
        }
        esc.addPrintAndLineFeed();

        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);// 设置为倍高倍宽
        esc.addSelectJustification(JUSTIFICATION.LEFT);
        esc.addText("总计：");
        esc.addSelectJustification(JUSTIFICATION.RIGHT);
        String totalStr = "￥" + totalCost;
        if (currPrintOrder.getDeliveryCharge() > 0) {
            totalStr += "(含运费￥" + currPrintOrder.getDeliveryCharge() + ")";
        }
        esc.addText(totalStr);
        esc.addPrintAndLineFeed();

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

    void sendReceiptWithResponse() {
        EscCommand esc = new EscCommand();
        esc.addInitializePrinter();
        esc.addPrintAndFeedLines((byte) 3);
        esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印居中
        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.ON, ENABLE.ON, ENABLE.OFF);// 设置为倍高倍宽
        esc.addText("Sample\n"); // 打印文字
        esc.addPrintAndLineFeed();

		/* 打印文字 */
        esc.addSelectPrintModes(FONT.FONTA, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF, ENABLE.OFF);// 取消倍高倍宽
        esc.addSelectJustification(JUSTIFICATION.LEFT);// 设置打印左对齐
        esc.addText("Print text\n"); // 打印文字
        esc.addText("Welcome to use SMARNET printer!\n"); // 打印文字

		/* 打印繁体中文 需要打印机支持繁体字库 */
        String message = "佳博智匯票據打印機\n";
        // esc.addText(message,"BIG5");
        esc.addText(message, "GB2312");
        esc.addPrintAndLineFeed();

		/* 绝对位置 具体详细信息请查看GP58编程手册 */
        esc.addText("智汇");
        esc.addSetHorAndVerMotionUnits((byte) 7, (byte) 0);
        esc.addSetAbsolutePrintPosition((short) 6);
        esc.addText("网络");
        esc.addSetAbsolutePrintPosition((short) 10);
        esc.addText("设备");
        esc.addPrintAndLineFeed();

		/* 打印图片 */
        // esc.addText("Print bitmap!\n"); // 打印文字
        // Bitmap b = BitmapFactory.decodeResource(getResources(),
        // R.drawable.gprinter);
        // esc.addRastBitImage(b, 384, 0); // 打印图片

		/* 打印一维条码 */
        esc.addText("Print code128\n"); // 打印文字
        esc.addSelectPrintingPositionForHRICharacters(HRI_POSITION.BELOW);//
        // 设置条码可识别字符位置在条码下方
        esc.addSetBarcodeHeight((byte) 60); // 设置条码高度为60点
        esc.addSetBarcodeWidth((byte) 1); // 设置条码单元宽度为1
        esc.addCODE128(esc.genCodeB("SMARNET")); // 打印Code128码
        esc.addPrintAndLineFeed();

		/*
         * QRCode命令打印 此命令只在支持QRCode命令打印的机型才能使用。 在不支持二维码指令打印的机型上，则需要发送二维条码图片
		 */
        esc.addText("Print QRcode\n"); // 打印文字
        esc.addSelectErrorCorrectionLevelForQRCode((byte) 0x31); // 设置纠错等级
        esc.addSelectSizeOfModuleForQRCode((byte) 3);// 设置qrcode模块大小
        esc.addStoreQRCodeData("www.smarnet.cc");// 设置qrcode内容
        esc.addPrintQRCode();// 打印QRCode
        esc.addPrintAndLineFeed();

		/* 打印文字 */
        esc.addSelectJustification(JUSTIFICATION.CENTER);// 设置打印左对齐
        esc.addText("Completed!\r\n"); // 打印结束
        // 开钱箱
        esc.addGeneratePlus(LabelCommand.FOOT.F5, (byte) 255, (byte) 255);
        esc.addPrintAndFeedLines((byte) 8);

        // 加入查询打印机状态，打印完成后，此时会接收到GpCom.ACTION_DEVICE_STATUS广播
        esc.addQueryPrinterStatus();

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
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    void sendLabelWithResponse() {
        LabelCommand tsc = new LabelCommand();
        tsc.addSize(60, 60); // 设置标签尺寸，按照实际尺寸设置
        tsc.addGap(0); // 设置标签间隙，按照实际尺寸设置，如果为无间隙纸则设置为0
        tsc.addDirection(DIRECTION.BACKWARD, MIRROR.NORMAL);// 设置打印方向
        tsc.addReference(0, 0);// 设置原点坐标
        tsc.addTear(ENABLE.ON); // 撕纸模式开启
        tsc.addCls();// 清除打印缓冲区
        // 绘制简体中文
        tsc.addText(20, 20, FONTTYPE.SIMPLIFIED_CHINESE, ROTATION.ROTATION_0, FONTMUL.MUL_1, FONTMUL.MUL_1,
                "Welcome to use SMARNET printer!");

        tsc.addPrint(1, 1); // 打印标签
        tsc.addSound(2, 100); // 打印标签后 蜂鸣器响
        tsc.addCashdrwer(LabelCommand.FOOT.F5, 255, 255);

        Vector<Byte> datas = tsc.getCommand(); // 发送数据
        byte[] bytes = GpUtils.ByteTo_byte(datas);
        String str = Base64.encodeToString(bytes, Base64.DEFAULT);
        int rel;
        try {
            rel = mGpService.sendLabelCommand(mPrinterIndex, str);
            GpCom.ERROR_CODE r = GpCom.ERROR_CODE.values()[rel];
            if (r != GpCom.ERROR_CODE.SUCCESS) {
                Toast.makeText(getApplicationContext(), GpCom.getErrorText(r), Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
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
}
