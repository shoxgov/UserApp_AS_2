package com.qingwing.safebox.bluetooth;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;

import com.qingwing.safebox.bean.UserInfo;
import com.qingwing.safebox.observable.ObservableBean;
import com.qingwing.safebox.observable.ObserverManager;
import com.qingwing.safebox.utils.BlueDeviceUtils;
import com.qingwing.safebox.utils.LogUtil;
import com.qingwing.safebox.utils.ToastTool;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BluetoothService extends Service {
    private static final String TAG = BluetoothService.class.getSimpleName();
    public static final String ACTION_GATT_WRITE_COMMAND = "com.qingwing.safe.WriteCommand.action";
    public static final String ACTION_BT_COMMAND = "com.qingwing.safe.Command.action";
    public static final String WRITE_COMMAND_VALUE = "command";
    //UUID_KEY_DATA是可以跟蓝牙模块串口通信的Characteristic
    public final static UUID UUID_KEY_DATA = UUID
            .fromString("0000fea0-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_WRITE = UUID
            .fromString("0000fea1-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_WRITEVA = UUID
            .fromString("0000fea2-0000-1000-8000-00805f9b34fb");
    /**
     * 连接指定的蓝牙Address
     */
    public static final int START_SCAN_BT = 0;
    public static final int CONNECT_BT_ADDRESS = 1;
    public static final int DISCONNECT_BT = 2;
    private static final int BLE_STOP_SCAN = 3001;
    private static final int BLE_SCAN_SERVICEDISCOVERY = 3002;
    private static final int BLE_STATE_CONNECTED = 3003;
    private static final int BLE_STATE_DISCONNECTED = 3004;
    private static final int BLE_GATT_SUCCESS = 3005;
    /**
     * 解析协议
     */
    private static final int ANLYLIZE_PROTOCAL = 3009;
    /**
     * 全系统就一个
     */
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;
    /**
     * 数据读写特征值
     */
    private BluetoothGattCharacteristic dataCharacterstic;
    private HashMap<String, String> blueIdAddress = new HashMap<String, String>();
    /**
     * 蓝牙是否连接上
     */
    public static boolean isConnected = false;
    /**
     * 是否在扫描设备中
     */
    public static boolean isScanning = false;
    /**
     * 发送蓝牙指令缓冲器
     */
    private Vector<String> writeCommand = new Vector<String>();
    /**
     * 创建单长期线程，用于轮询蓝牙指令的发送
     */
    ScheduledExecutorService scheduledThreadPool;
    /**
     * 创建单长期线程，用于轮询蓝牙指令的发送
     */
    ScheduledExecutorService anylizeBleThreadPool = Executors.newSingleThreadScheduledExecutor();
    /**
     * 发送蓝牙指令缓冲器
     */
    private Vector<String> receiverBleData = new Vector<String>();

    // ///////////////////handler start/////////////////////////
    private void handlerMsg(Message msg) {
        switch (msg.what) {
            case ANLYLIZE_PROTOCAL:
                if (receiverBleData.isEmpty()) {
                    return;
                }
                BLEAnylizeManager.anylize(receiverBleData.remove(0));
                break;

            case BLE_STATE_CONNECTED:
                ObservableBean on = new ObservableBean();
                on.setWhat(BleObserverConstance.BOX_CONNTEC_BLE_STATUS);
                on.setObject(true);
                ObserverManager.getObserver().setMessage(on);
                break;

            case BLE_STATE_DISCONNECTED:
                ObservableBean off = new ObservableBean();
                off.setWhat(BleObserverConstance.BOX_CONNTEC_BLE_STATUS);
                off.setObject(false);
                ObserverManager.getObserver().setMessage(off);
                //mBluetoothGatt.close();
                break;

            case BLE_GATT_SUCCESS:
                ObservableBean gatt = new ObservableBean();
                gatt.setWhat(BleObserverConstance.ACTION_GATT_CONNECT_SUCCESS);
                ObserverManager.getObserver().setMessage(gatt);
                break;

            case START_SCAN_BT:
                startBLEscan();
                break;

            case BLE_STOP_SCAN:// 蓝牙扫描结束时
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
                isScanning = false;
                LogUtil.d("BLE_STOP_SCAN ------------------------------------------------ isScanning=" + isScanning);
                break;

            case DISCONNECT_BT:
                if (mBluetoothGatt != null) {
                    mBluetoothGatt.close();
                    mBluetoothGatt = null;
                    isConnected = false;
                }
                break;

            case BLE_SCAN_SERVICEDISCOVERY:
                if (mBluetoothAdapter.isDiscovering()) {
                    mBluetoothAdapter.cancelDiscovery();
                }
                mBluetoothGatt.discoverServices();// //执行到这里其实蓝牙已经连接成功了
                LogUtil.d("handlerMsg  discoverServices-----------------");
                break;

            case CONNECT_BT_ADDRESS:// address连接到蓝牙设备
                ObservableBean ob = new ObservableBean();
                ob.setWhat(BleObserverConstance.BOX_CONNTECING_BLE);
                ObserverManager.getObserver().setMessage(ob);
                String deviceAddress = msg.obj.toString();
//				if (mBluetoothGatt == null) {
//                isConnected = true;
                mBluetoothGatt = mBluetoothAdapter.getRemoteDevice(deviceAddress).connectGatt(this, false,
                        mBluetoothGattCallback);
                LogUtil.d("mBluetoothGatt被初始化完成");
//				} else {
//					LogUtil.d("mBluetoothGatt已经被赋值过");
//				}
                if (scheduledThreadPool != null) {
                    scheduledThreadPool.shutdownNow();
                    scheduledThreadPool = null;
                }

                scheduledThreadPool = Executors.newScheduledThreadPool(1);
                scheduledThreadPool.scheduleAtFixedRate(new Runnable() {

                    @Override
                    public void run() {
                        try {
                            writeCommandToBle();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 100, 750, TimeUnit.MILLISECONDS);//表示延迟100微秒后每750微秒执行一次。
                break;
            default:
                break;
        }
    }

    public class MyBinder extends Binder {

        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    //通过binder实现了 调用者（client）与 service之间的通信
    private MyBinder binder = new MyBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        LogUtil.d("onCreate start BluetoothService >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        super.onCreate();
        isScanning = false;
        isConnected = false;
        initBluetoothManager();
        initReceiver();
        anylizeBleThreadPool.scheduleAtFixedRate(anylizeBleData, 300, 100, TimeUnit.MILLISECONDS);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    private void initBluetoothManager() {
        // 初始化 Bluetooth adapter, 通过蓝牙管理器得到一个参考蓝牙适配器(API必须在以上android4.3或以上和版本)
        BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
    }

    private void initReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_WRITE_COMMAND);
        intentFilter.addAction(ACTION_BT_COMMAND);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        LogUtil.d("BluetoothService  onDestroy---------------------------------------------------------------");
        if (scheduledThreadPool != null) {
            scheduledThreadPool.shutdownNow();
        }
        if (anylizeBleThreadPool != null) {
            anylizeBleThreadPool.shutdownNow();
        }
        blueIdAddress.clear();
        try {
            if (mBluetoothAdapter != null) {
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            if (mBluetoothGatt != null) {
                mBluetoothGatt.disconnect();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        isScanning = false;
        isConnected = false;
        stopSelf();
        super.onDestroy();
    }

    private void addScanDevice(BluetoothDevice device, byte[] scanRecord) {
        try {
            String interceptCompany = BlueDeviceUtils.binaryToHexString(scanRecord);
            LogUtil.d("addScanDevice: " + interceptCompany);
            String blueId = BlueDeviceUtils.interceptInitString(interceptCompany);
            LogUtil.d("设备的MAC地址和蓝牙ID  " + device.getAddress() + ":" + blueId);
            if (TextUtils.isEmpty(blueId)) {
                return;
            }
            if (!blueIdAddress.containsKey(blueId)) {
                blueIdAddress.put(blueId, device.getAddress());
            }
            if (UserInfo.UserBindState && !TextUtils.isEmpty(blueId)) {
                if (blueId.contains(UserInfo.BlueId) && !isConnected) {
                    // 如果找到了设备就直接去连接
                    mHandler.removeMessages(START_SCAN_BT);
                    mHandler.sendEmptyMessage(BLE_STOP_SCAN);
                    Message msg = new Message();
                    msg.what = CONNECT_BT_ADDRESS;
                    msg.obj = device.getAddress();
                    mHandler.sendMessage(msg);
                    return;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void startBLEscan() {
        isScanning = true;
        LogUtil.d("startBLEscan -------------------------------------------------- isScanning=" + isScanning);
        if (mBluetoothAdapter == null) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }
        mBluetoothAdapter.startLeScan(mLeScanCallback);
        if (!TextUtils.isEmpty(UserInfo.BlueId) && blueIdAddress.containsKey(UserInfo.BlueId)) {
            mHandler.removeMessages(START_SCAN_BT);
            mHandler.sendEmptyMessage(BLE_STOP_SCAN);
            Message msg = new Message();
            msg.what = CONNECT_BT_ADDRESS;
            msg.obj = blueIdAddress.get(UserInfo.BlueId);
            mHandler.sendMessage(msg);
            return;
        }
        if (isConnected) {
            mHandler.removeMessages(BLE_STOP_SCAN);
            mHandler.sendEmptyMessageDelayed(BLE_STOP_SCAN, 10000);
        }
    }

    public void closeBT() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
            isConnected = false;
        }
    }

    /**
     * 查找BLE设备
     * 扫描到特定类型的设备，则使用接口 startLeScan(UUID[], BluetoothAdapter.LeScanCallback)，通过UUID来查找设备。
     */
    private final BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, final byte[] scanRecord) {
            LogUtil.d("LeScanCallback  扫描到特定类型的设备：" + BlueDeviceUtils.binaryToHexString(scanRecord));
            addScanDevice(device, scanRecord);
        }
    };

    // 要注意，这里是一个异步回调
    private static String recoder = "";//专门用来处理读取记录信息
    private final BluetoothGattCallback mBluetoothGattCallback = new BluetoothGattCallback() {
        /**
         * 此处就是蓝牙收到的数据，用来解密解析
         */
        public synchronized void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            //保管箱发送状态记录条数信息时最多一次只能发14条，我这里可以无限收
            try {
                String temp = BlueDeviceUtils.binaryToHexString(characteristic.getValue());
                LogUtil.d(TAG + "接受加密数据 length=" + temp.length() + "  >>>>> " + temp);
                if (temp.length() < 16) {
                    recoder += temp;
                    String decodeRecoder = BlueDeviceUtils.binaryToHexString(BlueDeviceUtils.Decode(BlueDeviceUtils.hexStringToBinary(recoder)));
                    String recevier2 = BLECommandManager.getSendBlueId(decodeRecoder.substring(0, decodeRecoder.length() - 4), "", "");
                    if (decodeRecoder.equals(recevier2)) {//校验通过说明拼接完成了
                        ObservableBean obj = new ObservableBean();
                        obj.setWhat(BleObserverConstance.BOX_RECEIVER_READINFO);
                        obj.setObject(decodeRecoder);
                        ObserverManager.getObserver().setMessage(obj);
                        recoder = "";
                        return;
                    } else {//不通过 说明还有后面拼接，继续
                        return;
                    }
                }
                String decodeTemp = BlueDeviceUtils.binaryToHexString(BlueDeviceUtils.Decode(characteristic.getValue()));
                LogUtil.d(TAG + "接受到解密数据 length=" + decodeTemp.length() + "  >>>>> " + decodeTemp);
                String intercept = decodeTemp.substring(12, 16);
                if (intercept.equals("0117")) {//此处要拼装
                    String recevier1 = BLECommandManager.getSendBlueId(decodeTemp.substring(0, decodeTemp.length() - 4), "", "");
                    if (!recevier1.equals(decodeTemp)) {//说明还有后面部分，需要拼接
                        LogUtil.d("保管箱读取记录信息校验错误，说明还有后面部分，需要拼接！");
                        recoder = temp;
                        return;
                    } else {//说明就这一条，不需要拼接
                        ObservableBean obj = new ObservableBean();
                        obj.setWhat(BleObserverConstance.BOX_RECEIVER_READINFO);
                        obj.setObject(decodeTemp);
                        ObserverManager.getObserver().setMessage(obj);
                        recoder = "";
                        return;
                    }
                } else if (!decodeTemp.toUpperCase().contains("AAAA")) {//说明是保管读取记录的需要拼接部分
                    recoder += temp;
                    String decodeRecoder = BlueDeviceUtils.binaryToHexString(BlueDeviceUtils.Decode(BlueDeviceUtils.hexStringToBinary(recoder)));
                    String recevier2 = BLECommandManager.getSendBlueId(decodeRecoder.substring(0, decodeRecoder.length() - 4), "", "");
                    if (decodeRecoder.equals(recevier2)) {//校验通过说明拼接完成了
                        ObservableBean obj = new ObservableBean();
                        obj.setWhat(BleObserverConstance.BOX_RECEIVER_READINFO);
                        obj.setObject(decodeRecoder);
                        ObserverManager.getObserver().setMessage(obj);
                        return;
                    } else {//不通过 说明还有后面拼接，继续
                        return;
                    }
                }
                receiverBleData.addElement(decodeTemp);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED://
                    LogUtil.d(TAG + "onConnection StateChange=STATE_CONNECTED");
                    //isConnected = true;
                    mHandler.sendEmptyMessage(BLE_STATE_CONNECTED);
                    mHandler.sendEmptyMessage(BLE_SCAN_SERVICEDISCOVERY);
                    LogUtil.d(TAG + "send message BLE_SCAN_SERVICEDISCOVERY");
//                    mBluetoothGatt.discoverServices();// //执行到这里其实蓝牙已经连接成功了
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    LogUtil.d(TAG + "onConnection StateChange=STATE_DISCONNECTED");
                    isConnected = false;
                    mHandler.sendEmptyMessage(BLE_STATE_DISCONNECTED);
                    //如果需要重连在此重新扫描
                    mHandler.removeMessages(START_SCAN_BT);
                    mHandler.sendEmptyMessage(START_SCAN_BT);
                    break;

                default:
                    LogUtil.d(TAG + "onConnectionStateChange=default");
                    super.onConnectionStateChange(gatt, status, newState);
                    break;
            }
        }

        @Override
        // New services discovered
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtil.d(TAG + " onServicesDiscovered 接受信息注册成功");
                BluetoothGattService service = gatt.getService(UUID_KEY_DATA);
                if (service == null) {
                    LogUtil.d(TAG + " onServicesDiscovered getService == null ");
                    return;
                }
                //设备主动给手机发信息，则可以通过notification的方式，这种方式不用手机去轮询地读设备上的数据
                // /此处添加的对于某个Characteristic是enable的，那么当设备上的这个Characteristic改变时 会回调到onCharacteristicChanged()
                dataCharacterstic = service.getCharacteristic(UUID_WRITE);
                BluetoothGattCharacteristic vaCharacterstic = service.getCharacteristic(UUID_WRITEVA);
                boolean result = gatt.setCharacteristicNotification(vaCharacterstic, true);
                if (!result) {
                    return;
                }
                List<BluetoothGattDescriptor> list = vaCharacterstic.getDescriptors();
                BluetoothGattDescriptor descriptor = list.get(0);
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    if (mBluetoothGatt.writeDescriptor(descriptor)) {
                        LogUtil.d(TAG + "信息注册成功可以发送指令了");
                    }
                    isConnected = true;
                    mHandler.sendEmptyMessage(BLE_STOP_SCAN);
                }
//                mHandler.removeMessages(START_SCAN_BT);
                mHandler.sendEmptyMessage(BLE_GATT_SUCCESS);
            } else {
                LogUtil.d(TAG + " onServicesDiscovered status=" + status);
            }
        }

        @Override
        // 读写结果回调此方法。
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtil.d("BLE- read " + BlueDeviceUtils.binaryToHexString(characteristic.getValue()));
            } else {
                LogUtil.d("BLE- read  fail status=" + status);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                LogUtil.d("BLE- write success :" + BlueDeviceUtils.binaryToHexString(characteristic.getValue()));
            } else {
                LogUtil.d("BLE- read  fail status=" + status);
            }
        }
    };

    Runnable anylizeBleData = new Runnable() {
        @Override
        public void run() {
            if (receiverBleData.isEmpty()) {
                return;
            }
            mHandler.sendEmptyMessage(ANLYLIZE_PROTOCAL);
        }
    };

    private void writeCommandToBle() throws Exception {
        if (mBluetoothGatt == null) {
            LogUtil.d(TAG + " writeCommandToBle mBluetoothGatt=null");
            return;
        }
        if (writeCommand.isEmpty()) {
            return;
        }
        if (dataCharacterstic == null) {
            LogUtil.d(TAG + " writeCommandToBle dataCharacterstic=null");
            return;
        }
        String command = writeCommand.remove(0);
        LogUtil.d(TAG + " obtain command=" + command);
        //////////////////
        byte[] bts1 = BlueDeviceUtils.hexStringToBinary(command);
        byte[] encryption = BlueDeviceUtils.Encryption(bts1);
        LogUtil.d(TAG + "encryption command length=" + encryption.length + " encryption=" + BlueDeviceUtils.binaryToHexString(encryption));
        if (encryption.length > 20) {
            int count = encryption.length / 20;
            int yu = encryption.length % 20;
            for (int i = 0; i < count; i++) {
                byte[] range = Arrays.copyOfRange(encryption, 20 * i, (i + 1) * 20);
                try {
                    dataCharacterstic.setValue(range);
                    mBluetoothGatt.writeCharacteristic(dataCharacterstic);
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                LogUtil.d(TAG + "分割传送大于20字节的数据 第" + i + "轮");
            }
            byte[] range1 = Arrays.copyOfRange(encryption, encryption.length - yu, encryption.length);
            dataCharacterstic.setValue(range1);
            mBluetoothGatt.writeCharacteristic(dataCharacterstic);
            LogUtil.d(TAG + "写入大于20字节的数据成功了");
        } else {
            dataCharacterstic.setValue(encryption);
            mBluetoothGatt.writeCharacteristic(dataCharacterstic);
        }
    }

    /**
     * 接收广播  接收发送指令
     */
    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (TextUtils.isEmpty(action)) {
                return;
            }
            if (action.equals(ACTION_GATT_WRITE_COMMAND)) {//收到的发送命令
                writeCommand.addElement(intent.getStringExtra(WRITE_COMMAND_VALUE));//把组件加到向量尾部，同时大小加1，向量容量比以前大1 　
            } else if (action.equals(ACTION_BT_COMMAND)) {
                String command = intent.getStringExtra("command");
                if (command.equals("CONNECT_BT_ADDRESS")) {
                    LogUtil.d("BroadcastReceiver  ACTION_BT_COMMAND-----------------command:" + command);
                    String blueId = intent.getStringExtra("blueId");
                    String btAddress = BlueDeviceUtils.findAddress(blueIdAddress, blueId);
                    LogUtil.d("BroadcastReceiver  ACTION_BT_COMMAND >>>>>>>> blueId=" + blueId + ",  btAddress=" + btAddress);
                    if (TextUtils.isEmpty(btAddress)) {
                        ObservableBean ob = new ObservableBean();
                        ob.setWhat(BleObserverConstance.BOX_BIND_CONNECT_NODEVICE);
                        ObserverManager.getObserver().setMessage(ob);
                        return;
                    }
                    Message msg = new Message();
                    msg.what = CONNECT_BT_ADDRESS;
                    msg.obj = btAddress;
                    mHandler.sendMessage(msg);
                    mHandler.removeMessages(START_SCAN_BT);
                    mHandler.sendEmptyMessage(BLE_STOP_SCAN);
                }
            }
        }
    };

    private Mhandler mHandler = new Mhandler(BluetoothService.this);

    static class Mhandler extends Handler {
        private WeakReference<BluetoothService> target;

        public Mhandler(BluetoothService service) {
            target = new WeakReference<BluetoothService>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            if (target.get() == null) {
                return;
            }
            target.get().handlerMsg(msg);
        }
    }
}