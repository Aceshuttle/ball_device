package com.example.yu.ball_device;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    boolean isConnect=true;//连接还是断开
    Button ConnectButton;//定义连接按钮
    Button SendButton;//定义发送按钮
    EditText IPEditText;//定义ip输入框
    EditText PortText;//定义端口输入框
    EditText MsgEditText;//定义信息输出框
    TextView RrceiveEditText;//定义信息输入框
    ImageView imageView;
    Socket socket = null;//定义socket
     OutputStream outputStream=null;//定义输出流
     InputStream inputStream=null;//定义输入流

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectButton =  findViewById(R.id.Connect_Bt);//获得连接按钮对象
        SendButton = findViewById(R.id.Send_Bt);//获得发送按钮对象
        IPEditText = findViewById(R.id.ip_ET);//获得ip文本框对象
        PortText =  findViewById(R.id.Port_ET);//获得端口文本框按钮对象
        MsgEditText =  findViewById(R.id.Send_ET);//获得发送消息文本框对象
        RrceiveEditText =  findViewById(R.id.Receive_ET);//获得接收消息文本框对象
        imageView=findViewById(R.id.connect_image);
    }

    public void Connect_onClick(View v) {

            if (isConnect == true) //标志位 = true表示连接
            {
                Connect_Thread connect_Thread = new Connect_Thread();
                connect_Thread.start();
                JudgeThread judgeThread = new JudgeThread();
                judgeThread.start();
            }
            else //标志位 = false表示退出连接
            {
                isConnect = true;//置为true
                ConnectButton.setText("连接");//按钮上显示连接
                imageView.setImageResource(R.drawable.unconnected);
                try
                {
                    socket.close();//关闭连接
                    socket=null;
                }
                catch (IOException e)
                {
    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

    }

    /**
     *  判断socket是否为空，为空说明没有连接则不执行判断，
     *  之所以使用线程并休眠是起到一个延时作用，
     *  因为网络连接服务器需要时间。防止需要按两次。
     */
    class JudgeThread extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(100);
                if(socket!=null){
                    ConnectButton.setText("断开");//按钮上显示--断开
                    imageView.setImageResource(R.drawable.connected);
                    isConnect = false;//置为false
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void Send_onClick(View v) {
        Send_Thread send_thread = new Send_Thread();
        send_thread.start();
    }
    class Send_Thread extends Thread{
        @Override
        public void run() {
            try
            {
                outputStream = socket.getOutputStream();
                outputStream.write(MsgEditText.getText().toString().getBytes());
            }
            catch (Exception e)
            {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    //连接线程
    class Connect_Thread extends Thread//继承Thread
    {
        public void run()//重写run方法
        {
            try
            {
                if (socket == null)
                {
//用InetAddress方法获取ip地址
                    InetAddress ipAddress = InetAddress.getByName(IPEditText.getText().toString());
                    int port =Integer.valueOf(PortText.getText().toString());//获取端口号
                    socket = new Socket(ipAddress, port);//创建连接地址和端口-------------------这样就好多了
//在创建完连接后启动接收线程
                    Receive_Thread receive_Thread = new Receive_Thread();
                    receive_Thread.start();
                }

            }
            catch (Exception e)
            {
// TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    //接收线程
    class Receive_Thread extends Thread
    {
        public void run()//重写run方法
        {
            try
            {
                while (true)
                {
                    final byte[] buffer = new byte[1024];//创建接收缓冲区
                    inputStream = socket.getInputStream();
                    final int len = inputStream.read(buffer);//数据读出来，并且返回数据的长度
                    runOnUiThread(new Runnable()//不允许其他线程直接操作组件，用提供的此方法可以
                    {
                        public void run()
                        {
// TODO Auto-generated method stub
                            RrceiveEditText.setText(new String(buffer,0,len));
                        }
                    });
                }
            }
            catch (IOException e)
            {
// TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
}