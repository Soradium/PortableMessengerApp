package org.soradium.portablemessengerapp.dto;

import java.util.List;

public record SenderAndRetrievedMessageListDto(String requester, List<SentMessageDto> messages, String response) {
}

