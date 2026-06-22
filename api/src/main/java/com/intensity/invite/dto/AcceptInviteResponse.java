package com.intensity.invite.dto;

import java.util.UUID;

public record AcceptInviteResponse(UUID groupId, boolean membershipConfirmed) {
}
