package yiming.chris.GrabCourses.mq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import yiming.chris.GrabCourses.config.RabbitMQConfig;
import yiming.chris.GrabCourses.message.SecKillMessage;

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

    /**
     * 发送秒杀请求消息，包含用户和商品id
     * @param secKillMessage
     */

    public void sendSecKillMessage(SecKillMessage secKillMessage) {
        logger.info("用户" + secKillMessage.getStudent().getId() + "发起抢课:" + secKillMessage.getCoursesId() + " 请求");
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE, secKillMessage);
    }
}
