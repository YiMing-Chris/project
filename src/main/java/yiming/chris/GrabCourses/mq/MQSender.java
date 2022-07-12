package yiming.chris.GrabCourses.mq;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yiming.chris.GrabCourses.config.RabbitMQConfig;
import yiming.chris.GrabCourses.message.SecKillMessage;

import javax.annotation.PostConstruct;
import java.util.UUID;

/**
 * ClassName:MQSender
 * Package:yiming.chris.GrabCourses.mq
 * Description:
 *
 * @Author: ChrisEli
 */
@Component

public class MQSender {
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void messageConfirm() {
        //1.确认消息是否到达交换机中。(Producer ----->  Exchange)
        //通过实现 ConfirmCallback 接口，确认消息是否正确到达Exchange中
        //实现RabbitTemplate类的一个内部接口ConfirmCallback，
        //注意:所有推送到该交换机的消息都会进入该方法中，推送ack参数来判断该交换机是否确切收到消息
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            //如果ack为true则代表Exchange已经收到消息
            if (!ack) {
                logger.warn("消息发送到exchange失败，correlationData={}, cause={}", correlationData, cause);
            }
            logger.info("回调线程{}:消息{},交换机已收到", Thread.currentThread().getId(), correlationData);
        });

        //2.确认消息是否从交换机转发到了具体队列中。(Exchange----->  Queue)
        //实现RabbitTemplate类的一个内部接口ReturnCallback，启动消息失败返回,
        //只要消息没有从交换机转发到对应的队列中去就会进入该方法
        // 消息从交换到队列失败，失败原因可能是路由键不存在，通道未绑定等等，一般都跟配置有关系。
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            logger.error("未找到路由为{}的队列", routingKey);
        });
    }

    /**
     * 发送秒杀请求消息，包含用户和商品id
     *
     * @param secKillMessage
     */

    public void sendSecKillMessage(SecKillMessage secKillMessage) {
        String emailmsg = UUID.randomUUID().toString();

        //CorrelationData可以携带一个字符串信息，协同需要生产的消息一起推送给交换机
        //在消息确认中，可以针对CorrelationData对象携带字符串进行相关的业务操作
        //比如携带字符串为:order=123456,则在消息确认时就能知道是order队列发送的消息。
        //如果ack为false确认失败，也可以将其存起来，用以和上游业务比对数据
        logger.info("用户" + secKillMessage.getStudent().getId() + "发起抢课:" + secKillMessage.getCoursesId() + " 请求");

        rabbitTemplate.convertAndSend("amq.direct", "Grab", secKillMessage, new CorrelationData(emailmsg));
    }
}
