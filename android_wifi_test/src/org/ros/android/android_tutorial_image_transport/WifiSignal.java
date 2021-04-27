package org.ros.android.android_tutorial_image_transport;

import org.ros.concurrent.CancellableLoop;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.topic.Publisher;

import std_msgs.Int32;

public class WifiSignal extends AbstractNodeMain {
    private String topic_name;
    private Integer message;

    public WifiSignal() {
        topic_name = "chatter";
    }

    public WifiSignal(String topic)
    {
        topic_name = topic;
    }

    public void setMessage(Integer message){
        this.message=message;
    }

    @Override
    public GraphName getDefaultNodeName() {
        return GraphName.of("rosjava_tutorial_pubsub/rotate");
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
                std_msgs.Int32 val = publisher.newMessage();
                val.setData(message);
                publisher.publish(val);
                //Thread.sleep(1000);
            }
        });
    }
}
