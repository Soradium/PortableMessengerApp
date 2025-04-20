package org.soradium.portablemessengerapp.dto;

/// `targetType`- Group or User as a type requested, `tagret` - particular user or group

public record GroupUserChatTargetDtoRequest(String targetType, String target) {
}
