package com.intensity.convite.dto;

import java.util.UUID;

public record AcceptInviteResponse(UUID groupId, boolean membershipConfirmed) {
}
