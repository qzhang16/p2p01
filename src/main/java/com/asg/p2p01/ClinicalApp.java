package com.asg.p2p01;

import java.util.concurrent.CountDownLatch;

import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSProducer;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.naming.InitialContext;

import org.apache.activemq.artemis.jms.client.ActiveMQConnectionFactory;

public class ClinicalApp {
    public static void main(String[] args) throws Exception {
        InitialContext initialContext = new InitialContext();
        final CountDownLatch latch = new CountDownLatch(1);
        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616", "admin", "admin");
                JMSContext jmsContext = cf.createContext()) {

            Queue reqQ = (Queue) initialContext.lookup("queue/requestQueue");
            Queue replyQ = (Queue) initialContext.lookup("queue/replyQueue");

            JMSProducer reqProducer = jmsContext.createProducer();
            Patient a = new Patient();
            a.setId(123);
            a.setName("Joe");
            a.setInsuranceProvider("Blue1 cross Blue shield");
            a.setCopay(12.4);
            a.setTopay(34.1);

            ObjectMessage msg = jmsContext.createObjectMessage(a);

            // reqProducer.send(reqQ, a);
            reqProducer.send(reqQ, msg);

            JMSConsumer replyC = jmsContext.createConsumer(replyQ);
            replyC.setMessageListener(new ClinicalListener(latch));

            latch.await();

        }

    }

}

class ClinicalListener implements MessageListener {
    final CountDownLatch latch01;

    public ClinicalListener(CountDownLatch latch02) {
        latch01 = latch02;
    }

    @Override
    public void onMessage(Message message) {
        try{
            MapMessage msg = (MapMessage) message;
            
            System.out.println(msg.getBoolean("approved"));

            latch01.countDown();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
