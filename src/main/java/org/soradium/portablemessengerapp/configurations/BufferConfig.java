package org.soradium.portablemessengerapp.configurations;

import org.soradium.portablemessengerapp.entity.Chat;
import org.soradium.portablemessengerapp.entity.Message;
import org.soradium.portablemessengerapp.entity.User;
import org.soradium.portablemessengerapp.tools.BufferList;
import org.soradium.portablemessengerapp.tools.RingBufferList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:tool.properties")
@Configuration
public class BufferConfig {

    @Value("${ringbuffer.user.max-size}")
    private int userMaxSize;

    @Value("${ringbuffer.message.max-size}")
    private int messageMaxSize;

    @Value("${ringbuffer.chat.max-size}")
    private int chatMaxSize;

    @Bean
    public BufferList<User> userRingBuffer() {
        RingBufferList.RingBufferListBuilder<User> builder
                = new RingBufferList.RingBufferListBuilder<>();
        return builder.withMaxSize(this.userMaxSize).build();
    }

    @Bean
    public BufferList<Message> messageRingBuffer() {
        RingBufferList.RingBufferListBuilder<Message> builder
                = new RingBufferList.RingBufferListBuilder<>();
        return builder.withMaxSize(this.messageMaxSize).build();
    }

    @Bean
    public BufferList<Chat> chatRingBuffer() {
        RingBufferList.RingBufferListBuilder<Chat> builder
                = new RingBufferList.RingBufferListBuilder<>();
        return builder.withMaxSize(this.chatMaxSize).build();
    }


}
