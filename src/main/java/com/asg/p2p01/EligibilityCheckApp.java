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

public class EligibilityCheckApp {
    public static void main(String[] args) throws Exception {
        InitialContext initialContext = new InitialContext();

        final CountDownLatch latch = new CountDownLatch(1);

        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616", "admin", "admin");
                JMSContext jmsContext = cf.createContext()) {

            Queue reqQ = (Queue) initialContext.lookup("queue/requestQueue");

            JMSConsumer reqC = jmsContext.createConsumer(reqQ);
            reqC.setMessageListener(new EligibilityCheckListener(latch));

            latch.await();

        }
    }

}

class EligibilityCheckListener implements MessageListener {
    final CountDownLatch latch01;

    public EligibilityCheckListener(CountDownLatch latch02) {
        latch01 = latch02;
    }

    @Override
    public void onMessage(Message message) {
        try (ActiveMQConnectionFactory cf = new ActiveMQConnectionFactory("tcp://localhost:61616", "admin", "admin");
                JMSContext jmsContext = cf.createContext()) {
            ObjectMessage msg = (ObjectMessage) message;
            Patient b = (Patient) msg.getObject();
            System.out.println(b.getId());
            System.out.println(b.getName());
            
            String insuranceS = b.getInsuranceProvider();
            System.out.println(insuranceS);

            MapMessage msgR = jmsContext.createMapMessage();
            msgR.setBoolean("approved", false);

            if (insuranceS.equalsIgnoreCase("Blue cross Blue shield") || insuranceS.equalsIgnoreCase("United Health")) {
                if (b.getCopay() < 100 && b.getTopay() < 1000) {
                    msgR.setBoolean("approved", true);

                }

            }

            InitialContext initialContext = new InitialContext();
            Queue replyQ = (Queue) initialContext.lookup("queue/replyQueue");
            JMSProducer reqProducer = jmsContext.createProducer();
            reqProducer.send(replyQ, msgR);

            latch01.countDown();





        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

}
