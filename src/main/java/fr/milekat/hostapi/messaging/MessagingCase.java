package fr.milekat.hostapi.messaging;

public enum MessagingCase {
    //  From Proxy To Host
    INVITE_SENT,
    INVITE_RESULT_NOT_FOUND,
    INVITE_RESULT_DENY,

    //  From Host To Proxy
    GAME_READY,
    HOST_JOINED,
    GAME_FINISHED,

    HOST_INVITE_PLAYER,
    HOST_DENIED_REQUEST,

    //  From Lobby To Host
    JOIN_REQUEST,

    //  From Lobby To Proxy
    ASK_CREATE_HOST,
}
