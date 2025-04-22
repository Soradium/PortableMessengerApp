package org.soradium.portablemessengerapp.configurations;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.soradium.portablemessengerapp.dto.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.converter.BatchMessagingMessageConverter;
import org.springframework.kafka.support.converter.JsonMessageConverter;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableAsync
public class KafkaConfig {

    // Producer config

    @Bean
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        JsonSerializer<Object> serializer = new JsonSerializer<>();
        serializer.setAddTypeInfo(false);

        return new DefaultKafkaProducerFactory<>(
                producerConfigs(),
                new StringSerializer(),
                serializer
        );
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaMessageTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }


    // Consumer(listener) config

    @Bean
    public Map<String, Object> consumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return props;
    }

    @Bean
    public JsonMessageConverter jsonConverter() {
        return new JsonMessageConverter();
    }

    @Bean
    public ConsumerFactory<String, MessageDto> consumerMessageDtoFactory() {
        JsonDeserializer<MessageDto> deserializer =
                new JsonDeserializer<>(MessageDto.class);
        deserializer.addTrustedPackages("org.soradium.portablemessengerappfrontendapi.dto");
        deserializer.setUseTypeMapperForKey(false);
        deserializer.setRemoveTypeHeaders(true);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerMessageDtoContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, MessageDto>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setBatchListener(true);
        factory.setConsumerFactory(consumerMessageDtoFactory());
        factory.setBatchMessageConverter(
                new BatchMessagingMessageConverter(jsonConverter()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, RequestMessageListDto> consumerRequestMessageListFactory() {
        JsonDeserializer<RequestMessageListDto> deserializer =
                new JsonDeserializer<>(RequestMessageListDto.class);
        deserializer.addTrustedPackages("org.soradium.portablemessengerappfrontendapi.dto");
        deserializer.setUseTypeMapperForKey(false);
        deserializer.setRemoveTypeHeaders(true);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerRequestMessageListContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RequestMessageListDto>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setBatchListener(true);
        factory.setConsumerFactory(consumerRequestMessageListFactory());
        factory.setBatchMessageConverter(
                new BatchMessagingMessageConverter(jsonConverter()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, FriendRequestSenderAndReceiverDto> consumerFriendRequestFactory() {
        JsonDeserializer<FriendRequestSenderAndReceiverDto> deserializer =
                new JsonDeserializer<>(FriendRequestSenderAndReceiverDto.class);
        deserializer.addTrustedPackages("org.soradium.portablemessengerappfrontendapi.dto");
        deserializer.setUseTypeMapperForKey(false);
        deserializer.setRemoveTypeHeaders(true);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerFriendRequestContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, FriendRequestSenderAndReceiverDto>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setBatchListener(true);
        factory.setConsumerFactory(consumerFriendRequestFactory());
        factory.setBatchMessageConverter(
                new BatchMessagingMessageConverter(jsonConverter()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, UserRequesterAndUserRequestedToDto> consumerUserRequestedAndRequestedToFactory() {
        JsonDeserializer<UserRequesterAndUserRequestedToDto> deserializer =
                new JsonDeserializer<>(UserRequesterAndUserRequestedToDto.class);
        deserializer.addTrustedPackages("org.soradium.portablemessengerappfrontendapi.dto");
        deserializer.setUseTypeMapperForKey(false);
        deserializer.setRemoveTypeHeaders(true);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerUserRequestedAndRequestedToContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UserRequesterAndUserRequestedToDto>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setBatchListener(true);
        factory.setConsumerFactory(consumerUserRequestedAndRequestedToFactory());
        factory.setBatchMessageConverter(
                new BatchMessagingMessageConverter(jsonConverter()));
        return factory;
    }

    @Bean
    public ConsumerFactory<String, UsernameAsObjectDto> consumerUsernameAsDtoFactory() {
        JsonDeserializer<UsernameAsObjectDto> deserializer =
                new JsonDeserializer<>(UsernameAsObjectDto.class);
        deserializer.addTrustedPackages("org.soradium.portablemessengerappfrontendapi.dto");
        deserializer.setUseTypeMapperForKey(false);
        deserializer.setRemoveTypeHeaders(true);

        return new DefaultKafkaConsumerFactory<>(
                consumerConfigs(),
                new StringDeserializer(),
                deserializer
        );
    }

    @Bean
    public KafkaListenerContainerFactory<?> kafkaListenerUsernameAsDtoContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, UsernameAsObjectDto>
                factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setBatchListener(true);
        factory.setConsumerFactory(consumerUsernameAsDtoFactory());
        factory.setBatchMessageConverter(
                new BatchMessagingMessageConverter(jsonConverter()));
        return factory;
    }


}
