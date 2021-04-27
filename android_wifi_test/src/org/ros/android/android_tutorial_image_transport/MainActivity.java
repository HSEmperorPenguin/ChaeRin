/*
 * Copyright (C) 2011 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.ros.android.android_tutorial_image_transport;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;

/*import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.MainThread;*/
import android.util.Log;
/*import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;*/
import android.widget.TextView;


import org.apache.xmlrpc.util.ThreadPool;
import org.ros.address.InetAddressFactory;
import org.ros.android.BitmapFromCompressedImage;
import org.ros.android.RosActivity;
import org.ros.android.view.RosImageView;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;

import std_msgs.Int32;

/*import static android.os.SystemClock.sleep;*/


/**
 * @author ethan.rublee@gmail.com (Ethan Rublee)
 * @author damonkohler@google.com (Damon Kohler)
 */
public class MainActivity extends RosActivity {

  private RosImageView<sensor_msgs.CompressedImage> image;
  /*private Button go_button;
  private Button back_button,speed_button;
  private ImageView handle,blur;*/
/*  private Talker state;
  private Rotate rotate;*/

  /*wifi signal*/
  private WifiSignal wifiSignal;
  private TextView mySignal;
  //private int myCount=1;

  private WifiManager wifiManager;
  private WifiInfo wifiInfo;
  private int _rssi = 40;
  //private SignalThread signalThread;

  public MainActivity() {
    super("ImageTransportTutorial", "ImageTransportTutorial");
  }

  @SuppressWarnings("unchecked")
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);
    image = (RosImageView<sensor_msgs.CompressedImage>) findViewById(R.id.image);
    image.setTopicName("/usb_cam/image_raw/compressed");
    image.setMessageType(sensor_msgs.CompressedImage._TYPE);
    image.setMessageToBitmapCallable(new BitmapFromCompressedImage());

    /*wifi signal*/
    mySignal = (TextView)findViewById(R.id.signalTextView);
    mySignal.setText("wifi signal 세기: 0");
    signalThread st = new signalThread();
    st.start();
  }
  public void setSignalTextView(String str){
    mySignal.setText(str);
  }

  @Override
  protected void init(NodeMainExecutor nodeMainExecutor) {

    /*state = new Talker("joystick_state");
    state.setMessage("stop");

    rotate = new Rotate("joystick_rotate");
    rotate.setMessage(0);*/

    wifiSignal = new WifiSignal("wifi_signal");
    wifiSignal.setMessage(100);

    NodeConfiguration nodeConfiguration =
            NodeConfiguration.newPublic(InetAddressFactory.newNonLoopback().getHostAddress(),
                    getMasterUri());

    //nodeMainExecutor.execute(state, nodeConfiguration);
    //nodeMainExecutor.execute(rotate, nodeConfiguration);
    nodeMainExecutor.execute(wifiSignal, nodeConfiguration);
    nodeMainExecutor.execute(image, nodeConfiguration.setNodeName("android/video_view"));
  }
  private class signalThread extends Thread{
    @Override
    public void run() {

      while(true){
        wifiManager = (WifiManager) MainActivity.this.getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiInfo = wifiManager.getConnectionInfo();
        _rssi = wifiInfo.getRssi();
        Log.i("thread",""+_rssi);
        //rotate.setMessage(_rssi);
        setSignalTextView("wifi signal 세기: "+_rssi);

        try {
          Thread.sleep(1000);
        }catch (InterruptedException e){
          e.printStackTrace();
        }
      }
    }
  }
  /*wifiSignal 클래스: publisher임.*/
  class WifiSignal extends AbstractNodeMain {
    private String topic_name;
    private Integer message;

    public WifiSignal() {topic_name = "chatter";}

    public WifiSignal(String topic){topic_name = topic;}

    public void setMessage(Integer message){
      //this.message=message;

    }

    @Override
    public GraphName getDefaultNodeName() {
      return GraphName.of("rosjava_tutorial_pubsub/signal");
    }

    @Override
    public void onStart(final ConnectedNode connectedNode) {
      final Publisher<Int32> publisher =
              connectedNode.newPublisher(topic_name, std_msgs.Int32._TYPE);
      // This CancellableLoop will be canceled automatically when the node shuts
      // down.
      connectedNode.executeCancellableLoop(new CancellableLoop() {
        private int sequenceNumber;

        @Override
        protected void setup() {
          sequenceNumber = 0;
        }

        @Override
        protected void loop() throws InterruptedException {
          //message = _rssi;
          std_msgs.Int32 val =  publisher.newMessage();
          val.setData(_rssi); //wifi 신호 세기 pub.
          Log.i("PubLoop",""+_rssi);
          publisher.publish(val);
          Thread.sleep(1000);
        }
      });
    }

  }

}


