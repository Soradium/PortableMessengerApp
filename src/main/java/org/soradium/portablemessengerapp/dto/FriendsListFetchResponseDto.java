package org.soradium.portablemessengerapp.dto;

import java.util.List;

public record FriendsListFetchResponseDto(String usernameRequester, List<String> friendUsernames) {
}
